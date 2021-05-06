package co.uk.depotnet.onsa.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.networking.ConnectionHelper;
import co.uk.depotnet.onsa.views.MaterialAlertDialog;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PollingSurveyActivity extends AppCompatActivity
        implements View.OnClickListener {

    public static final String ARG_SUBMISSION = "Submission";

    private Submission submission;
    private LinearLayout llUiBlocker;
    private String[] files = new String[]{"poling_job_data.json", "poling_fluidity_task.json"
            , "poling_solution.json", "poling_asset_data.json", "poling_planning_risk_assessment.json"};

    private ImageView[] statusIcons = new ImageView[5];
    private Handler handler;
    private Job job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling_survey);
        llUiBlocker = findViewById(R.id.ll_ui_blocker);
        Intent intent = getIntent();
        submission = intent.getParcelableExtra(ARG_SUBMISSION);
        findViewById(R.id.btn_img_cancel).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.card_job_data).setOnClickListener(this);
        findViewById(R.id.card_fluidity_task).setOnClickListener(this);
        findViewById(R.id.card_solution).setOnClickListener(this);
        findViewById(R.id.card_asset_data).setOnClickListener(this);
        findViewById(R.id.card_poling_ra).setOnClickListener(this);
        job = DBHandler.getInstance().getJob(submission.getJobID());
        statusIcons[0] = findViewById(R.id.img_job_data);
        statusIcons[1] = findViewById(R.id.img_fluidity_task);
        statusIcons[2] = findViewById(R.id.img_solution);
        statusIcons[3] = findViewById(R.id.img_asset_data);
        statusIcons[4] = findViewById(R.id.img_pol_ra);

        if(job != null && job.isSubJob()){
            files[2] = "sub_job_poling_solution.json";
        }

        handler = new Handler();
    }

    private void initPoleItems() {
        for (int i = 0; i < statusIcons.length; i++) {
            statusIcons[i].setVisibility(DBHandler.getInstance().getJobModuleStatus(submission.getJobID(), files[i], submission.getID())
                    ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private boolean isValidate() {
        for (int i = 0; i < statusIcons.length; i++) {
            if (!DBHandler.getInstance().getJobModuleStatus(submission.getJobID(),
                    files[i], submission.getID())) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPoleItems();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_img_cancel:
            case R.id.btn_save:
                finish();
                return;
            case R.id.btn_submit:
                if (isValidate()) {
                    sendToServer();
                } else {
                    Toast.makeText(this, "Please complete survey first.", Toast.LENGTH_SHORT).show();
                }
                return;
            case R.id.card_job_data:
                submission.setJsonFile(files[0]);
                break;
            case R.id.card_fluidity_task:
                submission.setJsonFile(files[1]);
                break;
            case R.id.card_solution:

                submission.setJsonFile(files[2]);
                break;
            case R.id.card_asset_data:
                submission.setJsonFile(files[3]);
                break;
            case R.id.card_poling_ra:

                Answer answer = DBHandler.getInstance().getAnswer(submission.getID(), "submittedDate", "riskAssessmentSection", 0);

                if (answer == null) {
                    answer = new Answer(submission.getID(), "submittedDate");
                }

                answer.setAnswer(submission.getDate());
                answer.setDisplayAnswer(submission.getDate());
                answer.setRepeatID("riskAssessmentSection");
                answer.setRepeatCount(0);

                DBHandler.getInstance().replaceData(Answer.DBTable.NAME , answer.toContentValues());

                submission.setJsonFile(files[4]);
                break;
        }

        startForm();
    }

    private void sendToServer() {

        if (!CommonUtils.isNetworkAvailable(this)) {
            String title = "Submission Error";
            String message = "Internet connection is not available. Please check your internet connection. Your request is submitted in Queue.";
            DBHandler.getInstance().setSubmissionQueued(submission);
            showErrorDialog(title, message);
            return;
        }

        if(!CommonUtils.validateToken(PollingSurveyActivity.this)){
            return;
        }

        showProgressBar();
        new Thread(() -> {
            String url = "app/jobs/{jobId}/poling-surveys";
            String photoUrl = "app/jobs/{jobId}/poling-surveys";

            if(job != null && job.isSubJob()){
                url = "app/subjob/{jobId}/poling-survey";
                photoUrl = "app/subjob/{jobId}/photo";
            }
            final Response response = new ConnectionHelper(PollingSurveyActivity.this).
                    submitPollingSurvey(url, photoUrl,
                            submission, getSupportFragmentManager());


            if (response != null) {
                if (!response.isSuccessful()) {
                    if (response.code() != 400) {
                        DBHandler.getInstance().setSubmissionQueued(submission);
                    }
                } else {
                    DBHandler.getInstance().removeAnswers(submission);
                }
            }
            handler.post(() -> {
                hideProgressBar();
                String title = "Success";
                String message = "Submission was successful";

                if (response == null || !response.isSuccessful()) {
                    title = "Submission Error";
                    message = "Submission Error, your submission has been added to the queue";
                }

                if(response != null && response.code() != 200){

                        ResponseBody body = response.body();
                        if(body!= null){
                            try {
                                String data = body.string();
                                if (!TextUtils.isEmpty(data)) {
                                    JSONObject jsonObject = new JSONObject(data);
                                    if(jsonObject.has("status")) {
                                        title = jsonObject.getString("status");
                                    }

                                    if(jsonObject.has("message")) {
                                        message = jsonObject.getString("message");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                }
                showErrorDialog(title, message);
            });
        }).start();
    }


    public void showProgressBar() {
        llUiBlocker.setVisibility(View.VISIBLE);
        llUiBlocker.bringToFront();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }


    public void hideProgressBar() {
        llUiBlocker.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void showErrorDialog(String title, String message) {
        if(getSupportFragmentManager().isStateSaved()){
            setResult(Activity.RESULT_OK);
            finish();
            return;
        }
        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), (dialog1, i) -> {
                    dialog1.dismiss();
                    setResult(Activity.RESULT_OK);
                    finish();
                })
                .build();

        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "_ERROR_DIALOG");
    }


    public void startForm() {
        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        intent.putExtra(FormActivity.ARG_REPEAT_COUNT, 0);
        startActivityForResult(intent, 1234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1234) {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}
