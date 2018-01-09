package com.example.sagar.photogallery;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by sagar on 10/6/17.
 */

public abstract class VisibleFragment extends android.support.v4.app.Fragment {
    public static final String TAG = "visibleFragment";

    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getActivity(), "Gota broadcast" + intent.getAction(), Toast.LENGTH_LONG).show();
            Log.i(TAG, "oon on receive");
            setResultCode(Activity.RESULT_CANCELED);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
        Log.i("registered", "reg receiver");
        getActivity().registerReceiver(mOnShowNotification, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mOnShowNotification);
    }
}
