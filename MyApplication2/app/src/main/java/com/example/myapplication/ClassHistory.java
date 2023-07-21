package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClassHistory extends AppCompatActivity {

    private TextView historyTextView;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_history);

        historyTextView = findViewById(R.id.textView3);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        retrieveClassHistory();
    }

    private void retrieveClassHistory() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String classHistory = documentSnapshot.getString("classificationHis");
                                historyTextView.setText(classHistory);
                            } else {
                                historyTextView.setText("No class history found for the current user.");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(ClassHistory.this, "Failed to retrieve class history. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(ClassHistory.this, "No user is currently signed in.", Toast.LENGTH_SHORT).show();
        }
    }
}
