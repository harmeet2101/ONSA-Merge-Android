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
import androidx.core.app.ActivityCompat;
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
import co.uk.depotnet.onsa.modals.store.DataMyRequests;
import co.uk.depotnet.onsa.modals.store.DataMyStores;
import co.uk.depotnet.onsa.modals.store.DataReceipts;
import co.uk.depotnet.onsa.networking.APICalls;
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

    private static final String ARG_USER = "User";
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


    public FragmentHome() {

    }


    public static FragmentHome newInstance(User user) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            user = args.getParcelable(ARG_USER);
        }
        originalJobs = new ArrayList<>();
        jobs = new ArrayList<>();

        jobTags = new ArrayList<>();
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
                getJobs();
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
            //return  FORMATTER.format(day);
            return day.getDay() + "";
        });
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
        calendarView.setOnDateChangedListener(this);

//        calendarView.setHeaderTextAppearance(R.style.calender_header_styles);
//        calendarView.setWeekDayTextAppearance(R.style.calender_week_styles);
        // calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
        calendarView.setLeftArrowMask(null);
        calendarView.setRightArrowMask(null);
        calendarView.setTopbarVisible(false);

        Date date = new Date();
        final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendarView.setSelectedDate(calendar.getTime());

        Calendar instance1 = Calendar.getInstance();
//        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");

        selectedDate = instance1.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            selectedDate = sdf.parse(sdf.format(instance1.getTime()));
        }catch (Exception ex){
            ex.printStackTrace();
        }


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

        if (jobs.isEmpty()) {
            getJobs();
        }else{
            getJobsFromDb();
        }
        //  listener.setTitle(title);
        listener.onFragmentHomeVisible(true);
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
        FragmentJobDetail fragmentJobDetail = FragmentJobDetail.newInstance(user, job);
        listener.addFragment(fragmentJobDetail, false);
    }

    @Override
    public void openWorkLog(final Job job) {
        Intent intent = new Intent(context, WorkLogActivity.class);
        intent.putExtra(WorkLogActivity.ARG_USER, user);
        intent.putExtra(WorkLogActivity.ARG_JOB_ID, job.getjobId());
        intent.putExtra(WorkLogActivity.ARG_JOB_REFERENCE_NUMBER, job.getjobNumber());
        startActivity(intent);
    }

    @Override
    public void openJobPack(Job job) {
        checkStoragePermission(job);
    }

    private void checkStoragePermission(Job job) {

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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
    }

    @Override
    public void openSurvey(Job job) {
        Intent intent = new Intent(context, SurveyActivity.class);
        intent.putExtra(SurveyActivity.ARG_USER, user);
        intent.putExtra(SurveyActivity.ARG_JOB_ID, job.getjobId());
        intent.putExtra(SurveyActivity.ARG_JOB_REFERENCE_NUMBER, job.getjobNumber());
        startActivity(intent);
    }

    @Override
    public void openVisitorAttendance(Job job) {
        String jsonFileName = "visitor_attendance.json";
        Submission submission = new Submission(jsonFileName, "", job.getjobId());
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_USER, user);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }

    @Override
    public void openTakePhotoAndVideo(Job job) {
        Submission submission = new Submission("take_photo.json", "Take Photo/Video", job.getjobId());
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_USER, user);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }


    @Override
    public void openRiskAssessment(final Job job) {
        Intent intent = new Intent(context, RiskAssessmentActivity.class);
        intent.putExtra(RiskAssessmentActivity.ARG_JOB, job);
        intent.putExtra(RiskAssessmentActivity.ARG_USER, user);
        startActivity(intent);
    }

    @Override
    public void onQualityCheck(Job job) {
        Submission submission = new Submission("quality_check.json", "Quality Check", job.getjobId());
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_USER, user);
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
        switch (view.getId()) {
            case R.id.btn_cancel_search:
                closeSearch();
                break;
            case R.id.btn_img_search:
                openSearchDialog();
                break;
            case R.id.btn_img_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                intent.putExtra("User", user);
                startActivity(intent);
                break;
            case R.id.btn_img_cancel:
                ((Activity) context).onBackPressed();
        }
    }

    private void openSearchDialog() {
        btnImageSearch.setVisibility(View.GONE);
        btnImageSettings.setVisibility(View.GONE);
        txtToolbarTitle.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
        searchView.setIconified(false);
       /* PopupMenu popupMenu = new PopupMenu(getContext(), null , this);
        popupMenu.show(btnImageSearch);*/
    }



    private void onSearch(String keyword) {
        FragmentManager fm = ((MainActivity)context).getSupportFragmentManager();
        FragmentHome fragmentHome = (FragmentHome) (fm.findFragmentByTag(FragmentHome.class.getName()));
        if (fragmentHome != null && fragmentHome.isVisible()) {
            fragmentHome.searchLocal(keyword);
            btnImageSettings.setVisibility(View.GONE);
        }
    }


    private void getJobs() {
        isRefreshing = true;
        listener.showProgressBar();

        APICalls.getJobList(user.gettoken()).enqueue(new Callback<JobResponse>() {
            @Override
            public void onResponse(@NonNull Call<JobResponse> call, @NonNull Response<JobResponse> response) {
                if(CommonUtils.onTokenExpired(context , response.code())){
                    return;
                }
                if (response.isSuccessful()) {
                    DBHandler.getInstance().resetJobs();
                    JobResponse jobResponse = response.body();
                        if (jobResponse != null){
                            List<Job> jobs = jobResponse.getJobs();
                        if (jobs != null && !jobs.isEmpty()) {
                            for (Job j : jobs) {
                                DBHandler.getInstance().replaceData(Job.DBTable.NAME, j.toContentValues());
                            }
                        }
                    }
                }
                getJobsFromDb();
                if(Constants.isStoreEnabled) {
                    getMyRequests();
                }else{
                    isRefreshing = false;
                    refreshLayout.setRefreshing(false);
                    listener.hideProgressBar();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JobResponse> call, @NonNull Throwable t) {
                getJobsFromDb();
                isRefreshing = false;
                refreshLayout.setRefreshing(false);
                listener.hideProgressBar();
            }
        });
    }


    private void getReceipts(){
        APICalls.getReceipts(user.gettoken()).enqueue(new Callback<DataReceipts>() {
            @Override
            public void onResponse(@NonNull Call<DataReceipts> call,@NonNull Response<DataReceipts> response) {
                if(CommonUtils.onTokenExpired(context , response.code())){
                    return;
                }

                if (response.isSuccessful()) {
                    if(response.body()!= null){
                        response.body().toContentValues();
                        int count = response.body().getCount()+DBHandler.getInstance().getMyRequest().size();
                        listener.setReceiptsBadge(String.valueOf(count));
                    }
                }

                isRefreshing = false;
                refreshLayout.setRefreshing(false);
                listener.hideProgressBar();
            }

            @Override
            public void onFailure(@NonNull Call<DataReceipts> call, @NonNull Throwable t) {
                isRefreshing = false;
                refreshLayout.setRefreshing(false);
                listener.hideProgressBar();
            }
        });
    }


    private void getMyRequests(){

        APICalls.getMyRequests(user.gettoken()).enqueue(new Callback<DataMyRequests>() {
            @Override
            public void onResponse(@NonNull Call<DataMyRequests> call, @NonNull Response<DataMyRequests> response) {
                if(CommonUtils.onTokenExpired(context , response.code())){
                    return;
                }

                if(response.isSuccessful()){

                    DataMyRequests dataMyRequests = response.body();
                    if(dataMyRequests != null){
                        DBHandler.getInstance().resetMyRequest();
                        dataMyRequests.toContentValues();
                    }
                }
                getReceipts();

            }

            @Override
            public void onFailure(@NonNull Call<DataMyRequests> call, @NonNull Throwable t) {
                isRefreshing = false;
                refreshLayout.setRefreshing(false);
                listener.hideProgressBar();
            }
        });

    }

    private void getJobsFromDb() {
        List<Job> jobs = DBHandler.getInstance().getJobs();
        FragmentHome.this.originalJobs.clear();
        FragmentHome.this.jobs.clear();
        if (!jobs.isEmpty()) {
            FragmentHome.this.originalJobs.addAll(jobs);
        }
        filterOnDate(selectedDate);
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
        if(DBHandler.getInstance().getMyStores().isEmpty()){
            Toast.makeText(context , "No Stores found for this job" , Toast.LENGTH_SHORT).show();
            return;
        }
        String jsonFileName = "log_store.json";
        Submission submission = new Submission(jsonFileName, "", job.getjobId());
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_USER, user);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }

    private void getCurrentStoreList(Job job) {

        if (!CommonUtils.isNetworkAvailable(context)) {
            startLogStoreFrom(job);
            return;
        }

        listener.showProgressBar();
        APICalls.getMyStore(user.gettoken()).enqueue(new Callback<DataMyStores>() {


            @Override
            public void onResponse(@NonNull Call<DataMyStores> call,
                                   @NonNull Response<DataMyStores> response) {

                if(CommonUtils.onTokenExpired(context , response.code())){
                    return;
                }

                if (response.isSuccessful()) {
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
                    DBHandler.getInstance().removeJob(jobID);
                    getJobsFromDb();
                }
                showErrorDialog(title, message);
            });
        }).start();
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

    @Override
    public void openPhotoGallery(Job job) {
        Intent intent = new Intent(context , PhotoActivity.class);
        intent.putExtra("Job" , job);
        startActivity(intent);
    }
}
