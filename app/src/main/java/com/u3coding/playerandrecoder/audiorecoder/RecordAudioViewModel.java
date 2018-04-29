package com.u3coding.playerandrecoder.audiorecoder;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;

import com.u3coding.audioandvideo.R;

public class RecordAudioViewModel extends AndroidViewModel implements LifecycleObserver{
    private RecordPage page;
    private AudioRecorder recorder;
    private Application application;
    public RecordAudioViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        page = new RecordPage();
        page.recordText.set(application.getResources().getString(R.string.no_record));
        page.startBtText.set(application.getResources().getString(R.string.start));
        page.stopBtText.set(application.getResources().getString(R.string.stop));
        recorder = new AudioRecorder();
    }
    public RecordPage getPageText(){
        return page;
    }
    public void record(){
        page.recordText.set(application.getResources().getString(R.string.recording));
        recorder.record();
    }
    public void stop(){
        page.recordText.set(application.getResources().getString(R.string.no_record));
        recorder.stop();
    }
}
