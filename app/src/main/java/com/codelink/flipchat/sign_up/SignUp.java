package com.codelink.flipchat.sign_up;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.codelink.flipchat.R;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        Objects.requireNonNull(getSupportActionBar()).hide();

        UserCredentials userCredentials = new UserCredentials();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, userCredentials)
                    .commit();
        }

    }

}