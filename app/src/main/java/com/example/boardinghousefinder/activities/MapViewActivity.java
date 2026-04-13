package com.example.boardinghousefinder.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.boardinghousefinder.R;

public class MapViewActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker); // reuses the same simple WebView layout

        double lat   = getIntent().getDoubleExtra("latitude", 0.0);
        double lng   = getIntent().getDoubleExtra("longitude", 0.0);
        String title = getIntent().getStringExtra("title");
        if (title == null) title = "Boarding House";

        android.util.Log.d("MAP_DEBUG", "LAT: " + lat + " LNG: " + lng + " TITLE: " + title);

        WebView mapWebView = findViewById(R.id.mapWebView);
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

        mapWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        // Pass lat/lng/title via URL hash — avoids JS injection timing issues entirely.
        // Format: file:///android_asset/map_view.html#lat|lng|title
        String encodedTitle = android.net.Uri.encode(title);
        String url = "file:///android_asset/map_view.html#" + lat + "|" + lng + "|" + encodedTitle;
        mapWebView.loadUrl(url);
    }
}