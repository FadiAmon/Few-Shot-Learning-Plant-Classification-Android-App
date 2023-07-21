package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String SUPPORT_EMAIL = "rasheed2001rasheed@gmail.com";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button registerButton = findViewById(R.id.registerbutton);
        Button loginButton = findViewById(R.id.addbutton);
        Button aboutUsButton = findViewById(R.id.aboutusbutton);
        Button contactUsButton = findViewById(R.id.contactusbutton);

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LogIn.class);
            startActivity(intent);
        });

        aboutUsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutUs.class);
            startActivity(intent);
        });

        contactUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { SUPPORT_EMAIL });
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact Us");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello, I have a question...");

        startActivity(Intent.createChooser(emailIntent, "Send email"));
    }
}
