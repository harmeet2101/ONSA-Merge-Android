package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.fragments.FragmentA75;
import co.uk.depotnet.onsa.fragments.FragmentChildJobDetails;
import co.uk.depotnet.onsa.fragments.FragmentNotes;
import co.uk.depotnet.onsa.fragments.FragmentNotices;
import co.uk.depotnet.onsa.fragments.FragmentTasks;
import co.uk.depotnet.onsa.fragments.FragmentWorkItems;
import co.uk.depotnet.onsa.modals.Job;


public class JobDetailsPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private Job job;

    public JobDetailsPagerAdapter(Context context, Job job , FragmentManager fm) {
        super(fm);
        this.context = context;
        this.job = job;
    }


    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FragmentChildJobDetails.newInstance(job);
            case 1:
                return FragmentWorkItems.newInstance(job);
            case 2:
                return FragmentNotices.newInstance(job);
            case 3:
                return FragmentNotes.newInstance(job);
            case 4:
                return FragmentA75.newInstance(job);
            case 5:
                return FragmentTasks.newInstance(job);
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.job_detail);
            case 1:
                return context.getString(R.string.work_item);
            case 2:
                return context.getString(R.string.notice);
            case 3:
                return context.getString(R.string.notes);
            case 4:
                return context.getString(R.string.a75);
            case 5:
                return context.getString(R.string.task);
        }
        return null;
    }
}
