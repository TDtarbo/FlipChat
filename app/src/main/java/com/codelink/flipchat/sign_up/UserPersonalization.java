package com.codelink.flipchat.sign_up;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codelink.flipchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;
import gun0912.tedimagepicker.builder.TedImagePicker;
import gun0912.tedimagepicker.builder.listener.OnSelectedListener;

public class UserPersonalization extends Fragment {


    private ImageView chooseImageBtn, retryBtn;

    private CircleImageView choosedImage;

    private RelativeLayout imagePreview;

    private Button signUpBtn;

    private Uri imageUri;

    private String email, password, uid;


    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_personalization, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            email = bundle.getString("email");
            password = bundle.getString("password");
        }
        firebaseAuth = FirebaseAuth.getInstance();

        chooseImageBtn = view.findViewById(R.id.chooseImageBtn);
        choosedImage = view.findViewById(R.id.choosedImage);
        imagePreview = view.findViewById(R.id.imagePreview);
        retryBtn = view.findViewById(R.id.retryBtn);
        signUpBtn = view.findViewById(R.id.signUpBtn);

        chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePicker();
            }
        });

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePicker();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRegistration();
            }
        });

        return view;
    }

    private void uploadImage(String uid, ProgressDialog progressDialog) {
        progressDialog.setMessage("Uploading Image");

        if (imageUri != null){

            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("profilePhotos")
                    .child(uid);

            storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();

                            Log.d("url", url);

                            progressDialog.dismiss();
                        }
                    });
                }
            });
        }
    }

    private void imagePicker() {
        TedImagePicker.with(getContext())
                .start(new OnSelectedListener() {
                    @Override
                    public void onSelected(@NotNull Uri uri) {

                        chooseImageBtn.setVisibility(View.GONE);
                        imagePreview.setVisibility(View.VISIBLE);
                        choosedImage.setImageURI(uri);
                        imageUri = uri;
                    }
                });


    }

    private void userRegistration() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Signing you up...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // User registration successful
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            if (currentUser != null) {
                                uid = currentUser.getUid();
                                uploadImage(uid, progressDialog);
                            }

                        } else {
                            progressDialog.dismiss();
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                showEmailTakenDialog();
                            } else {
                                showGeneralErrorDialog();
                            }
                        }
                    }
                });
    }

    public void showEmailTakenDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Email Already Taken");
        builder.setMessage("The email address you entered is already registered. Please use a different email address.");
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showGeneralErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Error");
        builder.setMessage("An error occurred. Please try again later.");
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }





}