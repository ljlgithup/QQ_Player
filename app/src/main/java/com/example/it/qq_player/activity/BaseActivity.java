package com.example.it.qq_player.activity;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.it.qq_player.R;
import com.example.it.qq_player.interfaceView.BaseInterface;
import com.example.it.qq_player.utils.LogUtils;

public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener{


    /**
     * 规范代码结构，统一编码格式
     * 提供公共的方法
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutViewId());
        initView();
        initData();
        initListener();
        RegCommonBtn();
    }

    /**
     * 在多个界面都有的点击事件，已经由Base处理掉了，那么这个控件注册点击事件的监听也由base处理。
     */
    protected void RegCommonBtn(){
        View view = this.findViewById(R.id.back);
        if(view!=null){
            view.setOnClickListener(this);
        }
    }

    /**
     * 初始化数据的操作
     */
    public abstract void initData();
    /**
     * 初始化监听，点击事件处理
     */
    public abstract void initListener();

    /**
     * 初始化控件的操作
     */
    public abstract void initView();

    /**
     *返回当前Activity要显示的布局id，要子类自己实现。
     */
    public abstract int getLayoutViewId();

    /**
     * 多个界面都有的点击事件，在基类做统一处理
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //可以设置预设资源id
            case R.id.back:
                finish();
                break;
            default:
                processListener(v.getId());
                break;

        }
    }

    /**
     *没有统一的点击事件，由子类自己重新实现。
     */
    protected abstract void processListener(int id);

    /**
     * 多个界面都有的吐丝显示
     */
    protected void toast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    /**
     * 打印ERROR级别的Log，使用当前的类名为TAG。
     * @param msg
     */
    protected void LogE(String msg){
        LogUtils.LogE(msg);
    }

    /**
     * 打印Debug级别的Log，使用当前的类名为TAG。
     * @param msg
     */
    protected void LogD(String msg){
        LogUtils.LogD(msg);
    }
}
