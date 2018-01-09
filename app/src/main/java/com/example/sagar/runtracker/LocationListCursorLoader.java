package com.example.sagar.runtracker;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by sagar on 10/10/17.
 */

public class LocationListCursorLoader extends SQLiteCursorLoader {
    private long mRunId;

    public LocationListCursorLoader(Context c, long runId) {
        super(c);
        mRunId = runId;
    }

    @Override
    protected Cursor loadCursor() {
        return RunManager.get(getContext()).queryLocationsForRun(mRunId);
    }
}
