package co.uk.depotnet.onsa.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.core.app.ActivityCompat;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

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
import co.uk.depotnet.onsa.activities.SignatureActivity;
import co.uk.depotnet.onsa.adapters.FormAdapter;
import co.uk.depotnet.onsa.barcode.ScannedBarcodeActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.fragments.store.FragmentStoreDetail;
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
import co.uk.depotnet.onsa.modals.forms.Photo;
import co.uk.depotnet.onsa.modals.forms.Screen;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.responses.JobResponse;
import co.uk.depotnet.onsa.modals.store.ReceiptItems;
import co.uk.depotnet.onsa.modals.store.Receipts;
import co.uk.depotnet.onsa.modals.store.StoreDataset;
import co.uk.depotnet.onsa.networking.APICalls;
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

    public static final int ASSET_DATA_RESULT = 1111;
    public static final String ARGS_SCREEN = "screen";
    public static final String ARGS_FORM_TITLE = "form_title";
    public static final String ARGS_INDEX = "index";
    private static final int CAMERA_REQUEST = 1;
    private static final int SIGNATURE_REQUEST = 2;
    private static final int STORE_DETAIL_REQUEST = 3;
    private static final String ARG_SUBMISSION = "Submission";
    private static final String ARG_REPEAT_COUNT = "repeatCount";
    private static final String ARG_USER = "User";
    private static final int INTENT_PERMISSION = 1000;
    private Context context;
    private Screen screen;
    private FromActivityListener listener;
    private User user;
    private String jobID;
    private FormAdapter formAdapter;
    private Submission submission;
    private Handler handler;
    private int repeatCount;
    private String formTitle;


    public static FormFragment newInstance(Submission submission, User user,
                                           Screen screen, String formTitle, int index,
                                           int repeatCount) {
        FormFragment fragment = new FormFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SUBMISSION, submission);
        args.putParcelable(ARG_USER, user);
        args.putString(ARGS_FORM_TITLE, formTitle);
        args.putParcelable(ARGS_SCREEN, screen);
        args.putInt(ARGS_INDEX, index);
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
            user = args.getParcelable(ARG_USER);
            screen = args.getParcelable(ARGS_SCREEN);
            formTitle = args.getString(ARGS_FORM_TITLE);

            if (args.containsKey(ARG_REPEAT_COUNT)) {
                repeatCount = args.getInt(ARG_REPEAT_COUNT);
            }
        }

        jobID = submission.getJobID();
        this.handler = new Handler();

        if (submission.getJsonFileName().equalsIgnoreCase("risk_assessment.json") &&
                screen.getIndex() == 16) {
            onChangeChamberCount(false);
        } else if (submission.getJsonFileName().equalsIgnoreCase("risk_assessment.json") &&
                screen.getIndex() > 16 && getChamberCount() > 0) {
            repeatCount = screen.getItems().get(0).getRepeatCount();

        }

        formAdapter = new FormAdapter(context, submission, screen, this, this);
        if (repeatCount != -1) {
            formAdapter.setRepeatCount(repeatCount);
        }
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
    public void onAttach(Context context) {
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
        if (formAdapter.validate()) {
            String url = screen.getUrl();
            if (screen.isUpload() && !TextUtils.isEmpty(url)) {
                if (url.equalsIgnoreCase("appstores/logtojob")) {
                    sendLogJobRequest();
                } else {
                    sendToServer();
                }
            } else if (screen.isUpload()) {
                JobModuleStatus jobModuleStatus = new JobModuleStatus();
                jobModuleStatus.setStatus(true);
                jobModuleStatus.setJobId(jobID);
                jobModuleStatus.setModuleName(submission.getJsonFileName());
                jobModuleStatus.setSubmissionId(submission.getID());
                DBHandler.getInstance().replaceData(JobModuleStatus.DBTable.NAME,
                        jobModuleStatus.toContentValues());
                ((Activity) context).finish();
            } else if (submission.getJsonFileName().equalsIgnoreCase("poling_asset_data.json")) {
                Intent intent = new Intent(context, AssetDataActivity.class);
                intent.putExtra(AssetDataActivity.ARG_USER, user);
                intent.putExtra(AssetDataActivity.ARG_SUBMISSION, submission);
                startActivityForResult(intent, ASSET_DATA_RESULT);
            } else {
                listener.goToNextScreen(screen.getIndex());
            }
        }
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

        listener.showProgressBar();
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                    qty.setDisplayAnswer(String.valueOf((int)items.getquantity()));
                    qty.setAnswer(String.valueOf((int)items.getquantity()));
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
                                DBHandler.getInstance().replaceData(JobModuleStatus.DBTable.NAME,
                                        jobModuleStatus.toContentValues());
                            }
                        }
                        DBHandler.getInstance().removeAnswer(answer);
                        DBHandler.getInstance().removeAnswer(qty);
                    }
                    if (i == count - 1) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.hideProgressBar();
                                String title = "Success";
                                String message = "Submission was successful";

                                if (response == null || !response.isSuccessful()) {
                                    title = "Submission Error";
                                    message = "Submission Error, your submission has been added to the queue";
                                }

                                if (response != null && response.code() == 400) {
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
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public void sendLogJobRequest() {

        if (!CommonUtils.isNetworkAvailable(context)) {
            String title = "Submission Error";
            String message = "Internet connection is not available. Please check your internet connection.";
            DBHandler.getInstance().setSubmissionQueued(submission);
            showErrorDialog(title, message, false);
            return;
        }

        listener.showProgressBar();
        new Thread(new Runnable() {
            @Override
            public void run() {
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

                    if (response != null && response.isSuccessful() && i == count-1) {
                        String photoUrl = screen.getPhotoUrl();
                        photoUrl = photoUrl.replace("{jobId}", submission.getJobID());
                        new ConnectionHelper(context).uploadPhotos(photos , uniqueId , photoUrl , getChildFragmentManager() , "");
                        DBHandler.getInstance().removeAnswers(submission);
                        JobModuleStatus jobModuleStatus = new JobModuleStatus();
                        jobModuleStatus.setStatus(true);
                        jobModuleStatus.setJobId(jobID);
                        jobModuleStatus.setModuleName(formTitle);
                        DBHandler.getInstance().replaceData(JobModuleStatus.DBTable.NAME,
                                jobModuleStatus.toContentValues());
                    }



                    if (i == count - 1) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.hideProgressBar();
                                String title = "Success";
                                String message = "Submission was successful";

                                if (response == null || !response.isSuccessful()) {
                                    title = "Submission Error";
                                    message = "Submission Error, your submission has been added to the queue";
                                }

                                if (response != null && response.code() == 400) {
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
                            }
                        });
                    }
                }
            }
        }).start();
    }


    private void sendToServer() {

        if (!CommonUtils.isNetworkAvailable(context)) {
            String title = "Submission Error";
            String message = "Internet connection is not available. Please check your internet connection. Your request is submitted in Queue.";
            DBHandler.getInstance().setSubmissionQueued(submission);
            showErrorDialog(title, message, false);
            return;
        }

        listener.showProgressBar();
        new Thread(new Runnable() {
            @Override
            public void run() {


                final Response response = new ConnectionHelper(context).
                        submitForm(screen.getUrl(), screen.getPhotoUrl(),
                                submission, getChildFragmentManager());


                if (response != null) {
                    if (!response.isSuccessful()) {
                        if (response.code() != 400) {
                            DBHandler.getInstance().setSubmissionQueued(submission);
                        }
                    } else {
                        DBHandler.getInstance().removeAnswers(submission);
                        JobModuleStatus jobModuleStatus = new JobModuleStatus();
                        jobModuleStatus.setStatus(true);
                        jobModuleStatus.setJobId(jobID);
                        jobModuleStatus.setModuleName(formTitle);
                        DBHandler.getInstance().replaceData(JobModuleStatus.DBTable.NAME,
                                jobModuleStatus.toContentValues());


                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.hideProgressBar();
                        String title = "Success";
                        String message = "Submission was successful";
                        if (response != null && response.isSuccessful()) {
                            if (screen.getTitle().equalsIgnoreCase("Book Off")) {
                                AppPreferences.putString(jobID + "_" + Constants.IS_BOOK_ON, null);
                            } else if (screen.getTitle().equalsIgnoreCase("Book On")) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                                Date d = new Date();
                                AppPreferences.putString(jobID + "_" + Constants.IS_BOOK_ON, sdf.format(d));
                            } else if (submission.getJsonFileName().equalsIgnoreCase("risk_assessment.json")) {
                                getJobs();
                            }
                        }

                        if (response == null || !response.isSuccessful()) {
                            title = "Submission Error";
                            message = "Submission Error, your submission has been added to the queue";
                        }

                        if (response != null && response.code() == 400) {

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
                    }
                });
            }
        }).start();
    }

    private void getJobs() {

        listener.showProgressBar();
        APICalls.getJobList(user.gettoken()).enqueue(new Callback<JobResponse>() {
            @Override
            public void onResponse(@NonNull Call<JobResponse> call, @NonNull retrofit2.Response<JobResponse> response) {


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
    public void openCamera(long submissionId, FormItem formItem , int repeatCount) {
        Intent intent = new Intent(context, CameraActivity.class);
        intent.putExtra(CameraActivity.ARG_FORM_ITEM, formItem);
        intent.putExtra(CameraActivity.ARG_SUBMISSION_ID, submissionId);
        intent.putExtra(CameraActivity.ARG_REPEAT, repeatCount);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    public void openSignature(FormItem formItem, long submissionId, int repeatCount) {
        Intent intent = new Intent(context, SignatureActivity.class);
        intent.putExtra(SignatureActivity.ARG_FORM_ITEM, formItem);
        intent.putExtra(SignatureActivity.ARG_SUBMISSION_ID, submissionId);
        intent.putExtra(SignatureActivity.ARG_REPEAT_COUNT, repeatCount);
        startActivityForResult(intent, SIGNATURE_REQUEST);
    }

    @Override
    public void openForkFragment(FormItem formItem, long submissionId, int repeatCount) {
        listener.showBtnContainer(false);
        ForkFormFragment fragment = ForkFormFragment.newInstance(submission, user, formItem, repeatCount);
        listener.addFragment(fragment);
    }

    @Override
    public void openFragment(Fragment fragment) {
        listener.showBtnContainer(false);
        listener.addFragment(fragment);
    }

    @Override
    public void showBottomSheet(BottomSheetDialogFragment dialogFragment) {
        dialogFragment.show(getChildFragmentManager(), dialogFragment.getClass().getSimpleName());
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
                Intent intent = new Intent(context, FragmentStoreDetail.class);
                intent.putExtra("barcode_result", data.getStringExtra("barcode_result"));
                intent.putExtra("StaId", data.getStringExtra("StaId"));
                startActivityForResult(intent, STORE_DETAIL_REQUEST);
            } else if (requestCode == STORE_DETAIL_REQUEST) {

                ArrayList<Answer> answers = DBHandler.getInstance().getRepeatedAnswers(submission.getID(), "StaStockItemId", "Items");
                if (answers != null) {
                    String stockItemId = data.getStringExtra("stockItemId");
                    int count = answers.size();

                    for(Answer a : answers ){
                        if(!TextUtils.isEmpty(a.getAnswer()) && a.getAnswer().equalsIgnoreCase(stockItemId)){
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


            }

        }
    }

    public void showErrorDialog(String title, String message, boolean isSuccessful) {
        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        ((Activity) context).setResult(Activity.RESULT_OK);
                        ((Activity) context).finish();

                    }
                })
                .build();

        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "_ERROR_DIALOG");
    }


    public void requestPermissions(@NonNull final String[] permissions, String permissionRequestRationale) {
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
                        .setPositiveButton(getString(R.string.permission_continue), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                requestPermissions(permissions, INTENT_PERMISSION);
                            }
                        })
                        .setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
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
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);


            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {
                                listener.onSuccess(location);

                            } else {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                listener.onFailure();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.onFailure();
                            Log.d("ADD JOB", "Error trying to get last GPS location");
                            e.printStackTrace();
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

            } else {
                FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // GPS location can be null if GPS is switched off
                                if (location != null) {
                                    try {
                                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                        if (addresses != null && addresses.size() > 0) {
                                            String addressLine = addresses.get(0).getAddressLine(0);
//                                            etLocation.setText(addressLine);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("ADD JOB", "Error trying to get last GPS location");
                                e.printStackTrace();
                            }
                        });


            }
        } else {
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context, R.style.DialogTheme)
                    .setTitle(getString(R.string.permission_denied))
                    .setMessage(getString(R.string.permissions_location_failure))
                    .setPositiveButton(getString(R.string.permissions_settings), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", ((Activity) context).getApplication().getPackageName(), null);
                            intent.setData(uri);

                            startActivity(intent);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(getString(R.string.generic_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            dialog.show();
        }
    }

    @Override
    public void startActivityForResultFromAdapter(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }
}
