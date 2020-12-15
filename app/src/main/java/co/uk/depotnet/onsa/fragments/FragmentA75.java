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

import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.AdapterA75Groups;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.A75Groups;
import co.uk.depotnet.onsa.modals.Job;

public class FragmentA75 extends Fragment {
    private static final String ARG_JOB = "Job";
    private Context context;
    private ProgressBar progressBar;
    private AdapterA75Groups adapter;

    private Job job;
    private List<A75Groups> a75Gropus;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static FragmentA75 newInstance(Job job){
        FragmentA75 fragmentNotices = new FragmentA75();
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
        a75Gropus = new ArrayList<>();
        adapter = new AdapterA75Groups(context , a75Gropus);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work_item, container , false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(context , LinearLayoutManager.VERTICAL , false));
        recyclerView.setAdapter(adapter);
        getA75Groups();
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

    private void getA75Groups(){
        FragmentA75.this.a75Gropus.clear();
        ArrayList<A75Groups> a75Groups = DBHandler.getInstance().getA75Groups(job.getjobId());
        if(a75Groups != null && !a75Groups.isEmpty())
        this.a75Gropus.addAll(a75Groups);
        adapter.notifyDataSetChanged();


    }
}
