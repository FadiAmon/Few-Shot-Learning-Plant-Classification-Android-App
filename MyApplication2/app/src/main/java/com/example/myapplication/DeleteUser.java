package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class DeleteUser extends AppCompatActivity {
    private Button loginButton;
    private EditText emailText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        loginButton = findViewById(R.id.deletebutton);
        emailText = findViewById(R.id.deletenametext);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = emailText.getText().toString();

                if (!userEmail.isEmpty()) {
                    db.collection("users")
                            .whereEqualTo("email", userEmail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                            String userId = documentSnapshot.getId();
                                            String userPassword = documentSnapshot.getString("password");

                                            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                // Login success
                                                                Toast.makeText(DeleteUser.this, "Logged in successfully", Toast.LENGTH_SHORT).show();

                                                                // Delete the user
                                                                FirebaseAuth.getInstance().getCurrentUser().delete()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    // User deleted successfully
                                                                                    Toast.makeText(DeleteUser.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                                                                                } else {
                                                                                    // Failed to delete user
                                                                                    Toast.makeText(DeleteUser.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                            } else {
                                                                // Login failed
                                                                Exception exception = task.getException();
                                                                if (exception instanceof FirebaseAuthInvalidUserException) {
                                                                    Toast.makeText(DeleteUser.this, "User does not exist", Toast.LENGTH_SHORT).show();
                                                                } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                                                    Toast.makeText(DeleteUser.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(DeleteUser.this, "Failed to log in with the specified email", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(DeleteUser.this, "User not found", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(DeleteUser.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(DeleteUser.this, "Please enter the email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
