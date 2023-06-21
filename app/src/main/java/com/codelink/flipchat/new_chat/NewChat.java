package com.codelink.flipchat.new_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;


import com.codelink.flipchat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewChat extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference usersRef = db.collection("users");

    private RecyclerView userList;
    private NewUserViewAdapter adapter;
    private List<UsersModelClass> usersList;

    private TextView noUsersMessage;
    private ProgressDialog searching;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search, menu);

        MenuItem menuItem = menu.findItem(R.id.newUserSearch);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search name");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchUser(s.trim());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);

        userList = findViewById(R.id.userList);
        userList.setLayoutManager(new LinearLayoutManager(this));
        noUsersMessage = findViewById(R.id.noUsersMessage);

        usersList = new ArrayList<>();




    }

    @SuppressLint("NotifyDataSetChanged")
    private void searchUser(String userName) {

        noUsersMessage.setVisibility(View.GONE);
        userList.setVisibility(View.GONE);
        searchProgressDialog();
        usersList.clear(); // Clear the existing data

        usersRef.whereEqualTo("userName", userName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        searching.dismiss();

                        if (!queryDocumentSnapshots.isEmpty()) {

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                // Retrieve the data
                                Map<String, Object> data = documentSnapshot.getData();

                                String profileUrl = (String) data.get("profileUrl");
                                String userName = (String) data.get("userName");
                                String userBio = (String) data.get("bio");

                                // Create a UsersModelClass instance
                                UsersModelClass user = new UsersModelClass(profileUrl, userName, userBio);
                                usersList.add(user);
                            }

                            // Create an instance of the adapter and pass the usersList
                            adapter = new NewUserViewAdapter(usersList);

                            userList.setVisibility(View.VISIBLE);
                            userList.setAdapter(adapter);
                        } else {

                            noUsersMessage.setText("No results found for \"" + userName + "\"");
                            noUsersMessage.setVisibility(View.VISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        searching.dismiss();
                        Toast.makeText(NewChat.this, "Error while searching the user", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void searchProgressDialog(){
        searching = new ProgressDialog(this);
        searching.setMessage("Searching");
        searching.show();
    }

}