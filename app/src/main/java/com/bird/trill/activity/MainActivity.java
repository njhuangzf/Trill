package com.bird.trill.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TextView;

import com.bird.trill.R;
import com.bird.trill.databinding.ActivityMainBinding;
import com.bird.trill.base.BaseActivity;
import com.bird.trill.fragment.BuildFragment;
import com.bird.trill.fragment.CameraFragment;
import com.bird.trill.fragment.ShortVideoFragment;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private LayoutInflater mLayoutInflater;

    @Override
    protected int layoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        initView();
    }

    protected void setStatusBarColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(color);
        }
    }

    private void initView() {
        mLayoutInflater = LayoutInflater.from(this);
        binding.tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        binding.tabHost.addTab(newTabSpec("video", "首页"), ShortVideoFragment.class, null);
        binding.tabHost.addTab(newTabSpec("concern", "关注"), BuildFragment.class, null);
        binding.tabHost.addTab(newLargeTabSpec("camera"), CameraFragment.class, null);
        binding.tabHost.addTab(newTabSpec("news", "消息"), BuildFragment.class, null);
        binding.tabHost.addTab(newTabSpec("me", "我"), BuildFragment.class, null);
    }

    private TabHost.TabSpec newLargeTabSpec(String tag) {
        TabHost.TabSpec tabSpec = binding.tabHost.newTabSpec(tag);
        View view = mLayoutInflater.inflate(R.layout.view_big_tab, null);
        tabSpec.setIndicator(view);
        return tabSpec;
    }

    private TabHost.TabSpec newTabSpec(String tag, String name) {
        TabHost.TabSpec tabSpec = binding.tabHost.newTabSpec(tag);
        View view = mLayoutInflater.inflate(R.layout.view_tab_indicator, null);
        TextView tvTile = view.findViewById(R.id.tab_text);
        tvTile.setText(name);
        tabSpec.setIndicator(view);
        return tabSpec;
    }
}
