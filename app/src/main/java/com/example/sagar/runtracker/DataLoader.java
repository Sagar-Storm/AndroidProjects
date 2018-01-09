package com.example.sagar.runtracker;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by sagar on 10/9/17.
 */

public abstract class DataLoader<D> extends android.support.v4.content.AsyncTaskLoader<D> {

    private D mData;

    public DataLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if(mData != null) {
            deliverResult(mData);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(D data) {
        mData = data;
        if(isStarted()) {
            super.deliverResult(data);
        }
    }
}
