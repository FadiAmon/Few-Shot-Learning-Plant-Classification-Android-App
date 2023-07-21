package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangeAboutUs extends AppCompatActivity {

    private EditText editText;
    private Button changeButton;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_about_us);

        editText = findViewById(R.id.newAbouttext);
        changeButton = findViewById(R.id.changebutton);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newText = editText.getText().toString();

                if (!newText.isEmpty()) {
                    String userId = currentUser.getUid();
                    DocumentReference userRef = db.collection("users").document(userId);

                    userRef.update("aboutus", newText)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ChangeAboutUs.this, "About Us updated successfully", Toast.LENGTH_SHORT).show();
                                        // Update the label in the UI

                                    } else {
                                        Toast.makeText(ChangeAboutUs.this, "Failed to update About Us", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(ChangeAboutUs.this, "Please enter new text", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}

