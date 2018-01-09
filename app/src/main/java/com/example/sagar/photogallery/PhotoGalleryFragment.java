package com.example.sagar.photogallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

/**
 * Created by sagar on 10/5/17.
 */
/**
 * Created by sagar on 10/6/17.
 */
public class PhotoGalleryFragment extends VisibleFragment {
    private static final String TAG = "PhotoGalleryFragment" ;
    ArrayList<GalleryItem> mItems = null;
    GridView mGridView;
    ThumbNailDownloader<ImageView> mThumbNailThread;
    boolean[] sett = new boolean[102];



    private class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {

        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... voids) {
            Activity activity = getActivity();
            if(activity == null) {
                return new ArrayList<GalleryItem>();
            }
            String query = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(FlickrFetcher.PREF_SEARCH_QUERY, null);
            if(query != null) {
                return new FlickrFetcher().search(query);
            }
            return new FlickrFetcher().fetchItems();
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {
            Log.d(TAG, "on post execut " + galleryItems.size());
            mItems = galleryItems;
            setupAdapter();
        }


    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();
        PollService.setServiceAlarm(getActivity(), true);
        mThumbNailThread = new ThumbNailDownloader(new Handler());
        mThumbNailThread.setListener(new ThumbNailDownloader.Listener<ImageView>() {
            @Override
            public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
                if(isVisible()) {
                    imageView.setImageBitmap(thumbnail);
                }
            }
        });
        mThumbNailThread.start();
        mThumbNailThread.getLooper();
        Log.i(TAG, "Background thread started");
    }

    public void updateItems() {
        new FetchItemsTask().execute();
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_search:
                getActivity().onSearchRequested();
                return true;
            case R.id.menu_item_clear:
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString(FlickrFetcher.PREF_SEARCH_QUERY, null).commit();
                updateItems();
                return true;
            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getContext());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    getActivity().invalidateOptionsMenu();
                }
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if(PollService.isServiceAlarmOn(getActivity())) {
            toggleItem.setTitle(R.string.stop_polling);
        } else {
            toggleItem.setTitle(R.string.start_polling);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, parent, false);
        mGridView = (GridView)v.findViewById(R.id.gridView);
        setupAdapter();
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                GalleryItem item = mItems.get(position);
                Uri photoPageUri = Uri.parse(item.getPhotoPageUrl());
                Intent i = new Intent(getActivity(), PhotoPageActivity.class);
                i.setData(photoPageUri);
                startActivity(i);
            }
        });
        return v;
    }

    void setupAdapter() {
        if(getActivity() == null || mGridView == null) return;
        if(mItems != null) {
            Log.d(TAG, "mitems is not null");
            mGridView.setAdapter(new GalleryItemAdapter(mItems));
        } else {
            Log.d(TAG, "mitems is null");
            mGridView.setAdapter(null);
        }
    }

    private class GalleryItemAdapter extends ArrayAdapter<GalleryItem> {
        public GalleryItemAdapter(ArrayList<GalleryItem> items) {
            super(getActivity(), 0, items);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if(convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.gallery_item, parent, false);
            }
             ImageView imageView = (ImageView)convertView.findViewById(R.id.gallery_item_imageView);
            GalleryItem item = getItem(position);
                mThumbNailThread.queueThumbnail(imageView, item.getUrl());

            return convertView;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbNailThread.clearQueue();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbNailThread.quit();
        Log.i(TAG, "Background thread destroyed");
    }
}
