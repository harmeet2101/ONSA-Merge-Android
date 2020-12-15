package co.uk.depotnet.onsa.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.WelcomeActivity;

/**
 * Created by ABHIKR on 08/08/2020.
 */

public class NotificationUtils  extends ContextWrapper {

    private NotificationManager mManager;
    public static final String ANDROID_CHANNEL_ID = "co.uk.depotnet.onsa.Android";
    public static final String ANDROID_CHANNEL_NAME = "Onsa-Hseq";
    public static final String EXTRA_URL = "product_url";
    public NotificationUtils(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels() {

        // create android channel
        NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        //Use Importance HIGH for notifiation and popup screen if using default than only notifiaction show not popup..
        // Sets whether notifications posted to this channel should display notification lights
        androidChannel.enableLights(true);
        // Sets whether notification posted to this channel should vibrate.
        androidChannel.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        androidChannel.setLightColor(Color.GREEN);
        androidChannel.setShowBadge(true);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(androidChannel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
    // ...
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getAndroidChannelNotification(String title, String body) {
        Intent resultIntent = new Intent(
                this.getApplicationContext(), WelcomeActivity.class);

     /*   // pending implicit intent to view url
        Intent resultIntent = new Intent(Intent.ACTION_VIEW);
        resultIntent.setData(Uri.parse(link));*/
        //resultIntent.setAction(Intent.ACTION_VIEW);
        //resultIntent.setData(Uri.parse(link));
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this.getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new Notification.Builder(getApplicationContext(), ANDROID_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(Color.parseColor("#ED1C24"))//for changing color of app name
                .setLights(Color.MAGENTA, 500, 500)
                .setStyle(new Notification.BigTextStyle()
                        .bigText(body))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setColorized(true)
                .setShowWhen(true)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getOthersChannelNotification(String title, String body,Bitmap image,String url) {

        Intent resultIntent = new Intent(
                this.getApplicationContext(), WelcomeActivity.class);
        //construct the pending intent for your notification
        //TaskStackBuilder taskStackBuilder= TaskStackBuilder.create(this);
        //this uses android:parent activity name
        //taskStackBuilder.addNextIntentWithParentStack(resultIntent);
        //PendingIntent pendingIntent=taskStackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        //notificationBuilder.setContentIntent(pendingIntent);
        //its can contain home with all backstack activity
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//|Intent.FLAG_ACTIVITY_SINGLE_TOP if activity not singleTop
        resultIntent.putExtra("Intro", title);
        resultIntent.setData(Uri.parse(url));
        //resultIntent.putExtra("product_url",url);
        resultIntent.putExtra(
                NotificationUtils.EXTRA_URL, url);
        resultIntent.setAction(Intent.ACTION_VIEW);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this.getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);
        return new Notification.Builder(getApplicationContext(), ANDROID_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(Color.parseColor("#ED1C24"))
                .setStyle(new Notification.BigTextStyle()
                        .bigText(body))
                .setLargeIcon(image)
                .setStyle(new Notification.BigPictureStyle()
                        .bigPicture(image)
                        .bigLargeIcon((Bitmap) null))
                /*.setStyle(new Notification.BigPictureStyle()
                        .bigPicture(image))*//*Notification with Image*/
                .setLights(Color.MAGENTA, 500, 500)
                .setShowWhen(true)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Notification.Builder getwithoutchannel(String title, String body)
    {
        Intent resultIntent = new Intent(
            this.getApplicationContext(), WelcomeActivity.class);
        resultIntent.setAction(Intent.ACTION_VIEW);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this.getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(Color.parseColor("#217BA6"))
                .setStyle(new Notification.BigTextStyle()
                        .bigText(body))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setLights(Color.MAGENTA, 500, 500)
                .setShowWhen(true)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
    }
//...https://materialdoc.com/patterns/notifications/
}