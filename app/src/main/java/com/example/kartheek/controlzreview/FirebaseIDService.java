package com.example.kartheek.controlzreview;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseIDService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMSGService";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG,s);
    }
}
