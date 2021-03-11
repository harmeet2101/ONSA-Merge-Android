package co.uk.depotnet.onsa.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.Submission;

public class SurveyActivity extends AppCompatActivity
        implements View.OnClickListener {

    public static final String ARG_JOB_ID = "Job_ID";
    public static final String ARG_JOB_REFERENCE_NUMBER = "Job_Reference_Number";

    private String jobID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        Intent intent = getIntent();
        jobID = intent.getStringExtra(ARG_JOB_ID);

        Job job = DBHandler.getInstance().getJob(jobID);

        findViewById(R.id.btn_img_cancel).setOnClickListener(this);
       RelativeLayout llbtnPresiteSurvey = findViewById(R.id.btn_pre_site_survey);
        RelativeLayout llbtnPollingSurvey = findViewById(R.id.btn_poling_survey);
        RelativeLayout llbtnGoodtogoSurvey = findViewById(R.id.btn_goods2go_survey);

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
                openPreSiteSurvey("pre_site_survey.json", "Pre-Site Survey");
                break;
            case R.id.btn_poling_survey:
                openPolingSurveyList();
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

}
