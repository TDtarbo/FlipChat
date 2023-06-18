package com.codelink.flipchat.password_reset;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.codelink.flipchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordEmail extends AppCompatActivity {

    private Button sendBtn;

    private TextInputEditText resetEmail;

    private String emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_email);

        sendBtn = findViewById(R.id.sendBtn);
        resetEmail = findViewById(R.id.resetEmail);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isValidEmail();
            }
        });


    }

    private void EmailStatusPopup() {
        ViewGroup rootView = findViewById(android.R.id.content);

        View overlayView = new View(this);
        overlayView.setBackgroundColor(Color.parseColor("#80000000"));

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        rootView.addView(overlayView, params);

        View popupView = getLayoutInflater().inflate(R.layout.popup_mail_status, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        Animation startAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
        popupView.startAnimation(startAnimation);

        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);

        Button doneBtn = popupView.findViewById(R.id.doneBtn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                finish();
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Animation clearAnimation = AnimationUtils.loadAnimation(PasswordEmail.this, R.anim.slide_out_bottom);
                popupView.startAnimation(clearAnimation);

                rootView.removeView(overlayView);
            }
        });
    }


    public void isValidEmail(){

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        emailInput = resetEmail.getText().toString().trim();

        if (TextUtils.isEmpty(emailInput)) {
            resetEmail.setError("Email cannot be empty");

        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            resetEmail.setError("Invalid email format");

        }else {

            progressDialog.show();

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.fetchSignInMethodsForEmail(emailInput)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            boolean emailExists = task.getResult().getSignInMethods() != null
                                    && !task.getResult().getSignInMethods().isEmpty();

                            if (!emailExists) {
                                showInvalidEmailDialog();
                            } else {
                                sendPasswordResetEmail();
                            }
                        } else {
                            showGeneralErrorDialog();
                        }
                        progressDialog.dismiss();
                    });
        }
    }

    public void showGeneralErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("An error occurred. Please try again later.");
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showInvalidEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Email not found");
        builder.setMessage("The email address you entered is not registered for FlipChat. Please use a different email address.");
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendPasswordResetEmail() {

        FirebaseAuth.getInstance().sendPasswordResetEmail(emailInput)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            EmailStatusPopup();
                        } else {
                            // Password reset email sending failed
                            Toast.makeText(getApplicationContext(), "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}