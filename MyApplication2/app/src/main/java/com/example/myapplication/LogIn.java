package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LogIn extends AppCompatActivity {

    EditText email, password;
    FirebaseAuth Auth;
    Button forButton;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        email = findViewById(R.id.mailtext);
        password = findViewById(R.id.passtext);
        forButton = findViewById(R.id.forgetbutton);
        Auth = FirebaseAuth.getInstance();

        forButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LogIn.this, ResetPassword.class);
                startActivity(intent);

            }
        });

    }


    public void login(View view) {
        String email_input = email.getText().toString();
        String password_input = password.getText().toString();
        if(email_input.equals("admin1@gmail.com") && password_input.equals("123456")){

            Auth.signInWithEmailAndPassword(email_input, password_input).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LogIn.this, "התחברת בהצלחה!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Admin.class));
                    } else {
                        Toast.makeText(LogIn.this, "שם משתמש או סיסמה שגויים", Toast.LENGTH_SHORT).show();
                    }
                }
            });







        }
        else {
            Auth.signInWithEmailAndPassword(email_input, password_input).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LogIn.this, "התחברת בהצלחה!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), User.class));
                    } else {
                        Toast.makeText(LogIn.this, "שם משתמש או סיסמה שגויים", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
