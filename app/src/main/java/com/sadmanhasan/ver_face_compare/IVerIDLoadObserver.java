package com.sadmanhasan.ver_face_compare;

import com.appliedrec.verid.core2.VerID;

public interface IVerIDLoadObserver {

    void onVerIDLoaded(VerID verid);

    void onVerIDUnloaded();
}
