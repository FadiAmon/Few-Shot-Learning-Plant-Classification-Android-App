package com.example.myapplication;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AboutUs extends AppCompatActivity {

    private TextView aboutUsTextView;
    private Button mainButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        aboutUsTextView = findViewById(R.id.textView3);
        mainButton = findViewById(R.id.mainbutton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutUs.this, MainActivity.class));
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Retrieve the "aboutus" field from Firestore for the current user
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String aboutUs = documentSnapshot.getString("aboutus");
                            aboutUsTextView.setText(aboutUs);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure case
                    });
        } else {
            // User not logged in
        }
    }
}
