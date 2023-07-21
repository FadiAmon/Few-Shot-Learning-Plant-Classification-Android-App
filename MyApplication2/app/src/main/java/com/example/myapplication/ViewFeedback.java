package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class ViewFeedback extends AppCompatActivity {

    private EditText emailEditText;
    private TextView feedbackTextView;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feedback);

        emailEditText = findViewById(R.id.emailEditText);
        feedbackTextView = findViewById(R.id.textView3);
        Button viewButton = findViewById(R.id.viewButton);

        db = FirebaseFirestore.getInstance();

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                retrieveFeedback(email);
            }
        });
    }

    private void retrieveFeedback(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String feedback = queryDocumentSnapshots.getDocuments().get(0).getString("feedback");
                            feedbackTextView.setText(feedback);
                        } else {
                            feedbackTextView.setText("No feedback found for the given email.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ViewFeedback.this, "Failed to retrieve feedback. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
