package com.u3coding.playerandrecoder.audiorecoder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioRecorder {
    private int bufferSize;
    private AudioRecord audioRecord;
    private byte[] outBuf;
    private boolean isRecording;
    private Thread recordThread;
    AudioRecorder(){
        init();
    }
    private void init(){
        int sampleRateInHz = 48000;//采样率
        int channel= AudioFormat.CHANNEL_IN_STEREO;//声道数
        int audioFormat=AudioFormat.ENCODING_PCM_16BIT;//位宽
        bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channel, audioFormat);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz, channel, audioFormat, bufferSize*4);
        outBuf=new byte[bufferSize];
        Log.i(getClass().getSimpleName(), "init record="+bufferSize);
    }
    public void record(){
        isRecording = true;
        recordThread = new Thread(this::recordAudio);
        recordThread.start();
    }
    public void stop(){
        if (recordThread != null)
            isRecording = false;
    }
    private void recordAudio(){
        FileOutputStream fileOutputStream=null;
        try {
            audioRecord.startRecording();
            fileOutputStream=new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/temp.pcm"));
            while (isRecording)
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            audioRecord.stop();
            audioRecord.release();
            audioRecord=null;
        }
    }
}
