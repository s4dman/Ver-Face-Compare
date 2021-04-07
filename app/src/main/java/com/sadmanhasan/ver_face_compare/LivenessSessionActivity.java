package com.sadmanhasan.ver_face_compare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.appliedrec.verid.core2.VerID;
import com.appliedrec.verid.core2.VerIDFactory;
import com.appliedrec.verid.core2.VerIDFactoryDelegate;
import com.appliedrec.verid.core2.session.LivenessDetectionSessionSettings;
import com.appliedrec.verid.core2.session.RegistrationSessionSettings;
import com.appliedrec.verid.core2.session.VerIDSessionResult;
import com.appliedrec.verid.ui2.ISessionActivity;
import com.appliedrec.verid.ui2.IVerIDSession;
import com.appliedrec.verid.ui2.VerIDSession;
import com.appliedrec.verid.ui2.VerIDSessionDelegate;

import java.util.Arrays;

public class LivenessSessionActivity extends AppCompatActivity implements VerIDFactoryDelegate, VerIDSessionDelegate {

    private static final String TAG = "LivenessSessionActivity";

    @Override
    public boolean shouldSessionSpeakPrompts(IVerIDSession<?> session) {
        return false;
    }

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
        Log.d(TAG, "VerID Created: " + verID);
        LivenessDetectionSessionSettings settings = new LivenessDetectionSessionSettings();
        VerIDSession session = new VerIDSession(verID, settings);
        session.setDelegate(this);
        session.start();
    }

    @Override
    public void onVerIDCreationFailed(VerIDFactory verIDFactory, Exception e) {
        Log.d(TAG, "onVerIDCreationFailed: " + Arrays.toString(e.getStackTrace()));
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSessionFinished(IVerIDSession<?> iVerIDSession, VerIDSessionResult verIDSessionResult) {
        if (!verIDSessionResult.getError().isPresent()) {
            Log.d(TAG, "onSessionFinished: " + verIDSessionResult.getFaceCaptures().length);
        } else {
            Log.d(TAG, "onSessionFinished: Error " + verIDSessionResult.getError());
        }
    }

    @SuppressLint("NewApi")
    @Override
    public boolean shouldSessionDisplayResult(IVerIDSession<?> session, VerIDSessionResult result) {
        return !(session.getSettings() instanceof RegistrationSessionSettings && !result.getError().isPresent());
    }

    @Override
    public <A extends Activity & ISessionActivity> Class<A> getSessionResultActivityClass(IVerIDSession<?> session, VerIDSessionResult result) {
        Log.d(TAG, "getSessionResultActivityClass: ");
        Log.d(TAG, "getSessionResultActivityClass: " + result.getFaceCaptures().length);
        return (Class<A>) SessionResultActivity.class;
    }
}