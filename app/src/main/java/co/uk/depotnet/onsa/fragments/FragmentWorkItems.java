package co.uk.depotnet.onsa.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.AdapterWorkItems;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.Job;

import co.uk.depotnet.onsa.modals.JobWorkItem;
import co.uk.depotnet.onsa.modals.User;


import java.util.ArrayList;
import java.util.List;


public class FragmentWorkItems extends Fragment {
    private static final String ARG_USER = "User";
    private static final String ARG_JOB = "Job";
    private Context context;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private AdapterWorkItems adapter;
    private List<JobWorkItem> workItems;
    private User user;
    private Job job;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static FragmentWorkItems newInstance(User user , Job job){
        FragmentWorkItems fragmentWorkItems = new FragmentWorkItems();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER , user);
        args.putParcelable(ARG_JOB , job);
        fragmentWorkItems.setArguments(args);
        return fragmentWorkItems;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        user = args.getParcelable(ARG_USER);
        job = args.getParcelable(ARG_JOB);
        workItems = new ArrayList<>();
        adapter = new AdapterWorkItems(context , workItems);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work_item, container , false);
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(context , LinearLayoutManager.VERTICAL , false));
        recyclerView.setAdapter(adapter);
        if(workItems.isEmpty()) {
            getWorkItems();
        }
        return view;
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void getWorkItems(){
        FragmentWorkItems.this.workItems.clear();
        ArrayList<JobWorkItem> jobWorkItem = DBHandler.getInstance().getJobWorkItem(job.getjobId());
        if(jobWorkItem != null && !jobWorkItem.isEmpty())
            workItems.addAll(jobWorkItem);
        adapter.notifyDataSetChanged();
    }


}
