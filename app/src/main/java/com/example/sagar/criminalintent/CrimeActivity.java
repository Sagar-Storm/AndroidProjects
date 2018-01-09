package com.example.sagar.criminalintent;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;

public class CrimeActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment(){
        String position = getIntent().getStringExtra(CrimeFragment.EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(position);
    }
}
