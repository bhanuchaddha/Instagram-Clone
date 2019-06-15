package com.ben.instagramclone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class UserActivity extends AppCompatActivity {

    private FirebaseUser user;
    private TextView welcomeUser;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private ImageView selectedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        welcomeUser = findViewById(R.id.userWelcome);
        selectedImageView = findViewById(R.id.selectedImageView);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        user = mAuth.getCurrentUser();

        welcomeUser.setText("Welcome " + user.getEmail());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.share_image:
                accessImageGallery();
                break;
            case R.id.take_photo:
                accessCamera();
                break;

        }

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 7:
                    if (data != null && data.getData() != null) {
                        showSelectedImage(data.getData());
                        uploadFile(data.getData());
                    }
                    break;
                case 1:
                    showCameraImage(data);
                    uploadCameraImage(data);
                    break;
            }
        }

    }

    public void goToUserFeed(View view){
        Util.navigateTo(UserFeedActivity.class, this);
    }


    private void showSelectedImage(Uri path) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), path);
            selectedImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showCameraImage(Intent data) {
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        selectedImageView.setImageBitmap(imageBitmap);
    }

    private void accessImageGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 7);
    }

    private void accessCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 1);
        }

    }

    private void uploadCameraImage(Intent data){
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();

        StorageReference riversRef = mStorageRef.child("files/" + new Random().nextInt());

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("uploader", user.getEmail())
                .build();

        riversRef.putBytes(imageBytes, metadata)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                        Toast.makeText(UserActivity.this, "file uploaded at " + downloadUrl.getPath(), Toast.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(UserActivity.this, "File upload failure", Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void uploadFile(Uri path) {
        //Uri file = Uri.fromFile(new File(path));
        StorageReference riversRef = mStorageRef.child("files/" + path.getLastPathSegment());

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("uploader", user.getEmail())
                .build();

        riversRef.putFile(path, metadata)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                        Toast.makeText(UserActivity.this, "file uploaded at " + downloadUrl.getPath(), Toast.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(UserActivity.this, "File upload failure", Toast.LENGTH_LONG).show();
                    }
                });
    }
}