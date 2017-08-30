package com.mobileappsprn.alldealership.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mobileappsprn.alldealership.MainActivity;
import com.mobileappsprn.alldealership.R;

/**
 * Created by sri on 02/08/16.
 */
public class GCMNotificationIntentService extends IntentService {

    private static final String APP_NAME = "Total Car Care";
    public int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }

    public static final String TAG = "GCMNotificationService";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        Log.i(TAG, "onHandleIntent: bundle: "+extras.toString());
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString(),null);//, "0", "");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString(), null);//, "0", "");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.d(getClass().getName(), extras.toString());
//                addBadge(this, Integer.valueOf(extras.getString("badge")));

                /*if (!checkNotificationSend("" + extras.get(Config.MESSAGE_KEY))) {
                    if (AppController.isActivityVisible()) {
                        ReusableLogic.printLog(getClass().getName(), "Activity is open", ReusableLogic.v, true);
                        if (extras.getString("type").equalsIgnoreCase("order")) {
                            this.sendBroadcast(new Intent("NEW_ORDER"));
                        } else {
                            if (extras.getString("type").equalsIgnoreCase("random")) {
                                new SharedPref(this).saveRandomBadgeCount(Integer.valueOf(extras.getString("badge")));
                                Log.v(TAG, "random count : " + new SharedPref(this).getRandomBadgeCount());
                            }
                            this.sendBroadcast(new Intent("NEW_MESSAGE"));
                        }
                        *//*else if (extras.getString("type").equalsIgnoreCase("chat")) {
                            this.sendBroadcast(new Intent("NEW_MESSAGE"));
                        }*//*

                    } else {
                        ReusableLogic.printLog(getClass().getName(), "Activity is closed", ReusableLogic.v, true);
                        ShortcutBadger.applyCount(this, Integer.valueOf(extras.getString("badge")));
                        new SharedPref(this).saveBadgeCount(Integer.valueOf(extras.getString("badge")));
                        Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                    }
                    sendNotification("" + extras.get(Config.MESSAGE_KEY), extras.getString("room_id"), extras.getString("type"));
                }*/

                sendNotification(extras.getString("message"), extras.getString("image"));//), "1", "");

                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    /*private boolean checkNotificationSend(String id) {
        if ((new SharedPref(this).getNotificationMessage()).equalsIgnoreCase(id)) {
            return true;
        }
        return false;
    }*/

    private void sendNotification(String msg, String image) {
        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent myintent = new Intent(this, MainActivity.class);
        if(image!=null) {
            NotificationModel model = new NotificationModel(msg, image);
            myintent.putExtra("notification", model);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, myintent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.btn_car)
                        .setContentTitle(APP_NAME)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setSound(alarmSound)
                        .setAutoCancel(true)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    /*private void sendNotification(String msg, String notificationId, String type) {
        Log.d(TAG, "Preparing to send notification...: " + msg);
        new SharedPref(this).saveNotification(msg);
        if (type.equalsIgnoreCase("random")) {
            new SharedPref(this).saveRandomMessage(msg);
        }
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = null;
        if (new SharedPref(getApplicationContext()).getUserType().equalsIgnoreCase("1")) {
            contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, CustomerDashBoardActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).setAction(type + ":" + notificationId), 0);
        } else if (new SharedPref(getApplicationContext()).getUserType().equalsIgnoreCase("0")) {
            contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, VendorDashBoardActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).setAction(type + ":" + notificationId), 0);
        } else {
            contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).setAction(type + ":" + notificationId), 0);
        }
        Uri uri;
        String notificationSound = new SharedPref(this).getNotificationSoundUri();
        if (notificationSound != null && !notificationSound.equalsIgnoreCase("") && notificationSound.length() > 0) {
            uri = Uri.parse(notificationSound);
        } else {
            uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_got);
        }
        NotificationCompat.Builder mBuilder = null;
        Log.d(TAG, new SharedPref(this).getChatRoomId() + "::::" + notificationId);
        if ((new SharedPref(this).getChatRoomId()).equalsIgnoreCase(notificationId)) {
            Uri uriScreenOff = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.screen_off_notification_sound);
            if (AppController.isActivityVisible()) {
                mBuilder = new NotificationCompat.Builder(
                        this).setSmallIcon(R.drawable.app_launcher)
                        .setContentTitle("Bizzalley")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setSound(uriScreenOff);

            } else {
                mBuilder = new NotificationCompat.Builder(
                        this).setSmallIcon(R.drawable.app_launcher)
                        .setContentTitle("Bizzalley")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setSound(uri);
            }
        } else {
            mBuilder = new NotificationCompat.Builder(
                    this).setSmallIcon(R.drawable.app_launcher)
                    .setContentTitle("Bizzalley")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setContentText(msg)
                    .setAutoCancel(true)
                    .setSound(uri);
        }

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(Integer.parseInt(notificationId), mBuilder.build());
        Log.d(TAG, "Notification sent successfully.");
    }*/
}
