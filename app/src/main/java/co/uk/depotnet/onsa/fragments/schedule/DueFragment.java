package co.uk.depotnet.onsa.fragments.schedule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.adapters.schedule.DueAdaptor;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.OnScheduleListUpdate;
import co.uk.depotnet.onsa.listeners.ScheduleFragmentListener;
import co.uk.depotnet.onsa.listeners.ScheduleListner;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.schedule.Schedule;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.AppPreferences;

public class DueFragment extends Fragment implements ScheduleListner, OnScheduleListUpdate {

    private DueAdaptor dueAdaptor;
    private TextView error;
    private Context context;
    private SwipeRefreshLayout refreshLayout;
    private boolean isRefreshing;
    private ScheduleFragmentListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if(context instanceof ScheduleFragmentListener){
            listener = (ScheduleFragmentListener)context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_due, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mRecyclerView = view.findViewById(R.id.due_recyclerview);
        refreshLayout = view.findViewById(R.id.swipe_container);
        dueAdaptor=new DueAdaptor(context,this);
        mRecyclerView.setAdapter(dueAdaptor);
        error=view.findViewById(R.id.due_error);
        updateList();
        refreshLayout.setOnRefreshListener(() -> {

            if(!isRefreshing) {
                isRefreshing = true;
                fetchData();
            }
        });
    }

    private void fetchData() {
        if(!CommonUtils.isNetworkAvailable(context)){
            isRefreshing = false;
            refreshLayout.setRefreshing(false);
            return;
        }

        listener.refreshData(this::fetchData);
        refreshLayout.setRefreshing(false);
    }


    @Override
    public void updateList(){
        List<Schedule> overdueList = DBHandler.getInstance().getScheduleLocal(1);
        if (!overdueList.isEmpty()) {
            dueAdaptor.update(overdueList);
            dueAdaptor.notifyDataSetChanged();
            error.setVisibility(View.GONE);
        }
        else {
            error.setVisibility(View.VISIBLE);
        }
        isRefreshing = false;
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void StartScheduleInspection(Schedule schedule) {
        AppPreferences.setTheme(1);//setting hseq theme.
        String jsonFileName = "schedule_inspection.json";// case sensitive for submission model too
        Submission submission = new Submission(jsonFileName, "Schedule Due Inspection", schedule.getInspectionTemplateVersionId());// if jobid not than pass blank
        // unique submission id for every form
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        //global value for submission
        Answer answerjobID = DBHandler.getInstance().getAnswer(submissionID,
                "scheduledInspectionId", null, 0);

        if (answerjobID == null) {
            answerjobID = new Answer(submissionID, "scheduledInspectionId");
        }
        answerjobID.setAnswer(schedule.getId());
        answerjobID.setDisplayAnswer("Due id");
        answerjobID.setRepeatCount(0);
        DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answerjobID.toContentValues());
        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        intent.putExtra(FormActivity.ARG_SCHEDULE, schedule);
        startActivity(intent);
    }
}
