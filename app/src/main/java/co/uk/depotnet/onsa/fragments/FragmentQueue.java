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

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.activities.PollingSurveyActivity;
import co.uk.depotnet.onsa.adapters.OfflineQueueAdapter;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.fragments.store.ReceiptItemsFragment;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.JobModuleStatus;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.Form;
import co.uk.depotnet.onsa.modals.forms.Screen;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.responses.JobResponse;
import co.uk.depotnet.onsa.modals.store.MyStore;
import co.uk.depotnet.onsa.modals.store.Receipts;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.networking.ConnectionHelper;
import co.uk.depotnet.onsa.networking.Constants;
import co.uk.depotnet.onsa.utils.AppPreferences;
import co.uk.depotnet.onsa.utils.JsonReader;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;
import co.uk.depotnet.onsa.views.MaterialAlertDialog;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


public class FragmentQueue extends Fragment implements OfflineQueueAdapter.QueueListener {

    private ArrayList<Submission> submissions;
    private Context context;
    private OfflineQueueAdapter adapter;
    private FragmentActionListener listener;
    private User user;
    private Handler handler;

    public static FragmentQueue newInstance() {
        FragmentQueue fragment = new FragmentQueue();
        Bundle args = new Bundle();
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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        submissions.clear();
        submissions.addAll(DBHandler.getInstance().getQueuedSubmissions());
        adapter.notifyDataSetChanged();
    }

    public void showErrorDialog(String title, String message) {
        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), (dialog1, i) -> dialog1.dismiss())
                .build();

        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "_ERROR_DIALOG");
    }

    public void showQueue(final int position , final String title, String message, final Submission submission) {
        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.submit), (dialog1, i) -> {
                    String json = submission.getJsonFileName();

                    dialog1.dismiss();
                    if (!TextUtils.isEmpty(json) && (json.startsWith("store_log_"))) {
                        ArrayList<Answer> answers = DBHandler.getInstance().getRepeatedAnswers(submission.getID() , "StaStockItemId" ,
                                json.equalsIgnoreCase("store_log_issue.json") ? null: "Items");
                        if(answers.isEmpty()){
                            return;
                        }

                        HashMap<String , Object> map = new HashMap<>();

                        for (Answer a : answers){
                            if(a != null && !TextUtils.isEmpty(a.getAnswer())) {
                                Answer answer = DBHandler.getInstance().getAnswer(submission.getID(),
                                        "Quantity",
                                        a.getRepeatID(), a.getRepeatCount());
                                if(answer != null) {
                                    MyStore store = DBHandler.getInstance().getMyStores(a.getAnswer());
                                    if(store != null) {
                                        map.put(a.getAnswer(), store);
                                        map.put(a.getAnswer() + "_qty", Integer.parseInt(answer.getAnswer()));
                                    }
                                }
                            }
                        }

                        Intent intent = new Intent(context, FormActivity.class);
                        intent.putExtra(FormActivity.ARG_USER, DBHandler.getInstance().getUser());
                        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
                        intent.putExtra(FormActivity.ARG_MY_STORE_ITEMS, map);
                        startActivityForResult(intent, 1000);

                    }else if (!TextUtils.isEmpty(title) && (title.equalsIgnoreCase("receipt_accept") ||
                            title.equalsIgnoreCase("receipt_reject"))) {
                        Answer answer = DBHandler.getInstance().getAnswer(submission.getID(), "MyReceiptID",
                                null, 0);

                        if (answer != null) {
                            Receipts receipts = DBHandler.getInstance().getReceipt(answer.getAnswer());
                            listener.addFragment(ReceiptItemsFragment.newInstance(receipts, user, submission.getID()),
                                    false);
                        }

                    } else if (TextUtils.isEmpty(title) || !title.equalsIgnoreCase("poling_survey")) {
                        Intent intent = new Intent(context, FormActivity.class);
                        intent.putExtra(FormActivity.ARG_USER, user);
                        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
                        startActivity(intent);
                    } else {
//                        sendToServer(position , submission);
                            Intent intent = new Intent(context, PollingSurveyActivity.class);
                            intent.putExtra(FormActivity.ARG_USER, user);
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

        showQueue(position , submission.getTitle(),
                "Do you want to submit this information to server?", submission);

    }


    private void sendToServer(int position , Submission submission) {
        String jobID = submission.getJobID();
        String jsonFileName = submission.getJsonFileName();

        if(TextUtils.isEmpty(jsonFileName)){
            return;
        }
        Form form = JsonReader.loadForm(context, submission.getJsonFileName());
        if(form == null){
            return;
        }
        String formTitle = form.getTitle();
        ArrayList<Screen> screens = form.getScreens();
        if(screens == null || screens.isEmpty()){
            return;
        }

        Screen screen = screens.get(screens.size()-1);

        if (!CommonUtils.isNetworkAvailable(context)) {
            String title = "Submission Error";
            String message = "Internet connection is not available. Please check your internet connection. Your request is submitted in Queue.";
            DBHandler.getInstance().setSubmissionQueued(submission);
            showErrorDialog(position , title, message, false);
            return;
        }

        listener.showProgressBar();
        new Thread(() -> {
            final Response response = new ConnectionHelper(context).
                    submitForm(screen.getUrl(), screen.getPhotoUrl(),
                            submission, getChildFragmentManager());


            if (response != null) {
                if (!response.isSuccessful()) {
                    if (response.code() != 400) {
                        DBHandler.getInstance().setSubmissionQueued(submission);
                    }
                } else {

                    if (submission.getJsonFileName().equalsIgnoreCase("finish_on_site.json")) {
                        Answer answer = DBHandler.getInstance().getAnswer(submission.getID(), "rfna", null, 0);
                        if (answer != null && !TextUtils.isEmpty(answer.getAnswer()) && answer.getAnswer().equalsIgnoreCase("true")) {
                            Response response1 = new ConnectionHelper(context).sendRfna(submission);

                            if (response1 != null && response1.isSuccessful()) {
                                JobModuleStatus status = new JobModuleStatus();
                                status.setStatus(true);
                                status.setJobId(jobID);
                                status.setModuleName("RFNA");
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


                }
            }
            handler.post(() -> {
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

                if (!submission.getJsonFileName().equalsIgnoreCase("take_photo.json") &&
                        (response == null || !response.isSuccessful())) {
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
                showErrorDialog(position , title, message, response != null && response.isSuccessful());
            });
        }).start();
    }

    public void showErrorDialog(int position , String title, String message, boolean isSuccessful) {
        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), (dialog1, i) -> {
                    dialog1.dismiss();
                    if(isSuccessful){
                        Submission submission = submissions.remove(position);
                        DBHandler.getInstance().removeAnswers(submission);
                        adapter.notifyDataSetChanged();
                    }
//                    ((Activity) context).setResult(Activity.RESULT_OK);
//                    ((Activity) context).finish();

                })
                .build();

        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "_ERROR_DIALOG");
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

}
