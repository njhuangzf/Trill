package com.bird.trill.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;

import com.bird.trill.R;
import com.bird.trill.base.BaseActivity;
import com.bird.trill.databinding.ActivityMainBinding;
import com.bird.trill.fragment.BuildFragment;
import com.bird.trill.fragment.ShortVideoFragment;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private static Map<Integer, Class> FRAGMENTS = new HashMap<>();

    static {
        FRAGMENTS.put(R.id.tb_video, ShortVideoFragment.class);
        FRAGMENTS.put(R.id.tb_concern, BuildFragment.class);
        FRAGMENTS.put(R.id.tb_news, BuildFragment.class);
        FRAGMENTS.put(R.id.tb_me, BuildFragment.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setLayoutId(R.layout.activity_main);
        initView();
    }

    private void initView() {
        binding.tabHost.setOnCheckedChangeListener((group, checkedId) -> {
            try {
                if (FRAGMENTS.containsKey(checkedId)) {
                    replaceFragment((Fragment) FRAGMENTS.get(checkedId).newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.btnCamera.setOnClickListener(v ->
                startActivity(new Intent(this, CameraActivity.class)));

        binding.tbVideo.setChecked(true);
    }

    protected void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.tab_content, fragment);
        ft.commit();
    }
}
