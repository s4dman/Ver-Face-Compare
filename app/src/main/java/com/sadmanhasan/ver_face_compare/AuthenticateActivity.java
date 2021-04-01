package com.sadmanhasan.ver_face_compare;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.appliedrec.verid.core2.VerID;
import com.appliedrec.verid.core2.VerIDFactory;
import com.appliedrec.verid.core2.VerIDFactoryDelegate;
import com.appliedrec.verid.core2.session.AuthenticationSessionSettings;
import com.appliedrec.verid.core2.session.FaceExtents;
import com.appliedrec.verid.core2.session.LivenessDetectionSessionSettings;
import com.appliedrec.verid.core2.session.VerIDSessionResult;
import com.appliedrec.verid.ui2.IVerIDSession;
import com.appliedrec.verid.ui2.VerIDSession;
import com.appliedrec.verid.ui2.VerIDSessionDelegate;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class AuthenticateActivity extends AppCompatActivity implements VerIDFactoryDelegate, VerIDSessionDelegate {

    private static final String TAG = "AuthenticateActivity";
    private VerID verID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        startLivenessDetectionSession();
    }

    void startLivenessDetectionSession() {
        VerIDFactory veridFactory = new VerIDFactory(this);
        veridFactory.setDelegate(this);
        veridFactory.createVerID();
    }

    @Override
    public void onVerIDCreated(VerIDFactory verIDFactory, VerID verID) {
        LivenessDetectionSessionSettings settings = new LivenessDetectionSessionSettings();
        VerIDSession session = new VerIDSession(verID, settings);
        session.setDelegate(this);
        session.start();
        authenticate(verID);
    }

    @Override
    public void onVerIDCreationFailed(VerIDFactory verIDFactory, Exception e) {
        Log.d(TAG, "onVerIDCreationFailed: " + Arrays.toString(e.getStackTrace()));
    }

    private void authenticate(VerID verID) {
        int faceCaptureCount = 1;
        float yawThreshold = 17.0F;
        float pitchThreshold = 12.0F;
        FaceExtents expectedFaceExtents = new FaceExtents(0.65F, 0.85F);
        boolean faceCoveringDetectionEnabled = true;
        AtomicInteger sessionRunCount = new AtomicInteger(0);

        AuthenticationSessionSettings settings = new AuthenticationSessionSettings("default");
        settings.setFaceCaptureCount(faceCaptureCount);
        settings.setYawThreshold(yawThreshold);
        settings.setPitchThreshold(pitchThreshold);
        settings.setExpectedFaceExtents(expectedFaceExtents);
        settings.setFaceCoveringDetectionEnabled(faceCoveringDetectionEnabled);
        settings.setSessionDiagnosticsEnabled(true);
        sessionRunCount.set(0);

        VerIDSession authenticationSession;
        authenticationSession = new VerIDSession(verID, settings);
        authenticationSession.setDelegate(this);
        authenticationSession.start();
    }

    @Override
    public void onSessionFinished(IVerIDSession<?> iVerIDSession, VerIDSessionResult verIDSessionResult) {

    }

}