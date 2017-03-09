package com.example.it.qq_player.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by lenovo on 2016/9/23.
 */
public class MyFragmentAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> list;

    public MyFragmentAdapter(FragmentManager fm,ArrayList<Fragment> list) {
        super(fm);
        this.list = list;
    }

    /**
     * 根据当前的条目，返回当前的Fragment
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    /**
     * 返回有多少个fragment
     * @return
     */
    @Override
    public int getCount() {
        return list.size();
    }
}
