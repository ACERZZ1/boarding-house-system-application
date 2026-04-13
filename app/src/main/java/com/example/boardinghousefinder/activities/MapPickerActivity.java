package com.example.boardinghousefinder.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.boardinghousefinder.R;

public class MapPickerActivity extends AppCompatActivity {

    WebView mapWebView;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);

        mapWebView = findViewById(R.id.mapWebView);

        WebSettings settings = mapWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setLoadsImagesAutomatically(true);
        settings.setBlockNetworkImage(false);
        settings.setBlockNetworkLoads(false);

        // JS-to-Java bridge — named "Android" so JS calls Android.onLocationPicked()
        mapWebView.addJavascriptInterface(new AndroidBridge(), "Android");
        mapWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        // Load the HTML from assets/ folder
        mapWebView.loadUrl("file:///android_asset/map_picker.html");
    }

    class AndroidBridge {
        @JavascriptInterface
        public void onLocationPicked(String lat, String lng) {
            // Send coords back to AddBoardingActivity
            Intent result = new Intent();
            result.putExtra("latitude", lat);
            result.putExtra("longitude", lng);
            setResult(RESULT_OK, result);
            finish();
        }
    }
}