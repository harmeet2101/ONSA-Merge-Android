package co.uk.depotnet.onsa.activities.timesheet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.activities.ThemeBaseActivity;
import co.uk.depotnet.onsa.adapters.timesheet.LohHoursAdapter;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.dialogs.StatusDayDecorator;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.timesheet.TimeSheet;
import co.uk.depotnet.onsa.modals.timesheet.TimeSheetHours;
import co.uk.depotnet.onsa.modals.timesheet.TimeSheetResponse;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CallUtils;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.AppPreferences;
import co.uk.depotnet.onsa.utils.Utils;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimeSheetActivity extends ThemeBaseActivity implements View.OnClickListener, OnDateSelectedListener, OnMonthChangedListener {
    private static final int LOG_HOURS = 1;
    private static final int SUBMIT_TIME_SHEET = 2;
    private MaterialCalendarView calendarView;
    private TextView txtDailyTotalHours;
    private LinearLayout llUiBlocker;
    private LohHoursAdapter adapter;
    private Date currentDate;
    private Date weekDate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timesheet);
        calendarView = findViewById(R.id.calendar_view);
        txtDailyTotalHours = findViewById(R.id.txt_daily_log_hours);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        llUiBlocker = findViewById(R.id.ll_ui_blocker);

        Date date = Calendar.getInstance().getTime();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH , -2);

        calendarView.state().edit()
                .setMinimumDate(c.getTime())
                .setMaximumDate(date)
                .setFirstDayOfWeek(CommonUtils.getWeekCommencingDayInt()).commit();

        calendarView.setOnDateChangedListener(this);
        calendarView.setOnMonthChangedListener(this);
        currentDate = date;

        c = Calendar.getInstance();
        c.setFirstDayOfWeek(CommonUtils.getWeekCommencingDayInt());
//        c.add(Calendar.DAY_OF_WEEK , c.getFirstDayOfWeek());
//
        int weekDay = c.get(Calendar.DAY_OF_WEEK);
        int daySubstract = weekDay - c.getFirstDayOfWeek();
        if(daySubstract < 0){
            daySubstract += 7;
        }
        c.add(Calendar.DAY_OF_MONTH , -daySubstract);
        weekDate = c.getTime();
        calendarView.setSelectedDate(date);


        findViewById(R.id.ll_log_hours).setOnClickListener(this);
        findViewById(R.id.ll_submit_timesheet).setOnClickListener(this);
        findViewById(R.id.btn_img_cancel).setOnClickListener(this);

        adapter = new LohHoursAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this , LinearLayoutManager.VERTICAL , false));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(10));
        recyclerView.setAdapter(adapter);
        if(CommonUtils.isNetworkAvailable(this)) {
            getTimeSheets(weekDate);
        }
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
        AppPreferences.setTheme(ThemeBaseActivity.THEME_TIME_SHEET);
        String jsonFileName = formType == LOG_HOURS ? "timesheet_log_hours.json" : "timesheet_submit_timesheet.json";
        String title = formType == LOG_HOURS ? "Log Hours" : "Submit Timesheet";
        Submission submission = new Submission(jsonFileName, title, "");
        long submissionID = dbHandler.insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);

        Intent intent = new Intent(this, FormActivity.class);

        Answer answer = new Answer(submissionID , "selected_date" , null , 0);
        answer.setAnswer(Utils.formatDate(currentDate , "yyyy-MM-dd'T'HH:mm:ss"));
        answer.setDisplayAnswer(Utils.formatDate(currentDate , "dd/MM/yyyy"));
        answer.setShouldUpload(false);
        dbHandler.replaceData(Answer.DBTable.NAME , answer.toContentValues());

        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivityForResult(intent , 10001);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        currentDate = date.getDate();
        adapter.update(DBHandler.getInstance().getTimeSheetLogHours(Utils.formatDate(currentDate , "yyyy-MM-dd'T'HH:mm:ss")));
        txtDailyTotalHours.setText(adapter.getTotalHoursText());
    }


    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        currentDate = date.getDate();
        weekDate = date.getDate();
        getTimeSheetHours(Utils.formatDate(currentDate , "yyyy-MM-dd'T'HH:mm:ss"));
    }


    private void showProgressBar() {
        llUiBlocker.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    private void hideProgressBar() {
        llUiBlocker.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void getTimeSheetHours(String weekCommencing){
        if(TextUtils.isEmpty(weekCommencing)){
            return;
        }

        adapter.update(new ArrayList<>());
        txtDailyTotalHours.setText(adapter.getTotalHoursText());


        showProgressBar();
        User user = dbHandler.getUser();

        CallUtils.enqueueWithRetry(APICalls.getTimesheetHours(user.gettoken() , weekCommencing) , new Callback<TimeSheetHours>() {
            @Override
            public void onResponse(@NonNull Call<TimeSheetHours> call, @NonNull Response<TimeSheetHours> response) {
                if(response.isSuccessful()){
                    TimeSheetHours timeSheetHours = response.body();
                    if(timeSheetHours != null && !timeSheetHours.isEmpty()) {
                        timeSheetHours.setWeekCommencing();
                        timeSheetHours.toContentValues();
                        adapter.update(DBHandler.getInstance().getTimeSheetLogHours(Utils.formatDate(currentDate , "yyyy-MM-dd'T'HH:mm:ss")));

                        calendarView.removeDecorators();
                        HashMap<String, String> map = dbHandler.getTimeSheetLogHoursStatusByWeek(weekCommencing);

                        Set<String> keySet = map.keySet();
                        if(!keySet.isEmpty()) {
                            for (String key : keySet) {
                                String date = key;
                                if(!TextUtils.isEmpty(key)){
                                    date = key.split("T")[0];
                                }
                                calendarView.addDecorator(new StatusDayDecorator(TimeSheetActivity.this, date, map.get(key)));
                            }
                        }
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

    private void getTimeSheets(Date weekDate){
        showProgressBar();
        User user = dbHandler.getUser();

        CallUtils.enqueueWithRetry(APICalls.getTimesheets(user.gettoken()), new Callback<TimeSheetResponse>() {
            @Override
            public void onResponse(@NonNull Call<TimeSheetResponse> call, @NonNull Response<TimeSheetResponse> response) {
                if(response.isSuccessful()){
                    TimeSheetResponse timeSheetResponse = response.body();
                    if(timeSheetResponse != null && !timeSheetResponse.isEmpty()) {
                        timeSheetResponse.toContentValues();
                    }
                }
                getTimeSheetHours(Utils.formatDate(weekDate , "yyyy-MM-dd'T'HH:mm:ss"));
            }

            @Override
            public void onFailure(@NonNull Call<TimeSheetResponse> call, @NonNull Throwable t) {
                getTimeSheetHours(Utils.formatDate(weekDate , "yyyy-MM-dd'T'HH:mm:ss"));
                hideProgressBar();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10001 && resultCode == RESULT_OK){
            if(CommonUtils.isNetworkAvailable(this)) {
                getTimeSheets(weekDate);
            }
        }
    }
}