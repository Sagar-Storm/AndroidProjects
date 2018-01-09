package com.example.sagar.criminalintent;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.Size;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Exchanger;

/**
 * Created by sagar on 10/2/17.
 */

public class CrimeCameraFragment extends android.support.v4.app.Fragment{
    private static final String TAG = "CrimeCameraFragment" ;

    public static final String EXTRA_PHOTO_FILENAME = "com.android.sagar.photo_filename";

    private android.hardware.Camera mCamera;
    private SurfaceView mSurfaceView;

    private View mProgressContainer;

    private android.hardware.Camera.ShutterCallback mShutterCallBack = new android.hardware.Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };

    private android.hardware.Camera.PictureCallback mJPEGCallBack;

    {
        mJPEGCallBack = new android.hardware.Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, android.hardware.Camera camera) {
                String filename = UUID.randomUUID().toString() + ".jpg";
                FileOutputStream os = null;
                boolean success = true;
                try {
                    os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                    os.write(bytes);
                } catch (Exception e) {
                    Log.e(TAG, "Error writing to a filei " + filename);
                    success = false;
                } finally {
                    try {
                        if (os != null) {
                            os.close();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "error closing file " + filename, e);
                        success = false;
                    }
                }
                if (success) {
                    Intent i = new Intent();
                    i.putExtra(EXTRA_PHOTO_FILENAME, filename);
                    getActivity().setResult(Activity.RESULT_OK, i);
                } else {
                    getActivity().setResult(Activity.RESULT_CANCELED);
                }
                getActivity().finish();
            }
        };
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_camera, parent, false);

        mProgressContainer = v.findViewById(R.id.crime_camera_progress_container);
        mProgressContainer.setVisibility(View.INVISIBLE);

        Button takePictureButton = (Button) v.findViewById(R.id.crime_camera_takePictureButton);

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCamera != null) {
                    mCamera.takePicture(mShutterCallBack, null, mJPEGCallBack);
                }
            }
        });

        mSurfaceView = (SurfaceView) v.findViewById(R.id.crime_camera_surfaceview);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (Exception e) {
                    Log.e("error in camera", "error man in camera");
                }
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                try {
                    if (mCamera != null) {
                        mCamera.stopPreview();
                    }
                } catch (Exception e) {
                    Log.e("can't stop preview", "preview not stopped");
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mCamera == null) return;
                android.hardware.Camera.Parameters parameters = mCamera.getParameters();
                android.hardware.Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
                parameters.setPreviewSize(s.width, s.height);
                s = getBestSupportedSize(parameters.getSupportedPictureSizes(),width, height);
                parameters.setPictureSize(s.width, s.height);
                mCamera.setParameters(parameters);
                try {
                    mCamera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "could not start preview", e);
                    mCamera.release();
                    mCamera = null;
                }
            }
        });
        return  v;
    }




    private android.hardware.Camera.Size getBestSupportedSize(List<android.hardware.Camera.Size> sizes, int width, int height) {
        android.hardware.Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height ;
        for(android.hardware.Camera.Size s: sizes) {
            int area = s.width * s.height;
            if(area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }



    @Override
    public void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mCamera = android.hardware.Camera.open(0);
        } else {
            mCamera = android.hardware.Camera.open();
        }
    }




    @Override
    public void onPause() {
        super.onPause();
        if(mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
