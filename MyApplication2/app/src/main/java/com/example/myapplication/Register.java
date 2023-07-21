package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText email, password ,name;
    FirebaseAuth Auth;
    FirebaseFirestore Store;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name= findViewById(R.id.nametext);
        email = findViewById(R.id.mailtext);
        password = findViewById(R.id.passtext);

        Auth = FirebaseAuth.getInstance();
        Store = FirebaseFirestore.getInstance();

    }

    public void Submit_details(View view) {
        String name_input = name.getText().toString();
        String email_input=email.getText().toString();
        String password_input=password.getText().toString();

        Log.d("Tag",email_input + password_input);


        Auth.createUserWithEmailAndPassword(email_input,password_input).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Register.this, "משתמש נוצר בהצלחה!", Toast.LENGTH_SHORT).show();
                    String userID = Auth.getCurrentUser().getUid();

                    DocumentReference documentReference = Store.collection("users").document(userID);
                    Map<String, Object> user = new HashMap<>();
                    user.put("email",email_input);
                    user.put("name", name_input);
                    user.put("password", password_input);
                    user.put("feedback", ".");
                    user.put("classificationHis", ".");


                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "onSuccess: user profile is created for" + userID);


                        }
                    });
                    startActivity(new Intent(getApplicationContext(), LogIn.class));
                }
                else {
                    Toast.makeText(Register.this, "משהו לא התסדר... אנא נסה לפתוח משתמש שנית"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();



                }
            }
        });


    }


}