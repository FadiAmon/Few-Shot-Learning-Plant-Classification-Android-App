package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.ml.ImageClassifier;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

public class Picture extends AppCompatActivity {

    private ImageView imageView;
    private Button preButton;
    private Button pickButton;
    private Button SendFeedbackButton;
    private Button Showbutton;
    private Button sendresult;
    private TextView resView;
    private Bitmap bitmap;
    int imagesize=128;
    private static final int REQUEST_PERMISSION_CODE = 123;
    private static final int REQUEST_CAMERA_CODE = 124;
    FirebaseAuth Auth;
    FirebaseFirestore Store;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        storageReference = FirebaseStorage.getInstance().getReference();
        imageView = findViewById(R.id.imgview);
        preButton = findViewById(R.id.preButton);
        pickButton = findViewById(R.id.pick_button);
        SendFeedbackButton = findViewById(R.id.SendFeedback);
        Showbutton=findViewById(R.id.showButton);
        sendresult=findViewById(R.id.sendresultButton);
        resView = findViewById(R.id.resview);
        Auth = FirebaseAuth.getInstance();
        currentUser = Auth.getCurrentUser();
        Store = FirebaseFirestore.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_CODE);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_CODE);
            }
        }
        SendFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Picture.this, SendFeedback.class));
            }
        });


        preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addClassToCurrentUser(processImage(bitmap));
            }
        });

        Showbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), ClassHistory.class));
            }
        });


        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPhoto();
            }
        });
        sendresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult();
            }
        });


    }

    private void sendResult() {
        String result = resView.getText().toString();
        String recipientEmail = "rasheed123@gmail.com";

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { recipientEmail });
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Result from My Application");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Result: " + result);

        startActivity(Intent.createChooser(emailIntent, "Send email"));
    }




    private void showRateUsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rate Us");
        builder.setMessage("Enjoying the app? Please rate us on the Play Store!");
        builder.setPositiveButton("Rate Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Open the app's page on the Play Store for rating
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent rateIntent = new Intent(Intent.ACTION_VIEW, uri);
                if (getPackageManager().queryIntentActivities(rateIntent, 0).size() > 0) {
                    startActivity(rateIntent);
                } else {
                    // Handle the case where the Play Store app is not installed
                    // You can open the app's page on a web browser instead
                    Uri webUri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
                    startActivity(webIntent);
                }
            }
        });
        builder.setNegativeButton("Not Now", null);
        builder.show();
    }

    private void addClassToCurrentUser(String feedback) {
        if (currentUser != null) {
            // Get the current user's ID
            String currentUserId = currentUser.getUid();

            // Retrieve the current feedback value from Firestore
            Store.collection("users").document(currentUserId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String currentFeedback = documentSnapshot.getString("classificationHis");
                                String updatedFeedback = currentFeedback + "\n" + feedback;

                                // Add the updated feedback field to the current user in Firestore
                                Store.collection("users").document(currentUserId)
                                        .update("classificationHis", updatedFeedback)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(Picture.this, "Feedback added successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Picture.this, "Failed to add feedback. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(Picture.this, "User document does not exist.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Picture.this, "Failed to retrieve current feedback value. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // No user is currently signed in
            Toast.makeText(Picture.this, "No user is currently signed in.", Toast.LENGTH_SHORT).show();
        }
    }


    private void SaveImage() {
        if (bitmap != null) {

            FirebaseStorage storage = FirebaseStorage.getInstance();

// Create a reference to the root of your Firebase Storage
            StorageReference storageRef = storage.getReference();
            StorageReference imagesRef = storageRef.child("images");

            StorageReference imageRef = imagesRef.child("my_image.jpg");
            // Convert the Bitmap to bytes (you can choose a different format if needed)
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            // Upload the image to Firebase Storage
            UploadTask uploadTask = imageRef.putBytes(imageData);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Image uploaded successfully
                Toast.makeText(Picture.this, "IMAGE UPLOADED SUCCESSFULLY", Toast.LENGTH_SHORT).show();

                // You can retrieve the download URL if needed:
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();

                    // Save the image information in Firestore
                    saveImageInfoToFirestore(downloadUrl);
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(Picture.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private String processImage(Bitmap image) {
        try {
            ImageClassifier imageClassifier = ImageClassifier.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 128, 128, 3}, DataType.FLOAT32);
            ByteBuffer bytebuffer = ByteBuffer.allocateDirect(4*imagesize*imagesize*3);
            bytebuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imagesize*imagesize];
            image.getPixels(intValues,0,image.getWidth(),0,0,image.getWidth(),image.getHeight());
            int pixel=0;
            for(int i=0;i<imagesize;i++){
                for(int j=0;j<imagesize;j++){
                    int val=intValues[pixel++];
                    bytebuffer.putFloat(((val>>16)&0xFF)*(1.f/255));
                    bytebuffer.putFloat(((val>>8)&0xFF)*(1.f/255));
                    bytebuffer.putFloat((val&0xFF)*(1.f/255));
                }
            }

            inputFeature0.loadBuffer(bytebuffer);

            // Runs model inference and gets result.
            ImageClassifier.Outputs outputs = imageClassifier.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            float[] confidence = outputFeature0.getFloatArray();
            int maxpos = 0;
            float maxconfidence = 0;
            for(int i=0; i<confidence.length; i++){
                if(confidence[i] > maxconfidence){
                    maxconfidence = confidence[i];
                    maxpos = i;
                }
            }
            String Classes[] = {"Beeblossom", "Begonia Maculata", "Coleus", "Crown of thorns", "echeveria", "Elephant_s Ear"
                    , "House Leek", "Jade Plant", "Limonium sinuatum", "Lucky Bamboo", "Mesquites"
                    , "Moon Cactus", "Myoporum", "Nerve Plant", "Paddle Plant", "Parlor Palm",
                    "Pennisetum", "Poinsettia", "Sansevieria Ballyi", "String Of Banana",
                    "Venus Fly Trap", "woolly senecio", "Zebra Cactus"};
            resView.setText(Classes[maxpos]);
            // Releases model resources if no longer used.
            imageClassifier.close();
            return Classes[maxpos];
        } catch (IOException e) {
            // TODO Handle the exception
        }
        return "null";
    }






    private void saveImageInfoToFirestore(String downloadUrl) {
        // Get the currently authenticated user
        FirebaseUser currentUser = Auth.getCurrentUser();
        if (currentUser != null) {
            // Get the user's unique ID
            String userId = currentUser.getUid();

            // Create a document reference for the user in Firestore
            DocumentReference userRef = Store.collection("users").document(userId);

            // Create a new image document in Firestore
            Map<String, Object> imageData = new HashMap<>();
            imageData.put("imageUrl", downloadUrl);
            imageData.put("timestamp", FieldValue.serverTimestamp());

            // Add the image document to the user's collection in Firestore
            userRef.collection("images").add(imageData)
                    .addOnSuccessListener(documentReference -> {
                        // Image information saved successfully
                        Toast.makeText(Picture.this, "Image info saved to Firestore", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Error occurred while saving image information
                        Toast.makeText(Picture.this, "Error saving image info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // User not authenticated, handle accordingly
            Toast.makeText(Picture.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }



    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            getAction.launch(intent);
        }
    }

    private final ActivityResultLauncher<Intent> getAction = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                            imageView.setImageBitmap(bitmap);
                            bitmap=Bitmap.createScaledBitmap(bitmap,imagesize,imagesize,false);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    public void takePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            getAction.launch(intent);
        }
    }

    public void dropPhoto(View view) {
        imageView.setImageResource(R.mipmap.ic_launcher);
    }
}
