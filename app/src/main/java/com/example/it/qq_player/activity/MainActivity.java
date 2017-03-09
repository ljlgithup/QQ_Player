package com.example.it.qq_player.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.it.qq_player.R;
import com.example.it.qq_player.adapter.MyFragmentAdapter;
import com.example.it.qq_player.fragment.AudioFragment;
import com.example.it.qq_player.fragment.VideoFragment;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private ViewPager mViewPager;
    private ArrayList<Fragment> list;
    private MyFragmentAdapter adapter;
    private TextView tv_video;
    private TextView tv_audio;
    public static final int VIDEO_PAGER = 0;
    public static final int AUDIO_PAGER = 1;
    private View indicator_line;
    private String TAG = "MainActivity";

    @Override
    public void initData() {
        list = new ArrayList<Fragment>();
        VideoFragment videoFragment = new VideoFragment();
        AudioFragment audioFragment = new AudioFragment();
        list.add(videoFragment);
        list.add(audioFragment);
        //初始化指示器的宽度

    }

    @Override
    public void initListener() {
        adapter = new MyFragmentAdapter(getSupportFragmentManager(), list);
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(new MyOnPagerChangeListener());
        tv_video.setOnClickListener(this);
        tv_audio.setOnClickListener(this);
        //在初始化时，默认视频应该是选中变大的。
        ViewPropertyAnimator.animate(tv_video).scaleX(1.2f).scaleY(1.2f);
    }

    @Override
    public void initView() {
        tv_video = (TextView) findViewById(R.id.tv_video);
        tv_audio = (TextView) findViewById(R.id.tv_audio);
        mViewPager = (ViewPager) findViewById(R.id.my_viewpager);
        indicator_line = findViewById(R.id.indicator_line);
        //初始化指示器的宽度
        int width = getWindowManager().getDefaultDisplay().getWidth();
        indicator_line.getLayoutParams().width = width/2;
        //重新计算大小，并刷新界面。
        indicator_line.requestLayout();
        //重新刷新界面，不计算大小。
//        indicator_line.invalidate();
    }

    /**
     * 获取布局的id。
     * @return
     */
    @Override
    public int getLayoutViewId() {
        //取消标题的显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        return R.layout.activity_main;
    }

    /**
     * 多个界面没有统一的点击事件，由自己实现。
     */
    @Override
    protected void processListener(int id) {
        switch (id){
            case R.id.tv_video:
            //切换到视频界面,会走onPageSelected()方法。
            mViewPager.setCurrentItem(VIDEO_PAGER);
            break;
            case R.id.tv_audio:
            //切换到音频界面,会走onPageSelected()方法。
            mViewPager.setCurrentItem(AUDIO_PAGER);
            break;
        }
    }

    /**
     * 抽取onpagerchangeListener监听。
     */
    class MyOnPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //postion：是当前的位置，positionOffset：手指划过屏幕的百分比。positionOffsetPixels：手指划过屏幕的像素。
            //指示器的位置 = 起始的位置 + 偏移的位置。
            //起始位置 = postion * 指示器的宽度。
            int startX = position * indicator_line.getWidth();
            //偏移的位置算法1 = 手指划过屏幕的百分比 * 指示器的宽度。
//            int offsetX = (int) (positionOffset * indicator_line.getWidth());
            //偏移的位置算法2 = 手指划过屏幕的像素/指示器的个数。
            int offsetX = positionOffsetPixels/list.size();
            //指示器的位置 = 起始的位置 + 偏移的位置。
            int currentPostion = startX + offsetX;
            ViewHelper.setTranslationX(indicator_line,currentPostion);
        }

        @Override
        public void onPageSelected(int position) {
            //在onpager条目选中时高亮显示，其它没有选中的半灰色显示
            int green = getResources().getColor(R.color.green);
            int halfwhite = getResources().getColor(R.color.halfwhite);
            int tab = position;

            if(tab==VIDEO_PAGER){
                //视频高亮，音频半灰色显示。
                tv_video.setTextColor(green);
                tv_audio.setTextColor(halfwhite);
                //选中的变大，没有选中的还原动画
                ViewPropertyAnimator.animate(tv_video).scaleX(1.2f).scaleY(1.2f);
                ViewPropertyAnimator.animate(tv_audio).scaleX(1.0f).scaleY(1.0f);

            }else{
                //音频高亮，视频版灰色显示。
                tv_audio.setTextColor(green);
                tv_video.setTextColor(halfwhite);
                //选中的变大，没有选中的还原动画
                ViewPropertyAnimator.animate(tv_audio).scaleX(1.2f).scaleY(1.2f);
                ViewPropertyAnimator.animate(tv_video).scaleX(1.0f).scaleY(1.0f);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
