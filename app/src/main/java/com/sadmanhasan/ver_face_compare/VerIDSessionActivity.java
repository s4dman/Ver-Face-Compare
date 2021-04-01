package com.sadmanhasan.ver_face_compare;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.appliedrec.verid.core2.Bearing;
import com.appliedrec.verid.core2.VerID;
import com.appliedrec.verid.core2.VerIDFactory;
import com.appliedrec.verid.core2.VerIDFactoryDelegate;
import com.appliedrec.verid.core2.session.LivenessDetectionSessionSettings;
import com.appliedrec.verid.core2.session.VerIDSessionResult;
import com.appliedrec.verid.ui2.IVerIDSession;
import com.appliedrec.verid.ui2.VerIDSession;
import com.appliedrec.verid.ui2.VerIDSessionDelegate;


public class VerIDSessionActivity extends AppCompatActivity implements VerIDFactoryDelegate, VerIDSessionDelegate {

    private static final String TAG = "VerIDSessionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_id_session);
    }

    void startLivenessDetectionSession() {
        VerIDFactory veridFactory = new VerIDFactory(this);
        veridFactory.setDelegate(this);
        veridFactory.createVerID();
    }

    @Override
    public void onVerIDCreated(VerIDFactory verIDFactory, VerID verID) {
        LivenessDetectionSessionSettings settings = new LivenessDetectionSessionSettings();
        settings.setFaceCaptureCount(1);
        VerIDSession session = new VerIDSession(verID, settings);
        session.setDelegate(this);
        session.start();
    }

    @Override
    public void onVerIDCreationFailed(VerIDFactory verIDFactory, Exception e) {
        Log.e(TAG, "onVerIDCreationFailed: ", e.getCause());
    }

    @SuppressLint("NewApi")
    @Override
    public void onSessionFinished(IVerIDSession<?> iVerIDSession, VerIDSessionResult verIDSessionResult) {
        if (!verIDSessionResult.getError().isPresent()) {
            // Session succeeded
            verIDSessionResult.getFirstFaceCapture(Bearing.STRAIGHT).ifPresent(faceCapture -> {
                try {
                    // new ProfilePhotoHelper(this).setProfilePhoto(faceCapture.getFaceImage());
                } catch (Exception ignore) {
                }
            });
            //Intent intent = new Intent(this, RegisteredUserActivity.class);
            //startActivity(intent);
            //finish();
        } else {
            Log.e(TAG, "onSessionFinished: Session Failed");
        }
    }

}