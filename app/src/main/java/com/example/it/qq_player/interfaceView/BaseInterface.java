package com.example.it.qq_player.interfaceView;

/**
 * 在基类，baseactivity,basefragment都有的抽象方法抽取到接口中。
 */
public interface BaseInterface {
    /**
     * 初始化数据的操作
     */
    void initData();
    /**
     * 初始化监听，点击事件处理
     */
    void initListener();

    /**
     * 初始化控件的操作
     */
    void initView();

    /**
     *返回当前Activity要显示的布局id，要子类自己实现。
     */
    int getLayoutViewId();
}
