package co.uk.depotnet.onsa.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotifyStartServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotifyUtils.scheduleJob(context,false); // reschedule the job
    }
}
