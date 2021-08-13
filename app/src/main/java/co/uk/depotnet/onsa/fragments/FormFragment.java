package co.uk.depotnet.onsa.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import co.uk.depotnet.onsa.BuildConfig;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.AssetDataActivity;
import co.uk.depotnet.onsa.activities.CameraActivity;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.activities.SignatureActivity;
import co.uk.depotnet.onsa.adapters.FormAdapter;
import co.uk.depotnet.onsa.barcode.ScannedBarcodeActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.fragments.store.StoreDetailActivity;
import co.uk.depotnet.onsa.listeners.FormAdapterListener;
import co.uk.depotnet.onsa.listeners.FromActivityListener;
import co.uk.depotnet.onsa.listeners.LocationListener;
import co.uk.depotnet.onsa.listeners.OnChangeChamberCount;
import co.uk.depotnet.onsa.modals.ItemType;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.JobModuleStatus;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Screen;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.hseq.ToolTipModel;
import co.uk.depotnet.onsa.modals.responses.JobResponse;
import co.uk.depotnet.onsa.modals.schedule.JobEstimate;
import co.uk.depotnet.onsa.modals.store.ReceiptItems;
import co.uk.depotnet.onsa.modals.store.Receipts;
import co.uk.depotnet.onsa.modals.store.StoreDataset;
import co.uk.depotnet.onsa.modals.timesheet.TimeSheetHours;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CallUtils;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.networking.ConnectionHelper;
import co.uk.depotnet.onsa.networking.Constants;
import co.uk.depotnet.onsa.utils.AppPreferences;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;
import co.uk.depotnet.onsa.views.MaterialAlertDialog;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


public class FormFragment extends Fragment implements FormAdapterListener, OnChangeChamberCount {

    private static final int ASSET_DATA_RESULT = 1111;
    private static final String ARGS_SCREEN = "screen";
    private static final String ARGS_FORM_TITLE = "form_title";
    private static final String ARGS_INDEX = "index";
    private static final int CAMERA_REQUEST = 1;
    private static final int SIGNATURE_REQUEST = 2;
    private static final int STORE_DETAIL_REQUEST = 3;
    private static final String ARG_SUBMISSION = "Submission";
    private static final String ARG_REPEAT_COUNT = "repeatCount";
    private static final String ARGS_IS_SCHEDULED_INSPECTION = "isScheduledInspection";
    private static final String ARG_RECIPIENTS = "ARG_RECIPIENTS";
    private static final int INTENT_PERMISSION = 1000;
    private static final String ARG_COLOR = "Color";

    private Context context;
    private Screen screen;
    private FromActivityListener listener;
    private String jobID;
    private FormAdapter formAdapter;
    private Submission submission;
    private Handler handler;
    private int repeatCount;
    private String formTitle;
    private String themeColor;
    private boolean isScheduledInspection;
    private ArrayList<String> recipients;
    private FusedLocationProviderClient mFusedLocationClient;


    public static FormFragment newInstance(Submission submission,
                                           Screen screen, String formTitle, int index,
                                           int repeatCount, String themeColor, boolean isScheduledInspection, ArrayList<String> recipients) {
        FormFragment fragment = new FormFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SUBMISSION, submission);
        args.putString(ARGS_FORM_TITLE, formTitle);
        args.putParcelable(ARGS_SCREEN, screen);
        args.putInt(ARGS_INDEX, index);
        args.putBoolean(ARGS_IS_SCHEDULED_INSPECTION, isScheduledInspection);
        args.putString(ARG_COLOR, themeColor);
        args.putStringArrayList(ARG_RECIPIENTS, recipients);
        if (repeatCount != -1) {
            args.putInt(ARG_REPEAT_COUNT, repeatCount);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            submission = args.getParcelable(ARG_SUBMISSION);
            screen = args.getParcelable(ARGS_SCREEN);
            formTitle = args.getString(ARGS_FORM_TITLE);
            themeColor = args.getString(ARG_COLOR);
            isScheduledInspection = args.getBoolean(ARGS_IS_SCHEDULED_INSPECTION);
            recipients = args.getStringArrayList(ARG_RECIPIENTS);


            if (args.containsKey(ARG_REPEAT_COUNT)) {
                repeatCount = args.getInt(ARG_REPEAT_COUNT);
            }
        }


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        jobID = submission.getJobID();
        this.handler = new Handler();

        if ((submission.getJsonFileName().equalsIgnoreCase("risk_assessment.json") || submission.getJsonFileName().equalsIgnoreCase("sub_job_risk_assessment.json")) &&
                screen.getIndex() == 16) {
            onChangeChamberCount(false);
        } else if ((submission.getJsonFileName().equalsIgnoreCase("risk_assessment.json") || submission.getJsonFileName().equalsIgnoreCase("sub_job_risk_assessment.json")) &&
                screen.getIndex() > 16 && getChamberCount() > 0) {
            repeatCount = screen.getItems().get(0).getRepeatCount();

        } else if (submission.getJsonFileName().equalsIgnoreCase("finish_on_site.json") &&
                !isRFNAEnable()) {
            screen.getItems().remove(1);
        }

        formAdapter = new FormAdapter(context, submission, screen, themeColor, isScheduledInspection, this, this);
        if (repeatCount != -1) {
            formAdapter.setRepeatCount(repeatCount);
        }
    }

    private boolean isRFNAEnable() {

        Job job = DBHandler.getInstance().getJob(jobID);
        boolean hasRFNA = job != null && job.hasRFNA();

        return !hasRFNA &&
                DBHandler.getInstance().getJobModuleStatus(jobID, "Start on Site") &&
                !DBHandler.getInstance().getJobModuleStatus(jobID, "Eng Comp");
    }

    private int getChamberCount() {
        Answer answer = DBHandler.getInstance().getAnswer(submission.getID(), "noOfChambers",
                null, 0);
        if (answer == null) {
            return 0;
        }

        String value = answer.getAnswer();
        if (TextUtils.isEmpty(value)) {
            return 0;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public void onChangeChamberCount(boolean isToBeNotified) {
        int chamberCount = getChamberCount();
        listener.onChangeChamberCount(chamberCount);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_from, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(10);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(formAdapter);
        return rootView;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof FromActivityListener) {
            listener = (FromActivityListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        listener.onScreenChange(screen);
        formAdapter.reInflateItems(true);
        formAdapter.notifyDataSetChanged();

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void submit() {
        if (!formAdapter.validate()) {
            return;
        }

        formAdapter.removeUnnecessaryTasks();
        String url = screen.getUrl();
        if (screen.isUpload() && !TextUtils.isEmpty(url)) {
            if (url.equalsIgnoreCase("appstores/logtojob")) {
                sendLogJobRequest();
                return;
            }
            if (submission.getJsonFileName().equalsIgnoreCase("timesheet_submit_timesheet.json")) {
                sendToServer();
                return;
            }

            if (screen.isUpload() && submission.getJsonFileName().equalsIgnoreCase("schedule_inspection.json")) {
                Answer answer = DBHandler.getInstance().getAnswer(submission.getID() , "estimateNo" , null , 0);
                if(answer != null && !TextUtils.isEmpty(answer.getAnswer())) {
                    getEstimateOperative(answer.getAnswer(), 2 , true);
                }else{
                    showValidationDialog("Estimate No Error", "Please enter estimate Number.");
                }
            } else if (screen.isUpload() && submission.getJsonFileName().equalsIgnoreCase("good_2_go.json")) {
                getLocationForServer();
            } else if (screen.isUpload() && submission.getJsonFileName().equalsIgnoreCase("poling_risk_assessment.json")) {
                getLocationForServer();
            } else if (screen.isUpload() && submission.getJsonFileName().equalsIgnoreCase("hoist_risk_assessment.json")) {
                getLocationForServer();
            } else {
                sendToServer();
            }

        } else if (screen.isUpload() && (submission.getJsonFileName().equalsIgnoreCase("take_photo.json") || submission.getJsonFileName().equalsIgnoreCase("sub_job_take_photo.json"))) {
            sendToServer();
        } else if (screen.isUpload()) {
            JobModuleStatus jobModuleStatus = new JobModuleStatus();
            jobModuleStatus.setStatus(true);
            jobModuleStatus.setJobId(jobID);
            jobModuleStatus.setModuleName(submission.getJsonFileName());
            jobModuleStatus.setSubmissionId(submission.getID());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            jobModuleStatus.setSelectedDate(sdf.format(new Date()));
            DBHandler.getInstance().replaceData(JobModuleStatus.DBTable.NAME,
                    jobModuleStatus.toContentValues());
            ((Activity) context).finish();
        } else if (submission.getJsonFileName().equalsIgnoreCase("poling_asset_data.json")) {
            Intent intent = new Intent(context, AssetDataActivity.class);
            intent.putExtra(AssetDataActivity.ARG_SUBMISSION, submission);
            startActivityForResult(intent, ASSET_DATA_RESULT);
        } else if (submission.getJsonFileName().equalsIgnoreCase("good_2_go.json") &&
                screen.getIndex() == 0) {
            Answer answer = DBHandler.getInstance().getAnswer(submission.getID(), "canOverheadGangComplete",
                    null, 0);
            if (answer != null && (answer.getAnswer().equals("false") || answer.getAnswer().equals("2"))) {
                listener.goToNextScreen(38);
            } else {
                listener.goToNextScreen(screen.getIndex());
            }
        } else if (submission.getJsonFileName().equalsIgnoreCase("good_2_go.json") &&
                screen.getIndex() == 1) {
            Answer answer = DBHandler.getInstance().getAnswer(submission.getID(), "isAPolingSolutionRequired",
                    null, 0);
            if (answer != null && (answer.getAnswer().equals("false") || answer.getAnswer().equals("2"))) {
                listener.goToNextScreen(6);
            } else {
                listener.goToNextScreen(screen.getIndex());
            }
            //  screen.setIndex(8);

        } else if (submission.getJsonFileName().equalsIgnoreCase("good_2_go.json") &&
                screen.getIndex() == 7) {
            Answer answer = DBHandler.getInstance().getAnswer(submission.getID(), "isAerialCableRequired",
                    null, 0);
            if (answer != null && (answer.getAnswer().equals("false") || answer.getAnswer().equals("2"))) {
                listener.goToNextScreen(10);
            } else {
                listener.goToNextScreen(screen.getIndex());
            }

        } else if (submission.getJsonFileName().equalsIgnoreCase("good_2_go.json") &&
                screen.getIndex() == 11) {

            Answer answer = DBHandler.getInstance().getAnswer(submission.getID(), "areDropwiresRequired",
                    null, 0);
            if (answer != null && (answer.getAnswer().equals("false") || answer.getAnswer().equals("2"))) {
                listener.goToNextScreen(15);
            } else {
                listener.goToNextScreen(screen.getIndex());
            }
        } else {
            listener.goToNextScreen(screen.getIndex());
        }

    }

    private void setJobModuleStatus(Submission submission) {
        JobModuleStatus jobModuleStatus = new JobModuleStatus();
        jobModuleStatus.setStatus(true);
        jobModuleStatus.setJobId(jobID);
        jobModuleStatus.setModuleName(submission.getTitle());
        jobModuleStatus.setSubmissionId(submission.getID());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        jobModuleStatus.setSelectedDate(sdf.format(new Date()));
        DBHandler.getInstance().replaceData(JobModuleStatus.DBTable.NAME,
                jobModuleStatus.toContentValues());
    }

    @Override
    public void showProgressBar() {
        listener.showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        listener.hideProgressBar();
    }

    public void sendReceipts(final Receipts receipts) {
        if (!formAdapter.validate()) {
            return;
        }
        if (!CommonUtils.isNetworkAvailable(context)) {
            String title = "Submission Error";
            String message = "Internet connection is not available. Please check your internet connection.";
            DBHandler.getInstance().setSubmissionQueued(submission);
            showErrorDialog(title, message, false);
            return;
        }

        if (!CommonUtils.validateToken(context)) {
            return;
        }

        listener.showProgressBar();
        new Thread(() -> {
            ArrayList<ReceiptItems> receiptItems = receipts.getItems();

            int count = receiptItems.size();
            for (int i = 0; i < receiptItems.size(); i++) {
                ReceiptItems items = receiptItems.get(i);
                Answer answer = new Answer(submission.getID(), "goodsOutId");
                answer.setDisplayAnswer(items.getgoodsOutId());
                answer.setAnswer(items.getgoodsOutId());
                answer.setRepeatID(null);
                answer.setRepeatCount(0);

                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer.toContentValues());

                Answer qty = new Answer(submission.getID(), "Quantity");
                qty.setDisplayAnswer(String.valueOf(items.getquantity()));
                qty.setAnswer(String.valueOf(items.getquantity()));
                qty.setRepeatID(null);
                qty.setRepeatCount(0);


                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, qty.toContentValues());


                final Response response = new ConnectionHelper(context).
                        submitForm(screen.getUrl(), screen.getPhotoUrl(),
                                submission, getChildFragmentManager());


                if (response != null) {
                    if (response.isSuccessful()) {
                        if (i == count - 1) {
                            DBHandler.getInstance().removeAnswers(submission);
                            JobModuleStatus jobModuleStatus = new JobModuleStatus();
                            jobModuleStatus.setStatus(true);
                            jobModuleStatus.setJobId(jobID);
                            jobModuleStatus.setModuleName(formTitle);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            jobModuleStatus.setSelectedDate(sdf.format(new Date()));
                            DBHandler.getInstance().replaceData(JobModuleStatus.DBTable.NAME,
                                    jobModuleStatus.toContentValues());
                        }
                    }
                    DBHandler.getInstance().removeAnswer(answer);
                    DBHandler.getInstance().removeAnswer(qty);
                }
                if (i == count - 1) {
                    handler.post(() -> {
                        listener.hideProgressBar();
                        String title = "Success";
                        String message = "Submission was successful";

                        if (response == null || !response.isSuccessful()) {
                            title = "Submission Error";
                            message = "Submission Error, your submission has been added to the queue";
                        }

                        if (response != null && response.code() != 200) {
                            ResponseBody body = response.body();
                            if (body != null) {
                                try {
                                    String data = body.string();
                                    if (!TextUtils.isEmpty(data)) {
                                        JSONObject jsonObject = new JSONObject(data);
                                        if (jsonObject.has("status")) {
                                            title = jsonObject.getString("status");
                                        }

                                        if (jsonObject.has("message")) {
                                            message = jsonObject.getString("message");
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        showErrorDialog(title, message, response != null && response.isSuccessful());
                    });
                }
            }
        }).start();
    }

    private void sendLogJobRequest() {

        JobModuleStatus jobModuleStatus = new JobModuleStatus();
        jobModuleStatus.setStatus(true);
        jobModuleStatus.setJobId(jobID);
        jobModuleStatus.setModuleName(formTitle);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        jobModuleStatus.setSelectedDate(sdf.format(new Date()));
        DBHandler.getInstance().replaceData(JobModuleStatus.DBTable.NAME,
                jobModuleStatus.toContentValues());

        if (!CommonUtils.isNetworkAvailable(context)) {
            String title = "Submission Error";
            String message = "Internet connection is not available. Please check your internet connection.";
            DBHandler.getInstance().setSubmissionQueued(submission);
            showErrorDialog(title, message, false);
            return;
        }

        listener.showProgressBar();
        new Thread(() -> {
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
                }


                if (i == count - 1) {
                    handler.post(() -> {
                        listener.hideProgressBar();
                        String title = "Success";
                        String message = "Submission was successful";

                        if (response == null || !response.isSuccessful()) {
                            title = "Submission Error";
                            message = "Submission Error, your submission has been added to the queue";
                        }

                        if (response != null && response.code() != 200) {
                            ResponseBody body1 = response.body();
                            if (body1 != null) {
                                try {
                                    String data = body1.string();
                                    if (!TextUtils.isEmpty(data)) {
                                        JSONObject jsonObject1 = new JSONObject(data);
                                        if (jsonObject1.has("status")) {
                                            title = jsonObject1.getString("status");
                                        }

                                        if (jsonObject1.has("message")) {
                                            message = jsonObject1.getString("message");
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                        showErrorDialog(title, message, response != null && response.isSuccessful());
                    });
                }
            }
        }).start();
    }

    private void setBookOffOnStatus() {
        if (screen.getTitle().equalsIgnoreCase("Book Off")) {
            AppPreferences.putString(jobID + "_" + Constants.IS_BOOK_ON, null);
        } else if (screen.getTitle().equalsIgnoreCase("Book On")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date d = new Date();
            AppPreferences.putString(jobID + "_" + Constants.IS_BOOK_ON, sdf.format(d));
        }
    }

    private void setTimeSheetBookOffOnStatus() {
        if (screen.getTitle().equalsIgnoreCase("Timesheet Book Off")) {
            AppPreferences.putString("TimeSheet_" + Constants.IS_BOOK_ON, null);
        } else if (screen.getTitle().equalsIgnoreCase("Timesheet Book On")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date d = new Date();
            AppPreferences.putString("TimeSheet_" + Constants.IS_BOOK_ON, sdf.format(d));
        }
    }

    private void getLocationForServer() {

        getLocation(new LocationListener() {

            @Override
            public void onSuccess(Location location) {
                submission.setLatitude(location.getLatitude());
                submission.setLongitude(location.getLongitude());
                sendToServer();
            }

            @Override
            public void onFailure() {
                sendToServer();
            }

        });

    }

    private void sendToServer() {

        if (!CommonUtils.isNetworkAvailable(context)) {
            String title = "Submission Error";
            String message = "Internet connection is not available. Please check your internet connection. Your request is submitted in Queue.";
            DBHandler.getInstance().setSubmissionQueued(submission);
            setJobModuleStatus(submission);
            setBookOffOnStatus();
            setTimeSheetBookOffOnStatus();
            showErrorDialog(title, message, false);
            return;
        }

        if (!CommonUtils.validateToken(context)) {
            return;
        }


        listener.showProgressBar();
        new Thread(() -> {
            Response response;
            if (submission.getJsonFileName().equalsIgnoreCase("corrective_measure.json")
                    || submission.getJsonFileName().equalsIgnoreCase("cannot_rectify.json")
                    || submission.getJsonFileName().equalsIgnoreCase("incident_cannot_rectify.json")
                    || submission.getJsonFileName().equalsIgnoreCase("incident_corrective_measure.json")) {
                response = new ConnectionHelper(context).
                        submitActions(screen.getUrl(),
                                submission, getChildFragmentManager());
            } else if (submission.getJsonFileName().equalsIgnoreCase("slg_inspection.json")) {
                response = new ConnectionHelper(context).
                        submitInspections(screen.getUrl(), screen.getPhotoUrl(),
                                submission, getChildFragmentManager());
            }else {  //final Response response =

                response = new ConnectionHelper(context).
                        submitForm(screen.getUrl(), screen.getPhotoUrl(),
                                submission, getChildFragmentManager());
            }

            if (response == null || !response.isSuccessful()) {
                DBHandler.getInstance().setSubmissionQueued(submission);
            }

            setBookOffOnStatus();
            setTimeSheetBookOffOnStatus();
            if (submission.getJsonFileName().equalsIgnoreCase("finish_on_site.json")) {
                Answer answer = DBHandler.getInstance().getAnswer(submission.getID(), "rfna", null, 0);
                if (answer != null && !TextUtils.isEmpty(answer.getAnswer()) && answer.getAnswer().equalsIgnoreCase("true")) {
                    new ConnectionHelper(context).sendRfna(submission);
                    JobModuleStatus status = new JobModuleStatus();
                    status.setStatus(true);
                    status.setJobId(jobID);
                    status.setModuleName("Ready For Next Activity");
                    status.setSubmissionId(submission.getID());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    status.setSelectedDate(sdf.format(new Date()));
                    DBHandler.getInstance().replaceData(JobModuleStatus.DBTable.NAME,
                            status.toContentValues());

                }
            }

            if (response != null && response.isSuccessful()) {
                DBHandler.getInstance().removeAnswers(submission);
            }

            JobModuleStatus jobModuleStatus = new JobModuleStatus();
            jobModuleStatus.setStatus(true);
            jobModuleStatus.setJobId(jobID);
            jobModuleStatus.setModuleName(formTitle);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            jobModuleStatus.setSelectedDate(sdf.format(new Date()));
            DBHandler.getInstance().replaceData(JobModuleStatus.DBTable.NAME,
                    jobModuleStatus.toContentValues());

            Response finalResponse = response;
            handler.post(() -> {
                listener.hideProgressBar();
                String title = "Success";
                String message = "Submission was successful";
                if (finalResponse != null && finalResponse.isSuccessful()) {
                    if (submission.getJsonFileName().equalsIgnoreCase("risk_assessment.json") || submission.getJsonFileName().equalsIgnoreCase("sub_job_risk_assessment.json")) {
                        getJobs();
                    }
                }

                if (!submission.getJsonFileName().equalsIgnoreCase("take_photo.json") &&
                        (finalResponse == null || !finalResponse.isSuccessful())) {
                    title = "Submission Error";
                    message = "Submission Error, your submission has been added to the queue";
                }

                if (finalResponse != null && finalResponse.code() != 200) {
                    ResponseBody body = finalResponse.body();
                    if (body != null) {
                        try {
                            String data = body.string();
                            if (!TextUtils.isEmpty(data)) {
                                JSONObject jsonObject = new JSONObject(data);
                                if (jsonObject.has("status")) {
                                    title = jsonObject.getString("status");
                                }

                                if (jsonObject.has("message")) {
                                    message = jsonObject.getString("message");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                showErrorDialog(title, message, finalResponse != null && finalResponse.isSuccessful());
            });
        }).start();
    }

    private void getJobs() {
        if (!CommonUtils.validateToken(context)) {
            return;
        }
        User user = DBHandler.getInstance().getUser();
        listener.showProgressBar();
        CallUtils.enqueueWithRetry(APICalls.getJobList(user.gettoken()) , new Callback<JobResponse>() {
            @Override
            public void onResponse(@NonNull Call<JobResponse> call, @NonNull retrofit2.Response<JobResponse> response) {

                if (CommonUtils.onTokenExpired(context, response.code())) {
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    List<Job> jobs = response.body().getJobs();
                    DBHandler.getInstance().resetJobs();
                    if (jobs != null && !jobs.isEmpty()) {
                        for (Job j : jobs) {
                            DBHandler.getInstance().replaceData(Job.DBTable.NAME, j.toContentValues());
                        }
                    }
                }
                listener.hideProgressBar();
            }

            @Override
            public void onFailure(@NonNull Call<JobResponse> call, @NonNull Throwable t) {
                listener.hideProgressBar();
            }
        });
    }

    @Override
    public void openCamera(long submissionId, FormItem formItem, int repeatCount) {
        Intent intent = new Intent(context, CameraActivity.class);
        intent.putExtra(CameraActivity.ARG_FORM_ITEM, formItem);
        intent.putExtra(CameraActivity.ARG_SUBMISSION_ID, submissionId);
        intent.putExtra(CameraActivity.ARG_REPEAT, repeatCount);
        intent.putExtra(CameraActivity.ARG_COLOR, themeColor);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    public void openSignature(FormItem formItem, long submissionId, int repeatCount) {
        Intent intent = new Intent(context, SignatureActivity.class);
        intent.putExtra(SignatureActivity.ARG_FORM_ITEM, formItem);
        intent.putExtra(SignatureActivity.ARG_SUBMISSION_ID, submissionId);
        intent.putExtra(SignatureActivity.ARG_REPEAT_COUNT, repeatCount);
        intent.putExtra(SignatureActivity.ARG_COLOR, themeColor);
        startActivityForResult(intent, SIGNATURE_REQUEST);
    }

    @Override
    public void openForkFragment(FormItem formItem, long submissionId, int repeatCount) {
        listener.showBtnContainer(false);
        ForkFormFragment fragment = ForkFormFragment.newInstance(submission, formItem, repeatCount, themeColor, recipients);
        listener.addFragment(fragment);
    }

    @Override
    public void openFragment(Fragment fragment) {
        listener.showBtnContainer(false);
        listener.addFragment(fragment);
    }

    @Override
    public void showBottomSheet(BottomSheetDialogFragment dialogFragment) {
        try {
            dialogFragment.show(getChildFragmentManager(), dialogFragment.getClass().getSimpleName());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST ||
                    requestCode == SIGNATURE_REQUEST) {
                formAdapter.notifyDataSetChanged();
            } else if (requestCode == ASSET_DATA_RESULT) {
                ((Activity) context).finish();
            } else if (requestCode == ScannedBarcodeActivity.REQUEST_CODE) {
                Intent intent = new Intent(context, StoreDetailActivity.class);
                intent.putExtra("barcode_result", data.getStringExtra("barcode_result"));
                intent.putExtra("StaId", data.getStringExtra("StaId"));
                startActivityForResult(intent, STORE_DETAIL_REQUEST);
            } else if (requestCode == STORE_DETAIL_REQUEST) {

                ArrayList<Answer> answers = DBHandler.getInstance().getRepeatedAnswers(submission.getID(), "StaStockItemId", "Items");
                if (answers != null) {
                    String stockItemId = data.getStringExtra("stockItemId");
                    int count = answers.size();

                    for (Answer a : answers) {
                        if (!TextUtils.isEmpty(a.getAnswer()) && a.getAnswer().equalsIgnoreCase(stockItemId)) {
                            count = a.getRepeatCount();
                            break;
                        }
                    }


                    Answer answer = DBHandler.getInstance().getAnswer(submission.getID(), "StaStockItemId",
                            "Items", count);
                    if (answer == null || TextUtils.isEmpty(answer.getAnswer())) {
                        answer = new Answer(submission.getID(), "StaStockItemId");

                    }
                    answer.setAnswer(data.getStringExtra("stockItemId"));
                    answer.setRepeatCount(count);
                    answer.setRepeatID("Items");

                    DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer.toContentValues());


                    Answer Quantity = DBHandler.getInstance().getAnswer(submission.getID(), "Quantity",
                            "Items", count);
                    if (Quantity == null || TextUtils.isEmpty(Quantity.getAnswer())) {
                        Quantity = new Answer(submission.getID(), "Quantity");
                    }
                    Quantity.setAnswer(String.valueOf(data.getIntExtra("stock_quantity", 0)));
                    Quantity.setRepeatCount(count);
                    Quantity.setRepeatID("Items");

                    DBHandler.getInstance().replaceData(Answer.DBTable.NAME, Quantity.toContentValues());


                    Answer StaId = DBHandler.getInstance().getAnswer(submission.getID(), "StaId",
                            "Items", count);
                    if (StaId == null) {
                        StaId = new Answer(submission.getID(), "StaId");
                    }
                    StaId.setRepeatID("Items");
                    StaId.setRepeatCount(count);
                    StaId.setAnswer(data.getStringExtra("StaId"));

                    ItemType itemType = DBHandler.getInstance().getItemType(StoreDataset.DBTable.stas, data.getStringExtra("StaId"));
                    if (itemType != null) {
                        StaId.setDisplayAnswer(itemType.getDisplayItem());
                    }
                    DBHandler.getInstance().replaceData(Answer.DBTable.NAME, StaId.toContentValues());
                }
            } else if (requestCode == 10001) {
                Answer answer = DBHandler.getInstance().getAnswer(submission.getID() , "weekCommencing", null , 0);
                if(answer != null && !TextUtils.isEmpty(answer.getAnswer())) {
                    getTimeSheetHours(answer.getAnswer());
                }
            }

        }
    }

    private void getTimeSheetHours(String weekCommencing){
        if(TextUtils.isEmpty(weekCommencing)){
            return;
        }

        showProgressBar();
        User user = DBHandler.getInstance().getUser();

        CallUtils.enqueueWithRetry(APICalls.getTimesheetHours(user.gettoken() , weekCommencing), new Callback<TimeSheetHours>() {
            @Override
            public void onResponse(@NonNull Call<TimeSheetHours> call, @NonNull retrofit2.Response<TimeSheetHours> response) {
                if(response.isSuccessful()){
                    TimeSheetHours timeSheetHours = response.body();
                    if(timeSheetHours != null && !timeSheetHours.isEmpty()) {
                        timeSheetHours.setWeekCommencing();
                        timeSheetHours.toContentValues();
                        formAdapter.reInflateItems(true);
                    }
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(@NonNull Call<TimeSheetHours> call, @NonNull Throwable t) {
                hideProgressBar();
            }
        });
    }

    public void showErrorDialog(String title, String message, boolean isSuccessful) {

        if (!isAdded() || getChildFragmentManager().isStateSaved() ) {
            ((Activity) context).setResult(Activity.RESULT_OK);
            ((Activity) context).finish();
            return;
        }
        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), (dialog1, i) -> {
                    dialog1.dismiss();
                    ((Activity) context).setResult(Activity.RESULT_OK);
                    ((Activity) context).finish();
                })
                .build();

        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "_ERROR_DIALOG");
    }

    public void showValidationDialog(String title, String message) {
        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), (dialog1, i) -> dialog1.dismiss())
                .build();

        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "_ERROR_DIALOG");
    }


    private void requestPermissions(@NonNull final String[] permissions, String permissionRequestRationale) {
        boolean permissionFailure = false;
        boolean shouldShowRationale = false;
        for (String permission : permissions) {
            int checkResult = ContextCompat.checkSelfPermission(context, permission);
            if (checkResult != PackageManager.PERMISSION_GRANTED) {
                shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission);
                permissionFailure = true;
                break;
            }
        }

        if (!permissionFailure) {
            int[] permissionGrant = new int[permissions.length];
            for (int i = 0; i < permissions.length; i++) {
                permissionGrant[i] = PackageManager.PERMISSION_GRANTED;
            }
            onRequestPermissionsResult(INTENT_PERMISSION, permissions, permissionGrant);
        } else {
            if (shouldShowRationale && permissionRequestRationale != null) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.DialogTheme)
                        .setTitle(getString(R.string.permission_request_title))
                        .setMessage(permissionRequestRationale)
                        .setPositiveButton(getString(R.string.permission_continue), (dialog1, which) -> {
                            dialog1.dismiss();
                            requestPermissions(permissions, INTENT_PERMISSION);
                        })
                        .setNegativeButton(getString(android.R.string.cancel), (dialog12, which) -> dialog12.dismiss());
                dialog.show();
            } else {
                requestPermissions(permissions, INTENT_PERMISSION);
            }
        }
    }


    @Override
    public void getLocation(final LocationListener listener) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            //ask user to grant permission
            String permissionRationale = getString(R.string.location_default_permission_rationale);
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    permissionRationale);
        } else {

            mFusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            listener.onSuccess(location);
                        } else {
                            createLocationRequest(listener);
//                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            listener.onFailure();
                        }
                    })
                    .addOnFailureListener(e -> {
                        listener.onFailure();
                        createLocationRequest(listener);
                        Log.d("ADD JOB", "Error trying to get last GPS location");
                        e.printStackTrace();
                    });
        }
    }

    private void createLocationRequest(LocationListener listener) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);


        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener((Activity) context, locationSettingsResponse -> startLocationUpdates(listener));

        task.addOnFailureListener((Activity) context, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult((Activity) context,
                            11);
                } catch (IntentSender.SendIntentException sendEx) {
                    sendEx.printStackTrace();
                }
            }
        });
    }

    private void startLocationUpdates(LocationListener listener) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        if (locationResult == null) {
                            return;
                        }
                        for (Location location : locationResult.getLocations()) {
                            if (location != null) {
                                listener.onSuccess(location);
                                mFusedLocationClient.removeLocationUpdates(this);
                                break;
                            }
                        }
                    }
                },
                Looper.getMainLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context, R.style.DialogTheme)
                    .setTitle(getString(R.string.permission_denied))
                    .setMessage(getString(R.string.permissions_location_failure))
                    .setPositiveButton(getString(R.string.permissions_settings), (dialogInterface, i) -> {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", ((Activity) context).getApplication().getPackageName(), null);
                        intent.setData(uri);

                        startActivity(intent);
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton(getString(R.string.generic_cancel), (dialogInterface, i) -> dialogInterface.dismiss());
            dialog.show();
        }
    }

    @Override
    public void startActivityForResultFromAdapter(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }


    @Override
    public void openTaskAmendment(FormItem formItem, long submissionId, int repeatCount) {
        listener.showBtnContainer(false);
        ForkFormFragment fragment = ForkFormFragment.newInstance(submission, formItem, repeatCount, themeColor, recipients);
        listener.addFragment(fragment);
    }

    @Override
    public void getEstimateOperative(String estno, int position , boolean isSubmit) {
        if (!CommonUtils.isNetworkAvailable(context) || (estno.isEmpty() && position == -1)) {
            showErrorDialog("Internet Connection", "Internet connection is not available. Please check your internet connection.", false);
            return;
        }
        if(TextUtils.isEmpty(estno)){
            showValidationDialog("Estimate No Error", "Unfortunately the entered estimate number does not exist or it has no data. Please input a different or contact support.");
            return;
        }
        listener.showProgressBar();
        CallUtils.enqueueWithRetry(APICalls.GetJobEstimateDetail(estno, DBHandler.getInstance().getUser().gettoken()),new Callback<JobEstimate>() {
            @Override
            public void onResponse(@NonNull Call<JobEstimate> call, @NonNull retrofit2.Response<JobEstimate> response) {
                if (CommonUtils.onTokenExpired(context, response.code())) {
                    return;
                }
                if (response.isSuccessful()) {
                    JobEstimate jobEstimate = response.body();
                    if (jobEstimate != null) {
                        formAdapter.showEstimateOperative(jobEstimate, position);
                        if(isSubmit){
                            getInsQue();
                            return;
                        }
                    } else {
                        showValidationDialog("Estimate No Error", "Unfortunately the entered estimate number does not exist or it has no data. Please input a different or contact support.");
                    }
                } else {
                    showValidationDialog("Estimate No Error", "Unfortunately the entered estimate number does not exist or it has no data. Please input a different or contact support.");
                }
                listener.hideProgressBar();
            }

            @Override
            public void onFailure(@NonNull Call<JobEstimate> call, @NonNull Throwable t) {
                listener.hideProgressBar();
                showValidationDialog("Estimate No Error", "Unfortunately the entered estimate number does not exist or it has no data. Please input a different or contact support.");
            }
        });
    }

    private void getInsQue() {
        listener.showProgressBar();
        String latestTemplateVersionId = submission.getJobID();
        ArrayList<Answer> answersinspections = DBHandler.getInstance().getAnswers(submission.getID());
        for (int c = 0; c < answersinspections.size(); c++) {
            Answer answer = answersinspections.get(c);
            if (!TextUtils.isEmpty(answer.getUploadID()) && answer.getUploadID().equalsIgnoreCase("inspectionId")) {
                latestTemplateVersionId = DBHandler.getInstance().getLatestInspectionTemplateId(answer.getAnswer());
            }
        }
        String finalLatestTemplateVersionId = latestTemplateVersionId;
        if (!CommonUtils.isNetworkAvailable(context)) {
            listener.hideProgressBar();
            showValidationDialog("Start Inspection  Error", "Internet connection is not available. Please check your internet connection.");
            return;
        }
        User user = DBHandler.getInstance().getUser();
        CallUtils.enqueueWithRetry(APICalls.GetInspectionToolTipList(finalLatestTemplateVersionId, user.gettoken()),new Callback<ArrayList<ToolTipModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<ToolTipModel>> call, @NonNull retrofit2.Response<ArrayList<ToolTipModel>> response) {
                if (CommonUtils.onTokenExpired(context, response.code())) {
                    return;
                }
                if (response.isSuccessful()) {
                    ArrayList<ToolTipModel> models = response.body();
                    if (models != null && models.size() > 0) {
                        AppPreferences.setTheme(1);
                        String jsonFileName = "slg_inspection.json";
                        submission.setJsonFile(jsonFileName);
                        long submissionID = DBHandler.getInstance().replaceData(Submission.DBTable.NAME, submission.toContentValues());
                        submission.setJobID(finalLatestTemplateVersionId); // for tempplate
                        Intent intent = new Intent(context, FormActivity.class);
                        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
                        intent.putParcelableArrayListExtra(FormActivity.ARG_QUESTIONS, models);
                        startActivity(intent);
                        requireActivity().finish();
                    } else {
                        showValidationDialog("Inspection Error", "Unfortunately the selected inspection version does not exist or it has no questions. Please select a different inspection or contact support.");
                    }
                } else {
                    showValidationDialog("Inspection Error", "Unfortunately the selected inspection version does not exist or it has no questions. Please select a different inspection or contact support.");
                }
                listener.hideProgressBar();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<ToolTipModel>> call, @NonNull Throwable t) {
                listener.hideProgressBar();
                showValidationDialog("Inspection Error", "Unfortunately the selected inspection version does not exist or it has no questions. Please select a different inspection or contact support.");
            }
        });
    }
    @Override
    public void showDatePicker(DialogFragment dialogFragment) {
        dialogFragment.show(getChildFragmentManager() , dialogFragment.toString());
    }

}
