package com.u3coding.audioandvideo;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.u3coding.audioandvideo.audio.Audio;
import com.u3coding.audioandvideo.audio.AudioRandPViewModel;
import com.u3coding.audioandvideo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private AudioRandPViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        Audio audio = new Audio();
        binding.setAudioRandP(audio);
        viewModel = ViewModelProviders.of(this).get(AudioRandPViewModel.class);
        viewModel.setAudio(audio);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.record:
                viewModel.recordAudio();
                Log.i("ppp","record");
                break;
            case R.id.stop:
                viewModel.stop();
                Log.i("ppp","stop");
                break;
            case R.id.play:
                viewModel.play();
                Log.i("ppp","play");
                break;
            default:break;
        }
    }

}
