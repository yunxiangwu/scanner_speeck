package com.chaochaowu.characterrecognition;

import android.app.Application;

import com.chaochaowu.characterrecognition.tts.TTS_Manager;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TTS_Manager.getInstance().createUtility(this.getApplicationContext());
    }
}
