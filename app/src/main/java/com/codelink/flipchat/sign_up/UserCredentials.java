package com.codelink.flipchat.sign_up;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.codelink.flipchat.R;
import com.codelink.flipchat.login.LogIn;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.regex.Pattern;

public class UserCredentials extends Fragment {

    private Button signUpBtn;
    private TextInputLayout passwordLayout, cPasswordLayout;
    private TextInputEditText userEmail, password, cPassword;
    private String emailInput, passwordInput, cPasswordInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_credentials, container, false);

        signUpBtn = view.findViewById(R.id.signUpBtn);
        userEmail = view.findViewById(R.id.userEmail);
        password = view.findViewById(R.id.password);
        cPassword = view.findViewById(R.id.confirmPassword);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        cPasswordLayout = view.findViewById(R.id.confirmPasswordLayout);
        CheckBox userAgreed = view.findViewById(R.id.userAgreed);
        LinearLayout navigateToLoginBtn = view.findViewById(R.id.navigateToLoginBtn);

        signUpBtn.setEnabled(false);

        //set up onclick listeners
        signUpBtn.setOnClickListener(view1 -> {
            signUpHandler();
        });

        navigateToLoginBtn.setOnClickListener(view12 -> {
            Intent intent = new Intent(getActivity(), LogIn.class);
            startActivity(intent);
            requireActivity().finish();
        });

        userAgreed.setOnCheckedChangeListener((compoundButton, b) -> signUpBtn.setEnabled(b));

        return view;
    }

    //handling signup process
    private void signUpHandler() {
        emailInput = Objects.requireNonNull(userEmail.getText()).toString().trim();
        passwordInput = Objects.requireNonNull(password.getText()).toString().trim();
        cPasswordInput = Objects.requireNonNull(cPassword.getText()).toString().trim();

        if (validateFields()){
            if (isStrongPassword(passwordInput)){
                navigateToNextFragment();
            }else{
                showPasswordStrengthAlert();
            }
        }
    }

    //validate user inputs
    private boolean validateFields() {
        boolean isValid = true;

        if (TextUtils.isEmpty(emailInput)) {
            userEmail.setError("Email cannot be empty");
            isValid = false;

        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            setupPasswordToggle();
            userEmail.setError("Invalid email format");
            isValid = false;

        }else if (TextUtils.isEmpty(passwordInput)) {
            setupPasswordToggle();
            password.setError("Password cannot be empty");
            isValid = false;

        }else if (passwordInput.length() < 6) {
            setupPasswordToggle();
            password.setError("Password should be at least 6 characters long");
            isValid = false;

        }else if (TextUtils.isEmpty(cPasswordInput)) {
            setupConfirmPasswordToggle();
            cPassword.setError("Re-enter the password");
            isValid = false;

        }else if (!cPasswordInput.equals(passwordInput)) {
            setupConfirmPasswordToggle();
            cPassword.setError("Passwords doesn't match");
            isValid = false;
        }

        return isValid;
    }

    //check password strength
    private boolean isStrongPassword(String password) {

        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!()@#$%^&+=]).*$";
        Pattern regex = Pattern.compile(pattern);
        return regex.matcher(password).matches();
    }

    //navigate tho the UserPersonalization fragment
    private void navigateToNextFragment() {
        UserPersonalization nextFragment = new UserPersonalization();

        Bundle bundle = new Bundle();
        bundle.putString("email", emailInput);
        bundle.putString("password", passwordInput);

        nextFragment.setArguments(bundle);

        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, nextFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //Password strength alert
    private void showPasswordStrengthAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Password Strength")
                .setMessage("We suggest creating a strong password that contains at least one lowercase letter, one uppercase letter, one digit, and one special character.")
                .setPositiveButton("Keep", (dialog, which) -> navigateToNextFragment())
                .setNegativeButton("Change", (dialog, which) -> dialog.dismiss())
                .show();
    }

    //password toggle handlers
    private void setupPasswordToggle(){

        passwordLayout.setPasswordVisibilityToggleEnabled(false);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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
        cPassword.addTextChangedListener(new TextWatcher() {
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
}
