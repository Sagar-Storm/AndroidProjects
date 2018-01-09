package com.example.sagar.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by sagar on 10/6/17.
 */

public class PollService extends IntentService {

    private static final int POLL_INTERVAL = 1000*5;
    public static final String PREF_IS_ALARM_ON = "isAlarmOn";

    public static final String ACTION_SHOW_NOTIFICATION =
            "com.example.sagar.photogallery.SHOW_NOTIFICATION";
    public static final String PERM_PRIVATE =
            "com.example.sagar.photogallery.PRIVATE";

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(isOn) {
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PollService.PREF_IS_ALARM_ON, isOn)
                .commit();
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(
                context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null ;
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "Received a intent:" + intent);
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                boolean isNetworkAvailable = cm.getBackgroundDataSetting() && cm.getActiveNetworkInfo() != null;
                if(!isNetworkAvailable) return;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String query = prefs.getString(FlickrFetcher.PREF_SEARCH_QUERY, null);
        String lastResultId = prefs.getString(FlickrFetcher.PREF_LAST_RESULT_ID, null);
        ArrayList<GalleryItem> items;

        if(query != null) {
            items = new FlickrFetcher().search(query);
        } else {
            items = new FlickrFetcher().fetchItems();
        }



        if(items.size() == 0) return;

        String resultId = items.get(0).getId();

        if(!resultId.equals(lastResultId)) {
            Log.i(TAG, "got a new reuslt" + resultId);
            Resources r = getResources();
            PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, PhotoGalleryActivity.class), 0);
            Notification notification = new NotificationCompat.Builder(this).setTicker(r.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(r.getString(R.string.new_pictures_title))
                    .setContentText(r.getString(R.string.new_pictures_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();
//            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//            notificationManager.notify(0, notification);
           // sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION));
            showBackGroundNotification(0, notification);
            Log.i(TAG, "After executing sendBroadcast inorder");
        } else {
            Log.i(TAG, "Got an old result" + resultId);
        }
        prefs.edit()
                .putString(FlickrFetcher.PREF_LAST_RESULT_ID, resultId)
                .commit();
    }

    void showBackGroundNotification(int requestCode, Notification notification) {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra("REQUEST_CODE", requestCode);
        i.putExtra("NOTIFICATION", notification);
        sendOrderedBroadcast(i, null , null, null, Activity.RESULT_OK, null, null);
        Log.i(TAG, "sent background notification");
    }

}
