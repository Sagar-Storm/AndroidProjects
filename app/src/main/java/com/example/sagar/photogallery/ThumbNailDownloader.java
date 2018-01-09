package com.example.sagar.photogallery;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.sip.SipAudioCall;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sagar on 10/5/17.
 */


public class ThumbNailDownloader<Token> extends HandlerThread {
    private static final String TAG = "ThumbNailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;


    Handler mHandler;
    Map<Token, String> requestMap =
            Collections.synchronizedMap(new HashMap<Token, String>());

    Handler mResponseHandler;
    Listener<Token> mListener;

    public interface Listener<Token> {
        void onThumbnailDownloaded(Token token, Bitmap thumbnail);
    }

    public void setListener(Listener<Token> listener) {
        mListener = listener;
    }

    public ThumbNailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }
    public ThumbNailDownloader() {
        super(TAG);
    }
    public void queueThumbnail(Token token, String url) {
        Log.i(TAG,"got an url :" + url);
        requestMap.put(token, url);
        mHandler.obtainMessage(MESSAGE_DOWNLOAD, token).sendToTarget();
    }

    private void handleRequest(final Token token) {
        try {
            final String url = requestMap.get(token);
            if(url == null) return;

            byte[] bitmapBytes = new FlickrFetcher().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes,0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(requestMap.get(token) != url) {
                        return ;
                    }
                    requestMap.remove(token);
                    mListener.onThumbnailDownloaded(token, bitmap);

                }
            });
        } catch (IOException ioe) {
            Log.e(TAG, "Error Downloading image", ioe);
        }
    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MESSAGE_DOWNLOAD) {
                    @SuppressWarnings("unchecked")
                    Token token = (Token)msg.obj;
                    Log.i(TAG, "Got a request for url" + requestMap);
                    handleRequest(token);

                }
            }
        };
    }

}
