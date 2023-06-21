package com.codelink.flipchat;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class FlipChatApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Disable force dark mode for the entire application
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}