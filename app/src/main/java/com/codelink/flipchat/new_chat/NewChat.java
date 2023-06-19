package com.codelink.flipchat.new_chat;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.codelink.flipchat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class NewChat extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference usersRef = db.collection("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);

        TextView textView = findViewById(R.id.users);

        usersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                String users = "";

                     for (QueryDocumentSnapshot documentSnapShot : queryDocumentSnapshots){

                         Map<String, Object> data = documentSnapShot.getData();

                         // Access specific fields from the data
                         String field1 = (String) data.get("userName");

                         users += field1 + "\n\n";

                     }

                     textView.setText(users);
            }
        });

    }
}