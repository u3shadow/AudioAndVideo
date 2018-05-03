package com.u3coding.playerandrecoder.videorecoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class H264Encoder {
    private int bitrate;
    private MediaCodec mMediaCodec;
    private int width;
    private int height;
    final private static int FRAMERATE = 30;
    private BufferedOutputStream outputStream;
    final private static long TIMEOUT_USEC = 12000;
    private File fileTemp;
    private boolean isRecording = false;

    public H264Encoder(int width,int height){
        this.width = width;
        this.height = height;
        initMediaCodec(width,height);
        createfile();
    }
    private void initMediaCodec(int width,int height) {
        bitrate = 5 * width * height;//码率
        try {
            mMediaCodec = MediaCodec.createEncoderByType("video/avc");
            MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", height, width); //height和width一般都是照相机的height和width。
            //描述平均位速率（以位/秒为单位）的键。 关联的值是一个整数
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
            mediaFormat.setInteger(MediaFormat.KEY_BITRATE_MODE,MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ);
            //描述视频格式的帧速率（以帧/秒为单位）的键。
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAMERATE);//帧率，一般在15至30之内，太小容易造成视频卡顿。
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);//色彩格式，具体查看相关API，不同设备支持的色彩格式不尽相同
            //关键帧间隔时间，单位是秒
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
            mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mMediaCodec.start();//开始编码
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startRecord(){
        isRecording = true;
    }
    public void encode(Queue<byte[]> datas){

        new Thread(()->codeData(datas)).start();

    }
    public void stopRecord(){
        isRecording = false;
    }
     private long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / FRAMERATE;
    }
    private void codeData(Queue<byte[]> datas){
        while(datas.size() > 0) {
            byte[] input = datas.poll();
            byte[] yuv420sp = new byte[width * height * 3 / 2];
            NV21ToNV12(input, yuv420sp, width, height);
            input = yuv420sp;
            long pts = 0;
            long generateIndex = 0;
            if (input != null) {
                try {
                    ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();//拿到输入缓冲区,用于传送数据进行编码
                    ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();//拿到输出缓冲区,用于取到编码后的数据
                    int inputBufferIndex = mMediaCodec.dequeueInputBuffer(-1);
                    if (inputBufferIndex >= 0) {//当输入缓冲区有效时,就是>=0
                        pts = computePresentationTime(generateIndex);
                        ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                        inputBuffer.clear();
                        inputBuffer.put(input);//往输入缓冲区写入数据,
                        //                    //五个参数，第一个是输入缓冲区的索引，第二个数据是输入缓冲区起始索引，第三个是放入的数据大小，第四个是时间戳，保证递增就是
                        mMediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, pts, 0);
                        generateIndex++;

                    }

                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                    int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);//拿到输出缓冲区的索引
                    while (outputBufferIndex >= 0) {
                        ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                        byte[] outData = new byte[bufferInfo.size];
                        outputBuffer.get(outData);
                        //outData就是输出的h264数据
                        outputStream.write(outData, 0, outData.length);//将输出的h264数据保存为文件，用vlc就可以播放

                        mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                        outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }
    public boolean setName(String name){
        File fileTo = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+name);
        return fileTemp.renameTo(fileTo);
    }
    private void createfile(){
        fileTemp = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.h264");
        if(fileTemp.exists()){
            fileTemp.delete();
        }
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(fileTemp));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void NV21ToNV12(byte[] nv21,byte[] nv12,int width,int height){
        if(nv21 == null || nv12 == null)return;
        int framesize = width*height;
        int i,j;
        System.arraycopy(nv21, 0, nv12, 0, framesize);
        for(i = 0; i < framesize; i++){
            nv12[i] = nv21[i];
        }
        for (j = 0; j < framesize/2; j+=2)
        {
            nv12[framesize + j-1] = nv21[j+framesize];
        }
        for (j = 0; j < framesize/2; j+=2)
        {
            nv12[framesize + j] = nv21[j+framesize-1];
        }
    }

}
