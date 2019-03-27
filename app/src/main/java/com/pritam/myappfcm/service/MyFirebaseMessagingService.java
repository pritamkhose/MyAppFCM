package com.pritam.myappfcm.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pritam.myappfcm.MainActivity;
import com.pritam.myappfcm.R;

import java.util.Map;


// https://www.text2speech.org/
// https://github.com/pritamkhose/FCMNotifcation
// https://firebase.google.com/docs/cloud-messaging/android/first-message?authuser=1#retrieve-the-current-registration-token
// https://console.firebase.google.com/u/1/project/securance-94e24/notification
// https://github.com/pratikbutani/Firebase-FCMDemo

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message) {

        //sendMyNotification(message.getNotification().getTitle(),message.getNotification().getBody());
        sendMyNotification(message.getNotification().getTitle(),message.getNotification().getBody(), message.getData());
    }

    // https://github.com/pritamkhose/NotificationOreo/blob/master/app/src/main/java/com/pritam/emergency/HomeActivity.java
    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "1";
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    NotificationChannel notificationChannel;

    private void sendMyNotification(String title, String body, Map<String, String> data) {
        Log.d("-->>", data.toString());
        if(title == null) {
            title  ="Notification";
        }
        if(body == null) {
            body  ="No message";
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("message", body);
        intent.putExtra("data", "data");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mNotifyManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getApplicationContext(), null);
        mBuilder.setContentTitle(title)
                .setContentText(body)
                // .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setSmallIcon(R.mipmap.ic_launcher) // use png https://stackoverflow.com/questions/25317659/how-to-fix-android-app-remoteserviceexception-bad-notification-posted-from-pac
                //.setOnlyAlertOnce(true)
                .setSound(soundUri)
                .setLights(Color.YELLOW, 500, 5000)
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Configure the notification channel.
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationChannel.setSound(soundUri, attr);
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
        else {
            mBuilder.setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark))
                    .setVibrate(new long[]{0, 1000, 500, 1000})
                    .setLights(Color.YELLOW, 500, 5000)
                    .setAutoCancel(true)
                    .setSound(soundUri);
        }

        mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());

    }


    //https://stackoverflow.com/questions/6464080/how-to-play-mp3-file-in-raw-folder-as-notification-sound-alert-in-android
    private void sendMyNotification(String title, String message) {

        if(title == null) {
            title  ="Notification";
        }
        if(message == null) {
            message  ="No message";
        }
        //On click of notification it redirect to this Activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("data", "data");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setVibrate(new long[]{500, 500, 500, 500, 500, 500})
                .setLights(Color.RED, 3000, 3000)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }


}