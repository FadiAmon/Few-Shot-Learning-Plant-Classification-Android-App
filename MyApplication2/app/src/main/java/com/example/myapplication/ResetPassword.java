package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {
EditText email;
Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        email = findViewById(R.id.mailtext);
        button=findViewById(R.id.resetebutton);


        button.setOnClickListener(view -> {
            resetPassword(email.getText().toString());

        });



    }

    private void resetPassword(String email) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Password reset email sent successfully
                            Toast.makeText(ResetPassword.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to send password reset email
                            Toast.makeText(ResetPassword.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}