package com.u3coding.audioandvideo.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import com.u3coding.audioandvideo.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioRandP{
    private SurfaceView surfaceView;
    private byte[] outBuf;
    private boolean isStart=false;
    private AudioRecord audioRecord;
    private int bufferSize;
    private AudioTrack audioTrack;
    private int minBufferSize;

    public void recordAudio(){
        initAudio();
        isStart = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                record();
            }
        }).start();
    }
    public void stopRecord(){
        stop();
    }
    public void playAudio(){
        isStart = true;
        createPlayer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                play();
            }
        }).start();
    }
    private void initAudio(){
        int sampleRateInHz = 48000;//采样率
        int channel= AudioFormat.CHANNEL_IN_STEREO;//声道数
        int audioFormat=AudioFormat.ENCODING_PCM_16BIT;//位宽
        bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channel, audioFormat);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz, channel, audioFormat, bufferSize*4);
        outBuf=new byte[bufferSize];
        Log.i(getClass().getSimpleName(), "init record="+bufferSize);
    }
    private void record(){
        FileOutputStream fileOutputStream=null;
        try {
            audioRecord.startRecording();
            fileOutputStream=new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/outputs.pcm"));
            while (isStart)
            {
                int len = audioRecord.read(outBuf, 0, bufferSize);
                if (len == AudioRecord.ERROR_INVALID_OPERATION || len == AudioRecord.ERROR_BAD_VALUE) {
                    continue;
                }
                if (len != 0 && len != -1) {
                    fileOutputStream.write(outBuf, 0, len);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            audioRecord.stop();
            audioRecord.release();
            audioRecord=null;
        }
    }
    private void stop(){
        isStart = false;
    }
    private void createPlayer(){
        int sampleRateInHz=48000;//44100 48000
        int encodingPcm16bit = AudioFormat.ENCODING_PCM_16BIT;
        int channelConfig=AudioFormat.CHANNEL_IN_STEREO;

        minBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, encodingPcm16bit);
        audioTrack=new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRateInHz,
                channelConfig,
                encodingPcm16bit,
                minBufferSize,
                AudioTrack.MODE_STREAM);
    }
    private void play(){
        try {

            FileInputStream fileInputStream=new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/解码的pcm.pcm"));
            audioTrack.play();//开始播放
            int len=-1;
            byte[] arr=new byte[minBufferSize];
            while ( (len=fileInputStream.read(arr))!=-1 )
            {
                audioTrack.write(arr, 0, len);
            }
            audioTrack.stop();//停止播放
            audioTrack.release();//释放资源
            audioTrack=null;
            fileInputStream.close();
            isStart=false;

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
