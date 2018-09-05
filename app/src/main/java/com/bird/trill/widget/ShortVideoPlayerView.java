package com.bird.trill.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import com.alivc.player.VcPlayerLog;
import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.AliyunMediaInfo;
import com.aliyun.vodplayer.media.AliyunVodPlayer;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.aliyun.vodplayer.media.IAliyunVodPlayer.PlayerState;

public class ShortVideoPlayerView extends RelativeLayout {

    private static final String TAG = "ShortVideoPlayerView";

    //视频画面
    private SurfaceView mSurfaceView;
    //播放器
    private AliyunVodPlayer mVideoPlayer;
    //用来记录前后台切换时的状态，以供恢复。
    private PlayerState mPlayerState;
    //媒体信息
    private AliyunMediaInfo mMediaInfo;
    private boolean playing = false;

    public ShortVideoPlayerView(Context context) {
        super(context);
        initVideoView();
    }

    public ShortVideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoView();
    }

    public ShortVideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView();
    }

    @Override
    protected void onDetachedFromWindow() {
        onDestroy();
        super.onDetachedFromWindow();
    }

    private void initVideoView() {
        //初始化播放用的surfaceView
        initSurfaceView();
        //初始化播放器
        initPlayer();
    }

    /**
     * 初始化播放器显示view
     */
    private void initSurfaceView() {
        mSurfaceView = new SurfaceView(getContext().getApplicationContext());
        addSubView(mSurfaceView);

        SurfaceHolder holder = mSurfaceView.getHolder();
        //增加surfaceView的监听
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                VcPlayerLog.d(TAG, " surfaceCreated = surfaceHolder = " + surfaceHolder);
                mVideoPlayer.setDisplay(surfaceHolder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width,
                                       int height) {
                VcPlayerLog.d(TAG, " surfaceChanged surfaceHolder = " + surfaceHolder + " ,  width = " + width + " , height = " + height);
                mVideoPlayer.surfaceChanged();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                VcPlayerLog.d(TAG, " surfaceDestroyed = surfaceHolder = " + surfaceHolder);
            }
        });
    }

    /**
     * 初始化播放器
     */
    private void initPlayer() {
        Log.d(TAG, "initPlayer() called");
        mVideoPlayer = new AliyunVodPlayer(getContext());
        //循环播放
        mVideoPlayer.setCirclePlay(true);

        //出错时处理，查看接口文档中的错误码和错误消息
        mVideoPlayer.setOnErrorListener((arg0, arg1, msg) -> Log.e(TAG, "onError: error code = " + arg0 + "" + arg1 + ", msg = " + msg));

        //准备完成触发
        mVideoPlayer.setOnPreparedListener(() -> {
            Log.d(TAG, "onPrepared() called");
            if (mVideoPlayer == null) {
                return;
            }
            mMediaInfo = mVideoPlayer.getMediaInfo();
            if (mMediaInfo == null) {
                return;
            }
            if (playing) {
                //准备成功之后可以调用start方法开始播放
                start();
            }
        });
    }

    public void setOnInfoListener(IAliyunVodPlayer.OnInfoListener onInfoListener) {
        if (mVideoPlayer != null) {
            mVideoPlayer.setOnInfoListener(onInfoListener);
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        Log.d(TAG, "stop() called");
        playing = false;
        if (mVideoPlayer != null) {
            mVideoPlayer.stop();
        }
    }

    /**
     * 开始播放
     */
    public void start() {
        playing = true;
        Log.d(TAG, "start() called");
        if (mVideoPlayer == null) {
            return;
        }
        IAliyunVodPlayer.PlayerState playerState = mVideoPlayer.getPlayerState();
        if (playerState == IAliyunVodPlayer.PlayerState.Paused || playerState == IAliyunVodPlayer.PlayerState.Prepared || !isPlaying()) {
            mVideoPlayer.start();
        }

    }

    /**
     * 暂停播放
     */
    public void pause() {
        playing = false;
        if (mVideoPlayer == null) {
            return;
        }

        IAliyunVodPlayer.PlayerState playerState = mVideoPlayer.getPlayerState();
        if (playerState == IAliyunVodPlayer.PlayerState.Started) {
            mVideoPlayer.pause();
        }
    }

    public boolean isPlaying() {
        if (this.mVideoPlayer != null) {
            PlayerState playerState = mVideoPlayer.getPlayerState();
            return playerState == PlayerState.Started;
        }
        return false;
    }

    /**
     * addSubView
     * 添加子view到布局中
     *
     * @param view 子view
     */
    private void addSubView(View view) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(view, params);//添加到布局中
    }

    public void setVideoUrl(String videoUrl) {
        if (isPlaying()) {
            stop();
        }
        Log.d(TAG, "setVideoUrl() called with: videoUrl = [" + videoUrl + "]");
        if (mVideoPlayer != null) {
            AliyunLocalSource.AliyunLocalSourceBuilder asb = new AliyunLocalSource.AliyunLocalSourceBuilder();
            asb.setSource(videoUrl);
            mVideoPlayer.prepareAsync(asb.build());
        }
    }

    /**
     * 活动销毁，释放
     */
    public void onDestroy() {
        stop();
        if (mVideoPlayer != null) {
            mVideoPlayer.release();
        }
        mSurfaceView = null;
    }

    /**
     * Activity回来后，恢复之前的状态
     */
    public void resumePlayerState() {
        if (mVideoPlayer == null) {
            return;
        }

        if (mPlayerState == PlayerState.Paused) {
            pause();
        } else if (mPlayerState == PlayerState.Started) {
            start();
        }
    }

    /**
     * 保存当前的状态，供恢复使用
     */
    private void savePlayerState() {
        if (mVideoPlayer == null) {
            return;
        }

        mPlayerState = mVideoPlayer.getPlayerState();
        //然后再暂停播放器
        //如果希望后台继续播放，不需要暂停的话，可以注释掉pause调用。
        pause();

    }

    public void onPause() {
        //保存播放器的状态，供resume恢复使用。
        savePlayerState();
    }
}
