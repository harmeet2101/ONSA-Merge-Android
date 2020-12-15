package co.uk.depotnet.onsa.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.ui.ActionsActivity;
import co.uk.depotnet.onsa.activities.ui.BriefingsActivity;
import co.uk.depotnet.onsa.activities.ui.ScheduleInspectionActivity;
import co.uk.depotnet.onsa.activities.WelcomeActivity;

/**
 * Created by ABHIKR on 08/08/2020.
 */
public class NotificationBase {
    private  int NOTIFICATION_ID = 0;
    private NotificationManager mManager;
    private static final String ANDROID_CHANNEL_ID = "uk.co.depotnet.co.depotnet.Android";
    private static final String ANDROID_CHANNEL_NAME = "Onsa-Hseq";
    private static final String URL = "url";
    private static final String ACTIVITY = "activity";
    public static final String EXTRA_URL = "product_url";
    private Map<String, Class> activityMap = new HashMap<>();
    private Context mContext;

    public NotificationBase(Context mContext) {
        this.mContext = mContext;
        //Populate activity map
      /*  activityMap.put("Home", WelcomeActivity.class);
        activityMap.put("hseq", HSEQActivity.class);
        activityMap.put("briefings", BriefingsActivity.class);
        activityMap.put("actions", ActionsActivity.class);
        activityMap.put("schedule", ScheduleInspectionActivity.class);*/
        activityMap.put("0", WelcomeActivity.class);
        activityMap.put("1", ActionsActivity.class);
        activityMap.put("2", BriefingsActivity.class);
        activityMap.put("3", ScheduleInspectionActivity.class);
        activityMap.put("4", ScheduleInspectionActivity.class);
    }
    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
    public void displayNotification(NotificationModal notificationVO, Intent resultIntent) {
        {
            NOTIFICATION_ID = new Random().nextInt(60000);

            String message = notificationVO.getMessage();
            String title = notificationVO.getTitle();
            String iconUrl = notificationVO.getIconUrl();
            String action = notificationVO.getAction();
            String destination = notificationVO.getActionDestination();
            Bitmap iconBitMap = null;
            if (iconUrl != null) {
                iconBitMap = getBitmapFromURL(iconUrl);
            }
            final int icon = R.mipmap.ic_launcher;

            PendingIntent resultPendingIntent;

            if (URL.equals(action)) {
                //Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(destination));
                // below code for first open home page and than url
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);//|Intent.FLAG_ACTIVITY_SINGLE_TOP if activity not singleTop
                resultIntent.putExtra("Intro", title);
                resultIntent.setData(Uri.parse(destination));
                resultIntent.putExtra(
                        NotificationBase.EXTRA_URL, destination);
                resultIntent.setAction(Intent.ACTION_VIEW);

                resultPendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);
            } else if (ACTIVITY.equals(action) && activityMap.containsKey(destination)) {
                resultIntent = new Intent(mContext, activityMap.get(destination));

                resultPendingIntent =
                        PendingIntent.getActivity(
                                mContext,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT
                        );
            } else {
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                resultIntent.putExtra("Intro", title);
                resultPendingIntent =
                        PendingIntent.getActivity(
                                mContext,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT
                        );
            }


            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    mContext, ANDROID_CHANNEL_ID);

            Notification notification;
            //Uri abhiSound=  Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.dct);
            Uri abhiSound=  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (iconBitMap == null) {

                //When Inbox Style is applied, user can expand the notification
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

                inboxStyle.addLine(message);
                notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setLights(Color.MAGENTA, 500, 500)
                        .setShowWhen(true)
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setSound(abhiSound)
                        .setContentIntent(resultPendingIntent)
                        .setStyle(inboxStyle)
                        .setColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                        .setColorized(true)
                        .setContentText(message)
                        .build();

            } else {
                //If Bitmap is created from URL, show big icon
                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
                bigPictureStyle.setBigContentTitle(title);
                bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
                bigPictureStyle.bigPicture(iconBitMap);
                bigPictureStyle.bigLargeIcon(null);
                notification = mBuilder.setSmallIcon(icon).setTicker(title).setLights(Color.MAGENTA, 500, 500)
                        .setShowWhen(true)
                        .setAutoCancel(true)
                        .setTicker(message)
                        .setContentTitle(title)
                        .setSound(abhiSound)
                        .setContentIntent(resultPendingIntent)
                        .setColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setStyle(bigPictureStyle)
                        .setLargeIcon(iconBitMap)
                        .setContentText(message)
                        .build();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Creating an Audio Attribute
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();
                NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                        ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                 androidChannel.enableLights(true);
                androidChannel.enableVibration(false);
                androidChannel.setLightColor(Color.GREEN);
                androidChannel.setShowBadge(true);
                androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                androidChannel.setSound(abhiSound,audioAttributes);
                getManager().createNotificationChannel(androidChannel);
            }
            getManager().notify(NOTIFICATION_ID, notification);
        }
    }
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            java.net.URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
