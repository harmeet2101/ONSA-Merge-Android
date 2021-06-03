package co.uk.depotnet.onsa.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.forms.Submission;

public class VariationActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ARG_JOB_ID = "Job_ID";
    private String jobID;
    private Job job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_variation);

        Intent intent = getIntent();
        jobID = intent.getStringExtra(ARG_JOB_ID);
        job = DBHandler.getInstance(this).getJob(jobID);

        findViewById(R.id.btn_img_cancel).setOnClickListener(this);

        findViewById(R.id.btn_log_dfe).setOnClickListener(this);
        Button btnLogTdfs = findViewById(R.id.btn_log_tdfs);
        Button btnLogDCR = findViewById(R.id.btn_log_dcr);

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
                openVariation(prefix + "log_dfe.json", "Log DFE");
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


}
