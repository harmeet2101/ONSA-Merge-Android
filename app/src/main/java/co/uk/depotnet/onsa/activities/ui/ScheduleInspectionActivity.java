package co.uk.depotnet.onsa.activities.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import co.uk.depotnet.onsa.listeners.OnScheduleListUpdate;
import co.uk.depotnet.onsa.listeners.ScheduleFragmentListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.ScheduleInspectionAdapter;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.schedule.Schedule;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;

public class ScheduleInspectionActivity extends AppCompatActivity implements ScheduleFragmentListener {

    private User user;
    private ProgressBar progressBar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_inspection);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.ColorHseq));
        }
        user = DBHandler.getInstance().getUser();
        progressBar = findViewById(R.id.progress_bar_schedule);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("DUE"));
        tabLayout.addTab(tabLayout.newTab().setText("OVERDUE"));
        tabLayout.addTab(tabLayout.newTab().setText("NOT DUE"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        try {
            if (!CommonUtils.isNetworkAvailable(getApplicationContext())) {
                setupViewPager(viewPager);
            } else {
                refreshData(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        final ScheduleInspectionAdapter adapter = new ScheduleInspectionAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
        viewPager.setOffscreenPageLimit(limit);
    }


    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void refreshData(OnScheduleListUpdate listUpdate) {
        showProgressBar();
        APICalls.getHseqScheduleList(user.gettoken()).enqueue(new Callback<List<Schedule>>() {
            @Override
            public void onResponse(@NonNull Call<List<Schedule>> call, @NonNull Response<List<Schedule>> response) {
                if (CommonUtils.onTokenExpired(getApplicationContext(), response.code())) {
                    return;
                }
                if (response.isSuccessful()) {
                    DBHandler.getInstance().clearTable(Schedule.DBTable.NAME);
                    List<Schedule> scheduleList = response.body();
                    if (scheduleList != null) {
                        for (Schedule schedule : scheduleList) {
                            DBHandler.getInstance().replaceData(Schedule.DBTable.NAME, schedule.toContentValues());
                        }
                    }
                }
                if(listUpdate == null) {
                    setupViewPager(viewPager);
                }else {
                    listUpdate.updateList();
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(@NonNull Call<List<Schedule>> call, @NonNull Throwable t) {
                if(listUpdate == null) {
                    setupViewPager(viewPager);
                }else {
                    listUpdate.updateList();
                }
                hideProgressBar();
            }
        });
    }
}