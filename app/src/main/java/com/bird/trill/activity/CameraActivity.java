package com.bird.trill.activity;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.aliyun.common.utils.MySystemParams;
import com.aliyun.recorder.AliyunRecorderCreator;
import com.aliyun.recorder.supply.AliyunIClipManager;
import com.aliyun.recorder.supply.AliyunIRecorder;
import com.aliyun.struct.recorder.CameraType;
import com.aliyun.struct.recorder.FlashType;
import com.aliyun.struct.recorder.MediaInfo;
import com.bird.trill.R;
import com.bird.trill.base.BaseActivity;
import com.bird.trill.databinding.ActivityCameraBinding;
import com.bird.trill.util.OrientationDetector;

public class CameraActivity extends BaseActivity<ActivityCameraBinding> {

    private static int TEST_VIDEO_WIDTH = 540;
    private static int TEST_VIDEO_HEIGHT = 960;

    private static final int MIN_RECORD_TIME = 1000;
    private static final int MAX_RECORD_TIME = 60 * 1000;

    private CameraType cameraType = CameraType.FRONT;

    private FlashType flashType = FlashType.OFF;
    private OrientationDetector orientationDetector;
    private AliyunIRecorder recorder;
    private AliyunIClipManager clipManager;

    private float scaleFactor;
    private boolean isRecording;
    private int rotation;
    private int beautyLevel = 80;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MySystemParams.getInstance().init(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setLayoutId(R.layout.activity_camera);
        initOrientationDetector();
        initView();
        initSDK();
        initListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recorder.startPreview();
        if (orientationDetector != null && orientationDetector.canDetectOrientation()) {
            orientationDetector.enable();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRecording) {
            recorder.cancelRecording();
        }
        recorder.stopPreview();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (orientationDetector != null) {
            orientationDetector.disable();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recorder.destroy();
        AliyunRecorderCreator.destroyRecorderInstance();
        if (orientationDetector != null) {
            orientationDetector.setOrientationChangedListener(null);
        }
    }

    private void initOrientationDetector() {
        orientationDetector = new OrientationDetector(getApplicationContext());
        orientationDetector.setOrientationChangedListener(() -> {
            rotation = getPictureRotation();
            recorder.setRotation(rotation);
        });
    }

    private int getPictureRotation() {
        int orientation = orientationDetector.getOrientation();
        int rotation = 90;
        if ((orientation >= 45) && (orientation < 135)) {
            rotation = 180;
        }
        if ((orientation >= 135) && (orientation < 225)) {
            rotation = 270;
        }
        if ((orientation >= 225) && (orientation < 315)) {
            rotation = 0;
        }
        if (cameraType == CameraType.FRONT) {
            if (rotation != 0) {
                rotation = 360 - rotation;
            }
        }
        Log.d("MyOrientationDetector", "generated rotation ..." + rotation);
        return rotation;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        binding.surfaceView.setOnTouchListener((v, event) -> true);
        binding.back.setOnClickListener(v -> finish());
        binding.magic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.switchLight.setOnClickListener(v -> {
            if (flashType == FlashType.OFF) {
                flashType = FlashType.AUTO;
            } else if (flashType == FlashType.AUTO) {
                flashType = FlashType.ON;
            } else if (flashType == FlashType.ON) {
                flashType = FlashType.OFF;
            }
            switch (flashType) {
                case AUTO:
                    v.setSelected(false);
                    v.setActivated(true);
                    break;
                case ON:
                    v.setSelected(true);
                    v.setActivated(false);
                    break;
                case OFF:
                    v.setSelected(true);
                    v.setActivated(true);
                    break;
                default:
                    break;
            }
            recorder.setLight(flashType);
        });

        binding.switchCamera.setOnClickListener(v -> {
            int type = recorder.switchCamera();
            if (type == CameraType.BACK.getType()) {
                cameraType = CameraType.BACK;
            } else if (type == CameraType.FRONT.getType()) {
                cameraType = CameraType.FRONT;
            }
            switchLightBtnState();
        });
    }

    private void switchLightBtnState() {
        if (cameraType == CameraType.FRONT) {
            binding.switchLight.setVisibility(View.GONE);
        } else if (cameraType == CameraType.BACK) {
            binding.switchLight.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
 /*       FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) binding.surfaceView.getLayoutParams();
        Rect rect = new Rect();
        getWindowManager().getDefaultDisplay().getRectSize(rect);
        layoutParams.width = rect.width();
        layoutParams.height = rect.height();
        binding.surfaceView.setLayoutParams(layoutParams);*/
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
        //设置摄像头类型
        recorder.setCamera(cameraType);
        recorder.setBeautyLevel(beautyLevel);
        recorder.needFaceTrackInternal(true);
        recorder.setRate(1f);
    }
}
