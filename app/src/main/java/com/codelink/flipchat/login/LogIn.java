package com.codelink.flipchat.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codelink.flipchat.R;
import com.codelink.flipchat.bottom_tab_navigation.BottomTabActivity;
import com.codelink.flipchat.password_reset.PasswordEmail;
import com.codelink.flipchat.sign_up.SignUp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LogIn extends AppCompatActivity {

    private TextInputEditText userEmail, password;
    private FirebaseAuth firebaseAuth;
    private LinearLayout loginError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        getSupportActionBar().hide();

        Button loginBtn = findViewById(R.id.loginBtn);
        userEmail = findViewById(R.id.userEmail);
        password = findViewById(R.id.password);
        loginError = findViewById(R.id.loginError);
        TextView forgotPwdBtn = findViewById(R.id.forgotPwdBtn);
        LinearLayout navigateToSignUpBtn = findViewById(R.id.navigateToSignUpBtn);

        firebaseAuth = FirebaseAuth.getInstance();

        //setup onclick listener for login button
        loginBtn.setOnClickListener(view -> {
            String emailInput = Objects.requireNonNull(userEmail.getText()).toString();
            String passwordInput = Objects.requireNonNull(password.getText()).toString();

            userLogin(emailInput, passwordInput);
        });

        //setup onclick listener for forgot password button
        forgotPwdBtn.setOnClickListener(view -> {
            Intent intent = new Intent(LogIn.this, PasswordEmail.class);
            startActivity(intent);

        });

        //setup onclick listener for navigate to signup button
        navigateToSignUpBtn.setOnClickListener(view -> {
            Intent intent = new Intent(LogIn.this, SignUp.class);
            startActivity(intent);
            finish();
        });
    }

    //handle user authentication with firebase
    public void userLogin(String email, String password){

        ProgressDialog progressDialog = new ProgressDialog(LogIn.this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            Intent intent = new Intent(LogIn.this, BottomTabActivity.class);
            progressDialog.dismiss();
            startActivity(intent);
            finish();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            loginError.setVisibility(View.VISIBLE);
        });
    }
}