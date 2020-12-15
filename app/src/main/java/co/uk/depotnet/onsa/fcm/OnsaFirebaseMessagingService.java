package co.uk.depotnet.onsa.fcm;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import co.uk.depotnet.onsa.activities.WelcomeActivity;
import co.uk.depotnet.onsa.utils.AppPreferences;

/**
 * Created by ABHIKR on 08/08/2020.
 */

public class OnsaFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "AbhiFirebaseMS";
    private static final String TITLE = "title";
    private static final String EMPTY = "";
    private static final String MESSAGE = "message";
    private static final String IMAGE = "image";
    private static final String ACTION = "action";
    private static final String URL = "url";
    private static final String ACTION_DESTINATION = "action_destination";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        storeToken(s);
        FirebaseMessaging.getInstance().subscribeToTopic("hseq");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();
            handleData(data);
            try
            {
                PushSaving(data);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        } else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification());
        }

    }
    private void storeToken(String token) {
        AppPreferences.saveDeviceToken(token);
    }
    private void handleNotification(RemoteMessage.Notification RemoteMsgNotification) {
        String message = RemoteMsgNotification.getBody();
        String title = RemoteMsgNotification.getTitle();
        NotificationModal notification = new NotificationModal();
        notification.setTitle(title);
        notification.setMessage(message);

        Intent resultIntent = new Intent(getApplicationContext(), WelcomeActivity.class);
        NotificationBase notificationUtils = new NotificationBase(getApplicationContext());
        notificationUtils.displayNotification(notification, resultIntent);
        //DBHandler.getInstance().replaceData(NotificationModal.DBTable.NAME, notification.toContentValues());
    }

    private void handleData(Map<String, String> data) {
        String title = data.get(TITLE);
        String message = data.get(MESSAGE);
        String iconUrl = data.get(IMAGE);
        String action = data.get(ACTION);
        String actionDestination = data.get(ACTION_DESTINATION);
        NotificationModal notificationdm = new NotificationModal();
        notificationdm.setTitle(title);
        notificationdm.setMessage(message);
        notificationdm.setIconUrl(iconUrl);
        notificationdm.setAction(action);
        notificationdm.setActionDestination(actionDestination);

        Intent resultIntent = new Intent(getApplicationContext(), WelcomeActivity.class);

        NotificationBase notificationUtils = new NotificationBase(getApplicationContext());
        notificationUtils.displayNotification(notificationdm, resultIntent);
        //DBHandler.getInstance().replaceData(NotificationModal.DBTable.NAME, notificationdm.toContentValues());
    }
    private void PushSaving(Map<String, String> data)
    {
        if (!URL.equalsIgnoreCase(data.get(ACTION))) {
            return;
        }
      /*  params.put("user_id",SharedPrefManager.getInstance(getApplicationContext()).getKeyUserId());
        params.put("title",data.get(TITLE));
        params.put("message",data.get(MESSAGE));
        params.put("image",data.get(IMAGE));
        params.put("url",data.get(ACTION_DESTINATION));*/
    }
}
