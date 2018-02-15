package com.u3coding.audioandvideo.mp4;

import android.app.Activity;
import android.media.MediaExtractor;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.u3coding.audioandvideo.R;

/**
 * Created by u3-linux on 18-2-15.
 */

public class MediaExAndMuxerActivity extends Activity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_layout);
        MediaExtractor extractor = new MediaExtractor();
        VideoExtractor videoExtractor = new VideoExtractor();
        videoExtractor.process();
    }
}
