package co.uk.depotnet.onsa.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import co.uk.depotnet.onsa.BuildConfig;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.activities.PollingSurveyActivity;
import co.uk.depotnet.onsa.adapters.OfflineQueueAdapter;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.fragments.store.ReceiptItemsFragment;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.listeners.FromActivityListener;
import co.uk.depotnet.onsa.modals.JobModuleStatus;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.Form;
import co.uk.depotnet.onsa.modals.forms.Screen;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.hseq.ToolTipModel;
import co.uk.depotnet.onsa.modals.store.MyStore;
import co.uk.depotnet.onsa.modals.store.Receipts;
import co.uk.depotnet.onsa.modals.timesheet.TimeSheetHour;
import co.uk.depotnet.onsa.modals.timesheet.TimeSheetHours;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.networking.ConnectionHelper;
import co.uk.depotnet.onsa.networking.Constants;
import co.uk.depotnet.onsa.utils.AppPreferences;
import co.uk.depotnet.onsa.utils.JsonReader;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;
import co.uk.depotnet.onsa.views.MaterialAlertDialog;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;


public class FragmentQueue extends Fragment implements OfflineQueueAdapter.QueueListener {

    private final static String SHOULD_AUTOMATIC_UPLOAD = "SHOULD_AUTOMATIC_UPLOAD";
    private ArrayList<Submission> submissions;
    private Context context;
    private OfflineQueueAdapter adapter;
    private FragmentActionListener listener;
    private Button btnUploadAll;
    private User user;
    private Handler handler;
    private boolean shouldAutomaticallyUpload;

    public static FragmentQueue newInstance(boolean shouldAutomaticallyUpload) {
        FragmentQueue fragment = new FragmentQueue();
        Bundle args = new Bundle();
        args.putBoolean(SHOULD_AUTOMATIC_UPLOAD, shouldAutomaticallyUpload);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof FragmentActionListener) {
            listener = (FragmentActionListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            shouldAutomaticallyUpload = args.getBoolean(SHOULD_AUTOMATIC_UPLOAD);
        }
        submissions = new ArrayList<>();
        user = DBHandler.getInstance().getUser();
        adapter = new OfflineQueueAdapter(context, submissions, this);
        this.handler = new Handler();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_queue, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(20);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
        btnUploadAll = view.findViewById(R.id.btn_upload_all);
        btnUploadAll.setOnClickListener(v -> uploadAll());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        submissions.clear();
        submissions.addAll(DBHandler.getInstance().getQueuedSubmissions());
        if (submissions.isEmpty()) {
            btnUploadAll.setVisibility(View.GONE);
        } else {
            btnUploadAll.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    public void showErrorDialog(String title, String message) {
        if(getChildFragmentManager().isStateSaved()){
            return;
        }
        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), (dialog1, i) -> dialog1.dismiss())
                .build();

        dialog.setCancelable(false);
        if(!getChildFragmentManager().isStateSaved()) {
            dialog.show(getChildFragmentManager(), "_ERROR_DIALOG");
        }
    }

    private void showQueue(final int position, final String title, String message, final Submission submission) {
        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.submit), (dialog1, i) -> {
                    String json = submission.getJsonFileName();

                    dialog1.dismiss();
                    if(!TextUtils.isEmpty(json) && json.equalsIgnoreCase("rfna.json")){
                        sendRFNA(position , submission);
                    }else if ((!TextUtils.isEmpty(json)) && ( json.equalsIgnoreCase("corrective_measure.json")
                            ||  json.equalsIgnoreCase("cannot_rectify.json")))
                    {
                        AppPreferences.setTheme(3);
                        Intent intent = new Intent(context, FormActivity.class);
                        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
                        startActivity(intent);
                    }
                    else if(!TextUtils.isEmpty(json) && json.equalsIgnoreCase("briefing_sign.json"))
                    {
                        AppPreferences.setTheme(2);
                        Intent intent = new Intent(context, FormActivity.class);
                        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
                        startActivity(intent);

                    } else if(!TextUtils.isEmpty(json) && json.equalsIgnoreCase("slg_inspection.json"))
                    {
                        getInsQue(submission,true);// for auto load all dynamic questions
                    }else if (!TextUtils.isEmpty(json) && (json.startsWith("store_log_"))) {
                        ArrayList<Answer> answers = DBHandler.getInstance().getRepeatedAnswers(submission.getID(), "StaStockItemId",
                                json.equalsIgnoreCase("store_log_issue.json") ? null : "Items");
                        if (answers.isEmpty()) {
                            return;
                        }

                        HashMap<String, Object> map = new HashMap<>();

                        for (Answer a : answers) {
                            if (a != null && !TextUtils.isEmpty(a.getAnswer())) {
                                Answer answer = DBHandler.getInstance().getAnswer(submission.getID(),
                                        "Quantity",
                                        a.getRepeatID(), a.getRepeatCount());
                                if (answer != null) {
                                    MyStore store = DBHandler.getInstance().getMyStores(a.getAnswer());
                                    if (store != null) {
                                        map.put(a.getAnswer(), store);
                                        map.put(a.getAnswer() + "_qty", Integer.parseInt(answer.getAnswer()));
                                    }
                                }
                            }
                        }

                        Intent intent = new Intent(context, FormActivity.class);
                        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
                        intent.putExtra(FormActivity.ARG_MY_STORE_ITEMS, map);
                        startActivityForResult(intent, 1000);

                    } else if (!TextUtils.isEmpty(title) && (title.equalsIgnoreCase("receipt_accept") ||
                            title.equalsIgnoreCase("receipt_reject"))) {
                        Answer answer = DBHandler.getInstance().getAnswer(submission.getID(), "MyReceiptID",
                                null, 0);

                        if (answer != null) {
                            Receipts receipts = DBHandler.getInstance().getReceipt(answer.getAnswer());
                            listener.addFragment(ReceiptItemsFragment.newInstance(receipts, submission.getID()),
                                    false);
                        }

                    } else if (TextUtils.isEmpty(title) || !title.equalsIgnoreCase("poling_survey")) {
                        Intent intent = new Intent(context, FormActivity.class);
                        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
                        startActivity(intent);
                    } else {
//                        sendToServer(position , submission);
                        Intent intent = new Intent(context, PollingSurveyActivity.class);
                        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
                        intent.putExtra(FormActivity.ARG_REPEAT_COUNT, 0);
                        startActivityForResult(intent, 1234);
                    }
                }).setNegative(getString(R.string.delete), (dialog12, i) -> {
                    dialog12.dismiss();
                    submissions.remove(position);
                    DBHandler.getInstance().removeAnswers(submission);
                    adapter.notifyDataSetChanged();
                }).build();

        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "_ERROR_DIALOG");
    }

    @Override
    public void onItemClick(final Submission submission, final int position) {
        if (!CommonUtils.isNetworkAvailable(context)) {
            String title = "Submission Error";
            String message = "Internet connection is not available. Please check your internet connection.";
            showErrorDialog(title, message);
            listener.hideProgressBar();
            return;
        }

        showQueue(position, submission.getTitle(),
                "Do you want to submit this information to server?", submission);

    }


    private void sendToServer(Submission submission) {
        String jobID = submission.getJobID();
        String jsonFileName = submission.getJsonFileName();

        if (TextUtils.isEmpty(jsonFileName)) {
            return;
        }
        Form form = JsonReader.loadForm(context, submission.getJsonFileName());
        if (form == null) {
            return;
        }

        String formTitle = form.getTitle();
        ArrayList<Screen> screens = form.getScreens();
        if (screens == null || screens.isEmpty()) {
            return;
        }

        Screen screen = screens.get(screens.size() - 1);

        if (!CommonUtils.isNetworkAvailable(context)) {
            DBHandler.getInstance().setSubmissionQueued(submission);
            return;
        }

        if(!CommonUtils.validateToken(context)){
            return;
        }

        Response response =null;
        if (submission.getJsonFileName().equalsIgnoreCase("corrective_measure.json")
                || submission.getJsonFileName().equalsIgnoreCase("cannot_rectify.json"))
        {
            //for actions calls
            response =  new ConnectionHelper(context).
                    submitActions(screen.getUrl(),
                            submission, getChildFragmentManager());
        }
       /* else if(submission.getJsonFileName().equalsIgnoreCase("schedule_inspection.json"))
        {
           getInsQue(submission,true);

        }*/
        else if(submission.getJsonFileName().equalsIgnoreCase("slg_inspection.json"))
        {
            response =  new ConnectionHelper(context).
                    submitInspections(screen.getUrl(), screen.getPhotoUrl(),
                            submission, getChildFragmentManager());
        }
        else if (submission.getJsonFileName().equalsIgnoreCase("timesheet_submit_timesheet.json")) {
            screen.setUrl("app/timesheets/submit");
        }
        else
        {
            response = new ConnectionHelper(context).
                    submitForm(screen.getUrl(), screen.getPhotoUrl(),
                            submission, getChildFragmentManager());
        }

//        final Response response = new ConnectionHelper(context).
//                submitForm(screen.getUrl(), screen.getPhotoUrl(),
//                        submission, getChildFragmentManager());


        if (response != null && response.isSuccessful()) {

            if (submission.getJsonFileName().equalsIgnoreCase("finish_on_site.json")) {
                Answer answer = DBHandler.getInstance().getAnswer(submission.getID(), "rfna", null, 0);
                if (answer != null && !TextUtils.isEmpty(answer.getAnswer()) && answer.getAnswer().equalsIgnoreCase("true")) {
                    Response response1 = new ConnectionHelper(context).sendRfna(submission);

                    if (response1 != null && response1.isSuccessful()) {
                        JobModuleStatus status = new JobModuleStatus();
                        status.setStatus(true);
                        status.setJobId(jobID);
                        status.setModuleName("Ready For Next Activity");
                        status.setSubmissionId(submission.getID());
                        DBHandler.getInstance().replaceData(JobModuleStatus.DBTable.NAME,
                                status.toContentValues());
                    }
                }
            }
            DBHandler.getInstance().removeAnswers(submission);
            JobModuleStatus jobModuleStatus = new JobModuleStatus();
            jobModuleStatus.setStatus(true);
            jobModuleStatus.setJobId(jobID);
            jobModuleStatus.setModuleName(formTitle);
            DBHandler.getInstance().replaceData(JobModuleStatus.DBTable.NAME,
                    jobModuleStatus.toContentValues());

            if (screen.getTitle().equalsIgnoreCase("Book Off")) {
                AppPreferences.putString(jobID + "_" + Constants.IS_BOOK_ON, null);
            } else if (screen.getTitle().equalsIgnoreCase("Book On")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date d = new Date();
                AppPreferences.putString(jobID + "_" + Constants.IS_BOOK_ON, sdf.format(d));
            }

        }
    }

    private void updateLogHours( Submission submission) {

        if (!CommonUtils.isNetworkAvailable(context)) {
            DBHandler.getInstance().setSubmissionQueued(submission);
            return;
        }

        if (!CommonUtils.validateToken(context)) {
            return;
        }

        String jsonFileName = submission.getJsonFileName();

        if (TextUtils.isEmpty(jsonFileName)) {
            return;
        }
        Form form = JsonReader.loadForm(context, submission.getJsonFileName());
        if (form == null) {
            return;
        }

        ArrayList<Screen> screens = form.getScreens();
        if(screens == null || screens.isEmpty()){
            return;
        }

        Screen screen = screens.get(screens.size() - 1);

        Answer answer = DBHandler.getInstance().getAnswer(submission.getID() , "weekCommencing" , null , 0);
        if(answer == null || TextUtils.isEmpty(answer.getAnswer())){
            return;
        }

        String weekCommencing = answer.getAnswer();

        ArrayList<TimeSheetHour> hours = DBHandler.getInstance().getTimeHours(weekCommencing);
        boolean isEdited = false;
        for(int i = 0 ; i < hours.size() ; i++){
            if(hours.get(i).isEdited()){
                isEdited = true;
                break;
            }
        }

        if(!isEdited){
            submitTimeSheet(submission , weekCommencing);
            return;
        }


        new Thread(() -> {
            Response response = new ConnectionHelper(context).
                    submitForm(screen.getUrl(), screen.getPhotoUrl(),
                            submission, getChildFragmentManager());


            if (response == null || !response.isSuccessful()) {
                DBHandler.getInstance().setSubmissionQueued(submission);
            }

            handler.post(() -> {
                if(response == null){
                    return;
                }

                if(response.isSuccessful()){
                    getTimeSheetHours(submission , weekCommencing);
                }

            });
        }).start();
    }

    private void submitTimeSheet(Submission submission , String weekCommencing){
        ArrayList<TimeSheetHour> hours = DBHandler.getInstance().getTimeHours(weekCommencing);

        Answer signatureFileBytes = DBHandler.getInstance().getAnswer(submission.getID(), "signatureFileBytes",
                null, 0);

        DBHandler.getInstance().removeAnswers(submission);

        for(int i = 0 ; i < hours.size() ; i++){
            if(!TextUtils.isEmpty(hours.get(i).getTimesheetHoursId())){
                Answer answer = DBHandler.getInstance().getAnswer(submission.getID(), "timesheetHoursIds",
                        null, i);
                if (answer == null) {
                    answer = new Answer(submission.getID(), "timesheetHoursIds",
                            null, i);
                }

                answer.setAnswer(hours.get(i).getTimesheetHoursId());
                answer.setDisplayAnswer("");
                answer.setIsMultiList(1);
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer.toContentValues());
            }
        }

        Answer answer = new Answer(submission.getID(), "signatureFileBytes",
                null, 0);
        answer.setAnswer(signatureFileBytes.getAnswer());
        answer.setIsPhoto(1);
        answer.setDisplayAnswer(signatureFileBytes.getDisplayAnswer());
        DBHandler.getInstance().replaceData(Answer.DBTable.NAME , answer.toContentValues());

        sendToServer(submission);
    }

    private void getTimeSheetHours(Submission submission , String weekCommencing){


        User user = DBHandler.getInstance().getUser();

        APICalls.getTimesheetHours(user.gettoken() , weekCommencing).enqueue(new Callback<TimeSheetHours>() {
            @Override
            public void onResponse(Call<TimeSheetHours> call, retrofit2.Response<TimeSheetHours> response) {
                if(response.isSuccessful()){
                    TimeSheetHours timeSheetHours = response.body();
                    if(timeSheetHours != null && !timeSheetHours.isEmpty()) {
                        timeSheetHours.setWeekCommencing();
                        timeSheetHours.toContentValues();
                    }
                }

                submitTimeSheet(submission , weekCommencing);
            }

            @Override
            public void onFailure(Call<TimeSheetHours> call, Throwable t) {
                ((FromActivityListener)context).hideProgressBar();
                submitTimeSheet(submission , weekCommencing);
            }
        });
    }

    private void sendPollingSurvey(int position, Submission submission) {

        if (!CommonUtils.isNetworkAvailable(context)) {
            DBHandler.getInstance().setSubmissionQueued(submission);
            return;
        }
        if(!CommonUtils.validateToken(context)){
            return;
        }

        final Response response = new ConnectionHelper(context).
                submitPollingSurvey("app/jobs/{jobId}/poling-surveys", "app/jobs/{jobId}/photos",
                        submission, getChildFragmentManager());


        if (response != null && response.isSuccessful()) {
            DBHandler.getInstance().removeAnswers(submission);
            submissions.remove(position);
        }
    }

    private void sendLogJobRequest(int position, Submission submission) {

        if (!CommonUtils.isNetworkAvailable(context)) {
            DBHandler.getInstance().setSubmissionQueued(submission);
            return;
        }

        String jsonFileName = submission.getJsonFileName();

        if (TextUtils.isEmpty(jsonFileName)) {
            return;
        }
        Form form = JsonReader.loadForm(context, submission.getJsonFileName());
        if (form == null) {
            return;
        }

        ArrayList<Screen> screens = form.getScreens();
        if (screens == null || screens.isEmpty()) {
            return;
        }

        Screen screen = screens.get(screens.size() - 1);


        ArrayList<Answer> allanswers = DBHandler.getInstance().getAnswers(submission.getID());
        ArrayList<Answer> photos = new ArrayList<>();

        for (int i = 0; i < allanswers.size(); i++) {
            if (allanswers.get(i).isPhoto() != 0) {
                photos.add(allanswers.get(i));
            }
        }

        ArrayList<Answer> answers = DBHandler.getInstance().getRepeatedAnswers(submission.getID(), "StaStockItemId", "Items");
        int count = answers.size();

        String uniqueId = UUID.randomUUID().toString();
        for (int i = 0; i < answers.size(); i++) {
            Answer items = answers.get(i);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("submissionId", uniqueId);
            jsonObject.addProperty("StaStockItemId", items.getAnswer());

            Answer qty = DBHandler.getInstance().getAnswer(submission.getID(), "Quantity", "Items", items.getRepeatCount());
            if (qty != null) {
                jsonObject.addProperty("Quantity", qty.getAnswer());
            }

            Answer JobId = DBHandler.getInstance().getAnswer(submission.getID(), "JobId", null, 0);
            if (JobId != null) {
                submission.setJobID(JobId.getAnswer());
                jsonObject.addProperty("JobId", JobId.getAnswer());
            }

            Answer StaId = DBHandler.getInstance().getAnswer(submission.getID(), "StaId", "Items", items.getRepeatCount());
            if (StaId != null) {
                jsonObject.addProperty("StaId", StaId.getAnswer());
            }

            Answer Comments = DBHandler.getInstance().getAnswer(submission.getID(), "Comments", null, 0);
            if (Comments != null) {
                jsonObject.addProperty("Comments", Comments.getAnswer());
            }

            String jsonSubmission = new Gson().toJson(jsonObject);
            RequestBody body = RequestBody.create(ConnectionHelper.JSON, jsonSubmission);
            final Response response = new ConnectionHelper(context).performJSONNetworking(body, BuildConfig.BASE_URL + screen.getUrl());

            if (response != null && response.isSuccessful() && i == count - 1) {
                String photoUrl = screen.getPhotoUrl();
                photoUrl = photoUrl.replace("{jobId}", submission.getJobID());
                new ConnectionHelper(context).uploadPhotos(photos, uniqueId, photoUrl, getChildFragmentManager(), "");
                DBHandler.getInstance().removeAnswers(submission);
                JobModuleStatus jobModuleStatus = new JobModuleStatus();
                jobModuleStatus.setStatus(true);
                jobModuleStatus.setJobId(submission.getJobID());
                jobModuleStatus.setModuleName(submission.getTitle());
                DBHandler.getInstance().replaceData(JobModuleStatus.DBTable.NAME,
                        jobModuleStatus.toContentValues());
            }

        }

    }

    private void uploadAll() {
        listener.showProgressBar();
        ArrayList<Submission> temp = new ArrayList<>(submissions);
        new Thread(() -> {
            for (int i = 0; i < temp.size(); i++) {
                Submission submission = temp.get(i);
                String jsonFileName = submission.getJsonFileName();
                String title = submission.getTitle();
                if (!TextUtils.isEmpty(title) && (title.equalsIgnoreCase("receipt_accept") ||
                        title.equalsIgnoreCase("receipt_reject"))) {
                } else if (!TextUtils.isEmpty(title) && title.equalsIgnoreCase("poling_survey")) {
                    sendPollingSurvey(i, submission);
                } else if (!TextUtils.isEmpty(jsonFileName) && (jsonFileName.startsWith("store_log_"))) {
                    sendLogJobRequest(i , submission);
                }else if (submission.getJsonFileName().equalsIgnoreCase("timesheet_submit_timesheet.json")) {
                    updateLogHours(submission);
                } else {
                    sendToServer( submission);
                }
            }

            handler.post(() -> {listener.hideProgressBar();
                refreshQueue();});
        }).start();
    }

    private void sendRFNA(int position , Submission submission){

        if (!CommonUtils.isNetworkAvailable(context)) {
            String title = "Submission Error";
            String message = "Internet connection is not available. Please check your internet connection.";
            DBHandler.getInstance().setSubmissionQueued(submission);
            showErrorDialog(title, message);
            return;
        }

        if(!CommonUtils.validateToken(context)){
            return;
        }

        listener.showProgressBar();


        APICalls.sendRfna(submission.getJobID() , user.gettoken()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull retrofit2.Response<Void> response) {

                if(CommonUtils.onTokenExpired(context , response.code())){
                    return;
                }

                if(response.isSuccessful()){
                    showErrorDialog("Success", "Submission was successful");
                    DBHandler.getInstance().removeAnswers(submission);
                    refreshQueue();
                }else{
                    DBHandler.getInstance().setSubmissionQueued(submission);

                    showErrorDialog("Submission Error", "Submission Error, your submission has not been succeed");
                }
                listener.hideProgressBar();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                DBHandler.getInstance().setSubmissionQueued(submission);
                showErrorDialog("Submission Error", "Submission Error, your submission has not been succeed");
                listener.hideProgressBar();
            }
        });
    }

    public void getInsQue(Submission submission,boolean shouldstartQues)
    {
        listener.showProgressBar();
        String latestTemplateVersionId = submission.getJobID();
        ArrayList<Answer> answersinspections = DBHandler.getInstance().getAnswers(submission.getID());
        for (int c = 0; c < answersinspections.size(); c++) {
            Answer answer = answersinspections.get(c);
            if (!TextUtils.isEmpty(answer.getUploadID()) && answer.getUploadID().equalsIgnoreCase("inspectionId")){
                latestTemplateVersionId = DBHandler.getInstance().getLatestInspectionTemplateId(answer.getAnswer());
            }
        }
        String finalLatestTemplateVersionId = latestTemplateVersionId;
        if(!CommonUtils.isNetworkAvailable(context)) {
            String title = "Start Inspection  Error";
            String message = "Internet connection is not available. Please check your internet connection.";
            showErrorDialog(title, message);
            listener.hideProgressBar();
            return;
        }
        APICalls.GetInspectionToolTipList(finalLatestTemplateVersionId,user.gettoken()).enqueue(new Callback<ArrayList<ToolTipModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<ToolTipModel>> call, @NonNull retrofit2.Response<ArrayList<ToolTipModel>> response) {
                if(CommonUtils.onTokenExpired(context, response.code())){
                    return;
                }
                if (response.isSuccessful())
                {
                    ArrayList<ToolTipModel> models = response.body();
                    if (models!=null && models.size()>0) {
                        AppPreferences.setTheme(1);//setting hseq theme.
                        submission.setJobID(finalLatestTemplateVersionId); // for template
                        Intent intent = new Intent(context, FormActivity.class);
                        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
                        intent.putParcelableArrayListExtra(FormActivity.ARG_QUESTIONS, models);
                        startActivity(intent);
                    } else {
                        showErrorDialog("Inspection Error","Unfortunately the selected inspection version does not exist or it has no questions. Please select a different inspection or contact support.");
                    }
                }
                else
                {
                    showErrorDialog("Inspection Error","Unfortunately the selected inspection version does not exist or it has no questions. Please select a different inspection or contact support.");
                }
                listener.hideProgressBar();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<ToolTipModel>> call, @NonNull Throwable t) {
                showErrorDialog("Inspection Error","Unfortunately the selected inspection version does not exist or it has no questions. Please select a different inspection or contact support.");
                listener.hideProgressBar();
            }
        });
    }

    private void refreshQueue(){
        ArrayList<Submission> submissions = DBHandler.getInstance().getQueuedSubmissions();
        this.submissions.clear();
        this.submissions.addAll(submissions);
        adapter.notifyDataSetChanged();
    }
}
