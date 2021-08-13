package co.uk.depotnet.onsa.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

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

public class SurveyActivity extends AppCompatActivity
        implements View.OnClickListener {

    public static final String ARG_JOB_ID = "Job_ID";
    public static final String ARG_JOB_REFERENCE_NUMBER = "Job_Reference_Number";

    private String jobID;
    private boolean isSubJob;
    private User user;
    private DBHandler dbHandler;
    private Job job;
    private LinearLayout llBlockerUI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        dbHandler = DBHandler.getInstance(this);
        user = dbHandler.getUser();
        Intent intent = getIntent();
        jobID = intent.getStringExtra(ARG_JOB_ID);

        job = DBHandler.getInstance().getJob(jobID);
        isSubJob = job!= null && job.isSubJob();

        findViewById(R.id.btn_img_cancel).setOnClickListener(this);
       RelativeLayout llbtnPresiteSurvey = findViewById(R.id.btn_pre_site_survey);
        RelativeLayout llbtnPollingSurvey = findViewById(R.id.btn_poling_survey);
        RelativeLayout llbtnGoodtogoSurvey = findViewById(R.id.btn_goods2go_survey);
        llBlockerUI = findViewById(R.id.ll_ui_blocker);
        llbtnPresiteSurvey.setOnClickListener(this);
        llbtnPollingSurvey.setOnClickListener(this);
        llbtnGoodtogoSurvey.setOnClickListener(this);

        if(job.getSurveyTypeId() == 2){
            llbtnPresiteSurvey.setVisibility(View.VISIBLE);
        }else{
            llbtnPresiteSurvey.setVisibility(View.GONE);
        }
        if(job.getSurveyTypeId() == 1){
            llbtnPollingSurvey.setVisibility(View.VISIBLE);
        }else{
            llbtnPollingSurvey.setVisibility(View.GONE);
        }
        if(job.getSurveyTypeId() == 3){
            llbtnGoodtogoSurvey.setVisibility(View.VISIBLE);
        }else{
            llbtnGoodtogoSurvey.setVisibility(View.GONE);
        }

    }


    public void showProgressBar() {
        llBlockerUI.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    public void hideProgressBar() {
        llBlockerUI.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }




    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_img_cancel:
                finish();
                break;
            case R.id.btn_pre_site_survey:
                openPreSiteSurvey();
                break;
            case R.id.btn_poling_survey:
                getDFEItems();

                break;
            case R.id.btn_goods2go_survey:
                openGoodsToGoSurvey("good_2_go.json", "Goods2Go Survey");
                break;
        }
    }

    private void openPolingSurveyList() {
        Intent intent = new Intent(this, PoleSurveyListActivity.class);
        intent.putExtra(PoleSurveyListActivity.ARG_JOB_ID, jobID);
        startActivity(intent);
    }

    private void openGoodsToGoSurvey(String jsonName, String title) {
        Submission submission = new Submission(jsonName, title, jobID);
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        startFormActivity(submission);
    }

    public void openPreSiteSurvey(String jsonName, String title) {
        Submission submission = new Submission(jsonName, title, jobID);
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);


        startFormActivity(submission);
    }

    private void startFormActivity(Submission submission) {
        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }

    public void getDFEItems(){

        if(!CommonUtils.isNetworkAvailable(this)){
            openPolingSurveyList();
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
                openPolingSurveyList();
            }

            @Override
            public void onFailure(Call<AppDFEItems> call, Throwable t) {
                hideProgressBar();
                openPolingSurveyList();
            }
        });
    }

    private void openPreSiteSurvey(){
        if(isSubJob){
            openPreSiteSurvey("sub_job_pre_site_survey.json", "Pre-Site Survey");
        }else{
            openPreSiteSurvey("pre_site_survey.json", "Pre-Site Survey");
        }
    }

}
