package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import co.uk.depotnet.onsa.fragments.schedule.DueFragment;
import co.uk.depotnet.onsa.fragments.schedule.NotDueFragment;
import co.uk.depotnet.onsa.fragments.schedule.OverDueFragment;

public class ScheduleInspectionAdapter extends FragmentPagerAdapter {

    private HashMap<Integer , Fragment> fragments;
    private Context myContext;
    int totalTabs;

    public ScheduleInspectionAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        myContext = context;
        fragments = new HashMap<>();
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                DueFragment dueFragment = null;
                if(fragments.get(0) == null){
                    dueFragment = new DueFragment();
                    fragments.put(0 , dueFragment);
                }else{
                    dueFragment = (DueFragment)fragments.get(0);
                }
                return dueFragment;
            case 1:
                OverDueFragment overDueFragment = null;
                if(fragments.get(1) == null){
                    overDueFragment = new OverDueFragment();
                    fragments.put(1 , overDueFragment);
                }else{
                    overDueFragment = (OverDueFragment)fragments.get(1);
                }
                return overDueFragment;
            case 2:
                NotDueFragment notDueFragment = null;
                if(fragments.get(2) == null){
                    notDueFragment = new NotDueFragment();
                    fragments.put(2 , notDueFragment);
                }else{
                    notDueFragment = (NotDueFragment) fragments.get(2);
                }
                return notDueFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}

