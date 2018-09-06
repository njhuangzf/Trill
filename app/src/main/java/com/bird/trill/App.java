package com.bird.trill;

import android.app.Application;

import com.aliyun.common.httpfinal.QupaiHttpFinal;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        loadLibs();
        QupaiHttpFinal.getInstance().initOkHttpFinal();
    }

    /**
     * 加载阿里视屏so
     */
    private void loadLibs() {
        //音频编解码
        System.loadLibrary("fdk-aac");
        //视频编码
        System.loadLibrary("live-openh264");
        //SDK依赖的第三方库
        System.loadLibrary("QuCore-ThirdParty");
        //SDK核心库
        System.loadLibrary("QuCore");
    }
}
