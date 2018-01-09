package com.example.sagar.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by sagar on 10/7/17.
 */

public class RunManager {
    public static final String  TAG = "RunManager";

    public static final String ACTION_LOCATION = "" +
            "com.example.sagar.runtracker.ACTION_LOCATION";

    private static final String PREFS_FILE = "runs";
    private static final String PREF_CURRENT_ID = "RunManager";

    private static RunManager sRunManager;

    private Context mAppcontext;

    private LocationManager mLocationManager;

    private RunDataBaseHelper mHelper;
    private SharedPreferences mPrefs;
    private long mCurrentRunId;

    private RunManager(Context mAppcontext) {
        this.mAppcontext = mAppcontext;
        mLocationManager = (LocationManager)mAppcontext.getSystemService(Context.LOCATION_SERVICE);
        mHelper = new RunDataBaseHelper(mAppcontext);
        mPrefs = mAppcontext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mCurrentRunId = mPrefs.getLong(PREF_CURRENT_ID, -1);
    }

    public static RunManager get(Context c) {
        if(sRunManager == null) {
            sRunManager = new RunManager(c.getApplicationContext());
        }
        return sRunManager;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0: PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppcontext, 0, broadcast, flags);
    }

    public void startLocationUpdates() {
        String provider = LocationManager.GPS_PROVIDER;
//        try {
//            Location lastKnown = mLocationManager.getLastKnownLocation(provider);
//            if(lastKnown != null) {
//                lastKnown.setTime(System.currentTimeMillis());
//                broadcastLocation(lastKnown);
//            }
//        }catch (SecurityException e) {
//
//        }
        PendingIntent pi = getLocationPendingIntent(true);
        try {
            mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
        } catch (SecurityException e) {

        }
    }
//    private void broadcastLocation(Location location) {
//        Intent broadcast = new Intent(ACTION_LOCATION);
//        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
//        mAppcontext.sendBroadcast(broadcast);
//    }

    public Run startNewRun()  {
        Run run = insertRun();
        startTrackingRun(run);
        return run;
    }

    public void startTrackingRun(Run run) {
        mCurrentRunId = run.getmId();
        mPrefs.edit().putLong(PREF_CURRENT_ID, mCurrentRunId).commit();
        startLocationUpdates();
    }

    public void stopRun() {
        stopLocationUpdates();
        mCurrentRunId = -1;
        mPrefs.edit().remove(PREF_CURRENT_ID).commit();
    }

    private Run insertRun() {
        Run run = new Run() ;
        run.setmId(mHelper.insertRun(run));
        return run;
    }

   public Run getRun(long id) {
       Run run = null;
       RunDataBaseHelper.RunCursor cursor = mHelper.queryRun(id);
       cursor.moveToFirst();

       if(!cursor.isAfterLast()) {
           run = cursor.getRun();
       }
       cursor.close();
       return run;
   }
    public void insertLocation(Location loc) {
        if(mCurrentRunId != -1) {
            mHelper.insertLocation(mCurrentRunId, loc);

        } else {
            Log.e(TAG, "Location Received with no tracking run");
        }
    }

    public Location getLastLocationForRun(long runId) {
        Location location = null;
        RunDataBaseHelper.LocationCursor cursor = mHelper.queryLastKnownLocationForRun(runId);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) location = cursor.getLocation();
        cursor.close();
        return location;
    }


    public void stopLocationUpdates() {
        PendingIntent pi = getLocationPendingIntent(false);
        if(pi != null) {
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }

    public RunDataBaseHelper.LocationCursor queryLocationsForRun(long runId) {
        return mHelper.queryLastKnownLocationForRun(runId);
    }

    public RunDataBaseHelper.RunCursor queryRuns() {
        return mHelper.queryRuns();
    }

    public boolean isTrackingRun() {
        return getLocationPendingIntent(false) != null;
    }

    public boolean isTrackingRun(Run run) {
        return run != null && run.getmId() == mCurrentRunId;
    }
}
