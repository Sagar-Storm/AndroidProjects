package com.example.sagar.criminalintent;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by sagar on 9/30/17.
 */

public class CrimeLab {

    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crimes.json";

    private ArrayList<Crime> mCrimes;
    private CriminalIntentJSONSerializer mSerializer;

    private static CrimeLab ScrimeLab;
    private Context mAppContext;

    private CrimeLab(Context appContext) {
        mAppContext = appContext;
        mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);
        try {
            mCrimes = mSerializer.loadCrimes();
        } catch (Exception e) {
            mCrimes = new ArrayList<Crime>();
            Log.e(TAG, "Error loading crimes", e) ;
        }
    }

    public void addCrime(Crime crime) {
        mCrimes.add(crime);
    }
    public void deleteCrime(Crime crime) { mCrimes.remove(crime); }

    public static CrimeLab get(Context c) {
        if (ScrimeLab == null) {
            ScrimeLab = new CrimeLab(c.getApplicationContext());
        }
        return ScrimeLab;
    }

    public Crime getCrimeByPosition(int position) {
        return mCrimes.get(position);
    }

    public ArrayList<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id){
        for(Crime c: mCrimes) {
            if(c.getId() == id) {
                Log.d("returning not null", "not null");

                return c;
            }
        }
        Log.d("returning null", "null man");
        return null;
    }

    public boolean saveCrimes() {
        try {
            mSerializer.saveCrimes(mCrimes);
            Log.d(TAG, "saved");
            return true;
        } catch (Exception e) {
            Log.d(TAG, "not sav");
            return false;
        }
    }
}
