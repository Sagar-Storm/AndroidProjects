package com.example.sagar.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by sagar on 10/6/17.
 */

public class StartUpReceiver extends BroadcastReceiver {
    private static final String TAG = "StartUpReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast intent" + intent.getAction());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isOn = prefs.getBoolean(PollService.PREF_IS_ALARM_ON, false);
        PollService.setServiceAlarm(context,isOn);
    }
}
