package com.example.sagar.photogallery;

import android.app.SearchManager;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import static android.R.attr.fragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {
    private static final String TAG = "PhotoGalleryActivity";

    @Override
    protected Fragment createFragment() {
        return new PhotoGalleryFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        PhotoGalleryFragment fragment = (PhotoGalleryFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            getIntent();
            Log.i(TAG, "Received a new search query" + query + getIntent());
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(FlickrFetcher.PREF_SEARCH_QUERY, query)
                    .commit();
        }
        fragment.updateItems();

    }
}


