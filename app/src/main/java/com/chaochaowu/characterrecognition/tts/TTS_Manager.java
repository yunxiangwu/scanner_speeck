package com.chaochaowu.characterrecognition.tts;

import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;

public class TTS_Manager {
    private  SpeechSynthesizer speechSynthesizer;
    private String voicerLocal = "xiaoyan";

    private static final TTS_Manager ourInstance = new TTS_Manager();
    private Context mContext;

    public static TTS_Manager getInstance() {
        return ourInstance;
    }

    private TTS_Manager() {
    }

    public  void createUtility(Context context){
        mContext = context;
        if(null==SpeechUtility.getUtility()){
            SpeechUtility.createUtility(context, "appid=" + "5f0fb0cb");
        }
    }

    public synchronized void initTTS(InitListener initListener){
        if(speechSynthesizer==null){
            speechSynthesizer  = SpeechSynthesizer.createSynthesizer(mContext,initListener);
        }else {
            initListener.onInit(ErrorCode.SUCCESS);
        }
    }

    private void setParams() {
        // 清空参数
        speechSynthesizer.setParameter(SpeechConstant.PARAMS, null);
        //设置使用本地引擎
        speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        //设置发音人资源路径
        speechSynthesizer.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
        //设置发音人
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, this.voicerLocal);
        //mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY,"1");//支持实时音频流抛出，仅在synthesizeToUri条件下支持
        //设置合成语速
        speechSynthesizer.setParameter(SpeechConstant.SPEED, "50");//0-100
        //设置合成音调
        speechSynthesizer.setParameter(SpeechConstant.PITCH, "50");//0-100
        //设置合成音量
        speechSynthesizer.setParameter(SpeechConstant.VOLUME, "100");//0-100
        //设置播放器音频流类型
        speechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, AudioManager.STREAM_MUSIC + "");
        // 设置播放合成音频打断音乐播放，默认为true
        speechSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        speechSynthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
    }

    //获取发音人资源路径
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        String type = "tts";

        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, type + "/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, type + "/" + this.voicerLocal + ".jet"));

        return tempBuffer.toString();
    }


    /**
     *
     * @param text
     * @param path 保存的路径  path==null
     * @param listener
     * @return
     */
    public synchronized int start (@NonNull String text, String path, @NonNull SynthesizerListener listener){
        if(speechSynthesizer==null){
            return ErrorCode.ERROR_TTS_ENGINE_UNINIT;
        }
        setParams();

        int code;
        if(TextUtils.isEmpty(path))
            code = speechSynthesizer.startSpeaking(text, listener);
        else
            code = speechSynthesizer.synthesizeToUri(text, path, listener);
        return  code;
    }

    public void onDestory(){
        speechSynthesizer.destroy();
    }

    }
