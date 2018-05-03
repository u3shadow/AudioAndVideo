package com.u3coding.playerandrecoder.videorecoder;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RecordVideoViewModel extends AndroidViewModel{
    private H264Encoder encoder;

	private Queue<byte[]> YUVQueue;

    public RecordVideoViewModel(@NonNull Application application) {
        super(application);
    }
    public void startRecord(int width,int height){
        if (encoder == null)
        encoder = new H264Encoder(width,height);
        encoder.startRecord();
        YUVQueue = new LinkedBlockingQueue<>();
    }
    public void recordData(byte[] data){
        YUVQueue.add(data);
    }
    public void setName(String name){
        encoder.setName(name);
    }
    public void stopRecord(){
        encoder.stopRecord();
        encoder.encode(YUVQueue);
    }

}
