package com.sadmanhasan.ver_face_compare;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;
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

import java.io.IOException;

public class SessionResultActivity extends AppCompatActivity implements ISessionActivity {

    private static final String TAG = "SessionResultActivity";
    private VerID verID;
    private VerIDSessionResult sessionResult;
    private VerIDImageBitmap verImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_result);
        SharedPreferences sharedPref = this.getSharedPreferences("MY_PREF", MODE_PRIVATE);
        Uri imgURI = Uri.parse(sharedPref.getString("IMG_URI", ""));
        Log.d(TAG, "IMG SessionResult: " + imgURI);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgURI);
//            verImage = new VerIDImageBitmap(bitmap, ExifInterface.ORIENTATION_NORMAL);
//            faceCompare(verImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void setSessionParameters(SessionParameters sessionParameters) {
        verID = sessionParameters.getVerID();
        sessionResult = sessionParameters.getSessionResult().orElse(null);
    }

    @SuppressLint("NewApi")
    private void faceCompare(VerIDImageBitmap bitmap) throws VerIDCoreException {
        Face[] faces = verID.getFaceDetection().detectFacesInImage(bitmap, 1, 0);
        if (faces.length > 0) {
            RecognizableFace[] recognizableFaces = verID.getFaceRecognition().createRecognizableFacesFromFaces(faces, bitmap);
            sessionResult.getFirstFaceCapture(Bearing.STRAIGHT).ifPresent(faceCapture -> {
                try {
                    float score = verID.getFaceRecognition().compareSubjectFacesToFaces(recognizableFaces, new RecognizableFace[]{faceCapture.getFace()});
                    Log.d(TAG, "score: " + score);
                } catch (VerIDCoreException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}