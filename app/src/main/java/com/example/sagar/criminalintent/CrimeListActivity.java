package com.example.sagar.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by sagar on 9/30/17.
 */

public class CrimeListActivity extends SingleFragmentActivity {

    public Fragment createFragment() {
        return new CrimeListFragment();
    }
}
