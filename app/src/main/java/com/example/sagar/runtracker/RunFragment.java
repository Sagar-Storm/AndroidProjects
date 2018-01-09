package com.example.sagar.runtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by sagar on 10/7/17.
 */

public class RunFragment extends Fragment {
    public static final String TAG = "RunFragment";

    private Button mStartButton, mStopButton, mMapButton;
    private TextView mStartedTextView, mLatitudeTextview, mLongitudeTextView, mAltitudeTextView, mDurationTextView;

    private RunManager mRunManager;
    private Run mRun;

    private Location mLastLocation;
    private BroadcastReceiver mLocationReceiver = new LocationReceiver() {

        @Override
        protected void onLocationReceived(Context context, Location location) {
            if(!mRunManager.isTrackingRun()) return;

            Log.i(TAG, "dynamic broadcast received" );
            mLastLocation = location;
            if(isVisible())
                updateUI();
        }

        @Override
        protected void onProviderEnabledChanged(boolean enabled) {
            int toastText = enabled ? R.string.gps_disabled : R.string.gps_disabled;
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT);
        }

    };

    private static final String ARG_RUN_ID = "RUN_ID";
    private static final int LOAD_RUN = 0;
    private static final int LOAD_LOCATION = 1;

    public static RunFragment newInstance(long runId) {
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunFragment rf = new RunFragment();
        rf.setArguments(args);
        return rf;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mRunManager = RunManager.get(getActivity());
        Bundle args = getArguments();
        if(args != null) {
            long runId = args.getLong(ARG_RUN_ID, -1);
            if(runId != -1) {
                LoaderManager lm = getLoaderManager();
                lm.initLoader(LOAD_RUN, args, new RunLoaderCallbacks());
                lm.initLoader(LOAD_LOCATION, args, new LocationLoaderCallbacks());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_run, parent, false);
        mStartedTextView = (TextView) view.findViewById(R.id.run_startedTextView);
        mLatitudeTextview = (TextView) view.findViewById(R.id.run_latitudeTextView);
        mLongitudeTextView = (TextView) view.findViewById(R.id.run_longitudeTextView);
        mAltitudeTextView = (TextView) view.findViewById(R.id.run_altitudeTextView);
        mDurationTextView = (TextView) view.findViewById(R.id.run_durationTextView);

        mStartButton  = (Button)view.findViewById(R.id.run_startButton);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mRun == null) {
                    mRunManager.startNewRun();
                } else {
                    mRunManager.startTrackingRun(mRun);
                }
            }
        });
        mStopButton = (Button) view.findViewById(R.id.run_stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRunManager.stopRun();
                updateUI();
            }
        });

        mMapButton = (Button)view.findViewById(R.id.run_mapButton);
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), RunMapActivity.class);
                i.putExtra(RunMapActivity.EXTRA_RUN_ID,mRun.getmId());
                startActivity(i);
            }
        });

        updateUI();
        return view;
    }

    private void updateUI() {
        boolean started = mRunManager.isTrackingRun();
        boolean trackingThisRun = mRunManager.isTrackingRun(mRun);
        if(mRun != null) {
            mStartedTextView.setText(mRun.getStartDate().toString());
        }

        int durationSeconds = 0;

        if(mRun != null && mLastLocation != null) {
            durationSeconds = mRun.getDurationSeconds(mLastLocation.getTime());
            mLatitudeTextview.setText(Double.toString(mLastLocation.getLatitude()));
            mLongitudeTextView.setText(Double.toString(mLastLocation.getLongitude()));
            mAltitudeTextView.setText(Double.toString(mLastLocation.getAltitude()));
            mMapButton.setEnabled(true);
        } else {
            mMapButton.setEnabled(false);
        }
        mDurationTextView.setText(Run.formatDuration(durationSeconds));
        mStartButton.setEnabled(!started);
        mStopButton.setEnabled(started && trackingThisRun);
    }



    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mLocationReceiver, new IntentFilter(RunManager.ACTION_LOCATION));
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mLocationReceiver);
        super.onStop();
    }


    private class RunLoaderCallbacks implements LoaderManager.LoaderCallbacks<Run> {
        @Override
        public Loader<Run> onCreateLoader(int id, Bundle args) {
            return new RunLoader(getActivity(), args.getLong(ARG_RUN_ID)) ;
        }

        @Override
        public void onLoaderReset(Loader<Run> loader) {

        }

        @Override
        public void onLoadFinished(Loader<Run> loader, Run run) {
            mRun = run;
            updateUI();
        }
    }

    private class LocationLoaderCallbacks implements LoaderManager.LoaderCallbacks<Location> {
        @Override
        public Loader<Location> onCreateLoader(int id, Bundle args) {
            return new LastLocationLoader(getActivity(), args.getLong(ARG_RUN_ID)) ;
        }

        @Override
        public void onLoaderReset(Loader<Location> loader) {

        }

        @Override
        public void onLoadFinished(Loader<Location> loader, Location location) {
            mLastLocation = location;
            updateUI();
        }
    }
}
