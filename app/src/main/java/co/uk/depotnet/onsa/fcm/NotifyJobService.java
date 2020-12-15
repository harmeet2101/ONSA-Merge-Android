package co.uk.depotnet.onsa.fcm;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import co.uk.depotnet.onsa.activities.WelcomeActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.notify.NotifyModel;
import co.uk.depotnet.onsa.modals.notify.NotifyReadPush;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NotifyJobService extends JobService {
    private static final String TAG = "SyncService";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG,"job started: "+params);
        ArrayList<NotifyModel> notifyModels = DBHandler.getInstance().getNotification();
        if (notifyModels!=null && notifyModels.size()>0)
        {
            for (NotifyModel model : notifyModels) {
                if (!model.getHasBeenRead() && !model.getHasBeenPushed()) {
                    createAndPushNotify(model);
                    break;// to display single notify
                }
            }
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG,"job started end: "+params);
        return true;
    }
    public void createAndPushNotify(NotifyModel notifyModel) {

        User user = DBHandler.getInstance().getUser();
       APICalls.callNotificationMarkPush(new NotifyReadPush(notifyModel.getNotificationId()),user.gettoken()).enqueue(new Callback<NotifyReadPush>() {
                @Override
                public void onResponse(@NonNull Call<NotifyReadPush> call, @NonNull Response<NotifyReadPush> response) {
                    if(CommonUtils.onTokenExpired(NotifyJobService.this, response.code())){
                        return;
                    }
                    if (response.isSuccessful()) {
                        Log.d(TAG,"notification pushed updated for: "+notifyModel.getNotificationId());
                        NotificationModal notificationVO = new NotificationModal();
                        notificationVO.setTitle(String.format("%s, %s","Hey",user.getuserName()));
                        notificationVO.setMessage(notifyModel.getNotificationText());
                        //notificationVO.setIconUrl(""); // passing nothing to display without image
                        notificationVO.setAction("activity");
                        notificationVO.setActionDestination(String.valueOf(notifyModel.getNotificationType()));
                        Intent resultIntent = new Intent(getApplicationContext(), WelcomeActivity.class);
                        NotificationBase notificationUtils = new NotificationBase(getApplicationContext());
                        notificationUtils.displayNotification(notificationVO, resultIntent);
                    }
                    else
                    {
                        Log.d(TAG,"notification pushed failed for: "+notifyModel.getNotificationId());
                    }
                    NotifyUtils.scheduleJob(getApplicationContext(),false);
                }

                @Override
                public void onFailure(@NonNull Call<NotifyReadPush> call, @NonNull Throwable t) {
                    Log.d(TAG,"notification pushed failed for: "+notifyModel.getNotificationId());
                }
            });
    }
}
