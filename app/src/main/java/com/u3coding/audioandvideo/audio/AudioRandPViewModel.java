package com.u3coding.audioandvideo.audio;

import android.arch.lifecycle.ViewModel;

public class AudioRandPViewModel extends ViewModel{
    private AudioRandP audio;
    public AudioRandPViewModel(){
        audio = new AudioRandP();
    }
    public void recordAudio(){
        audio.recordAudio();
    }
    public void stop(){
        audio.stopRecord();
    }
    public void play(){
        audio.playAudio();
    }
    public void setAudio(Audio audio){
        audio.recordString.set("1");
        audio.stopString.set("2");
        audio.playString.set("3");
    }
}
