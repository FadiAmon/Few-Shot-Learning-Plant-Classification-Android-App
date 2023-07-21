package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.ChangeName;
import com.example.myapplication.ChangePass;
import com.example.myapplication.DeleteMyAccount;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Picture;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class User extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Button pictureButton = findViewById(R.id.picturebutton);
        Button nameButton = findViewById(R.id.namebutton);
        Button passButton = findViewById(R.id.passbutton);
        Button deleteButton = findViewById(R.id.deletebutton);
        Button logoutButton = findViewById(R.id.logoutbutton);

        textView = findViewById(R.id.textView4);

        db = FirebaseFirestore.getInstance();

        // Retrieve the authenticated user's ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Retrieve the username from Firestore
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String username = document.getString("name");
                            textView.setText("Hello " + username);
                        }
                    } else {
                        Toast.makeText(User.this, "Failed to retrieve username", Toast.LENGTH_SHORT).show();
                    }
                });

        Intent intent = new Intent(this, Picture.class);
        Intent intent1 = new Intent(this, ChangeName.class);
        Intent intent2 = new Intent(this, ChangePass.class);
        Intent intent3 = new Intent(this, DeleteMyAccount.class);

        logoutButton.setOnClickListener(v -> {
            Intent intent4 = new Intent(this, MainActivity.class);
            startActivity(intent4);
        });

        pictureButton.setOnClickListener(v -> {
            startActivity(intent);
        });

        nameButton.setOnClickListener(v -> {
            startActivity(intent1);
        });

        passButton.setOnClickListener(v -> {
            startActivity(intent2);
        });

        deleteButton.setOnClickListener(v -> {
            startActivity(intent3);
        });
    }
}
