package com.codelink.flipchat.sign_up;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
    private String email;
    private String password;
    private String uid;
    private String profileUrl;
    private String userNameInput;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private TextInputEditText userName, userBio;
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
        userBio = view.findViewById(R.id.userBio);
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

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasInternetConnection()) {
                    userRegistration();
                } else {
                    Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

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

                    Log.d("imagePicker", "imagePicker: " + imageUri);
                });
    }

    //Signing up the user with Firebase Authentication
    private void userRegistration() {

        userNameInput = Objects.requireNonNull(userName.getText()).toString().trim();

        if (TextUtils.isEmpty(userNameInput)) {
            userName.setError("User name cannot be empty");

        } else {
            progressDialog.setMessage("Signing you up");
            progressDialog.setCancelable(false);
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener((Activity) requireContext(), task -> {

                        if (task.isSuccessful()) {
                            currentUser = firebaseAuth.getCurrentUser();
                            if (currentUser != null) {

                                uid = currentUser.getUid();
                                try {
                                    uploadImage(uid);
                                } catch (FileNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
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
    private void uploadImage(String uid) throws FileNotFoundException {
        if (imageUri != null) {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (bitmap != null) {
                int MAX_IMAGE_SIZE = 1024; // Set your desired max image size
                bitmap = resizeBitmap(bitmap, MAX_IMAGE_SIZE);
                byte[] imageData = convertBitmapToByteArray(bitmap, 80);

                uploadImageToStorage(uid, imageData);
            } else {
                handleNullBitmap();
            }
        } else {
            setDefaultProfileImage(uid);
        }
    }

    // Method to resize the bitmap to a desired maximum image size
    private Bitmap resizeBitmap(Bitmap bitmap, int maxImageSize) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width > maxImageSize || height > maxImageSize) {
            float bitmapRatio = (float) width / (float) height;
            if (bitmapRatio > 1) {
                width = maxImageSize;
                height = (int) (width / bitmapRatio);
            } else {
                height = maxImageSize;
                width = (int) (height * bitmapRatio);
            }
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    // Method to convert a bitmap to a byte array
    private byte[] convertBitmapToByteArray(Bitmap bitmap, int compressionQuality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    // Method to upload an image as a user profile image to Firebase Storage
    private void uploadImageToStorage(String uid, byte[] imageData) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("profilePhotos")
                .child(uid);

        storageReference.putBytes(imageData)
                .addOnCompleteListener(task -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            profileUrl = uri.toString();
                            addUserToDatabase();
                        })
                        .addOnFailureListener(e -> {
                            showGeneralErrorDialog();
                            Log.d("ImageUploadFail", "onFailure: " + e);
                            progressDialog.dismiss();
                        }));
    }

    // Method to handle the case when the bitmap is null
    private void handleNullBitmap() {
        showGeneralErrorDialog();
        progressDialog.dismiss();
    }

    // Method to set up the default profile image for the user
    private void setDefaultProfileImage(String uid) {
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


    //add user to FireStore database along with personal info
    private void addUserToDatabase() {

        progressDialog.setMessage("Almost there!");
        CollectionReference userCollectionRef = FirebaseFirestore.getInstance().collection("users");

        Map<String, Object> userData = new HashMap<>();

        userData.put("userName", userNameInput);
        userData.put("email", email);
        userData.put("profileUrl", profileUrl);
        userData.put("uid", uid);
        userData.put("timestamp", FieldValue.serverTimestamp());

        String userBioInput = Objects.requireNonNull(userBio.getText()).toString().trim();

        if (userBioInput.isEmpty()){
            userData.put("bio", "Hey! I'm using FlipChat");
        }else {
            userData.put("bio", userBioInput);
        }



        userCollectionRef.document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    completeContainer.setVisibility(View.VISIBLE);
                    personalizeContainer.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    showGeneralErrorDialog();
                    progressDialog.dismiss();
                });
    }

    //show general error alert
    public void showGeneralErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Error");
        builder.setMessage("An error occurred. Please try again later.");
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //check internet connection
    private boolean hasInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
