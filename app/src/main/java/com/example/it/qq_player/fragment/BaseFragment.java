package com.example.it.qq_player.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.it.qq_player.interfaceView.BaseInterface;

/**
 * Created by lenovo on 2016/9/23.
 */
public abstract class BaseFragment extends Fragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(getActivity(), getLayoutViewId(), null);
        initView();
        initListener();
        initData();
        return view;
    }

    /**
     * findViewById()操作。
     * @param id
     * @return
     */
    public View  findViewById(int id){
        return view.findViewById(id);
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

}
