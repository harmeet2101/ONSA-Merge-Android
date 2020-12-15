package co.uk.depotnet.onsa.fcm;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

public class NotifyUtils {
    // schedule the start of the service every 10 - 30 seconds
    public static void scheduleJob(Context context, boolean hasaction) {
        ComponentName serviceComponent = new ComponentName(context, NotifyJobService.class);
        JobInfo.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            builder = new JobInfo.Builder(0, serviceComponent);
            builder.setMinimumLatency(30 * 1000); // wait at least
            builder.setOverrideDeadline(60 * 1000); // maximum delay
            //builder.setPeriodic(2 * 60 * 1000);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // require any network
            builder.setRequiresDeviceIdle(false); // device should be idle
            builder.setRequiresCharging(false); // we don't care if the device is charging or not
            JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
            if (hasaction) {
                jobScheduler.schedule(builder.build());
            } else {
                jobScheduler.cancel(0);
                jobScheduler.cancelAll(); // to cancel
            }
        }
    }
}
