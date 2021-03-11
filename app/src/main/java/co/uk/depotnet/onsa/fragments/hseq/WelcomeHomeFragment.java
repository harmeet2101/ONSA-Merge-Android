package co.uk.depotnet.onsa.fragments.hseq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.activities.MainActivity;
import co.uk.depotnet.onsa.activities.SettingsActivity;
import co.uk.depotnet.onsa.activities.timesheet.TimeSheetActivity;
import co.uk.depotnet.onsa.activities.ui.BriefingsActivity;
import co.uk.depotnet.onsa.activities.ui.HSEQActivity;
import co.uk.depotnet.onsa.activities.WelcomeActivity;
import co.uk.depotnet.onsa.adapters.WelcomeHomeAdapter;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.fragments.store.FragmentStore;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.responses.JobResponse;
import co.uk.depotnet.onsa.modals.store.DataMyRequests;
import co.uk.depotnet.onsa.modals.store.DataReceipts;
import co.uk.depotnet.onsa.modals.timesheet.TimeSheet;
import co.uk.depotnet.onsa.modals.timesheet.TimeSheetResponse;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.networking.Constants;
import co.uk.depotnet.onsa.utils.AppPreferences;
import co.uk.depotnet.onsa.utils.Utils;
import co.uk.depotnet.onsa.views.MaterialAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomeHomeFragment extends Fragment implements WelcomeHomeAdapter.OnItemClickListener {

    private User user;
    private int apiCounter;
    private Context context;
    private FragmentActionListener listener;
    private DBHandler dbHandler;

    public static WelcomeHomeFragment newInstance() {
        WelcomeHomeFragment fragment = new WelcomeHomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        listener = (FragmentActionListener)context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = DBHandler.getInstance(context);
        user = dbHandler.getUser();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mRecyclerView = view.findViewById(R.id.welcome_home_recycler);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context,2));
        WelcomeHomeAdapter mAdapter = new WelcomeHomeAdapter(context, this);
        view.findViewById(R.id.btn_img_settings).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });
        mRecyclerView.setAdapter(mAdapter);
    }



    @Override
    public void onResume() {
        super.onResume();
        apiCounter = 0;
        getJobs();
        if(Constants.isStoreEnabled && CommonUtils.isNetworkAvailable(context)) {
            listener.showProgressBar();
            getMyRequests();
            getReceipts();
        }
    }

    private void getJobs() {
        if(!CommonUtils.validateToken(context)){
            return;
        }

        APICalls.getJobList(user.gettoken()).enqueue(new Callback<JobResponse>() {
            @Override
            public void onResponse(@NonNull Call<JobResponse> call, @NonNull Response<JobResponse> response) {
                apiCounter++;
                if(CommonUtils.onTokenExpired(context , response.code())){
                    return;
                }
                if (response.isSuccessful()) {
                    dbHandler.resetJobs();
                    JobResponse jobResponse = response.body();
                    if (jobResponse != null){
                        List<Job> jobs = jobResponse.getJobs();
                        if (jobs != null && !jobs.isEmpty()) {
                            for (Job j : jobs) {
                                dbHandler.replaceData(Job.DBTable.NAME, j.toContentValues());
                            }
                        }
                    }
                }

                onApiCallResponse();
            }

            @Override
            public void onFailure(@NonNull Call<JobResponse> call, @NonNull Throwable t) {
                apiCounter++;
                onApiCallResponse();
            }
        });
    }


    @Override
    public void onItemClick(int WH_id) {
        switch (WH_id)
        {
            case 1:
             //   if(isBookOn()) {
                    AppPreferences.setTheme(5);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
              /*  }else{
                    showBookOnDialog();
                }*/
                break;
            case 2:
                AppPreferences.setTheme(1);
                Intent hseq = new Intent(context, HSEQActivity.class);
                startActivity(hseq);
                break;
            case 3:
                Utils.store_call = true;
                FragmentStore fragmentStore = FragmentStore.newInstance();
                ((WelcomeActivity)context).addFragment(fragmentStore, false);
                break;
            case 4:
                AppPreferences.setTheme(2);
                Intent brief = new Intent(context, BriefingsActivity.class);
                startActivity(brief);
                break;
            case 5:
                getTimeSheets();
                break;
            case 6:
                AppPreferences.setTheme(2);
                openIncident();
                break;
            default:
                break;
        }
    }



    private void openIncident(){
        Submission submission = new Submission("incident.json", "Incident", "");
        long submissionID = dbHandler.insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);

        Answer answer = dbHandler.getAnswer(submissionID, "isUniqueIncident",
                null, 0);

        if (answer == null) {
            answer = new Answer(submissionID, "isUniqueIncident");
            answer.setAnswer("false");
            answer.setRepeatID(null);
            answer.setRepeatCount(0);

            dbHandler.replaceData(Answer.DBTable.NAME, answer.toContentValues());
        }

        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }

    private void getTimeSheets() {
        listener.showProgressBar();

        APICalls.getTimesheets(user.gettoken()).enqueue(new Callback<TimeSheetResponse>() {
            @Override
            public void onResponse(@NonNull Call<TimeSheetResponse> call, @NonNull Response<TimeSheetResponse> response) {
               boolean isRejected = false;
                String title = "Timesheet Rejected";
                String message = "";
                String key = "";

                if (response.isSuccessful()) {
                    TimeSheetResponse timeSheetResponse = response.body();
                    if (timeSheetResponse != null && !timeSheetResponse.isEmpty()) {
                        timeSheetResponse.toContentValues();
                        ArrayList<TimeSheet> timeSheets = timeSheetResponse.getTimesheets();

                        for (int i = 0 ; i < timeSheets.size() ; i++){
                            TimeSheet timeSheet = timeSheets.get(i);
                            if(!TextUtils.isEmpty(timeSheet.getRejectionReason())){
                                key += timeSheet.getWeekCommencing();
                                isRejected = true;
                                String dateValue = timeSheet.getWeekCommencing();
                                Date date = Utils.parseDate(dateValue , "yyyy-MM-dd'T'hh:mm:ss");
                                if(date != null){
                                    dateValue = Utils.formatDate(date , "dd/MM/yyyy");
                                }
                                message += "Date - "+dateValue+"\n";
                                message += "Reason - "+timeSheet.getRejectionReason()+"\n\n\n";
                            }
                        }
                    }
                }

                if(!isRejected || AppPreferences.getInt(key ,0) == 1){
                    AppPreferences.setTheme(5);
                    Intent timeSheet = new Intent(context, TimeSheetActivity.class);
                    startActivity(timeSheet);
                }else{
                    AppPreferences.putInt(key , 1);
                    showErrorDialog(title , message);
                }
                listener.hideProgressBar();
            }

            @Override
            public void onFailure(@NonNull Call<TimeSheetResponse> call, @NonNull Throwable t) {
                listener.hideProgressBar();
                AppPreferences.setTheme(5);
                Intent timeSheet = new Intent(context, TimeSheetActivity.class);
                startActivity(timeSheet);
            }
        });
    }

    /*private boolean isBookOn() {
        if(!Constants.isTimeSheetEnabled){
            return true;
        }
        if(user != null && !user.isCompleteTimesheets()){
            return true;
        }
        String book_on_date = AppPreferences.getString( "TimeSheet_" + Constants.IS_BOOK_ON, null);
        if (TextUtils.isEmpty(book_on_date)) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date;
        Date currentDate;
        try {
            date = sdf.parse(book_on_date);
            currentDate = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            return false;
        }

        if(date == null || currentDate == null){
            return false;
        }
        return date.compareTo(currentDate) == 0;
    }*/

    /*private void showBookOnDialog(){
        String title = "Book On";
        String message = "Please Book on before viewing any jobs";
        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), (dialog1, i) -> {
                    listener.hideProgressBar();
                    dialog1.dismiss();
                    Submission submission = new Submission("my_work_book_on.json","Timesheet Book On", "");
                    long submissionID = dbHandler.insertData(Submission.DBTable.NAME, submission.toContentValues());
                    submission.setId(submissionID);
                    Intent intent = new Intent(context, FormActivity.class);
                    intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
                    startActivityForResult(intent , 1000);
                }).setNegative(getString(R.string.generic_cancel), (dialog12, which) -> dialog12.dismiss())
                .build();

        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "_ERROR_DIALOG");

    }*/

    private void showErrorDialog(String title , String message){

        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), (dialog1, i) -> {
                    dialog1.dismiss();
                    AppPreferences.setTheme(5);
                    Intent timeSheet = new Intent(context, TimeSheetActivity.class);
                    startActivity(timeSheet);
                })
                .build();

        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "_ERROR_DIALOG");

    }


    private void getReceipts(){
        if(!CommonUtils.validateToken(context)){
            return;
        }
        APICalls.getReceipts(user.gettoken()).enqueue(new Callback<DataReceipts>() {
            @Override
            public void onResponse(@NonNull Call<DataReceipts> call, @NonNull Response<DataReceipts> response) {
                apiCounter++;
                if(CommonUtils.onTokenExpired(context , response.code())){
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    response.body().toContentValues();
                }
                onApiCallResponse();
            }

            @Override
            public void onFailure(@NonNull Call<DataReceipts> call, @NonNull Throwable t) {
                onApiCallResponse();
            }
        });
    }

    private void onApiCallResponse(){
        if(apiCounter == 3 || (!Constants.isStoreEnabled && apiCounter == 1)){
            listener.hideProgressBar();
        }
    }


    private void getMyRequests(){
        if(!CommonUtils.validateToken(context)){
            return;
        }
        APICalls.getMyRequests(user.gettoken()).enqueue(new Callback<DataMyRequests>() {
            @Override
            public void onResponse(@NonNull Call<DataMyRequests> call, @NonNull Response<DataMyRequests> response) {
                apiCounter++;
                if(CommonUtils.onTokenExpired(context , response.code())){
                    return;
                }

                if(response.isSuccessful()){
                    DataMyRequests dataMyRequests = response.body();
                    if(dataMyRequests != null){
                        dbHandler.resetMyRequest();
                        dataMyRequests.toContentValues();
                    }
                }
                onApiCallResponse();
            }

            @Override
            public void onFailure(@NonNull Call<DataMyRequests> call, @NonNull Throwable t) {
                onApiCallResponse();
            }
        });
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == Activity.RESULT_OK){
            if(isBookOn()) {
                AppPreferences.setTheme(5);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        }
    }*/
}

