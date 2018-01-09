package com.example.sagar.runtracker;

import android.content.Context;
import android.location.Location;

/**
 * Created by sagar on 10/7/17.
 */

public class TrackingLocationReceiver extends LocationReceiver {

    @Override
    protected void onLocationReceived(Context context, Location location) {
        RunManager.get(context).insertLocation(location);
    }
}
