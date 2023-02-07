package com.example.provacippi;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import androidx.core.content.FileProvider;



import android.widget.Toast;

import java.io.File;


public class MainActivity extends AppCompatActivity {


    private Uri imageToUploadUri;
    private ActivityResultLauncher<Intent> takePicActivityLauncher;
    private ActivityResultLauncher<Intent> loadPicActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTheme(R.style.Theme_ProvaCippi);

        takePicActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if(imageToUploadUri != null){
                        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                        intent.putExtra("uri", imageToUploadUri);
                        startActivity(intent);
                    }else{
                        Toast.makeText(MainActivity.this, "error while capturing image", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "error while capturing image (Result non OK)", Toast.LENGTH_LONG).show();
                }
            }
        });

        loadPicActivityLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Uri imageUri = result.getData().getData();
                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                    intent.putExtra("uri", imageUri);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "error while choosing image", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    //per scattare la foto (e fare l'attività associata)
    public void onCameraButton(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File image = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"Image.jpg");
        Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", image);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
        imageToUploadUri = Uri.fromFile(image);
        if(takePictureIntent.resolveActivity(this.getPackageManager())!= null){
            takePicActivityLauncher.launch(takePictureIntent); //TODO (onActivityResult)
        }else{
            Toast.makeText(this, "Error while choosing image", Toast.LENGTH_SHORT).show();
        }
    }


    //per prendere una foto da galleria
    public void onLoadButton(View view){
        Intent loadPictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        loadPictureIntent.setType("image/*");
        loadPicActivityLauncher.launch(loadPictureIntent); //TODO (onActivityResult)
    }





}