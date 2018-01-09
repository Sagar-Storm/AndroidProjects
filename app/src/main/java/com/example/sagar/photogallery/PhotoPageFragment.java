package com.example.sagar.photogallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by sagar on 10/7/17.
 */

public class PhotoPageFragment extends VisibleFragment {
    private String mUrl;
    private WebView mWebView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mUrl = getActivity().getIntent().getData().toString();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_page, parent, false);
        mWebView = (WebView)v.findViewById(R.id.webView);
        final ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.progressbar);
        progressBar.setMax(100);
        final TextView titleTextView = (TextView)v.findViewById(R.id.textview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.i(TAG, "made invisible");
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    Log.i(TAG, "is visible");
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                Log.i(TAG, "setting title");
              //  titleTextView.setText(title);
                Log.i(TAG, "setted title" + title);
            }
        });
        mWebView.loadUrl(mUrl);
        return v;
    }
}
