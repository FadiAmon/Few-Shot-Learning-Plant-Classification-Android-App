package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SendFeedback extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText feedbackEditText;
    private FirebaseUser currentUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Get reference to the feedback EditText
        feedbackEditText = findViewById(R.id.feedbacktext);

        // Get current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Set OnClickListener for the sendButton
        Button sendButton = findViewById(R.id.sendbutton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = feedbackEditText.getText().toString();
                addFeedbackToCurrentUser(feedback);
            }
        });
    }

    private void addFeedbackToCurrentUser(String feedback) {
        if (currentUser != null) {
            // Get the current user's ID
            String currentUserId = currentUser.getUid();

            // Retrieve the current feedback value from Firestore
            db.collection("users").document(currentUserId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String currentFeedback = documentSnapshot.getString("feedback");
                                String updatedFeedback = currentFeedback + "\n" + feedback;

                                // Add the updated feedback field to the current user in Firestore
                                db.collection("users").document(currentUserId)
                                        .update("feedback", updatedFeedback)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(SendFeedback.this, "Feedback added successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SendFeedback.this, "Failed to add feedback. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(SendFeedback.this, "User document does not exist.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SendFeedback.this, "Failed to retrieve current feedback value. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // No user is currently signed in
            Toast.makeText(SendFeedback.this, "No user is currently signed in.", Toast.LENGTH_SHORT).show();
        }
    }

}
