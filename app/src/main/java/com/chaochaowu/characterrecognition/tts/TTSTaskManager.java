package com.chaochaowu.characterrecognition.tts;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;

import java.util.Vector;

public  class TTSTaskManager implements OncompleteListener {
    private  final Vector<String> studentTask= new Vector();
    private  final Vector<String> parentTask = new Vector();
    private   static   TTSTaskManager  ttsTaskManager;
    private static boolean isInited = false;
    private Thread thread;
    private static TTS_Manager tts_manager;
    private SynthesizerListenerImpl synthesizerListener;
    private InitListener initListener;

    private TTSTaskManager() {
        tts_manager = TTS_Manager.getInstance();
    }

    public static TTSTaskManager getInstance(){
        if(ttsTaskManager==null){
            synchronized (TTSTaskManager.class){
                if(ttsTaskManager==null){
                    ttsTaskManager =new TTSTaskManager();
                }
            }
        }
        return ttsTaskManager;
    }


     public   void addInStu(String s){
        studentTask.add(s);
        excute();
    }

    public  void addInPar(String s){
        parentTask.add(s);
        excute();
    }
    private synchronized void excute() {
        if(!isInited){
            if(initListener==null){
                initListener = new InitListenerImpl();
            }
            tts_manager.initTTS(initListener);
        }

        if(thread==null&&isInited&&(studentTask.size()>0||parentTask.size()>0)){
            thread = new ThreadTTS();
            thread.start();
            //开始语音播报了，这里需要发广播通知一日生活调小声音

        }
    }

    @Override
    public void onCompleted() {
        if(studentTask.size()==0||parentTask.size()==0){

        }
    }

    class InitListenerImpl implements InitListener {

        @Override
        public void onInit(int i) {
            if(i == ErrorCode.SUCCESS){
                isInited = true;
                excute();
            }

        }
    }
    private class ThreadTTS extends Thread {
        @Override
        public void run() {
            super.run();
            while (studentTask.size()==0&&parentTask.size()==0){
                if(synthesizerListener==null){
                    synthesizerListener = new SynthesizerListenerImpl(TTSTaskManager.this);
                }
                if(studentTask.size()>0){
                    tts_manager.start(studentTask.remove(0),null,synthesizerListener);
                }else {
                    tts_manager.start(parentTask.remove(0),null,synthesizerListener);
                }
            }
            thread = null;
        }
    }
}
