package com.u3coding.audioandvideo.mp4;

import android.arch.lifecycle.ViewModel;
import android.media.MediaExtractor;

public class ExtractorViewModel extends ViewModel{
   private VideoExtractor extractor;
   public ExtractorViewModel(){
      extractor = new VideoExtractor();
   }
   public void exrtract(){
       extractor.exactor(new MediaExtractor());
   }
   public void merge(){
       extractor.muxerMediaAudio();
   }
}
