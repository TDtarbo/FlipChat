package com.codelink.flipchat.password_reset;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codelink.flipchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.util.regex.Pattern;

public class ResetPassword extends AppCompatActivity {

    private TextInputEditText password, confirmPassword;

    private Button resetBtn;

    private String passwordInput, cPasswordInput;

    private TextInputLayout passwordLayout, cPasswordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        FirebaseApp.initializeApp(this);

        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        passwordLayout = findViewById(R.id.passwordLayout);
        cPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        resetBtn = findViewById(R.id.resetBtn);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordInput = password.getText().toString().trim();
                cPasswordInput = confirmPassword.getText().toString().trim();

                    resetUserPassword();

            }
        });


    }

    private boolean validateFields() {
        boolean isValid = true;

        if (TextUtils.isEmpty(passwordInput)) {
            setupPasswordToggle();
            password.setError("Password cannot be empty");
            isValid = false;

        }else if (!isValidPassword(passwordInput)){
            setupPasswordToggle();
            password.setError("Must include at least one lowercase, uppercase, symbol and number");
            isValid = false;

        } else if (!(passwordInput.length() == 8)) {
            setupPasswordToggle();
            password.setError("Password must be 8 characters long");
            isValid = false;
        }

        else if (TextUtils.isEmpty(cPasswordInput)) {
            setupConfirmPasswordToggle();
            confirmPassword.setError("Re-enter the password");
            isValid = false;

        } else if (!cPasswordInput.equals(passwordInput)) {
            setupConfirmPasswordToggle();
            confirmPassword.setError("Passwords doesn't match");
            isValid = false;
        }

        return isValid;
    }

    private void setupPasswordToggle(){

        passwordLayout.setPasswordVisibilityToggleEnabled(false);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No implementation needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordLayout.setPasswordVisibilityToggleEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupConfirmPasswordToggle(){

        cPasswordLayout.setPasswordVisibilityToggleEnabled(false);
        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cPasswordLayout.setPasswordVisibilityToggleEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private boolean isValidPassword(String password) {

        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).*$";
        Pattern regex = Pattern.compile(pattern);
        return regex.matcher(password).matches();
    }

    public void resetUserPassword() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        Toast.makeText(this, "Current user: " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();

        if (currentUser != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), "12345Aa@");

            // Prompt the user to re-enter their password
            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> reauthTask) {
                            if (reauthTask.isSuccessful()) {
                                // Re-authentication successful, update the password
                                currentUser.updatePassword("newPassword")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> updateTask) {
                                                if (updateTask.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to re-authenticate user", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Failed to authenticate user", Toast.LENGTH_SHORT).show();
        }
    }

}