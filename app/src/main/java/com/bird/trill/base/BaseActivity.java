package com.bird.trill.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity<B extends ViewDataBinding> extends AppCompatActivity {

    protected B binding;

    protected void setLayoutId(int layoutResID) {
        binding = DataBindingUtil.setContentView(this, layoutResID);
    }
}
