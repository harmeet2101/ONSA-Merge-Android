package co.uk.depotnet.onsa.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.AdapterWorkLog;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.OnItemClickListener;
import co.uk.depotnet.onsa.modals.JobModuleStatus;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.WorkLog;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.networking.Constants;
import co.uk.depotnet.onsa.utils.AppPreferences;
import co.uk.depotnet.onsa.utils.JsonReader;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;
import co.uk.depotnet.onsa.views.MaterialAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkLogActivity extends AppCompatActivity
        implements View.OnClickListener, OnItemClickListener<WorkLog> {

    public static final String ARG_USER = "User";
    public static final String ARG_JOB_ID = "Job_ID";
    public static final String ARG_JOB_REFERENCE_NUMBER = "Job_Reference_Number";

    private ArrayList<WorkLog> workLogs;
    private AdapterWorkLog adapter;
    private String jobID;
    private String jobReferenceNumber;
    private User user;

    private RelativeLayout rlWarning;
    private RecyclerView recyclerView;
    private boolean isClickable = false;
    private LinearLayout llUiBlocker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_log);

        Intent intent = getIntent();
        user = intent.getParcelableExtra(ARG_USER);
        jobID = intent.getStringExtra(ARG_JOB_ID);
        jobReferenceNumber = intent.getStringExtra(ARG_JOB_REFERENCE_NUMBER);

        llUiBlocker = findViewById(R.id.ll_ui_blocker);
        findViewById(R.id.btn_img_cancel).setOnClickListener(this);
        findViewById(R.id.btn_risk_assessment).setOnClickListener(this);
        recyclerView = findViewById(R.id.recycler_view);
        rlWarning = findViewById(R.id.rl_warning);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(20);
        recyclerView.addItemDecoration(itemDecoration);
        String fileName = "work_log.json";
        workLogs = JsonReader.loadFormJSON(this, WorkLog.class, fileName);

        adapter = new AdapterWorkLog(this, workLogs, this, jobID);
        recyclerView.setAdapter(adapter);

    }

    private boolean isBookOn() {
        String book_on_date = AppPreferences.getString(jobID + "_" + Constants.IS_BOOK_ON, null);
        if (TextUtils.isEmpty(book_on_date)) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        try {
            Date date = sdf.parse(book_on_date);
            Date currentDate = sdf.parse(sdf.format(new Date()));
            return date.compareTo(currentDate) == 0;
        } catch (ParseException e) {
            return false;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        for (WorkLog w : workLogs) {
            w.setStatus(DBHandler.getInstance().getJobModuleStatus(jobID,
                    w.getTitle()));
        }


        boolean status =
                DBHandler.getInstance().getJobModuleStatus(jobID , "Risk Assessment");

        if(status){
            recyclerView.setVisibility(View.VISIBLE);
            rlWarning.setVisibility(View.GONE);
            boolean isBookOn = isBookOn();
            if (isBookOn) {
                workLogs.get(0).setStatus(false);
                workLogs.get(0).setJson("book_off.json");
                workLogs.get(0).setTitle("Book Off");
            } else {
                workLogs.get(0).setStatus(false);
                workLogs.get(0).setJson("book_on.json");
                workLogs.get(0).setTitle("Book On");
            }


            adapter.setBookOn(isBookOn);
            adapter.notifyDataSetChanged();
        }else{
            recyclerView.setVisibility(View.GONE);
            rlWarning.setVisibility(View.VISIBLE);
        }
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_img_cancel:
                finish();
                break;
            case R.id.btn_risk_assessment:
                openRiskAssessment(jobID);
                break;
        }
    }

    public void openRiskAssessment(String jobID) {
        String jsonFileName = "risk_assessment.json";
        Submission submission = new Submission(jsonFileName, "Risk Assessment", jobID);
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtra(FormActivity.ARG_USER, user);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }


    public void showProgressBar() {
        llUiBlocker.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }


    public void hideProgressBar() {
        llUiBlocker.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onItemClick(WorkLog workLog, int position) {

//        if(workLog.isStatus()){
//            showErrorDialog(workLog.getTitle(), "Already Submitted Successfully");
//            return;
//        }

        String jsonName = workLog.getJson();

        if (jsonName == null || jsonName.isEmpty()) {
            showErrorDialog(workLog.getTitle(), "Work in Progress");
            return;
        }

        if (jsonName.equalsIgnoreCase("rfna.json")) {



                String title = "RFNA";
                String message = "This Process is irreversible. Do you really want to submit  ";
                showRFNAConfirmation(title, message);
                return;


        }

        if (!isBookOn() && !jsonName.equalsIgnoreCase("book_on.json")) {
            return;
        }

        if (jsonName.equalsIgnoreCase("raise_variation.json")) {
            Intent intent = new Intent(WorkLogActivity.this, VariationActivity.class);
            intent.putExtra(SurveyActivity.ARG_USER, user);
            intent.putExtra(SurveyActivity.ARG_JOB_ID, jobID);
            intent.putExtra(SurveyActivity.ARG_JOB_REFERENCE_NUMBER, jobReferenceNumber);
            startActivity(intent);
        } else {
            Submission submission = new Submission(jsonName, workLog.getTitle(), jobID);
            long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
            submission.setId(submissionID);
            Intent intent = new Intent(this, FormActivity.class);
            intent.putExtra(FormActivity.ARG_USER, user);
            intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
            startActivity(intent);
        }
    }

    public void showErrorDialog(String title, String message) {

        final MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .build();

        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "_ERROR_DIALOG");
    }

    public void showRFNAConfirmation(String title, String message) {

        final MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.submit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendRFNA();
                        dialogInterface.dismiss();
                    }
                })
                .setNegative(getString(R.string.generic_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .build();

        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "_ERROR_DIALOG");
    }

    private void sendRFNA(){
        if (!CommonUtils.isNetworkAvailable(this)) {
            String title = "Submission Error";
            String message = "Internet connection is not available. Please check your internet connection.";
            showErrorDialog(title, message);
            return;
        }

        showProgressBar();
        String jsonFileName = "rfna.json";
        Submission submission = new Submission(jsonFileName, "Ready For Next Activity", jobID);
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);

        APICalls.sendRfna(jobID , user.gettoken()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    JobModuleStatus jobModuleStatus = new JobModuleStatus();
                    jobModuleStatus.setStatus(true);
                    jobModuleStatus.setJobId(jobID);
                    jobModuleStatus.setModuleName("RFNA");
                    jobModuleStatus.setSubmissionId(submission.getID());
                    DBHandler.getInstance().replaceData(JobModuleStatus.DBTable.NAME,
                            jobModuleStatus.toContentValues());
                    adapter.notifyDataSetChanged();
                    showErrorDialog("Success", "Submission was successful");
                }else{
                    showErrorDialog("Submission Error", "Submission Error, your submission has not been succeed");
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showErrorDialog("Submission Error", "Submission Error, your submission has not been succeed");
                hideProgressBar();
            }
        });
    }
}
