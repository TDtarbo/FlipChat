package com.codelink.flipchat.sign_up;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.codelink.flipchat.R;
import com.codelink.flipchat.login.LogIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import gun0912.tedimagepicker.builder.TedImagePicker;
import gun0912.tedimagepicker.builder.listener.OnSelectedListener;

public class UserPersonalization extends Fragment {

    private ImageView chooseImageBtn;
    private CircleImageView pickedImage;
    private RelativeLayout imagePreview;
    private Uri imageUri;
    private String email, password,uid,profileUrl, userNameInput;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private TextInputEditText userName;
    private ProgressDialog progressDialog;
    private LinearLayout personalizeContainer, completeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_personalization, container, false);

        //fetch data from previous fragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            email = bundle.getString("email");
            password = bundle.getString("password");
        }

        firebaseAuth = FirebaseAuth.getInstance();
        userName = view.findViewById(R.id.userName);
        chooseImageBtn = view.findViewById(R.id.chooseImageBtn);
        Button navigateToLoginBtn = view.findViewById(R.id.navigateToLoginBtn);
        pickedImage = view.findViewById(R.id.pickedImage);
        imagePreview = view.findViewById(R.id.imagePreview);
        personalizeContainer = view.findViewById(R.id.personalizeContainer);
        completeContainer = view.findViewById(R.id.completeContainer);
        ImageView retryBtn = view.findViewById(R.id.retryBtn);
        Button signUpBtn = view.findViewById(R.id.signUpBtn);

        progressDialog = new ProgressDialog(getContext());

        //setup onclick listeners
        chooseImageBtn.setOnClickListener(view1 -> imagePicker());

        retryBtn.setOnClickListener(view12 -> imagePicker());

        signUpBtn.setOnClickListener(view13 -> userRegistration());

        navigateToLoginBtn.setOnClickListener(view14 -> {
            Intent intent = new Intent(getActivity(), LogIn.class);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    //TedImage picker
    private void imagePicker() {
        TedImagePicker.with(requireContext())
                .start(uri -> {
                    chooseImageBtn.setVisibility(View.GONE);
                    imagePreview.setVisibility(View.VISIBLE);
                    pickedImage.setImageURI(uri);
                    imageUri = uri;
                });
    }

    //Signing up the user with Firebase Authentication
    private void userRegistration() {

        userNameInput = Objects.requireNonNull(userName.getText()).toString().trim();

        if (TextUtils.isEmpty(userNameInput)) {
            userName.setError("User name cannot be empty");

        }else {
            progressDialog.setMessage("Signing you up");
            progressDialog.setCancelable(false);
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener((Activity) requireContext(), task -> {

                        if (task.isSuccessful()) {
                            currentUser = firebaseAuth.getCurrentUser();
                            if (currentUser != null) {

                                uid = currentUser.getUid();
                                uploadImage(uid);
                            }

                        } else {
                            progressDialog.dismiss();
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                showEmailTakenDialog();
                            } else {
                                showGeneralErrorDialog();
                            }
                        }
                    });
        }
    }

    //email already taken alert
    public void showEmailTakenDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Email Already Taken");
        builder.setMessage("The email address you entered is already registered. Please use a different email address.");
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //uploading user profile image
    private void uploadImage(String uid) {

        if (imageUri != null){
            //upload picked image as user profile image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("profilePhotos")
                    .child(uid);

            storageReference.putFile(imageUri)
                    .addOnCompleteListener(task -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                profileUrl = uri.toString();
                                addUserToDatabase();
                            })
                            .addOnFailureListener(e -> {
                                showGeneralErrorDialog();
                                Log.d("ImageUploadFail", "onFailure: "+ e);
                                progressDialog.dismiss();
                            }));

        } else {

            //set up default profile image to user
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("profilePhotos")
                    .child("defaultUser.jpg");

            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                profileUrl = uri.toString();
                addUserToDatabase();
            }).addOnFailureListener(e -> {
                showGeneralErrorDialog();
                progressDialog.dismiss();
            });
        }
    }

    //add user to FireStore database along with personal info
    private void addUserToDatabase() {

        progressDialog.setMessage("Almost there");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("users");



        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", uid);
        userData.put("userName", userNameInput);
        userData.put("email", email);
        userData.put("joinedDate", FieldValue.serverTimestamp());
        userData.put("profileUrl", profileUrl);

        usersCollection.add(userData)
                .addOnSuccessListener(documentReference -> {

                    progressDialog.dismiss();

                    //setup animation for linear layouts
                    personalizeContainer.setAlpha(1f);

                    personalizeContainer.animate()
                            .alpha(0f)
                            .translationY(100f)
                            .setDuration(300)
                            .start();

                    personalizeContainer.setVisibility(View.GONE);

                    completeContainer.setVisibility(View.VISIBLE);
                    completeContainer.setAlpha(0f);
                    completeContainer.setTranslationY(100f);

                    completeContainer.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(300)
                            .setStartDelay(150)
                            .start();

                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showGeneralErrorDialog();
                    Log.d("addUserToDatabaseFail", "onFailure: "+ e);
                });
    }

    //General error message
    public void showGeneralErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Error");
        builder.setMessage("An error occurred. Please try again later.");
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }





}