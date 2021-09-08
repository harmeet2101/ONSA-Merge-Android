package co.uk.depotnet.onsa.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.AdapterWorkLog;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.OnItemClickListener;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.WorkLog;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.responses.MeasureItemResponse;
import co.uk.depotnet.onsa.modals.responses.MenSplitResponse;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CallUtils;
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

    public static final String ARG_JOB_ID = "Job_ID";
    public static final String ARG_JOB_REFERENCE_NUMBER = "Job_Reference_Number";

    private ArrayList<WorkLog> workLogs;
    private AdapterWorkLog adapter;
    private String jobID;
    private String jobReferenceNumber;
    private User user;

    private RelativeLayout rlWarning;
    private RecyclerView recyclerView;
    private LinearLayout llUiBlocker;
    private Job job;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private String today;
    private DBHandler dbHandler;
    private String riskAssessmentTitle;
    private boolean isSubJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_log);
        this.dbHandler = DBHandler.getInstance(this);
        this.today = sdf.format(new Date());
        user = dbHandler.getUser();

        Intent intent = getIntent();
        jobID = intent.getStringExtra(ARG_JOB_ID);
        jobReferenceNumber = intent.getStringExtra(ARG_JOB_REFERENCE_NUMBER);
        job = dbHandler.getJob(jobID);
        this.isSubJob = job!=null && job.isSubJob();

        TextView txtToolbarTitle = findViewById(R.id.txt_toolbar_title);
        if(isSubJob){
            txtToolbarTitle.setText(String.format("Work Log: %s-S%s", job.getestimateNumber(), job.getSubJobNumber()));
        }else{
            txtToolbarTitle.setText(String.format("Work Log: %s", job.getestimateNumber()));
        }


        this.riskAssessmentTitle = getRiskAssessmentTitle();

        llUiBlocker = findViewById(R.id.ll_ui_blocker);

        findViewById(R.id.btn_img_cancel).setOnClickListener(this);
        findViewById(R.id.btn_risk_assessment).setOnClickListener(this);
        Button btnRiskAssessment = findViewById(R.id.btn_risk_assessment);
        TextView tv_riskAssessment_heading = findViewById(R.id.tv_risk_assessment_heading);
        recyclerView = findViewById(R.id.recycler_view);
        rlWarning = findViewById(R.id.rl_warning);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(20);
        recyclerView.addItemDecoration(itemDecoration);

        String prefix = isSubJob ? "sub_job_":"";
        String fileName = prefix+"work_log.json";
        workLogs = JsonReader.loadFormJSON(this, WorkLog.class, fileName);

        if (!user.isReinstatement()) {
            for (int i = 0; i < workLogs.size(); i++) {
                if (workLogs.get(i).getJson().equalsIgnoreCase(prefix+"log_reinstatement.json")) {
                    workLogs.remove(i);
                    break;
                }
            }
        }

        if (!user.isBackfill()) {
            for (int i = 0; i < workLogs.size(); i++) {
                if (workLogs.get(i).getJson().equalsIgnoreCase(prefix+"log_back_fill.json")) {
                    workLogs.remove(i);
                    break;
                }
            }
        }

        if (!user.isSiteClear()) {
            for (int i = 0; i < workLogs.size(); i++) {
                if (workLogs.get(i).getJson().equalsIgnoreCase(prefix+"log_site_clear.json")) {
                    workLogs.remove(i);
                    break;
                }
            }
        }


            for (int i = 0; i < workLogs.size(); i++) {
                if (workLogs.get(i).getJson().equalsIgnoreCase("job_site_clear.json")) {
                    if(job.getSiteTasksCount() == 0){
                        workLogs.get(i).setJson("job_site_clear_unscheduled.json");
                    }
                }
            }


        if (!user.isMuckaway()) {
            for (int i = 0; i < workLogs.size(); i++) {
                if (workLogs.get(i).getJson().equalsIgnoreCase("log_muckaway.json")) {
                    workLogs.remove(i);
                    break;
                }
            }
        }

        if (!user.isServiceMaterialDrop()) {
            for (int i = 0; i < workLogs.size(); i++) {
                if (workLogs.get(i).getJson().equalsIgnoreCase(prefix+"service_material.json")) {
                    workLogs.remove(i);
                    break;
                }
            }
        }

        if (isSubJob) {
            for (int i = 0; i < workLogs.size(); i++) {
                if (workLogs.get(i).getJson().equalsIgnoreCase("rfna.json")) {
                    workLogs.remove(i);
                    break;
                }
            }
            for (int i = 0; i < workLogs.size(); i++) {
                if (workLogs.get(i).getJson().equalsIgnoreCase("record_return.json")) {
                    workLogs.remove(i);
                    break;
                }
            }
        }

        if (!TextUtils.isEmpty(user.getroleName()) && !user.getroleName().equalsIgnoreCase("Supervisor")) {
            for (int i = 0; i < workLogs.size(); i++) {
                if (workLogs.get(i).getJson().equalsIgnoreCase("request_task.json")) {
                    workLogs.remove(i);
                    break;
                }
            }
        }

        adapter = new AdapterWorkLog(this, workLogs, this, jobID);
        recyclerView.setAdapter(adapter);

        if (job.getRiskAssessmentTypeId() == 2) {
            btnRiskAssessment.setText(R.string.go_to_hoist_risk_assessment);
            tv_riskAssessment_heading.setText(R.string.wraning_hoist_risk_assessment);
        } else if (job.getRiskAssessmentTypeId() == 3) {
            btnRiskAssessment.setText(R.string.poling_ra);
            tv_riskAssessment_heading.setText(R.string.wraning_polling_risk_assessment);
        }else{
            btnRiskAssessment.setText(R.string.go_to_risk_assessment);
            tv_riskAssessment_heading.setText(R.string.wraning_risk_assessment);
        }

    }

    private boolean isBookOn() {
        String book_on_date = AppPreferences.getString(jobID + "_" + Constants.IS_BOOK_ON, null);
        if (TextUtils.isEmpty(book_on_date)) {
            return false;
        }

        Date date;
        Date currentDate;
        try {
            date = sdf.parse(book_on_date);
            currentDate = sdf.parse(today);
        } catch (ParseException e) {
            return false;
        }

        if (date == null || currentDate == null) {
            return false;
        }
        return date.compareTo(currentDate) == 0;
    }

    @Override
    protected void onResume() {
        super.onResume();

        for (WorkLog w : workLogs) {
            w.setStatus(dbHandler.getJobModuleStatus(jobID,
                    w.getTitle() , today));
        }

        boolean status =
                dbHandler.getJobModuleStatus(jobID, riskAssessmentTitle , today);
//        status = true;
        if (status) {
            recyclerView.setVisibility(View.VISIBLE);
            rlWarning.setVisibility(View.GONE);
            boolean isBookOn = isBookOn();
            workLogs.get(0).setStatus(false);
            if (isBookOn) {
                if(isSubJob){
                    workLogs.get(0).setJson("sub_job_book_off.json");
                }else {
                    workLogs.get(0).setJson("book_off.json");
                }
                workLogs.get(0).setTitle("Book Off");
            } else {
                if(isSubJob) {
                    workLogs.get(0).setJson("sub_job_book_on.json");
                }else{
                    workLogs.get(0).setJson("book_on.json");
                }
                workLogs.get(0).setTitle("Book On");
            }
            adapter.setBookOn(isBookOn);
            adapter.notifyDataSetChanged();
        } else {
            recyclerView.setVisibility(View.GONE);
            rlWarning.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_img_cancel){
            finish();
        }else if(view.getId() == R.id.btn_risk_assessment){
            openRiskAssessment();
        }
    }

    private String getRiskAssessmentTitle(){
        String title = "Risk Assessment";
        if (job.getRiskAssessmentTypeId() == 2) {
            title = "Hoist Only Risk Assessment";
        } else if (job.getRiskAssessmentTypeId() == 3) {
            title = "Poling Risk Assessment";
        }
        return title;
    }

    private String getRiskAssessmentJson(){
        String jsonFileName;
        if (job.getRiskAssessmentTypeId() == 2) {
            jsonFileName = "hoist_risk_assessment.json";
        } else if (job.getRiskAssessmentTypeId() == 3) {
            jsonFileName = "poling_risk_assessment.json";
        } else {
            jsonFileName = isSubJob ? "sub_job_risk_assessment.json" : "risk_assessment.json";
        }
        return jsonFileName;
    }

    public void openRiskAssessment() {

        String jsonFileName = getRiskAssessmentJson();
        String title = getRiskAssessmentTitle();

        Submission submission = new Submission(jsonFileName, title, job.getjobId());
        long submissionID = dbHandler.insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);

        Intent intent = new Intent(this, FormActivity.class);
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
        String jsonName = workLog.getJson();

        if (jsonName == null || jsonName.isEmpty()) {
            showErrorDialog(workLog.getTitle(), "Work in Progress");
            return;
        }

        if (!isBookOn() && (!jsonName.equalsIgnoreCase("book_on.json") && !jsonName.equalsIgnoreCase("sub_job_book_on.json"))) {
            return;
        }

        if (jsonName.equalsIgnoreCase("log_measure.json") || jsonName.equalsIgnoreCase("sub_job_log_measure.json")) {
            openLogMeasure(workLog);
            return;
        }

        if (jsonName.equalsIgnoreCase("raise_variation.json")) {
            Intent intent = new Intent(WorkLogActivity.this, VariationActivity.class);
            intent.putExtra(SurveyActivity.ARG_JOB_ID, jobID);
            intent.putExtra(SurveyActivity.ARG_JOB_REFERENCE_NUMBER, jobReferenceNumber);
            startActivity(intent);
        } else {
            Submission submission = new Submission(jsonName, workLog.getTitle(), jobID);
            long submissionID = dbHandler.insertData(Submission.DBTable.NAME, submission.toContentValues());
            submission.setId(submissionID);
            Intent intent = new Intent(this, FormActivity.class);
            intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
            startActivity(intent);
        }
    }

    private void openLogMeasure(WorkLog workLog) {
        if (!CommonUtils.isNetworkAvailable(WorkLogActivity.this)) {
            Submission submission = new Submission(workLog.getJson(), workLog.getTitle(), jobID);
            long submissionID = dbHandler.insertData(Submission.DBTable.NAME, submission.toContentValues());
            submission.setId(submissionID);
            Intent intent = new Intent(this, FormActivity.class);
            intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
            startActivity(intent);
            return;
        }
        if (!CommonUtils.validateToken(WorkLogActivity.this)) {
            return;
        }

        showProgressBar();


        CallUtils.enqueueWithRetry(APICalls.getMenSplits(user.gettoken()),new Callback<MenSplitResponse>() {
            @Override
            public void onResponse(@NonNull Call<MenSplitResponse> call, @NonNull Response<MenSplitResponse> response) {
                if (response.isSuccessful()) {
                    MenSplitResponse menSplits = response.body();
                    if (menSplits != null) {
                        menSplits.toContentValues();
                    }

                }

                CallUtils.enqueueWithRetry(APICalls.getMeasureItems(user.gettoken()),new Callback<MeasureItemResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MeasureItemResponse> call, @NonNull Response<MeasureItemResponse> response) {
                        hideProgressBar();

                        if (response.isSuccessful()) {
                            MeasureItemResponse measureItemResponse = response.body();
                            if (measureItemResponse != null) {
                                measureItemResponse.toContentValues();
                                Submission submission = new Submission(workLog.getJson(), workLog.getTitle(), jobID);
                                long submissionID = dbHandler.insertData(Submission.DBTable.NAME, submission.toContentValues());
                                submission.setId(submissionID);
                                Intent intent = new Intent(WorkLogActivity.this, FormActivity.class);
                                intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
                                startActivity(intent);
                            }
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<MeasureItemResponse> call, @NonNull Throwable t) {
                        hideProgressBar();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<MenSplitResponse> call, @NonNull Throwable t) {
                hideProgressBar();
            }
        });

    }

    public void showErrorDialog(String title, String message) {
        if (getSupportFragmentManager().isStateSaved()) {
            return;
        }
        final MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), (dialogInterface, i) -> dialogInterface.dismiss())
                .build();

        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "_ERROR_DIALOG");
    }
}
