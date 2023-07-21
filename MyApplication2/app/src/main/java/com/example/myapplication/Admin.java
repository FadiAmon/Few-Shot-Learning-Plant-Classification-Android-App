package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class Admin extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button deleteButton = findViewById(R.id.deletebutton);
        Button addButton = findViewById(R.id.addbutton);
        Button changeButton = findViewById(R.id.changebutton);
        Button viewButton = findViewById(R.id.viewbutton);


        deleteButton.setOnClickListener(view -> {
            Intent intent = new Intent(Admin.this, DeleteUser.class);
            startActivity(intent);
        });

        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(Admin.this, AddUser.class);
            startActivity(intent);
        });

        changeButton.setOnClickListener(view -> {
            Intent intent = new Intent(Admin.this, ChangeAboutUs.class);
            startActivity(intent);
        });
        viewButton.setOnClickListener(view -> {
            Intent intent = new Intent(Admin.this, ViewFeedback.class);
            startActivity(intent);
        });

    }
}






