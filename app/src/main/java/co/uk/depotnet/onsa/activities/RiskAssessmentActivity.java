package co.uk.depotnet.onsa.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Submission;

public class RiskAssessmentActivity extends AppCompatActivity implements View.OnClickListener{


    public static final String ARG_JOB = "job";

    private Job job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        job = intent.getParcelableExtra(ARG_JOB);
        setContentView(R.layout.activity_risk_assessment);
        TextView tvRiskAssessmentType =findViewById(R.id.tv_risk_assessment_select);
        findViewById(R.id.btn_img_cancel).setOnClickListener(this);
        findViewById(R.id.btn_risk_assessment).setOnClickListener(this);
        findViewById(R.id.btn_visitor_log).setOnClickListener(this);
        if (job.getRiskAssessmentTypeId() == 2) {
            tvRiskAssessmentType.setText(getString(R.string.hoist_only_risk_assessment));
        } else if (job.getRiskAssessmentTypeId() == 3) {
            tvRiskAssessmentType.setText(getString(R.string.poling_ra));
        }else{
            tvRiskAssessmentType.setText(getString(R.string.risk_assessment));
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
            case R.id.btn_risk_assessment:
                openRiskAssessment();
                break;
            case R.id.btn_visitor_log:
                openVisitorLog();
                break;
        }
    }

    private void openVisitorLog() {
        String jsonFileName = "visitor_attendance.json";
        Submission submission = new Submission(jsonFileName, "", job.getjobId());
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }


    public void openRiskAssessment() {

        String jsonFileName = "";
        String title = "";

        if (job.getRiskAssessmentTypeId() == 2) {
            jsonFileName = "hoist_risk_assessment.json";
            title = "Hoist Only Risk Assessment";
        } else if (job.getRiskAssessmentTypeId() == 3) {
            jsonFileName = "poling_risk_assessment.json";
            title = "Poling Risk Assessment";
        }else{
            jsonFileName = "risk_assessment.json";
            title = "Risk Assessment";
        }

        Submission submission = new Submission(jsonFileName, title, job.getjobId());
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);

        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }

}
