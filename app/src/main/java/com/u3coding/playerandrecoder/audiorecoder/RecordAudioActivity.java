package com.u3coding.playerandrecoder.audiorecoder;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.u3coding.audioandvideo.R;
import com.u3coding.audioandvideo.databinding.ActivityRecordAudioBinding;

public class RecordAudioActivity extends AppCompatActivity {
    private RecordAudioViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       ActivityRecordAudioBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_record_audio);
       viewModel = ViewModelProviders.of(this).get(RecordAudioViewModel.class);
       binding.setPagetext(viewModel.getPageText());
    }
}
