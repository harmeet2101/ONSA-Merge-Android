package co.uk.depotnet.onsa.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.AppDFEItems;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.WorkItem;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.responses.DatasetResponse;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VariationActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ARG_JOB_ID = "Job_ID";
    private String jobID;
    private Job job;
    private LinearLayout llUIBlocker;
    private DBHandler dbHandler;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_variation);
        this.dbHandler = DBHandler.getInstance(this);
        user = dbHandler.getUser();
        Intent intent = getIntent();
        jobID = intent.getStringExtra(ARG_JOB_ID);
        job = DBHandler.getInstance(this).getJob(jobID);

        findViewById(R.id.btn_img_cancel).setOnClickListener(this);

        findViewById(R.id.btn_log_dfe).setOnClickListener(this);
        Button btnLogTdfs = findViewById(R.id.btn_log_tdfs);
        Button btnLogDCR = findViewById(R.id.btn_log_dcr);
        llUIBlocker = findViewById(R.id.ll_ui_blocker);

        if (job != null && (job.isSubJob())) {
            btnLogTdfs.setVisibility(View.GONE);
        } else {
            btnLogTdfs.setOnClickListener(this);
        }

        if (job != null && !job.isSubJob() && !job.hasDCR()) {
            btnLogDCR.setVisibility(View.GONE);
        } else {
            btnLogDCR.setVisibility(View.VISIBLE);
            btnLogDCR.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        String prefix = job.isSubJob() ? "sub_job_" : "";
        switch (v.getId()) {
            case R.id.btn_img_cancel:
                finish();
                break;
            case R.id.btn_log_dcr:
                openVariation(prefix + "log_dcr.json", "Log DCR");
                break;
            case R.id.btn_log_dfe:
                getDFEItems(prefix + "log_dfe.json", "Log DFE");
                break;
            case R.id.btn_log_tdfs:
                openVariation(prefix + "log_tdfs.json", "Log TDFS");
                break;
        }
    }

    private void openVariation(String jsonName, String title) {
        Submission submission = new Submission(jsonName, title, jobID);
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }

    public void getDFEItems(String jsonName, String title){

        if(!CommonUtils.isNetworkAvailable(this)){
            openVariation(jsonName , title);
            return;
        }

        showProgressBar();
        APICalls.getDfeItems(user.gettoken() , job.getcontract() , job.getRateIssueNumber()).enqueue(new Callback<AppDFEItems>() {
            @Override
            public void onResponse(Call<AppDFEItems> call, Response<AppDFEItems> response) {
                if(response.isSuccessful()){
                    AppDFEItems appDFEItems = response.body();
                    if(appDFEItems != null) {
                        ArrayList<WorkItem> workItems = appDFEItems.getAppDFEItems();
                        if (workItems != null && !workItems.isEmpty()) {
                            dbHandler.replaceDataDFEWorkItems(workItems, DatasetResponse.DBTable.dfeWorkItems);
                        }
                    }
                }
                hideProgressBar();
                openVariation(jsonName , title);
            }

            @Override
            public void onFailure(Call<AppDFEItems> call, Throwable t) {
                hideProgressBar();
                openVariation(jsonName , title);
            }
        });
    }

    public void showProgressBar() {
        llUIBlocker.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    public void hideProgressBar() {
        llUIBlocker.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


}
