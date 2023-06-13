package com.codelink.flipchat.ui.splash_screen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.codelink.flipchat.R;
import com.codelink.flipchat.ui.on_boarding.onBoarding;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, onBoarding.class);
                startActivity(intent);
                finish();
            }
        }, 1800);
    }
}