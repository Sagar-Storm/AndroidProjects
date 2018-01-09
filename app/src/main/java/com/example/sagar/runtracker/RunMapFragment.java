package com.example.sagar.runtracker;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sagar on 10/9/17.
 */

public class RunMapFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String ARG_RUN_ID = "RUN_ID";

    private static final int LOAD_LOCATIONS = 0;

    private RunDataBaseHelper.LocationCursor mLocationCursor;
    private GoogleMap mGoogleMap;

    public static RunMapFragment newInstance(long runId) {
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunMapFragment rf = new RunMapFragment();
        rf.setArguments(args);
        return rf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState, false);
        mGoogleMap = getMap();
        mGoogleMap.setMyLocationEnabled(true);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            long runId = args.getLong(ARG_RUN_ID, -1);
            if(runId != -1) {
                LoaderManager lm = getLoaderManager();
                lm.initLoader(LOAD_LOCATIONS, args, this);
            }
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        long runId = args.getLong(ARG_RUN_ID, -1);
        return new LocationListCursorLoader(getActivity(), runId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mLocationCursor = (RunDataBaseHelper.LocationCursor)cursor;
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mLocationCursor.close();
        mLocationCursor = null;
    }
}
