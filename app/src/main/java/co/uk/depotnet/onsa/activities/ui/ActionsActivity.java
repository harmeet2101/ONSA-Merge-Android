package co.uk.depotnet.onsa.activities.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.fragments.actions.ClearedFragment;
import co.uk.depotnet.onsa.fragments.actions.OutstandingFragment;
import co.uk.depotnet.onsa.utils.AppPreferences;

public class ActionsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.ColorActions));
        }

        viewPager =  findViewById(R.id.actions_viewpager);
        setupViewPager(viewPager);
        tabLayout=findViewById(R.id.actions_tabs);
        tabLayout.setupWithViewPager(viewPager);
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

    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerHome adapter = new ViewPagerHome(getSupportFragmentManager());
        adapter.addFrag(new OutstandingFragment(), "Outstanding");
        adapter.addFrag(new ClearedFragment(), "Cleared");
        viewPager.setAdapter(adapter);
        /* the ViewPager requires a minimum of 1 as OffscreenPageLimit */
        int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
        //adapter.notifyDataSetChanged();
        viewPager.setOffscreenPageLimit(limit);
    }

    class ViewPagerHome extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerHome(FragmentManager manager) {
            super(manager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //return null;// this icon only not text
            return mFragmentTitleList.get(position); // custom text with above icon
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppPreferences.setTheme(5);
    }

}