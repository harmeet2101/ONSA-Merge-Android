package co.uk.depotnet.onsa.fragments.actions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import co.uk.depotnet.onsa.OnsaApp;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.adapters.actions.ActionsMainAdaptor;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.ActionsListner;
import co.uk.depotnet.onsa.modals.actions.ActionsClose;
import co.uk.depotnet.onsa.modals.actions.OutstandingAction;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.AppPreferences;


public class ClearedFragment extends Fragment implements ActionsListner {
    private ProgressBar progressBar;
    private RecyclerView mRecyclerView;
    private ActionsMainAdaptor actionAdapter;
    private TextView error;
    private List<OutstandingAction> actionList=new ArrayList<>();
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cleared, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar=view.findViewById(R.id.progress_bar_actions);
        mRecyclerView=view.findViewById(R.id.cleared_recyclerview);
        actionAdapter=new ActionsMainAdaptor(this);
        mRecyclerView.setAdapter(actionAdapter);
        error=view.findViewById(R.id.cleared_error);
        try {
            progressBar.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);
            if(CommonUtils.isNetworkAvailable(getContext())) {
                GetActionsCall();
            } else {
                getActionsFromDb();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    private void GetActionsCall()
    {
        progressBar.setVisibility(View.VISIBLE);
        APICalls.getActionsClearedList(DBHandler.getInstance().getUser().gettoken()).enqueue(new Callback<List<OutstandingAction>>() {
            @Override
            public void onResponse(@NonNull Call<List<OutstandingAction>> call, @NonNull Response<List<OutstandingAction>> response) {
                if(CommonUtils.onTokenExpired(getContext(), response.code())){
                    return;
                }
                if (response.isSuccessful()) {
                    DBHandler.getInstance().resetActions();
                    List<OutstandingAction> actionsresponse=response.body();
                    if (actionsresponse!=null && actionsresponse.size()>0) {
                        for (OutstandingAction modal : actionsresponse)
                        {
                            modal.setActionType("Cleared"); // for filter data
                            DBHandler.getInstance().replaceData(OutstandingAction.DBTable.NAME, modal.toContentValues());
                        }
                        getActionsFromDb();
                    }
                    else {
                        error.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    error.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<List<OutstandingAction>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);
            }
        });
    }
    private void getActionsFromDb()
    {
        List<OutstandingAction> jobs = DBHandler.getInstance().getActions("Cleared");
        actionList.clear();
        if (jobs!=null && !jobs.isEmpty()) {
            actionList.addAll(jobs);
            actionAdapter.SetActionsList(actionList);
            actionAdapter.notifyDataSetChanged();
            error.setVisibility(View.GONE);
        } else
        {
            error.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }
    @Override
    public void StartCorrectiveMeasure(ActionsClose action) {
        AppPreferences.setTheme(3);
        String jsonFileName = "corrective_measure.json";// case sensitive for submission model too
        Submission submission = new Submission(jsonFileName, "Corrective Measure", action.getInspectionQuestionId());// if jobid not than 0
        // unique submission id for every form
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }

    @Override
    public void StartCannotRectify(ActionsClose action) {
        AppPreferences.setTheme(3);
        String jsonFileName = "cannot_rectify.json";// case sensitive for submission model too
        Submission submission = new Submission(jsonFileName, "Cannot Rectify", action.getInspectionQuestionId());// if jobid not than 0
        // unique submission id for every form
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }
}