package com.example.it.qq_player.activity;

import android.content.Intent;

import com.example.it.qq_player.R;
public class SplashActivity extends BaseActivity {

    @Override
    protected void onResume() {
        super.onResume();
        //等待2秒后，跳转到主界面
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        },2000);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initView() {

    }

    @Override
    public int getLayoutViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void processListener(int id) {

    }
}
