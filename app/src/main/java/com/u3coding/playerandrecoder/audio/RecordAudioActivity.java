package com.u3coding.playerandrecoder.audio;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.u3coding.audioandvideo.R;
import com.u3coding.audioandvideo.databinding.ActivityRecordAudioBinding;

public class RecordAudioActivity extends AppCompatActivity implements View.OnClickListener{
    private RecordAudioViewModel viewModel;
    private AACDeCoder deCoder;

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
                v.setVisibility(View.INVISIBLE);
                findViewById(R.id.bt_stop).setVisibility(View.VISIBLE);
                break;
            case R.id.bt_stop:
                viewModel.stop();
                v.setVisibility(View.INVISIBLE);
                findViewById(R.id.ll_name).setVisibility(View.VISIBLE);
                break;
            case R.id.bt_name:
                String name = ((EditText)findViewById(R.id.et_name)).getText().toString();
                if (!name.isEmpty()){
                    viewModel.saveAACFile(name);
                    findViewById(R.id.ll_name).setVisibility(View.INVISIBLE);
                    findViewById(R.id.bt_start).setVisibility(View.VISIBLE);
                }else{
                    Snackbar.make(findViewById(R.id.bt_start),"empty name",Snackbar.LENGTH_LONG).show();
                }
            default:break;
        }
    }
}
