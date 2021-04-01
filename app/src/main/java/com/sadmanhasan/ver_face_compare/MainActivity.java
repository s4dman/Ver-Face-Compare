package com.sadmanhasan.ver_face_compare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.appliedrec.verid.core2.VerID;
import com.appliedrec.verid.core2.VerIDFactory;
import com.appliedrec.verid.core2.VerIDFactoryDelegate;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int SELECT_PHOTO = 1;
    private ImageView imageUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVerID(); // Creating Ver-ID instance
        Button btnSelectImage = findViewById(R.id.btn_select_img);
        btnSelectImage.setOnClickListener(view -> openGallery());
    }

    // Accessing local storage for image
    @SuppressLint("NewApi")
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select an image"), SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PHOTO) {
            //Creating a Bitmap image
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Matrix matrix = new Matrix();
                matrix.postRotate(0);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                imageFromBitmap(rotatedBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void imageFromBitmap(Bitmap bitmap) {
        imageUser = findViewById(R.id.img_user_face);
        Glide.with(this)
                .load(bitmap)
                .into(imageUser);
        imageUser.setImageBitmap(bitmap);
        int rotationDegree = 0;
        InputImage image = InputImage.fromBitmap(bitmap, rotationDegree);
        detectFace(image);
    }

    // Start face detection from image
    private void detectFace(InputImage image) {
        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .setMinFaceSize(0.15f)
                        .enableTracking()
                        .build();

        FaceDetector detector = FaceDetection.getClient(options);
        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(faces -> {
                            if (faces.size() > 0) {
                                Toast.makeText(this, "Face Detected", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(this, AuthenticateActivity.class);
                                startActivity(intent);
                            } else Log.d(TAG, String.valueOf(faces.size()));
                        })
                        .addOnFailureListener(
                                e -> {
                                    Toast.makeText(MainActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "detectFace: " + e.getMessage());
                                    Log.d(TAG, "detectFace: " + e.getLocalizedMessage());
                                    Log.d(TAG, "detectFace: " + e.getCause());
                                    Log.d(TAG, "detectFace: " + Arrays.toString(e.getStackTrace()));
                                });
    }

    private void initVerID() {
        VerIDFactory verIDFactory = new VerIDFactory(this, new VerIDFactoryDelegate() {
            @Override
            public void onVerIDCreated(VerIDFactory verIDFactory, VerID verID) {
                // You can now use the VerID instance
            }

            @Override
            public void onVerIDCreationFailed(VerIDFactory verIDFactory, Exception e) {
                Log.e(TAG, "onVerIDCreationFailed: ", e.getCause());
            }
        });
        verIDFactory.createVerID();
    }


}