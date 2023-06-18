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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {

    private Button loginBtn;
    private TextInputEditText userEmail, password;

    private FirebaseAuth firebaseAuth;

    private LinearLayout loginError;

    private TextView forgotPwdBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        getSupportActionBar().hide();

        loginBtn = findViewById(R.id.loginBtn);
        userEmail = findViewById(R.id.userEmail);
        password = findViewById(R.id.password);
        loginError = findViewById(R.id.loginError);
        forgotPwdBtn = findViewById(R.id.forgotPwdBtn);

        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailInput = userEmail.getText().toString();
                String passwordInput = password.getText().toString();

                userLogin(emailInput, passwordInput);
            }
        });

        forgotPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogIn.this, PasswordEmail.class);
                startActivity(intent);

            }
        });
    }

    public void userLogin(String email, String password){

        ProgressDialog progressDialog = new ProgressDialog(LogIn.this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Intent intent = new Intent(LogIn.this, BottomTabActivity.class);
                progressDialog.dismiss();
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                loginError.setVisibility(View.VISIBLE);
            }
        });
    }
}