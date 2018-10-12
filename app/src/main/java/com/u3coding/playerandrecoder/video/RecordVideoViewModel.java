package com.u3coding.playerandrecoder.video;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class RecordVideoViewModel extends AndroidViewModel{
    private H264Encoder encoder;

	private Queue<byte[]> YUVQueue;

    public RecordVideoViewModel(@NonNull Application application) {
        super(application);
    }
    public void startRecord(int width,int height){
        YUVQueue = new LinkedBlockingQueue<>();
    }
    public void recordData(byte[] data){
        if (YUVQueue != null&&data != null) {
            YUVQueue.add(data);
        }
    }
    public void setName(String name){
    }
    public void stopRecord(){
        encoder = new H264Encoder(1280,720,30,1,YUVQueue);
        encoder.StartEncoderThread();
    }

}
