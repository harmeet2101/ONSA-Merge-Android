package co.uk.depotnet.onsa.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.AdapterNotes;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.Note;

public class FragmentNotes extends Fragment {
    private static final String ARG_JOB = "Job";
    private Context context;
    private ProgressBar progressBar;
    private AdapterNotes adapter;

    private Job job;
    private List<Note> notes;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static FragmentNotes newInstance(Job job){
        FragmentNotes fragmentNotices = new FragmentNotes();
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
        notes = new ArrayList<>();
        adapter = new AdapterNotes(context , notes);
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
        FragmentNotes.this.notes.clear();
        ArrayList<Note> notes = DBHandler.getInstance().getNotes(job.getjobId());
        if(notes != null && !notes.isEmpty())
        this.notes.addAll(notes);
        adapter.notifyDataSetChanged();
    }
}
