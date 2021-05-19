package co.uk.depotnet.onsa.fragments.store;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.CurrentStoreActivity;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.store.DataMyRequests;
import co.uk.depotnet.onsa.modals.store.DataReceipts;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CallUtils;
import co.uk.depotnet.onsa.networking.CommonUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentStore extends Fragment implements
         View.OnClickListener{

    private Context context;
    private FragmentActionListener listener;
    private TextView txtRequestNoti;
    private TextView txtReceiptNoti;
    private boolean isRequestDataFetched;
    private User user;
    private DBHandler dbHandler;
    private int apiCounter;

    public static FragmentStore newInstance() {
        FragmentStore fragment = new FragmentStore();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dbHandler = DBHandler.getInstance(context);
        this.user = dbHandler.getUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        view.findViewById(R.id.rl_my_current_store).setOnClickListener(this);
        view.findViewById(R.id.rl_goods_out).setOnClickListener(this);
        view.findViewById(R.id.rl_receipts).setOnClickListener(this);
        view.findViewById(R.id.rl_my_request).setOnClickListener(this);
        view.findViewById(R.id.btn_img_cancel).setOnClickListener(v -> ((Activity)context).onBackPressed());

        txtReceiptNoti = view.findViewById(R.id.txt_receipt_notification);
        txtRequestNoti = view.findViewById(R.id.txt_request_notification);

        int receiptCount = DBHandler.getInstance().getReceipts().size();
        txtReceiptNoti.setText(String.valueOf(receiptCount));


        int requestCount = DBHandler.getInstance().getMyRequest().size();
        txtRequestNoti.setText(String.valueOf(requestCount));

        if(!isRequestDataFetched){
            isRequestDataFetched = true;
            if(CommonUtils.isNetworkAvailable(context) && CommonUtils.validateToken(context)) {
                listener.showProgressBar();
                getMyRequests();
                getReceipts();
            }
        }

        return view;
    }

    private void getMyRequests() {
        CallUtils.enqueueWithRetry(APICalls.getMyRequests(user.gettoken()), new Callback<DataMyRequests>() {
            @Override
            public void onResponse(@NonNull Call<DataMyRequests> call, @NonNull Response<DataMyRequests> response) {
               apiCounter++;
                if (CommonUtils.onTokenExpired(context, response.code())) {
                    onApiResponse();
                    return;
                }

                if (response.isSuccessful()) {
                    DataMyRequests dataMyRequests = response.body();
                    if (dataMyRequests != null) {
                        DBHandler.getInstance().resetMyRequest();
                        dataMyRequests.toContentValues();
                    }
                }
                int requestCount = DBHandler.getInstance().getMyRequest().size();
                txtRequestNoti.setText(String.valueOf(requestCount));
                onApiResponse();

            }

            @Override
            public void onFailure(@NonNull Call<DataMyRequests> call, @NonNull Throwable t) {
                apiCounter++;
                int requestCount = DBHandler.getInstance().getMyRequest().size();
                txtRequestNoti.setText(String.valueOf(requestCount));
                onApiResponse();
            }

        });
    }

    private void getReceipts() {

        CallUtils.enqueueWithRetry(APICalls.getReceipts(user.gettoken()), new Callback<DataReceipts>() {
            @Override
            public void onResponse(@NonNull Call<DataReceipts> call, @NonNull Response<DataReceipts> response) {
                apiCounter++;
                if(CommonUtils.onTokenExpired(context , response.code())){
                    onApiResponse();
                    return;
                }

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        DBHandler.getInstance().resetReceipts();
                        response.body().toContentValues();
                        listener.hideProgressBar();

                    }
                }
                int receiptCount = DBHandler.getInstance().getReceipts().size();
                txtReceiptNoti.setText(String.valueOf(receiptCount));
                onApiResponse();
            }

            @Override
            public void onFailure(@NonNull Call<DataReceipts> call, @NonNull Throwable t) {
                apiCounter++;
                int receiptCount = DBHandler.getInstance().getReceipts().size();
                txtReceiptNoti.setText(String.valueOf(receiptCount));
                onApiResponse();
            }
        });
    }

    private void onApiResponse(){
        if(apiCounter == 2){
            listener.hideProgressBar();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        listener.setTitle("Store");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof FragmentActionListener) {
            listener = (FragmentActionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_img_cancel:
                ((Activity) context).onBackPressed();
                break;
            case R.id.rl_my_current_store:
                startActivity(new Intent(context , CurrentStoreActivity.class));
                break;
            case R.id.rl_goods_out:
                openFormActivity("store_standard_goods_out.json","Goods out");
                break;
            case R.id.rl_receipts:
                listener.addFragment(ReceiptsFragment.newInstance() , false);
                break;
            case R.id.rl_my_request:
                listener.addFragment(FragmentMyRequests.newInstance() , false);
                break;
        }
    }

    private void openFormActivity(String jsonFileName, String title) {

        Submission submission = new Submission(jsonFileName, title, "");
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);

        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivityForResult(intent, 1000);

    }
}
