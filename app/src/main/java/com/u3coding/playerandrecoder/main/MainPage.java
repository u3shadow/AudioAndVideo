package com.u3coding.playerandrecoder.main;

import android.content.Intent;
import android.view.View;

import com.u3coding.audioandvideo.video.VideoActivity;
import com.u3coding.playerandrecoder.audiorecoder.RecordAudioActivity;

public class MainPage{
    public void recordAudio(View view){
        Intent intent = new Intent(view.getContext(),RecordAudioActivity.class);
        view.getContext().startActivity(intent);
    }
    public void recordVideo(View view){
        Intent intent = new Intent(view.getContext(),VideoActivity.class);
        view.getContext().startActivity(intent);
    }
}
