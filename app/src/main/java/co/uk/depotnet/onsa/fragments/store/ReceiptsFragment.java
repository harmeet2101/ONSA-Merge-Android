package co.uk.depotnet.onsa.fragments.store;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.store.AdapterMyReceipts;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.store.DataReceipts;
import co.uk.depotnet.onsa.modals.store.Receipts;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CallUtils;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReceiptsFragment extends Fragment {

    private ArrayList<Receipts> receipts;
    private FragmentActionListener listener;
    private Context context;
    private AdapterMyReceipts adapter;
    private DBHandler dbHandler;
    private User user;



    public static ReceiptsFragment newInstance() {
        ReceiptsFragment fragment = new ReceiptsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.dbHandler = DBHandler.getInstance(context);
        this.user = dbHandler.getUser();

        receipts = new ArrayList<>();
        adapter = new AdapterMyReceipts(receipts , listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receipts_list, container, false);


        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.setAdapter(adapter);
        VerticalSpaceItemDecoration decoration = new VerticalSpaceItemDecoration(16);
        recyclerView.addItemDecoration(decoration);

        view.findViewById(R.id.btn_img_cancel).setOnClickListener(v -> ((Activity)context).onBackPressed());
        getReceipts();
        return view;
    }

    private void getReceipts() {
        if(!CommonUtils.isNetworkAvailable(context)){
            return;
        }
        if(!CommonUtils.validateToken(context)){
            return;
        }
        listener.showProgressBar();
        CallUtils.enqueueWithRetry(APICalls.getReceipts(user.gettoken()), new Callback<DataReceipts>() {
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
                        receipts.clear();
                        receipts.addAll( DBHandler.getInstance().getReceipts());
                        adapter.notifyDataSetChanged();
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
    public void onResume() {
        super.onResume();
        receipts.clear();
        receipts.addAll( DBHandler.getInstance().getReceipts());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if(context instanceof FragmentActionListener){
            listener = (FragmentActionListener)context;
        }
    }


}
