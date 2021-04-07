package com.sadmanhasan.ver_face_compare;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import com.appliedrec.verid.core2.Bearing;
import com.appliedrec.verid.core2.Face;
import com.appliedrec.verid.core2.RecognizableFace;
import com.appliedrec.verid.core2.VerID;
import com.appliedrec.verid.core2.VerIDCoreException;
import com.appliedrec.verid.core2.VerIDImageBitmap;
import com.appliedrec.verid.core2.session.VerIDSessionResult;
import com.appliedrec.verid.ui2.ISessionActivity;
import com.appliedrec.verid.ui2.SessionParameters;
import com.bumptech.glide.Glide;

import java.io.IOException;

public class SessionResultActivity extends AppCompatActivity implements ISessionActivity {

    private static final String TAG = "SessionResultActivity";
    private VerID verID;
    private VerIDSessionResult sessionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_result);
        SharedPreferences sharedPref = this.getSharedPreferences("MY_PREF", MODE_PRIVATE);
        Uri imgURI = Uri.parse(sharedPref.getString("IMG_URI", ""));
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgURI);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            ImageView galleryImg = findViewById(R.id.img_gallery);
            Glide.with(this).load(rotatedBitmap).into(galleryImg);
            faceCompare(rotatedBitmap);
        } catch (IOException | VerIDCoreException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void setSessionParameters(SessionParameters sessionParameters) {
        verID = sessionParameters.getVerID();
        sessionResult = sessionParameters.getSessionResult().orElse(null);
    }

    @SuppressLint("NewApi")
    private void faceCompare(Bitmap bitmap) throws VerIDCoreException {
        VerIDImageBitmap image = new VerIDImageBitmap(bitmap, ExifInterface.ORIENTATION_NORMAL);
        Face[] faces = verID.getFaceDetection().detectFacesInImage(bitmap, 1, 0);
        if (faces.length > 0) {
            RecognizableFace[] recognizableFaces = verID.getFaceRecognition().createRecognizableFacesFromFaces(faces, image);
            sessionResult.getFirstFaceCapture(Bearing.STRAIGHT).ifPresent(faceCapture -> {
                try {
                    float score = verID.getFaceRecognition().compareSubjectFacesToFaces(recognizableFaces, new RecognizableFace[]{faceCapture.getFace()});
                    Log.d(TAG, "SessionScore: " + score);
                } catch (VerIDCoreException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}