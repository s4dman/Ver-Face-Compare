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
import android.widget.TextView;

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
    private VerID mVerID;
    private VerIDSessionResult mSessionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_result);
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.MY_PREF), MODE_PRIVATE);
        Uri imgURI = Uri.parse(sharedPref.getString(getString(R.string.IMG_URI), ""));
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgURI);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
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
        mVerID = sessionParameters.getVerID();
        mSessionResult = sessionParameters.getSessionResult().orElse(null);
    }

    @SuppressLint("NewApi")
    private void faceCompare(Bitmap bitmap) throws VerIDCoreException {
        TextView scoreText = findViewById(R.id.text_score);
        VerIDImageBitmap image = new VerIDImageBitmap(bitmap, ExifInterface.ORIENTATION_NORMAL);
        Face[] faces = mVerID.getFaceDetection().detectFacesInImage(image.createFaceDetectionImage(), 1, 0);
        if (faces.length > 0) {
            RecognizableFace[] recognizableFaces = mVerID.getFaceRecognition().createRecognizableFacesFromFaces(faces, image);
            mSessionResult.getFirstFaceCapture(Bearing.STRAIGHT).ifPresent(faceCapture -> {
                try {
                    float score = mVerID.getFaceRecognition().compareSubjectFacesToFaces(recognizableFaces, new RecognizableFace[]{faceCapture.getFace()});
                    scoreText.setText(String.format("%s %s", getString(R.string.Score), score));
                } catch (VerIDCoreException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}