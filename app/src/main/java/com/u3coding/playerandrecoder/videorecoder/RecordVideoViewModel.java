package com.u3coding.playerandrecoder.videorecoder;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

public class RecordVideoViewModel extends AndroidViewModel{
    private H264Encoder encoder;
    public RecordVideoViewModel(@NonNull Application application) {
        super(application);
    }
    public void startRecord(int width,int height){
        if (encoder == null)
        encoder = new H264Encoder(width,height);
        encoder.startRecord();
    }
    public void encode(byte[] data){
        encoder.encode(data);
    }
    public void setName(String name){
        encoder.setName(name);
    }
    public void stopRecord(){
        encoder.stopRecord();
    }

}
