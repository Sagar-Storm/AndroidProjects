package com.example.sagar.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.example.sagar.criminalintent.Crime;
import com.example.sagar.criminalintent.CrimeActivity;
import com.example.sagar.criminalintent.CrimeFragment;
import com.example.sagar.criminalintent.CrimeLab;
import com.example.sagar.criminalintent.CrimeListFragment;
import com.example.sagar.criminalintent.R;

import java.util.ArrayList;

/**
 * Created by sagar on 9/30/17.
 */

public class CrimePagerActivity extends ActionBarActivity {
    private ViewPager mViewPager;
    private ArrayList<Crime> mCrimes;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);
        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fm = getSupportFragmentManager();

        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(String.valueOf(position));
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        String crimeId = getIntent().getStringExtra(CrimeFragment.EXTRA_CRIME_ID);
        mViewPager.setCurrentItem(Integer.valueOf(crimeId));
        Crime c = CrimeLab.get(this).getCrimeByPosition(Integer.valueOf(crimeId));
        setTitle(c.getTitle());

    }
}
