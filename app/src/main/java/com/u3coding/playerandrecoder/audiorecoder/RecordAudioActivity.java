package com.u3coding.playerandrecoder.audiorecoder;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.u3coding.audioandvideo.R;
import com.u3coding.audioandvideo.databinding.ActivityRecordAudioBinding;

public class RecordAudioActivity extends AppCompatActivity implements View.OnClickListener{
    private RecordAudioViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       ActivityRecordAudioBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_record_audio);
       viewModel = ViewModelProviders.of(this).get(RecordAudioViewModel.class);
       binding.setPagetext(viewModel.getPageText());
       getLifecycle().addObserver(viewModel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_start:
                viewModel.record();
                break;
            case R.id.bt_stop:
                viewModel.stop();
               AlertDialog dialog =  new AlertDialog.Builder(this).setView(R.layout.name_type_layout).create();
               dialog.show();
                break;
            default:break;
        }
    }
}
