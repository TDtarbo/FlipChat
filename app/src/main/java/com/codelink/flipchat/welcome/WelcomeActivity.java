package com.codelink.flipchat.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.codelink.flipchat.R;
import com.codelink.flipchat.bottom_tab_navigation.BottomTabActivity;
import com.codelink.flipchat.login.LogIn;
import com.codelink.flipchat.sign_up.SignUp;

public class WelcomeActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_activity);

        isUserAlreadyLoggedIn();

        Button signUpBtn = findViewById(R.id.signUpBtn);
        Button logInBtn = findViewById(R.id.logInBtn);

        //handle sign up button
        signUpBtn.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, SignUp.class);
            startActivity(intent);
            finish();
        });

        //handle sign up button
        logInBtn.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, LogIn.class);
            startActivity(intent);
            finish();
        });


    }

    private void isUserAlreadyLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String loginCheck = sharedPreferences.getString("login", "");

        if (loginCheck.equals("true")){
            Intent intent = new Intent(WelcomeActivity.this, BottomTabActivity.class);
            startActivity(intent);
            finish();
        }
    }
}