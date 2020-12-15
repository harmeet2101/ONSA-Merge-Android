package co.uk.depotnet.onsa.fragments.store;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.adapters.store.AdapterReceiptItems;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.store.DataReceipts;
import co.uk.depotnet.onsa.modals.store.ReceiptItems;
import co.uk.depotnet.onsa.modals.store.Receipts;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.Utils;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReceiptItemsFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_RECEIPT = "Receipts";
    private static final String ARG_SUBMISSION_ID = "_arg_submission_id";
    private Receipts receipt;
    private FragmentActionListener listener;
    private User user;
    private AdapterReceiptItems adapterReceiptItems;
    private Context context;
    private long submissionId;

    public static ReceiptItemsFragment newInstance(Receipts receipts, long submissionId) {
        ReceiptItemsFragment fragment = new ReceiptItemsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RECEIPT, receipts);
        args.putLong(ARG_SUBMISSION_ID, submissionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            receipt = args.getParcelable(ARG_RECEIPT);
            user = DBHandler.getInstance().getUser();
            submissionId = args.getLong(ARG_SUBMISSION_ID);
            ArrayList<ReceiptItems> receiptItems =new ArrayList<>();
            if (receipt != null && receipt.getItems() != null && !receipt.getItems().isEmpty()) {
                receiptItems = receipt.getItems();
            }
            adapterReceiptItems = new AdapterReceiptItems(receiptItems);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receipt_items_list, container, false);
        try {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            if (Utils.store_call) {
                params.bottomMargin += 160;
                view.requestLayout();
            }
            else {
                params.bottomMargin += 20;
                view.requestLayout();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.setAdapter(adapterReceiptItems);
        VerticalSpaceItemDecoration decoration = new VerticalSpaceItemDecoration(16);
        recyclerView.addItemDecoration(decoration);

        view.findViewById(R.id.btn_accept).setOnClickListener(this);
        view.findViewById(R.id.btn_reject).setOnClickListener(this);
        view.findViewById(R.id.btn_img_cancel).setOnClickListener(this);

        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof FragmentActionListener) {
            listener = (FragmentActionListener) context;
        }
    }


    private void onAccept(Receipts items) {

        adapterReceiptItems.clearFocus();
        String jsonFileName = "store_receipt_accept.json";

        Submission submission;
        if(submissionId == 0){
            submission = new Submission(jsonFileName, "receipt_accept", "");
            long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
            submission.setId(submissionID);
        }else{
            submission = DBHandler.getInstance().getSubmission(String.valueOf(submissionId));
        }


        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        intent.putExtra(FormActivity.ARG_RECEIPT, items);
        startActivityForResult(intent, 1000);
    }


    private void onReject(Receipts items) {
        adapterReceiptItems.clearFocus();
        String jsonFileName = "store_receipt_reject.json";

        Submission submission;
        if(submissionId == 0){
            submission = new Submission(jsonFileName, "receipt_reject", "");
            long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
            submission.setId(submissionID);
        }else{
            submission = DBHandler.getInstance().getSubmission(String.valueOf(submissionId));
        }


        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        intent.putExtra(FormActivity.ARG_RECEIPT, items);
        startActivityForResult(intent, 1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            getReceipts();
        }
    }

    private void getReceipts() {
        if(!CommonUtils.validateToken(context)){
            return;
        }
        listener.showProgressBar();
        APICalls.getReceipts(user.gettoken()).enqueue(new Callback<DataReceipts>() {
            @Override
            public void onResponse(@NonNull Call<DataReceipts> call, @NonNull Response<DataReceipts> response) {
                if(CommonUtils.onTokenExpired(context , response.code())){
                    return;
                }

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        DBHandler.getInstance().resetReceipts();
                        response.body().toContentValues();
                        listener.hideProgressBar();
                        listener.setReceiptsBadge(String.valueOf(response.body().getCount()));
                        ((Activity)context).onBackPressed();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataReceipts> call, @NonNull Throwable t) {
                listener.hideProgressBar();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_accept:
                onAccept(receipt);
                break;
            case R.id.btn_reject:
                onReject(receipt);
                break;
            case R.id.btn_img_cancel:
                ((Activity)context).onBackPressed();
                break;
        }
    }
}
