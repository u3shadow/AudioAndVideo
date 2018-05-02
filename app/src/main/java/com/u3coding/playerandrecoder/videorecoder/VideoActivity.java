package com.u3coding.playerandrecoder.videorecoder;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;

import com.u3coding.audioandvideo.R;
import com.u3coding.playerandrecoder.audiorecoder.RecordAudioViewModel;

/**
 * Created by u3-linux on 18-2-14.
 */

public class VideoActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener,View.OnClickListener{
    private Camera mCamera;
    private CameraPreview mPreview;
    private TextureView textureView;
    private RecordVideoViewModel viewModel;
    private boolean isStart = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.video_layout);
        viewModel = ViewModelProviders.of(this).get(RecordVideoViewModel.class);
        if(checkCameraHardware(this)){
            mCamera = getCameraInstance();
        }
        init();
    }

    private void init() {
        mPreview = new CameraPreview(this,mCamera);
        //FrameLayout frameLayout = (FrameLayout)findViewById(R.id.camera_preview);
        textureView = (TextureView)findViewById(R.id.texture);
        textureView.setSurfaceTextureListener(this);
        //frameLayout.addView(mPreview);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21);
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);
        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
                if (isStart)
                viewModel.encode(bytes);
            }
        });
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        try{
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_record:
                viewModel.startRecord(768,1366);
                isStart = true;
                break;
            case R.id.bt_stop:
                viewModel.stopRecord();
                isStart =false;
                break;
                default:break;
        }
    }
}
