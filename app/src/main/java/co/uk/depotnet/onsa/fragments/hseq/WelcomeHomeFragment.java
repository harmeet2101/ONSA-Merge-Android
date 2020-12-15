package co.uk.depotnet.onsa.fragments.hseq;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.MainActivity;
import co.uk.depotnet.onsa.activities.SettingsActivity;
import co.uk.depotnet.onsa.activities.ui.BriefingsActivity;
import co.uk.depotnet.onsa.activities.ui.HSEQActivity;
import co.uk.depotnet.onsa.activities.WelcomeActivity;
import co.uk.depotnet.onsa.adapters.WelcomeHomeAdapter;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.fragments.store.FragmentStore;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.store.DataMyRequests;
import co.uk.depotnet.onsa.modals.store.DataReceipts;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.networking.Constants;
import co.uk.depotnet.onsa.utils.AppPreferences;
import co.uk.depotnet.onsa.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomeHomeFragment extends Fragment implements WelcomeHomeAdapter.OnItemClickListener {

    private User user;
    private int apiCounter;
    private Context context;
    private FragmentActionListener listener;

    public static WelcomeHomeFragment newInstance() {
        WelcomeHomeFragment fragment = new WelcomeHomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        listener = (FragmentActionListener)context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = DBHandler.getInstance().getUser();

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mRecyclerView = view.findViewById(R.id.welcome_home_recycler);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context,2));
        WelcomeHomeAdapter mAdapter = new WelcomeHomeAdapter(context, this);
        view.findViewById(R.id.btn_img_settings).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });
        mRecyclerView.setAdapter(mAdapter);
    }



    @Override
    public void onResume() {
        super.onResume();
        apiCounter = 0;
        if(Constants.isStoreEnabled && CommonUtils.isNetworkAvailable(context)) {
            listener.showProgressBar();
            getMyRequests();
            getReceipts();
        }
    }

    @Override
    public void onItemClick(int WH_id) {
        switch (WH_id)
        {
            case 1:
                AppPreferences.setTheme(5);// setting to wrong value for depotnet theme
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                break;
            case 2:
                AppPreferences.setTheme(1);
                Intent hseq = new Intent(context, HSEQActivity.class);
                startActivity(hseq);
                break;
            case 3:
                Utils.store_call = true;
                FragmentStore fragmentStore = FragmentStore.newInstance();
                ((WelcomeActivity)context).addFragment(fragmentStore, false);
                break;
            case 4:
                AppPreferences.setTheme(2);
                Intent brief = new Intent(context, BriefingsActivity.class);
                startActivity(brief);
                break;
            default:
                break;
        }
    }

    private void getReceipts(){
        if(!CommonUtils.validateToken(context)){
            return;
        }
        APICalls.getReceipts(user.gettoken()).enqueue(new Callback<DataReceipts>() {
            @Override
            public void onResponse(@NonNull Call<DataReceipts> call, @NonNull Response<DataReceipts> response) {
                apiCounter++;
                if(CommonUtils.onTokenExpired(context , response.code())){
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    response.body().toContentValues();
                }
                onApiCallResponse();
            }

            @Override
            public void onFailure(@NonNull Call<DataReceipts> call, @NonNull Throwable t) {
                onApiCallResponse();
            }
        });
    }

    private void onApiCallResponse(){
        if(apiCounter == 2 || (!Constants.isStoreEnabled && apiCounter == 0)){
            listener.hideProgressBar();
        }
    }


    private void getMyRequests(){
        if(!CommonUtils.validateToken(context)){
            return;
        }
        APICalls.getMyRequests(user.gettoken()).enqueue(new Callback<DataMyRequests>() {
            @Override
            public void onResponse(@NonNull Call<DataMyRequests> call, @NonNull Response<DataMyRequests> response) {
                apiCounter++;
                if(CommonUtils.onTokenExpired(context , response.code())){
                    return;
                }

                if(response.isSuccessful()){
                    DataMyRequests dataMyRequests = response.body();
                    if(dataMyRequests != null){
                        DBHandler.getInstance().resetMyRequest();
                        dataMyRequests.toContentValues();
                    }
                }
                onApiCallResponse();
            }

            @Override
            public void onFailure(@NonNull Call<DataMyRequests> call, @NonNull Throwable t) {
                onApiCallResponse();
            }
        });
    }

}

