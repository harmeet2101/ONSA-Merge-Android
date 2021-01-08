package co.uk.depotnet.onsa.activities.timesheet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.activities.ThemeBaseActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.forms.Submission;

public class TimeSheetActivity extends ThemeBaseActivity implements View.OnClickListener {
    private static final int LOG_HOURS = 1;
    private static final int SUBMIT_TIME_SHEET = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timesheet);
        findViewById(R.id.ll_log_hours).setOnClickListener(this);
        findViewById(R.id.ll_submit_timesheet).setOnClickListener(this);
        findViewById(R.id.btn_img_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_log_hours:
                openForm(LOG_HOURS);
                break;
            case R.id.ll_submit_timesheet:
                openForm(SUBMIT_TIME_SHEET);
                break;
            case R.id.btn_img_cancel:
                finish();
                break;
        }
    }

    public void openForm(int formType) {
        String jsonFileName = formType == LOG_HOURS ? "timesheet_log_hours.json" : "timesheet_submit_timesheet.json";
        String title = formType == LOG_HOURS ? "Log Hours" : "Submit Timesheet";
        Submission submission = new Submission(jsonFileName, title, "");
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);

        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }

}