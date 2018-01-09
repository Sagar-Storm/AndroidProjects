package com.example.sagar.runtracker;

import android.content.Context;

/**
 * Created by sagar on 10/9/17.
 */

public class RunLoader extends DataLoader<Run> {
    private Long mRunId;

    public RunLoader(Context context, long runId) {
        super(context);
        mRunId = runId;
    }

    @Override
    public Run  loadInBackground() {
        return RunManager.get(getContext()).getRun(mRunId);
    }
}
