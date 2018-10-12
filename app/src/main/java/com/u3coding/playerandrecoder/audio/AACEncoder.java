package com.u3coding.playerandrecoder.audio;

import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class AACEncoder extends BaseEncoder{

    private String MIME_TYPE="audio/mp4a-latm";
    private int KEY_CHANNEL_COUNT=2;
    private int KEY_SAMPLE_RATE=44100;
    private int bitRate = 96000;
    private Worker mWorker;
    private final String TAG="AudioEncoder";
    private byte[] mFrameByte;
    @Override
    public boolean encode(String path,String name) {
         if(mWorker==null){
            mWorker=new Worker();
        }
        mWorker.setPath(path,name);
        mWorker.start();
        return true;
    }

    public void stop(){
        if(mWorker!=null){
            mWorker=null;
        }
    }

     private class Worker extends Thread{
        private final int mFrameSize = 2048;
        private byte[] mBuffer;
        private MediaCodec mEncoder;
        private AudioRecord mRecord;
        private String path,name;
        MediaCodec.BufferInfo mBufferInfo;
        private ByteBuffer[] inputBufferArray;
        private ByteBuffer[] outputBufferArray;
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
         @Override
        public void run() {
            if(!prepare()){
                Log.d(TAG,"音频编码器初始化失败");
            }
            try {
               fileInputStream=new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/temp.pcm"));
               fileOutputStream = new FileOutputStream(new File(path+name));
               while(fileInputStream.read(mBuffer,0,mFrameSize) != -1)
                    encode(mBuffer);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            release();
        }
        void setPath(String path,String name){
           this.path =  path;
           this.name = "/"+name+".aac";
        }
        /**
         * 释放资源
         */
        private void release() {
            if(mEncoder!=null){
                mEncoder.stop();
                mEncoder.release();
            }
            if(mRecord!=null){
                mRecord.stop();
                mRecord.release();
                mRecord = null;
            }
        }

        /**
         * @return true配置成功，false配置失败
         */
        private boolean prepare() {
            try {
                mBufferInfo = new MediaCodec.BufferInfo();
                MediaFormat mediaFormat = getMediaFormat();
                initEncoder(mediaFormat);
                mBufferInfo = new MediaCodec.BufferInfo();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            mBuffer = new byte[mFrameSize];
            return true;
        }

         private MediaCodec initEncoder(MediaFormat mediaFormat) throws IOException {
             mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
             mEncoder.configure(mediaFormat, null, null,
                     MediaCodec.CONFIGURE_FLAG_ENCODE);
             mEncoder.start();
             inputBufferArray = mEncoder.getInputBuffers();
             outputBufferArray = mEncoder.getOutputBuffers();
             return mEncoder;
         }

         @NonNull
         private MediaFormat getMediaFormat() {
             MediaFormat mediaFormat = MediaFormat.createAudioFormat(MIME_TYPE,
                     KEY_SAMPLE_RATE, KEY_CHANNEL_COUNT);
             mediaFormat.setString(MediaFormat.KEY_MIME, MIME_TYPE);
             mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
             mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
             mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 2);
             mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1024 * 1024);
             mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, 44100);
             return mediaFormat;
         }

         private void encode(byte[] data) {
            int inputIndex = mEncoder.dequeueInputBuffer(-1);
            if (inputIndex >= 0) {
                ByteBuffer inputByteBuf = inputBufferArray[inputIndex];
                inputByteBuf.clear();
                inputByteBuf.put(data);//添加数据
                inputByteBuf.limit(data.length);//限制ByteBuffer的访问长度
                mEncoder.queueInputBuffer(inputIndex, 0, data.length, 0, 0);//把输入缓存塞回去给MediaCodec
            }

            int outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
            while (outputBufferIndex >= 0) {
                //获取缓存信息的长度
                int byteBufSize = mBufferInfo.size;
                //添加ADTS头部后的长度
                int bytePacketSize = byteBufSize + 7;

                ByteBuffer  outPutBuf = outputBufferArray[outputBufferIndex];
                outPutBuf.position(mBufferInfo.offset);
                outPutBuf.limit(mBufferInfo.offset+mBufferInfo.size);

                byte[] targetByte = new byte[bytePacketSize];
                //添加ADTS头部
                addADTStoPacket(targetByte,bytePacketSize);

                outPutBuf.get(targetByte,7,byteBufSize);

                outPutBuf.position(mBufferInfo.offset);
                try {
                    fileOutputStream.write(targetByte);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mEncoder.releaseOutputBuffer(outputBufferIndex,false);
                outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo,0);
            }
        }

        /**
         * 给编码出的aac裸流添加adts头字段
         */
        private void addADTStoPacket(byte[] packet, int packetLen) {
            int profile = 2;  //AAC LC
            int freqIdx = 4;  //44.1KHz
            int chanCfg = 2;  //CPE
            packet[0] = (byte)0xFF;
            packet[1] = (byte)0xF9;
            packet[2] = (byte)(((profile-1)<<6) + (freqIdx<<2) +(chanCfg>>2));
            packet[3] = (byte)(((chanCfg&3)<<6) + (packetLen>>11));
            packet[4] = (byte)((packetLen&0x7FF) >> 3);
            packet[5] = (byte)(((packetLen&7)<<5) + 0x1F);
            packet[6] = (byte)0xFC;
        }
    }
}
