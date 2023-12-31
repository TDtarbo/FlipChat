package com.codelink.flipchat.sign_up;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.codelink.flipchat.R;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Objects.requireNonNull(getSupportActionBar()).hide();

        UserCredentials userCredentials = new UserCredentials();

        //set up default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, userCredentials)
                    .commit();
        }
    }
    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (fragment instanceof UserCredentials) {
            ((UserCredentials) fragment).onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}