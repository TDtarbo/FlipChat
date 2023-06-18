package com.codelink.flipchat.on_boarding;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.codelink.flipchat.R;
import com.codelink.flipchat.login.LogIn;
import com.codelink.flipchat.sign_up.SignUp;

public class onBoarding extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.on_boarding_activity);

        Button signUpBtn = findViewById(R.id.signUpBtn);
        Button signInBtn = findViewById(R.id.signInBtn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(onBoarding.this, SignUp.class);
                startActivity(intent);
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(onBoarding.this, LogIn.class);
                startActivity(intent);
            }
        });
    }
}