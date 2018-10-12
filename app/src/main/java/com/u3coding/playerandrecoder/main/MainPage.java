package com.u3coding.playerandrecoder.main;

import android.content.Intent;
import android.view.View;

import com.u3coding.playerandrecoder.audio.RecordAudioActivity;
import com.u3coding.playerandrecoder.video.RecordVideoActivity;

public class MainPage{
    public void recordAudio(View view){
        Intent intent = new Intent(view.getContext(),RecordAudioActivity.class);
        view.getContext().startActivity(intent);
    }
    public void recordVideo(View view){
        Intent intent = new Intent(view.getContext(),RecordVideoActivity.class);
        view.getContext().startActivity(intent);
    }
}
