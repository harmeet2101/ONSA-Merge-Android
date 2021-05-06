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

import java.util.ArrayList;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.JobDetailAdapter;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.JobDetailItem;
import co.uk.depotnet.onsa.utils.Utils;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;

public class FragmentChildJobDetails extends Fragment {
    private static final String ARG_JOB = "Job";
    private ArrayList<JobDetailItem> arrayList;
    private Context context;
    private Job job;

    public static FragmentChildJobDetails newInstance(Job job) {
        FragmentChildJobDetails fragmentChildJobDetails = new FragmentChildJobDetails();
        Bundle args = new Bundle();
        args.putParcelable(ARG_JOB, job);
        fragmentChildJobDetails.setArguments(args);
        return fragmentChildJobDetails;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            job = args.getParcelable(ARG_JOB);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_job_detail, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        getJobDetail();

        JobDetailAdapter adapter = new JobDetailAdapter(context, arrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(10));
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void getJobDetail() {
        arrayList = new ArrayList<>();

        JobDetailItem jobDetailItem = new JobDetailItem();
        jobDetailItem.setTitle("Contract");
        jobDetailItem.setValue(job.getcontract());
        arrayList.add(jobDetailItem);

        jobDetailItem = new JobDetailItem();
        jobDetailItem.setTitle("Job Status");
        jobDetailItem.setValue(job.getstatus());
        arrayList.add(jobDetailItem);

        jobDetailItem = new JobDetailItem();
        jobDetailItem.setTitle("Estimate Number");
        jobDetailItem.setValue(job.getestimateNumber());
        arrayList.add(jobDetailItem);

        jobDetailItem = new JobDetailItem();
        jobDetailItem.setTitle("Exchange");
        jobDetailItem.setValue(job.getexchange());
        arrayList.add(jobDetailItem);


        jobDetailItem = new JobDetailItem();
        jobDetailItem.setTitle("Job Category");
        jobDetailItem.setValue(job.getjobCatagory());
        arrayList.add(jobDetailItem);


        jobDetailItem = new JobDetailItem();
        jobDetailItem.setTitle("Job Number");
        jobDetailItem.setValue(job.getjobNumber());
        arrayList.add(jobDetailItem);

        jobDetailItem = new JobDetailItem();
        jobDetailItem.setTitle("Location Address");
        jobDetailItem.setValue(job.getlocationAddress());
        arrayList.add(jobDetailItem);

        jobDetailItem = new JobDetailItem();
        jobDetailItem.setTitle("Post Code");
        jobDetailItem.setValue(job.getpostCode());
        arrayList.add(jobDetailItem);

        jobDetailItem = new JobDetailItem();
        jobDetailItem.setTitle("Job Order Notes");
        jobDetailItem.setValue(job.getJobOrderNotes());
        arrayList.add(jobDetailItem);

        jobDetailItem = new JobDetailItem();
        jobDetailItem.setTitle("Priority");
        jobDetailItem.setValue(job.getpriority());
        arrayList.add(jobDetailItem);

        jobDetailItem = new JobDetailItem();
        jobDetailItem.setTitle("Required By Date");
        jobDetailItem.setValue(Utils.formatDate(job.getrequiredByDate() , "yyyy-MM-dd'T'HH:mm:ss","dd/MM/yyyy"));
        arrayList.add(jobDetailItem);

        jobDetailItem = new JobDetailItem();
        jobDetailItem.setTitle("Scheduled Start Date");
        jobDetailItem.setValue(Utils.formatDate(job.getScheduledStartDate() , "yyyy-MM-dd'T'HH:mm:ss","dd/MM/yyyy"));
        arrayList.add(jobDetailItem);

        jobDetailItem = new JobDetailItem();
        jobDetailItem.setTitle("Scheduled End Date");
        jobDetailItem.setValue(Utils.formatDate(job.getScheduledEndDate() , "yyyy-MM-dd'T'HH:mm:ss","dd/MM/yyyy"));
        arrayList.add(jobDetailItem);



        jobDetailItem = new JobDetailItem();
        jobDetailItem.setTitle("short Address");
        jobDetailItem.setValue(job.getshortAddress());
        arrayList.add(jobDetailItem);

        jobDetailItem = new JobDetailItem();
        jobDetailItem.setTitle("Special Instructions");
        jobDetailItem.setValue(job.getspecialInstructions());
        arrayList.add(jobDetailItem);


        jobDetailItem = new JobDetailItem();
        jobDetailItem.setTitle("Work Title");
        jobDetailItem.setValue(job.getworkTitle());
        arrayList.add(jobDetailItem);


    }

}
