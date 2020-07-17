package com.chaochaowu.characterrecognition.module;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaochaowu.characterrecognition.FileUtil;
import com.chaochaowu.characterrecognition.R;
import com.chaochaowu.characterrecognition.tts.OncompleteListener;
import com.chaochaowu.characterrecognition.tts.SynthesizerListenerImpl;
import com.chaochaowu.characterrecognition.tts.TTS_Manager;
import com.iflytek.cloud.InitListener;
import com.wang.avi.AVLoadingIndicatorView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author chaochaowu
 * @Description : 主界面Activity,处理View部分
 * @class : MainActivity
 * @time Create at 6/4/2018 4:24 PM
 */


public class MainActivity extends AppCompatActivity implements MainContract.View{

    private Context mContext;

    private ImageView imageView;
    private TextView textView;
    private Button button;
    private Button button2;
    private Button button3;

    private MainPresenter mPresenter;
    File mTmpFile;
    Uri imageUri;
    private TextView tv_voice_percent;

    String savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "HHZ" + File.separator;
    String audioPath = savePath + "audio_file/";
    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private RecyclerView rv_voice_list;
    private RelativeLayout mrlRootView;
    private TextView mtvBack;
    private ArrayList<String> files=new ArrayList<>();
    private ArrayList<String> filesName=new ArrayList<>();
    private CommonAdapter adapter;
    private String speeckText="";
    private String remainText="";
    private View mMaskView;
    private AVLoadingIndicatorView mLoadingView;
    private int currentPos=-1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        tv_voice_percent=findViewById(R.id.tv_voice_percent);
        button2 = findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        mPresenter = new MainPresenter(this);
        rv_voice_list=findViewById(R.id.rv_voice_list);
        mrlRootView=findViewById(R.id.rl_root_view);
        mtvBack=findViewById(R.id.tv_back);

        mMaskView=findViewById(R.id.mask);
        mLoadingView=findViewById(R.id.loading);
        mLoadingView.hide();
        TTS_Manager.getInstance().initTTS(new InitListener() {
            @Override
            public void onInit(int i) {

            }
        });
//        mPresenter.getAccessToken();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
//                takePhoto();
                /*Resources r = mContext.getResources();
                Bitmap bmp = BitmapFactory.decodeResource(r, R.drawable.test);
                mPresenter.getRecognitionResultByImage(bmp);*/
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textStr=textView.getText().toString();
                speech(textStr);
                textView.setText("");
//                takePhoto();
                /*Resources r = mContext.getResources();
                Bitmap bmp = BitmapFactory.decodeResource(r, R.drawable.test);
                mPresenter.getRecognitionResultByImage(bmp);*/
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mrlRootView.setVisibility(View.VISIBLE);
            }
        });

        tv_voice_percent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        mtvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mrlRootView.setVisibility(View.GONE);
            }
        });

        rv_voice_list.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<String> fff=FileUtil.getFiles(audioPath);
        Log.e("------->", files.toString());
        adapter = new CommonAdapter<String>(this,R.layout.view_layout,filesName){

            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                Log.e("11111111----->",currentPos+"--------->"+position);
                if(currentPos!=-1&&currentPos==position){
                    holder.setBackgroundRes(R.id.tv_text,R.color.color_40FFEF);
                }else {
                    holder.setBackgroundRes(R.id.tv_text,R.color.translant);
                }
                holder.setText(R.id.tv_text,s);
            }
        };

        rv_voice_list.setAdapter(adapter);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                currentPos=position;
                playVoice(files.get(position));
                adapter.notifyDataSetChanged();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        if(fff!=null){
            files.addAll(fff);
            for (int i = 0; i < fff.size(); i++) {
                String[] split = fff.get(i).split("/");
                filesName.add(split[split.length-1]);
            }
        }
        adapter.notifyDataSetChanged();


    }

    @Override
    public void updateUI(String s) {
        textView.setText(textView.getText().toString()+"\n"+s);
        Toast.makeText(this, "识别完成", Toast.LENGTH_SHORT).show();
        mLoadingView.hide();
        mMaskView.setVisibility(View.GONE);
    }

    private void speech(String speeckStr){

        remainText = speeckStr;
        if(remainText.length()>=8000){
            speeckText = remainText.substring(0, 8000);
            remainText = remainText.substring(8000, remainText.length()-8000);
        }else {
            speeckText=remainText;
            remainText="";

        }
        if(!TextUtils.isEmpty(speeckText))
            speechIng();

    }

    private void speechIng(){

        mMaskView.setVisibility(View.VISIBLE);
        mLoadingView.show();
        TTS_Manager.getInstance().start(speeckText, audioPath + "/" + speeckText.substring(0, 20)+".wav", new SynthesizerListenerImpl(new OncompleteListener() {
            @Override
            public void onCompleted() {
                mMaskView.setVisibility(View.GONE);
                mLoadingView.hide();

                files.add(audioPath + "/" + speeckText.substring(0, 20)+".wav");
                filesName.add(speeckText.substring(0, 20)+".wav");
                adapter.notifyDataSetChanged();
                speeckText="";
                if(remainText.length()>0){
                    if(remainText.length()>=8000){
                        speeckText = remainText.substring(0, 8000);
                        remainText = remainText.substring(8000, remainText.length()-8000);
                    }else {
                        speeckText=remainText;
                        remainText="";
                    }

                }
                if(!TextUtils.isEmpty(speeckText)) {
                    speechIng();
                }
            }
        }));
    }



    private boolean hasPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                /*|| ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED*/) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE/*, Manifest.permission.CAMERA*/}, PERMISSIONS_REQUEST_CODE);
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        return;
                    }
                }
                choosePhoto();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
                Bitmap photo = BitmapFactory.decodeFile(mTmpFile.getAbsolutePath());
                mPresenter.getRecognitionResultByImage(photo);
                imageView.setImageBitmap(photo);
        }else if(requestCode ==RC_CHOOSE_PHOTO){
            Uri uri = data.getData();
            String filePath = FileUtil.getFilePathByUri(this, uri);
            Bitmap photo = BitmapFactory.decodeFile(filePath);
            mPresenter.getRecognitionResultByImage(photo);
            Toast.makeText(this, "正在扫描，请稍后", Toast.LENGTH_SHORT).show();
            mLoadingView.show();
            mMaskView.setVisibility(View.VISIBLE);
        }
    }

    public static final int RC_CHOOSE_PHOTO = 3;

    private void choosePhoto() {
        if (!hasPermission()) {
            return;
        }
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, RC_CHOOSE_PHOTO);
    }
    private MediaPlayer bigAudioFile;
    private void playVoice(String path){

        if (bigAudioFile == null) {
            bigAudioFile = new MediaPlayer();
        }

        bigAudioFile.reset();
        try {
            Log.e("9999999:",path);
            bigAudioFile.setDataSource(path);
            bigAudioFile.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bigAudioFile.start();
        bigAudioFile.setVolume(1f,1f);
        bigAudioFile.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(currentPos<files.size()-1){
                    currentPos++;
                    playVoice(files.get(currentPos));
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bigAudioFile!=null) {
            bigAudioFile.stop();
        }
    }
}
