package com.u3coding.audioandvideo.mp4;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by u3-linux on 18-2-14.
 */

public class VideoExtractor {
    public void exactor(MediaExtractor mediaExtractor){
        FileOutputStream videoOutputStream = null;
        FileOutputStream audioOutputStream = null;
        try{
            File videoFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(),"output_video.mp4");
            File audioFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(),"output_audio");
            videoOutputStream = new FileOutputStream(videoFile);
            audioOutputStream = new FileOutputStream(audioFile);
            mediaExtractor.setDataSource(Environment.getExternalStorageDirectory().getAbsoluteFile()+ "/input.mp4");
            int trackCount = mediaExtractor.getTrackCount();
            int audioTrackIndex = -1;
            int videoTrackIndex = -1;
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                String mineType = trackFormat.getString(MediaFormat.KEY_MIME);
                //视频信道
                if (mineType.startsWith("video/")) {
                    videoTrackIndex = i;
                }
                //音频信道
                if (mineType.startsWith("audio/")) {
                    audioTrackIndex = i;
                }
            }
            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            //切换到视频信道
            mediaExtractor.selectTrack(videoTrackIndex);
            while (true) {
                int readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleCount < 0) {
                    break;
                }
                //保存视频信道信息
                byte[] buffer = new byte[readSampleCount];
                byteBuffer.get(buffer);
                videoOutputStream.write(buffer);
                byteBuffer.clear();
                mediaExtractor.advance();
            }
            //切换到音频信道
            mediaExtractor.selectTrack(audioTrackIndex);
            while (true) {
                int readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleCount < 0) {
                    break;
                }
                //保存音频信息
                byte[] buffer = new byte[readSampleCount];
                byteBuffer.get(buffer);
                audioOutputStream.write(buffer);
                byteBuffer.clear();
                mediaExtractor.advance();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mediaExtractor.release();
            try {
                videoOutputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void muxerMediaVideo(){
        try {
            MediaExtractor mediaExtractor = getMediaExtractor(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/input.mp4");
            int videoIndex = getIndex(mediaExtractor,"video/");
            //切换道视频信号的信道
            mediaExtractor.selectTrack(videoIndex);
            MediaFormat trackFormat = mediaExtractor.getTrackFormat(videoIndex);
            MediaMuxer mediaMuxer = new MediaMuxer( Environment.getExternalStorageDirectory().getAbsoluteFile()+ "/output_video.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int videoSampleTime;
            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            videoSampleTime = trackFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
            int trackIndex = mediaMuxer.addTrack(trackFormat);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            mediaMuxer.start();
            bufferInfo.presentationTimeUs = 0;
            while (true) {
                int readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleSize < 0) {
                    break;
                }
                mediaExtractor.advance();
                bufferInfo.size = readSampleSize;
                bufferInfo.offset = 0;
                bufferInfo.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
                bufferInfo.presentationTimeUs += 1000*1000 / videoSampleTime;
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

    public void muxerMediaAudio(){
        try {
            MediaExtractor mediaExtractor = getMediaExtractor(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/input.mp4");
            int audioIndex1 = getIndex(mediaExtractor, "audio/");
            mediaExtractor.selectTrack(audioIndex1);
            MediaFormat trackFormat = mediaExtractor.getTrackFormat(audioIndex1);
            MediaMuxer mediaMuxer = new MediaMuxer(Environment.getExternalStorageDirectory().getAbsoluteFile()+ "/output_audio.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
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
