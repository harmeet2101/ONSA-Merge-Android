package co.uk.depotnet.onsa.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
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


        findViewById(R.id.btn_img_cancel).setOnClickListener(this);
        findViewById(R.id.btn_pre_site_survey).setOnClickListener(this);
        findViewById(R.id.btn_poling_survey).setOnClickListener(this);

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
        }
    }

    private void openPolingSurveyList() {
        Intent intent = new Intent(this, PoleSurveyListActivity.class);
        intent.putExtra(PoleSurveyListActivity.ARG_JOB_ID, jobID);
        startActivity(intent);
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
