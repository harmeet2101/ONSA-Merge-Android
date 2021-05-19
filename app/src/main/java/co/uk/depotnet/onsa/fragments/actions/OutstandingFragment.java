package co.uk.depotnet.onsa.fragments.actions;

import android.app.Activity;
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

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.adapters.actions.ActionsMainAdaptor;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.ActionsListener;
import co.uk.depotnet.onsa.modals.actions.Action;
import co.uk.depotnet.onsa.modals.actions.ActionResponse;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CallUtils;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.AppPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OutstandingFragment extends Fragment implements ActionsListener {
    private ProgressBar progressBar;
    private ActionsMainAdaptor actionAdapter;
    private final List<String> actionList = new ArrayList<>();
    private TextView error;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_outstanding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar=view.findViewById(R.id.progress_bar_actions);
        RecyclerView mRecyclerView = view.findViewById(R.id.outstanding_recyclerview);
        actionAdapter=new ActionsMainAdaptor(context,"Outstanding" , this);
        mRecyclerView.setAdapter(actionAdapter);
        error=view.findViewById(R.id.outstanding_error);
    }

    @Override
    public void onResume() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);
            if(CommonUtils.isNetworkAvailable(context)) {
                GetActionsCall();
            }
            else
            {
                getActionsFromDb();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onResume();
    }

    private void GetActionsCall()
    {
        CallUtils.enqueueWithRetry(APICalls.getActionsOutstandingList(DBHandler.getInstance().getUser().gettoken()),new Callback<List<ActionResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ActionResponse>> call, @NonNull Response<List<ActionResponse>> response) {
                if(CommonUtils.onTokenExpired(getContext(), response.code())){
                    return;
                }
                if (response.isSuccessful()) {
                    DBHandler.getInstance().clearTable(Action.DBTable.NAME);
                    List<ActionResponse> actionsresponse=response.body();
                    if (actionsresponse!=null && !actionsresponse.isEmpty()) {
                        for (ActionResponse modal : actionsresponse)
                        {
                            modal.setActionType("Outstanding");
                            modal.toContentValues();
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
            public void onFailure(@NonNull Call<List<ActionResponse>> call, @NonNull Throwable t) {
                error.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void getActionsFromDb()
    {
        List<String> dueDates = DBHandler.getInstance().getActionDueDates("Outstanding");

        actionList.clear();
        if (dueDates!=null && !dueDates.isEmpty())
        {
            actionList.addAll(dueDates);
            actionAdapter.SetActionsList(dueDates);
            actionAdapter.notifyDataSetChanged();
            error.setVisibility(View.GONE);
        }
        else
        {
            error.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);


    }

    Action currentAction;
    @Override
    public void startCorrectiveMeasure(Action action) {
        AppPreferences.setTheme(3);
        currentAction = action;
        String jsonFileName = action.isIncidentAction() ?"incident_corrective_measure.json" :"corrective_measure.json";// case sensitive for submission model too
        Submission submission = new Submission(jsonFileName, "Corrective Measure", action.getActionId());// if jobid not than 0
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivityForResult(intent , 10001);
    }

    @Override
    public void startCannotRectify(Action action) {
        AppPreferences.setTheme(3);
        currentAction = action;
        String jsonFileName = action.isIncidentAction() ?"incident_cannot_rectify.json" :"cannot_rectify.json";// case sensitive for submission model too
        Submission submission = new Submission(jsonFileName, "Cannot Rectify", action.getActionId());// if jobid not than 0
        // unique submission id for every form
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivityForResult(intent , 10001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == 10001){
            if(currentAction != null){
//                currentAction.setActionType("Cleared");
//                DBHandler.getInstance().replaceData(Action.DBTable.NAME , currentAction.toContentValues());
//                actionAdapter.notifyDataSetChanged();
            }
        }
    }
}