package com.bird.trill.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.aliyun.common.utils.MySystemParams;
import com.aliyun.recorder.AliyunRecorderCreator;
import com.aliyun.recorder.supply.AliyunIClipManager;
import com.aliyun.recorder.supply.AliyunIRecorder;
import com.aliyun.struct.recorder.CameraType;
import com.aliyun.struct.recorder.MediaInfo;
import com.bird.trill.R;
import com.bird.trill.databinding.ActivityCameraBinding;
import com.bird.trill.base.BaseActivity;

public class CameraActivity extends BaseActivity<ActivityCameraBinding> {

    private static int TEST_VIDEO_WIDTH = 540;
    private static int TEST_VIDEO_HEIGHT = 960;

    private static final int MIN_RECORD_TIME = 1000;
    private static final int MAX_RECORD_TIME = 60 * 1000;

    private CameraType cameraType = CameraType.FRONT;

    private AliyunIRecorder recorder;
    private AliyunIClipManager clipManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setLayoutId(R.layout.activity_camera);
        MySystemParams.getInstance().init(this);
        initSDK();
    }

    @Override
    public void onResume() {
        recorder.startPreview();
        super.onResume();
    }

    @Override
    public void onPause() {
        recorder.stopPreview();
        recorder = null;
        super.onPause();
    }

    private void initSDK() {
        recorder = AliyunRecorderCreator.getRecorderInstance(this);
        recorder.setDisplayView(binding.surfaceView);

        clipManager = recorder.getClipManager();
        clipManager.setMaxDuration(MAX_RECORD_TIME);
        clipManager.setMinDuration(MIN_RECORD_TIME);

        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.setVideoWidth(TEST_VIDEO_WIDTH);
        mediaInfo.setVideoHeight(TEST_VIDEO_HEIGHT);
        mediaInfo.setHWAutoSize(true);//硬编时自适应宽高为16的倍数
        recorder.setMediaInfo(mediaInfo);

        cameraType = recorder.getCameraCount() == 1 ? CameraType.BACK : cameraType;
        recorder.setCamera(cameraType);
    }
}
