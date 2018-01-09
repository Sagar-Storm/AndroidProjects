package com.example.sagar.photogallery;

import android.support.v4.app.Fragment;

/**
 * Created by sagar on 10/7/17.
 */

public class PhotoPageActivity  extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return new PhotoPageFragment();
    }
}
