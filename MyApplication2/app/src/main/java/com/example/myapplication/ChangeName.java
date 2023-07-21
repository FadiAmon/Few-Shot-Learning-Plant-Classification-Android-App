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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ChangeName extends AppCompatActivity {

    private EditText newNameEditText;
    private Button changeNameButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        newNameEditText = findViewById(R.id.newnametext);
        changeNameButton = findViewById(R.id.changebutton);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        changeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeName();
            }
        });
    }

    private void changeName() {
        String newName = newNameEditText.getText().toString().trim();

        if (newName.isEmpty()) {
            Toast.makeText(this, "Please enter a new name", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = mFirestore.collection("users").document(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newName);

        userRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ChangeName.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Finish the activity and return to the previous screen
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChangeName.this, "Failed to update name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
