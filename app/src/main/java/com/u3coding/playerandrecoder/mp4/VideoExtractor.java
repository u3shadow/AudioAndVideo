package com.u3coding.playerandrecoder.mp4;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.tracks.h264.H264TrackImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * Created by u3-linux on 18-2-14.
 */

public class VideoExtractor {
    public void muxerMP4(){
        try {
            String h264Path = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/oooo.h264";

            DataSource videoFile = new FileDataSourceImpl(h264Path);

            H264TrackImpl h264Track = new H264TrackImpl(videoFile, "eng", 5, 1); // 5fps. you can play with timescale and timetick to get non integer fps, 23.967 is 24000/1001

            Movie movie = new Movie();

            movie.addTrack(h264Track);

            Container out = new DefaultMp4Builder().build(movie);
            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+ "/output.mp4"));
            out.writeContainer(fos.getChannel());

            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void muxerMediaAudio(){
        try {
            MediaExtractor mediaExtractor = getMediaExtractor(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/oooo.aac");
            int audioIndex1 = getIndex(mediaExtractor, "audio/");
            mediaExtractor.selectTrack(audioIndex1);
            MediaFormat trackFormat = mediaExtractor.getTrackFormat(audioIndex1);
            MediaMuxer mediaMuxer = new MediaMuxer(Environment.getExternalStorageDirectory().getAbsoluteFile()+ "/output.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            int trackIndex = mediaMuxer.addTrack(trackFormat);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            mediaMuxer.start();
            bufferInfo.presentationTimeUs = 0;
            long stampTime = getStampTime(mediaExtractor, byteBuffer);
            mediaExtractor.unselectTrack(audioIndex1);
            mediaExtractor.selectTrack(audioIndex1);
            while (true) {
                int readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleSize < 0) {
                    break;
                }
                mediaExtractor.advance();
                bufferInfo.size = readSampleSize;
                bufferInfo.offset = 0;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                bufferInfo.presentationTimeUs += stampTime;
                mediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo);
            }
            //release
            mediaMuxer.stop();
            mediaExtractor.release();
            mediaMuxer.release();
            Log.e("TAG", "finish");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private MediaExtractor getMediaExtractor(String source) throws IOException {
        MediaExtractor mediaExtractor = new MediaExtractor();
        mediaExtractor.setDataSource(source);
        return mediaExtractor;
    }

    private long getStampTime(MediaExtractor mediaExtractor, ByteBuffer byteBuffer) {
        long stampTime = 0;
        //获取帧之间的间隔时间
        {
            mediaExtractor.readSampleData(byteBuffer, 0);
            if (mediaExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                mediaExtractor.advance();
            }
            mediaExtractor.readSampleData(byteBuffer, 0);
            long secondTime = mediaExtractor.getSampleTime();
            mediaExtractor.advance();
            mediaExtractor.readSampleData(byteBuffer, 0);
            long thirdTime = mediaExtractor.getSampleTime();
            stampTime = Math.abs(thirdTime - secondTime);
            Log.e("audio111", stampTime + "");
        }
        return stampTime;
    }

    private int getIndex(MediaExtractor mediaExtractor,String channal) {
        int index = -1;
        int trackCount = mediaExtractor.getTrackCount();
        for (int i = 0; i < trackCount; i++) {
            MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
            if (trackFormat.getString(MediaFormat.KEY_MIME).startsWith(channal)) {
                index = i;
            }
        }
        return index;
    }
}
