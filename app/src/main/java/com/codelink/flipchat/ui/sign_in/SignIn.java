package com.codelink.flipchat.ui.sign_in;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.codelink.flipchat.R;
import com.codelink.flipchat.ui.bottom_navigation.ButtomNavigation;

import java.util.zip.Inflater;

public class SignIn extends AppCompatActivity {

    private Button signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        getSupportActionBar().hide();

        signInBtn = findViewById(R.id.signInBtn);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, ButtomNavigation.class);
                startActivity(intent);
            }
        });
    }
}