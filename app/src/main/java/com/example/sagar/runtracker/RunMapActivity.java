package com.example.sagar.runtracker;

import android.support.v4.app.Fragment;

/**
 * Created by sagar on 10/9/17.
 */

public class RunMapActivity extends SingleFragmentActivity{

    public static final String EXTRA_RUN_ID = "com.example.sagar.android.runtracker.run_id";

    @Override
    protected Fragment createFragment() {
        long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        if(runId != -1) {
            return RunMapFragment.newInstance(runId);
        }
        return new RunMapFragment();
    }
}
