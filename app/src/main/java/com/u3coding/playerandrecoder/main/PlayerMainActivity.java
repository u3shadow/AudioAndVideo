package com.u3coding.playerandrecoder.main;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.u3coding.audioandvideo.R;
import com.u3coding.audioandvideo.databinding.ActivityPlayerMainBinding;

public class PlayerMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       ActivityPlayerMainBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_player_main);
       binding.setMainpage(new MainPage());
    }

}
