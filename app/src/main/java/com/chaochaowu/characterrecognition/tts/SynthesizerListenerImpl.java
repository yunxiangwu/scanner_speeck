package com.chaochaowu.characterrecognition.tts;

import android.os.Bundle;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;

public class SynthesizerListenerImpl implements SynthesizerListener {
    private OncompleteListener onCompleteListener;

    public SynthesizerListenerImpl(OncompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void onBufferProgress(int i, int i1, int i2, String s) {

    }

    @Override
    public void onSpeakPaused() {

    }

    @Override
    public void onSpeakResumed() {

    }

    @Override
    public void onSpeakProgress(int i, int i1, int i2) {

    }

    @Override
    public void onCompleted(SpeechError speechError) {
        if(onCompleteListener!=null){
            onCompleteListener.onCompleted();
        }
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }
}
