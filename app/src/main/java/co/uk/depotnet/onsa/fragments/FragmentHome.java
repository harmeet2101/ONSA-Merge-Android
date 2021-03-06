package co.uk.depotnet.onsa.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.DateFormatTitleFormatter;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import co.uk.depotnet.onsa.BuildConfig;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.activities.KeywordListActivity;
import co.uk.depotnet.onsa.activities.MainActivity;
import co.uk.depotnet.onsa.activities.PhotoActivity;
import co.uk.depotnet.onsa.activities.RiskAssessmentActivity;
import co.uk.depotnet.onsa.activities.SettingsActivity;
import co.uk.depotnet.onsa.activities.SurveyActivity;
import co.uk.depotnet.onsa.activities.WorkLogActivity;
import co.uk.depotnet.onsa.adapters.HomeAdapter;
import co.uk.depotnet.onsa.adapters.JobTagAdapter;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.listeners.HomeJobListListener;
import co.uk.depotnet.onsa.modals.ItemType;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.responses.JobResponse;
import co.uk.depotnet.onsa.modals.store.DataMyStores;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CallUtils;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.networking.ConnectionHelper;
import co.uk.depotnet.onsa.networking.Constants;
import co.uk.depotnet.onsa.utils.DayEnableDecorator;
import co.uk.depotnet.onsa.utils.HorizontalSpaceItemDecoration;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;
import co.uk.depotnet.onsa.views.DisclaimerDialog;
import co.uk.depotnet.onsa.views.MaterialAlertDialog;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentHome extends Fragment implements HomeJobListListener,
        OnDateSelectedListener, View.OnClickListener{

    private static final int STORAGE_PERMISSION_CODE = 1;
    private final DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);

    private List<Job> jobs;
    private List<Job> originalJobs;
    private HomeAdapter adapter;
    private JobTagAdapter jobTagAdapter;
    private Context context;
    private FragmentActionListener listener;
    private User user;
    private TextView txtToolbarTitle;
    private ImageView btnImageSearch;
    private ImageView btnImageSettings;
    private SwipeRefreshLayout refreshLayout;

    private SearchView searchView;
    private MaterialCalendarView calendarView;
    private ArrayList<ItemType> jobTags;
    private Date selectedDate;
    private boolean isRefreshing;
    private DBHandler dbHandler;


    public static FragmentHome newInstance() {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dbHandler = DBHandler.getInstance(context);
        user = dbHandler.getUser();
        originalJobs = new ArrayList<>();
        jobs = new ArrayList<>();

        jobTags = new ArrayList<>();


        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        selectedDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            selectedDate = sdf.parse(sdf.format(calendar.getTime()));
        }catch (Exception ex){
            ex.printStackTrace();
        }

        jobTagAdapter = new JobTagAdapter(context, jobTags, position -> filterOnDate(selectedDate));
        adapter = new HomeAdapter(context, jobs, this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_list_job, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        txtToolbarTitle = view.findViewById(R.id.txt_toolbar_title);
        btnImageSearch = view.findViewById(R.id.btn_img_search);

        btnImageSearch.setOnClickListener(this);
        btnImageSettings = view.findViewById(R.id.btn_img_settings);
        btnImageSettings.setOnClickListener(this);
        ImageView btnImageCancel = view.findViewById(R.id.btn_img_cancel);
        btnImageCancel.setOnClickListener(this);
        btnImageCancel.setVisibility(View.GONE);
        searchView = view.findViewById(R.id.simpleSearchView);
        btnImageSettings.setVisibility(View.VISIBLE);


        int searchCloseButtonId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = this.searchView.findViewById(searchCloseButtonId);
        closeButton.setImageResource(R.drawable.ic_close_white);

        int id = searchView.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);
        textView.setHintTextColor(Color.WHITE);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        closeButton.setOnClickListener(v -> closeSearch());

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(20);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
        refreshLayout = view.findViewById(R.id.swipe_container);
        RecyclerView filterRecyclerView = view.findViewById(R.id.filter_recycler_view);
        filterRecyclerView.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false));
        HorizontalSpaceItemDecoration horizontalSpaceItemDecoration = new HorizontalSpaceItemDecoration(20);
        filterRecyclerView.addItemDecoration(horizontalSpaceItemDecoration);

        filterRecyclerView.setAdapter(jobTagAdapter);

        calendarView = view.findViewById(R.id.calendarView);
        initCalendar();

        view.findViewById(R.id.btn_add_keyword).setOnClickListener(v -> {

            Intent intent = new Intent(context, KeywordListActivity.class);
            intent.putParcelableArrayListExtra("keywords", jobTags);
            startActivityForResult(intent, 1000);
        });

        refreshLayout.setOnRefreshListener(() -> {
            if(!isRefreshing) {
                isRefreshing = true;
                fetchData();
            }
        });

        return view;
    }

    private void initCalendar() {
        ArrayList<CalendarDay> enabledDates = new ArrayList<>();
        calendarView.setHeaderTextAppearance(R.style.TextAppearance_MyHeader);
        calendarView.setWeekDayTextAppearance(R.style.TextAppearance_MyWeekDay);
        calendarView.setDateTextAppearance(R.style.TextAppearance_MyWeekDay);
        calendarView.setTitleFormatter(new DateFormatTitleFormatter(dateFormat));

        calendarView.setDayFormatter(day -> {
            return day.getDay() + "";
        });
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
        calendarView.setOnDateChangedListener(this);

        calendarView.setLeftArrowMask(null);
        calendarView.setRightArrowMask(null);
        calendarView.setTopbarVisible(false);

        Date date = new Date();
        Calendar instance1 = Calendar.getInstance();
        calendarView.setSelectedDate(instance1.getTime());


        int noOfDaysInMonth = instance1.getActualMaximum(Calendar.DAY_OF_MONTH);
        SimpleDateFormat formatter = new SimpleDateFormat("dd", Locale.ENGLISH);
        String dateCurrent = formatter.format(date);
        int cDay = instance1.get(Calendar.DAY_OF_WEEK);
        int firstDay = (Integer.parseInt(dateCurrent) - (cDay - 1));
        instance1.set(instance1.get(Calendar.YEAR),
                instance1.get(Calendar.MONTH), firstDay);


        Calendar instance2 = Calendar.getInstance();
        int lastDay = getShowMaxDate(firstDay, noOfDaysInMonth);
        int nextMonth = instance2.get(Calendar.MONTH);

        if (lastDay < firstDay) {
            nextMonth += 1;

            if (nextMonth >= 12) {
                nextMonth = 12 - nextMonth;
            }
        }

        int currentYear = instance2.get(Calendar.YEAR);

        if (nextMonth < instance2.get(Calendar.MONTH)) {
            currentYear++;
        }


        instance2.set(currentYear, nextMonth, lastDay);

        calendarView.addDecorator(new DayEnableDecorator(context, enabledDates));
        calendarView.state().edit()
                .setMinimumDate(instance1.getTime())
                .setMaximumDate(instance2.getTime())
                .commit();



    }

    private int getShowMaxDate(int lowestDate, int maxMonthDate) {
        int resultDate = lowestDate + 14;
        if (resultDate > maxMonthDate) {
            resultDate = resultDate - maxMonthDate;
        }

        return resultDate - 1;
    }

    @Override
    public void onResume() {
        super.onResume();
        String title = user.getuserName();
        String gangRef = user.getroleName();
        if (gangRef != null && !gangRef.isEmpty()) {
            title += "(" + gangRef + ")";
        }
        if (txtToolbarTitle != null) {
            txtToolbarTitle.setText(title);
        }

        if (jobs!=null && jobs.size() > 0){
            jobs.clear();
        }
        fetchData();
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
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void openJobDetail(Job job) {

        FragmentJobDetail fragmentJobDetail = FragmentJobDetail.newInstance(job);
        listener.addFragment(fragmentJobDetail, false);

//        if(taskCounter == 0) {
//            getSiteActivityTasks(job, Constants.TYPE_ID_SERVICE_MATERAL , true);
//            getSiteActivityTasks(job, Constants.TYPE_ID_MUCKAWAY, true);
//            getSiteActivityTasks(job, Constants.TYPE_ID_BACKFILL, true);
//            getSiteActivityTasks(job, Constants.TYPE_ID_REINSTATEMENT, true);
//            getSiteActivityTasks(job, Constants.TYPE_ID_SITE_CLEAR, true);
//        }
    }

    @Override
    public void openWorkLog(final Job job) {

        Intent intent = new Intent(context, WorkLogActivity.class);
        intent.putExtra(WorkLogActivity.ARG_JOB_ID, job.getjobId());
        intent.putExtra(WorkLogActivity.ARG_JOB_REFERENCE_NUMBER, job.getjobNumber());
        startActivity(intent);
//        if(taskCounter == 0) {
//            getSiteActivityTasks(job, Constants.TYPE_ID_SERVICE_MATERAL, false);
//            getSiteActivityTasks(job, Constants.TYPE_ID_MUCKAWAY, false);
//            getSiteActivityTasks(job, Constants.TYPE_ID_BACKFILL, false);
//            getSiteActivityTasks(job, Constants.TYPE_ID_REINSTATEMENT, false);
//            getSiteActivityTasks(job, Constants.TYPE_ID_SITE_CLEAR, false);
//        }

    }

    private Job jobPackJob;
    @Override
    public void openJobPack(Job job) {
        jobPackJob = job;
        checkStoragePermission(job);
    }

    private void checkStoragePermission(Job job) {

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE);

            }
        } else {
            FragmentJobPackList fragment = FragmentJobPackList.newInstance(job.getjobId());
            listener.addFragment(fragment, false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(jobPackJob != null) {
                    FragmentJobPackList fragment = FragmentJobPackList.newInstance(jobPackJob.getjobId());
                    listener.addFragment(fragment, false);
                }
            }
        }
    }

    @Override
    public void openSurvey(Job job) {
        Intent intent = new Intent(context, SurveyActivity.class);
        intent.putExtra(SurveyActivity.ARG_JOB_ID, job.getjobId());
        intent.putExtra(SurveyActivity.ARG_JOB_REFERENCE_NUMBER, job.getjobNumber());
        startActivity(intent);
    }

    @Override
    public void openVisitorAttendance(Job job) {
        String jsonFileName = "visitor_attendance.json";
        if(job.isSubJob()){
            jsonFileName = "sub_job_visitor_attendance.json";
        }
        Submission submission = new Submission(jsonFileName, "", job.getjobId());
        long submissionID = dbHandler.insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }

    @Override
    public void openTakePhotoAndVideo(Job job) {
        String jsonFileName = "take_photo.json";
        if(job.isSubJob()){
            jsonFileName = "sub_job_take_photo.json";
        }
        Submission submission = new Submission(jsonFileName, "Take Photo or Video", job.getjobId());
        long submissionID = dbHandler.insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }


    @Override
    public void openRiskAssessment(final Job job) {
        Intent intent = new Intent(context, RiskAssessmentActivity.class);
        intent.putExtra(RiskAssessmentActivity.ARG_JOB, job);
        startActivity(intent);
    }

    @Override
    public void onQualityCheck(Job job) {
        Submission submission = new Submission("quality_check.json", "Quality Check", job.getjobId());
        long submissionID = dbHandler.insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }

    @Override
    public void openSiteClear(Job job) {
        String prefix = job.isSubJob()?"sub_job_":"";
        String json = prefix+"job_site_clear.json";
        if(job.getSiteTasksCount() == 0){
            json = prefix+"job_site_clear_unscheduled.json";
        }
        Submission submission = new Submission(json, "Site Clear", job.getjobId());
        long submissionID = dbHandler.insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }

    public void search(String keyword) {

        if (keyword == null || keyword.isEmpty()) {
            Toast.makeText(getContext(), "Please enter text to search", Toast.LENGTH_SHORT).show();
            return;
        }

        listener.showProgressBar();

//        APICalls.searchJobs(user.gettoken(), keyword).enqueue(new Callback<List<Job>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<Job>> call, @NonNull Response<List<Job>> response) {
//                if (response.isSuccessful()) {
//                    List<Job> jobs = response.body();
//                    if (jobs != null && !jobs.isEmpty()) {
////                        adapter.update(jobs);
//                    }
//                }
//                listener.hideProgressBar();
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<Job>> call, Throwable t) {
//                listener.hideProgressBar();
//            }
//        });
    }

    public void clearSearch() {
        filterOnDate(selectedDate);

    }

    private void searchLocal(String keyword) {
        ArrayList<Job> tempJobs = new ArrayList<>();
        if (TextUtils.isEmpty(keyword)) {
            return;
        }

        for (Job job : originalJobs) {
            if (isFiltered(job, keyword)) {
                tempJobs.add(job);
            }
        }

        if (!tempJobs.isEmpty()) {
            adapter.update(tempJobs);
        }

    }

    private boolean isFiltered(Job job, String keyword) {
        keyword = keyword.toLowerCase();
        String jobNumber = job.getestimateNumber();

        if (TextUtils.isEmpty(jobNumber)) {
            jobNumber = "";
        } else {
            jobNumber = jobNumber.toLowerCase();
        }

        String exchange = job.getexchange();
        if (TextUtils.isEmpty(exchange)) {
            exchange = "";
        } else {
            exchange = exchange.toLowerCase();
        }

        String address = job.getpostCode();
        if (TextUtils.isEmpty(address)) {
            address = "";
        } else {
            address = address.toLowerCase();
        }

        return (!jobNumber.isEmpty() && jobNumber.contains(keyword)) ||
                (!exchange.isEmpty() && exchange.contains(keyword)) ||
                (!address.isEmpty() && address.contains(keyword));
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        int day, month, year;
        day = date.getDay();
        month = date.getMonth();
        year = date.getYear();
        String input_date = year + "-" + (month + 1) + "-" + day;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date currentDate = sdf.parse(input_date);
            selectedDate = currentDate;
            SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
            if(currentDate != null) {
                Toast.makeText(getActivity(), sdf2.format(currentDate), Toast.LENGTH_SHORT).show();
                filterOnDate(currentDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void filterOnDate(Date currentDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        jobs.clear();

        if (originalJobs != null && !originalJobs.isEmpty()) {
            for (int i = 0; i < originalJobs.size(); i++) {
                Job job = originalJobs.get(i);
                Date startDate = getDate(job.getScheduledStartDate(), sdf, true);
                Date endDate = getDate(job.getScheduledEndDate(), sdf, false);
                if (startDate.compareTo(currentDate) <= 0 && endDate.compareTo(currentDate) >= 0) {
                    jobs.add(job);
                }
            }
        }

        if (!jobTags.isEmpty()) {
            searchOnKeywords(jobTags);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private Date getDate(String strDate, SimpleDateFormat sdf, boolean isStartDate) {
        Date date;
        if (TextUtils.isEmpty(strDate)) {
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            if (isStartDate) {
                strDate = sdf.format(today);
            } else {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date tomorrow = calendar.getTime();
                strDate = sdf.format(tomorrow);
            }
        }

        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            date = new Date();
            e.printStackTrace();
        }
        return date;
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_cancel_search){
            closeSearch();
        }else if(view.getId() == R.id.btn_img_search){
            openSearch();
        }else if(view.getId() == R.id.btn_img_settings){
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        }else if(view.getId() == R.id.btn_img_cancel){
            ((Activity) context).onBackPressed();
        }
    }

    private void openSearch() {
        btnImageSearch.setVisibility(View.GONE);
        btnImageSettings.setVisibility(View.GONE);
        txtToolbarTitle.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
        searchView.setIconified(false);
    }



    private void onSearch(String keyword) {
        FragmentManager fm = ((MainActivity)context).getSupportFragmentManager();
        FragmentHome fragmentHome = (FragmentHome) (fm.findFragmentByTag(FragmentHome.class.getName()));
        if (fragmentHome != null && fragmentHome.isVisible()) {
            fragmentHome.searchLocal(keyword);
            btnImageSettings.setVisibility(View.GONE);
        }
    }


    private void fetchData(){
        if(!CommonUtils.isNetworkAvailable(context)){
            getJobsFromDb();
            if(Constants.isStoreEnabled) {
                int count = dbHandler.getReceipts().size() + dbHandler.getMyRequest().size();
                listener.setReceiptsBadge(String.valueOf(count));
            }
            return;
        }

        isRefreshing = true;
        listener.showProgressBar();
        getJobs();
    }

    private void getJobs() {

        if(!CommonUtils.validateToken(context)){
            return;
        }

        CallUtils.enqueueWithRetry(APICalls.getJobList(user.gettoken()) , new Callback<JobResponse>() {
            @Override
            public void onResponse(@NonNull Call<JobResponse> call, @NonNull Response<JobResponse> response) {
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
                getJobsFromDb();
                onApiCallResponse();
            }

            @Override
            public void onFailure(@NonNull Call<JobResponse> call, @NonNull Throwable t) {
                getJobsFromDb();
                onApiCallResponse();
            }
        });
    }

    private void onApiCallResponse(){
            isRefreshing = false;
            refreshLayout.setRefreshing(false);
            listener.hideProgressBar();
            if(Constants.isStoreEnabled) {
                int count = dbHandler.getReceipts().size() + dbHandler.getMyRequest().size();
                listener.setReceiptsBadge(String.valueOf(count));
            }
    }




    private void getJobsFromDb() {
        List<Job> jobs = dbHandler.getJobs();
        FragmentHome.this.originalJobs.clear();
        FragmentHome.this.jobs.clear();
        if (!jobs.isEmpty()) {
            FragmentHome.this.originalJobs.addAll(jobs);
        }
        filterOnDate(selectedDate);
    }

    @Override
    public void openAddNotes(Job job) {
        String jsonFileName;
        if(job.isSubJob()){
            jsonFileName = "sub_job_add_notes.json";
        }else{
            jsonFileName = "add_notes.json";
        }
        Submission submission = new Submission(jsonFileName, "Add Notes", job.getjobId());
        long submissionID = dbHandler.insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }

    private void closeSearch() {
        FragmentManager fm = ((MainActivity)context).getSupportFragmentManager();
        FragmentHome fragmentHome = (FragmentHome) (fm.findFragmentByTag(FragmentHome.class.getName()));
        if (fragmentHome != null && fragmentHome.isVisible()) {
            fragmentHome.clearSearch();
            btnImageSearch.setVisibility(View.VISIBLE);
            btnImageSettings.setVisibility(View.VISIBLE);
            txtToolbarTitle.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.GONE);
            searchView.setQuery("", false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 1000) {
            ArrayList<ItemType> tags = data.getParcelableArrayListExtra("keywords");
            if(tags!=null) {
                this.jobTags.clear();
                this.jobTags.addAll(tags);
                filterOnDate(selectedDate);
                jobTagAdapter.notifyDataSetChanged();
            }
        }
    }

    private void searchOnKeywords(ArrayList<ItemType> keywords) {

        ArrayList<Job> tempJob = new ArrayList<>();

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            for (int j = 0; j < keywords.size(); j++) {
                ItemType tags = keywords.get(j);
                String keyword = tags.getDisplayItem();
                if (TextUtils.isEmpty(keyword)) {
                    break;
                }

                String jobCat = job.getjobCatagory();
                if (TextUtils.isEmpty(jobCat)) {
                    break;
                }

                if (jobCat.equalsIgnoreCase(keyword)) {
                    tempJob.add(job);
                    break;
                }
            }
        }

        jobs.clear();
        if (!tempJob.isEmpty()) {
            jobs.addAll(tempJob);
        }


        adapter.notifyDataSetChanged();

    }

    @Override
    public void showHotJobDialog(final HomeAdapter.ViewHolder holder) {
        DisclaimerDialog dialog = new DisclaimerDialog.Builder(context)
                .setNegative((dialog1, which) -> {
                    dialog1.dismiss();
                    removeJob(holder.job.getjobId());
                })
                .setPositive((dialog12, i) -> {
                    dialog12.dismiss();
                    holder.imgViewJob.setVisibility(View.VISIBLE);
                    holder.viewJobPanel.setVisibility(View.VISIBLE);
                })
                .build();

        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "_ERROR_DIALOG");
    }

    @Override
    public void onLogStores(Job job) {
        getCurrentStoreList(job);
    }

    private void startLogStoreFrom(Job job){
        if(dbHandler.getMyStores().isEmpty()){
            Toast.makeText(context , "No Stores found for this job" , Toast.LENGTH_SHORT).show();
            return;
        }
        String jsonFileName = "log_store.json";
        Submission submission = new Submission(jsonFileName, "", job.getjobId());
        long submissionID = dbHandler.insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }

    private void getCurrentStoreList(Job job) {

        if (!CommonUtils.isNetworkAvailable(context)) {
            startLogStoreFrom(job);
            return;
        }

        if(!CommonUtils.validateToken(context)){
            return;
        }
        listener.showProgressBar();
        CallUtils.enqueueWithRetry(APICalls.getMyStore(user.gettoken()) , new Callback<DataMyStores>() {


            @Override
            public void onResponse(@NonNull Call<DataMyStores> call,
                                   @NonNull Response<DataMyStores> response) {

                if(CommonUtils.onTokenExpired(context , response.code())){
                    return;
                }

                if (response.isSuccessful()) {
                    dbHandler.resetMyStores();//to reset my store
                    DataMyStores dataMyStores = response.body();
                    if (dataMyStores != null) {
                        dataMyStores.toContentValues();
                    }

                }
                listener.hideProgressBar();
                startLogStoreFrom(job);
            }

            @Override
            public void onFailure(@NonNull Call<DataMyStores> call, @NonNull Throwable t) {
                listener.hideProgressBar();
                startLogStoreFrom(job);
            }
        });
    }

    private void removeJob(final String jobID) {

        final Handler handler = new Handler();

        if (!CommonUtils.isNetworkAvailable(context)) {
            String title = "Submission Error";
            String message = "Internet connection is not available. Please check your internet connection. Your request is submitted in Queue.";
            showErrorDialog(title, message);
            return;
        }

        if(!CommonUtils.validateToken(context)){
            return;
        }

        listener.showProgressBar();
        new Thread(() -> {

            HashMap<String, String> map = new HashMap<>();
            String uniqueId = UUID.randomUUID().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss",
                    Locale.ENGLISH);
            String date = sdf.format(new Date());
            String url = BuildConfig.BASE_URL + "app/jobs/" + jobID + "/decline";

            map.put("submissionId", uniqueId);
            map.put("latitude", "0");
            map.put("longitude", "0");
            map.put("submittedDate", date);
            String jsonSubmission = new Gson().toJson(map);
            RequestBody body = RequestBody.create(ConnectionHelper.JSON, jsonSubmission);

            final okhttp3.Response response = new ConnectionHelper(context).performJSONNetworking(body, url);

            handler.post(() -> {
                listener.hideProgressBar();
                String title = "Success";
                String message = "Submission was successful";

                if (response == null) {
                    title = "Submission Error";
                    message = "Submission Error, your submission has been added to the queue";

                }else if(!response.isSuccessful()){
                    title = "Submission Error";
                    ResponseBody body1 = response.body();
                    if(body1 != null){
                        try {
                            String data = body1.string();
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
                    }else{
                        message = "Submission Error, your submission has been added to the queue";
                    }

                } else {
                    dbHandler.removeJob(jobID);
                    getJobsFromDb();
                }
                showErrorDialog(title, message);
            });
        }).start();
    }

    @Override
    public void openRequestTask(Job job) {
        String jsonFileName = "request_task.json";
        Submission submission = new Submission(jsonFileName, "Request Task", job.getjobId());
        long submissionID = dbHandler.insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
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
        dialog.show(getChildFragmentManager(), "_ERROR_DIALOG");
    }

    @Override
    public void openPhotoGallery(Job job) {
        Intent intent = new Intent(context , PhotoActivity.class);
        intent.putExtra("Job" , job);
        startActivity(intent);
    }
}
