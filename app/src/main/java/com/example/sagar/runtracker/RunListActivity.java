package com.example.sagar.runtracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by sagar on 10/7/17.
 */

public class RunListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RunListFragment();
    }
}
