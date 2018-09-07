package com.bird.trill.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;

import com.aliyun.common.utils.ToastUtil;
import com.bird.trill.R;
import com.bird.trill.base.BaseActivity;
import com.bird.trill.databinding.ActivityMainBinding;
import com.bird.trill.fragment.BuildFragment;
import com.bird.trill.fragment.ShortVideoFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private static Map<Integer, Class> FRAGMENTS = new HashMap<>();
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    static {
        FRAGMENTS.put(R.id.tb_video, ShortVideoFragment.class);
        FRAGMENTS.put(R.id.tb_concern, BuildFragment.class);
        FRAGMENTS.put(R.id.tb_news, BuildFragment.class);
        FRAGMENTS.put(R.id.tb_me, BuildFragment.class);
    }

    private static final int PERMISSION_CODES = 1001;

    private boolean permissionGranted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setLayoutId(R.layout.activity_main);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission() {
        List<String> p = new ArrayList<>();
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                p.add(permission);
            }
        }
        if (p.size() > 0) {
            requestPermissions(p.toArray(new String[p.size()]), PERMISSION_CODES);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODES:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    ToastUtil.showToast(this, getString(R.string.need_permission));
                    permissionGranted = false;
                } else {
                    permissionGranted = true;
                }
                break;
            default:
                break;
        }

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

        binding.btnCamera.setOnClickListener(v -> {
            if (permissionGranted) {
                startActivity(new Intent(this, CameraActivity.class));
            } else {
                ToastUtil.showToast(this, getString(R.string.need_permission));
            }
        });

        binding.tbVideo.setChecked(true);
    }

    protected void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.tab_content, fragment);
        ft.commit();
    }
}
