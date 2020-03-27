package co.uk.depotnet.onsa.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Submission;

public class VariationActivity extends AppCompatActivity implements View.OnClickListener {

public static final String ARG_USER = "User";
public static final String ARG_JOB_ID = "Job_ID";
public static final String ARG_JOB_REFERENCE_NUMBER = "Job_Reference_Number";



private String jobID;
private String jobReferenceNumber;
private User user;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_variation);

    Intent intent = getIntent();
    user = intent.getParcelableExtra(ARG_USER);
    jobID = intent.getStringExtra(ARG_JOB_ID);
    jobReferenceNumber = intent.getStringExtra(ARG_JOB_REFERENCE_NUMBER);


    findViewById(R.id.btn_img_cancel).setOnClickListener(this);
    findViewById(R.id.btn_log_dcr).setOnClickListener(this);
    findViewById(R.id.btn_log_dfe).setOnClickListener(this);
    findViewById(R.id.btn_log_tdfs).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_img_cancel:
                finish();
                break;
            case R.id.btn_log_dcr:
                openVariation("log_dcr.json" , "Log DCR");
                break;
            case R.id.btn_log_dfe:
                openVariation("log_dfe.json" , "Log DFE");
                break;
            case R.id.btn_log_tdfs:
                openVariation("log_tdfs.json" , "Log TDFS");
                break;
        }
    }

    private void openVariation(String jsonName ,  String title){
        Submission submission = new Submission( jsonName , title , jobID);
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME , submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(this , FormActivity.class);
        intent.putExtra(FormActivity.ARG_USER , user);
        intent.putExtra(FormActivity.ARG_SUBMISSION , submission);
        startActivity(intent);
    }


}
