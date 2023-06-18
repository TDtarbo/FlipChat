package com.codelink.flipchat.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.codelink.flipchat.R;
import com.codelink.flipchat.login.LogIn;
import com.codelink.flipchat.sign_up.SignUp;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_activity);

        Button signUpBtn = findViewById(R.id.signUpBtn);
        Button logInBtn = findViewById(R.id.logInBtn);

        //handle sign up button
        signUpBtn.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, SignUp.class);
            startActivity(intent);
        });

        //handle sign up button
        logInBtn.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, LogIn.class);
            startActivity(intent);
        });
    }
}