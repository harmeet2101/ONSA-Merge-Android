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
import co.uk.depotnet.onsa.adapters.AdapterNotice;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.Job;

import co.uk.depotnet.onsa.modals.Notice;

import java.util.ArrayList;
import java.util.List;

public class FragmentNotices extends Fragment {
    private static final String ARG_JOB = "Job";
    private Context context;
    private ProgressBar progressBar;
    private AdapterNotice adapter;

    private Job job;
    private List<Notice> jobNotices;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static FragmentNotices newInstance(Job job){
        FragmentNotices fragmentNotices = new FragmentNotices();
        Bundle args = new Bundle();
        args.putParcelable(ARG_JOB , job);
        fragmentNotices.setArguments(args);
        return fragmentNotices;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        job = args.getParcelable(ARG_JOB);
        jobNotices = new ArrayList<>();
        adapter = new AdapterNotice(context , jobNotices);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work_item, container , false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(context , LinearLayoutManager.VERTICAL , false));
        recyclerView.setAdapter(adapter);
        getNotice();
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

    private void getNotice(){
        FragmentNotices.this.jobNotices.clear();
        ArrayList<Notice> notices = DBHandler.getInstance().getNotices(job.getjobId());
        if(notices != null && !notices.isEmpty())
        FragmentNotices.this.jobNotices.addAll(notices);
        adapter.notifyDataSetChanged();


    }
}
