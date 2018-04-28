package com.u3coding.playerandrecoder.audiorecoder;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import com.u3coding.audioandvideo.R;

public class RecordAudioViewModel extends AndroidViewModel {
    private RecordPage page;
    private Context context;
    public RecordAudioViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        page = new RecordPage();
        page.recordText.set(context.getString(R.string.no_record));
        page.startBtText.set(context.getString(R.string.start));
        page.stopBtText.set(context.getString(R.string.stop));
    }
    public RecordPage getPageText(){
        return page;
    }
}
