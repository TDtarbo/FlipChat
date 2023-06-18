package com.codelink.flipchat.splash_screen;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.codelink.flipchat.R;
import com.codelink.flipchat.welcome.WelcomeActivity;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //set delay to navigate
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }, 1800);
    }
}