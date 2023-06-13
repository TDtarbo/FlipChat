package com.codelink.flipchat.ui.sign_up;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.codelink.flipchat.R;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        getSupportActionBar().hide();
    }
}