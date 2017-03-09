package com.example.it.qq_player.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.it.qq_player.R;
import com.example.it.qq_player.View.VideoView;
import com.example.it.qq_player.bean.VideoBean;
import com.example.it.qq_player.utils.LogUtils;
import com.example.it.qq_player.utils.StringUtils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

public class VideoPlayerActivity extends BaseActivity {

    private VideoView videoView;
    private TextView tv_title;
    private TextView tv_time;
    private ImageView iv_battery;
    private MyBroadcastReceiver receiver;
    private SeekBar voice_seekbar;
    private AudioManager audioManager;
    private ImageView iv_voice;
    private int currentvoice;
    private float startY;
    private int maxVolume;
    private int currentVoice;
    private View alpha_view;
    private float currentAlpha;
    private TextView tv_currenttime;
    private TextView tv_duration;
    private SeekBar player_seekbar;
    private ImageView iv_back;
    private ImageView iv_pre;
    private ImageView iv_next;
    private ImageView iv_pause;
    private ArrayList<VideoBean> list;
    private int postion;
    private LinearLayout ll_top;
    private LinearLayout ll_bottom;
    private boolean isShowing;
    private static final int UPDATA_SYSTEM_TIME = 0;
    private static final int UPDATA_PLAYER_TIME = 1;
    private static final int AUTO_HIDE_CONTRLOR = 3;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATA_SYSTEM_TIME:
                    UpdateSystemTime();
                    break;
                case UPDATA_PLAYER_TIME:
                    StartUpdatePostion();
                    break;
                case AUTO_HIDE_CONTRLOR:
                    HideControlor();
                    break;
            }
        }
    };
    private GoogleApiClient client;
    private GestureDetector gestureDetector;
    private ImageView iv_screen;
    private LinearLayout ll_loding;

    @Override
    public void initData() {
        Uri uri = getIntent().getData();
        LogD("uri:::"+uri);
        if (uri!=null){
            //是从外部文件中发起的调用
            videoView.setVideoURI(uri);
            iv_pre.setEnabled(false);
            iv_next.setEnabled(false);
            tv_title.setText(uri.getPath());
        }else {
            //从内部发起的调用
            //获取arraylist集合数据，和当前的postion。
            list = (ArrayList<VideoBean>) getIntent().getSerializableExtra("videobean");
            postion = getIntent().getIntExtra("postion", -1);
            //播放指定的Postion的视频。
            PlayerItem();
        }
        //系统电量变化会发送广播的，要注册电量广播接收者。
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        receiver = new MyBroadcastReceiver();
        //注册广播接收者
        registerReceiver(receiver, filter);
        //获取当前的系统时间，并显示在当前的时间上。
        tv_time.setText(StringUtils.FormatSystemTime());
        //要实现系统时间不断的轮询更新系统时间的显示
        UpdateSystemTime();

        //获取音量控制管理
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        //获取最大的音量.
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        voice_seekbar.setMax(maxVolume);
        //获取当前的音量
        int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        voice_seekbar.setProgress(streamVolume);
        //设置Alerpha为高亮的
        ViewHelper.setAlpha(alpha_view, 0);
        //隐藏控制面板
        InitHideControlPanel();
    }

    /**
     * 隐藏控制面板
     */
    private void InitHideControlPanel() {
        //getMeasuredHeight(),在measure测量后才可以获取高度。
        ll_top.measure(0, 0);
//        LogD("getonMeauserHight:" + ll_top.getMeasuredHeight() + ";getHeight:" + ll_top.getHeight());
        ViewPropertyAnimator.animate(ll_top).translationY(-ll_top.getMeasuredHeight());
        //getHeight(),在onlayout摆放布局后，才可以获取高度。
        ll_bottom.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                LogD("getHeight:"+ll_bottom.getHeight());
                //获取到高度后，就取消layout布局的监听
                ll_bottom.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                ViewPropertyAnimator.animate(ll_bottom).translationY(ll_bottom.getHeight());
            }
        });
    }

    /**
     * 每500毫秒更新系统一次系统时间
     */
    private void UpdateSystemTime() {
        tv_time.setText(StringUtils.FormatSystemTime());
        handler.sendEmptyMessageDelayed(UPDATA_SYSTEM_TIME, 500);
//        LogUtils.LogD("系统时间：：：" + StringUtils.FormatSystemTime());
    }

    @Override
    public void initListener() {
        videoView.setOnPreparedListener(new MyOnPreparedListener());
        iv_pause.setOnClickListener(this);
        //给seekbar设置进度条变化的监听
        voice_seekbar.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
        //给声音设置点击事件
        iv_voice.setOnClickListener(this);
        //给Player_Seekbar设置进度条变化的监听
        player_seekbar.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
        //给videoView设置播放结束的监听
        videoView.setOnCompletionListener(new MyOnCompletionListener());
        //给videoView设置视频播放失败的监听
        videoView.setOnErrorListener(new MyErrorListener());
        //给返回键设置点击事件
        iv_back.setOnClickListener(this);
        //给上一曲，下一曲设置点击事件
        iv_pre.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        //给全屏按钮设置点击事件
        iv_screen.setOnClickListener(this);

        //创建手势识别器
        gestureDetector = new GestureDetector(this, new MySimpleOnGestureListener());
    }

    @Override
    public void initView() {
        videoView = (VideoView) findViewById(R.id.videoview);
        //顶部布局name
        tv_title = (TextView) findViewById(R.id.videoplayer_tv_title);
        //顶部布局时间
        tv_time = (TextView) findViewById(R.id.videoplayer_tv_time);
        //顶部布局系统电量
        iv_battery = (ImageView) findViewById(R.id.videoplayer_iv_battery);
        //顶部声音的seekbar。
        voice_seekbar = (SeekBar) findViewById(R.id.videopalyer_voice_seekbar);
        //顶部声音的icon
        iv_voice = (ImageView) findViewById(R.id.videopalyer_iv_voice);
        //透明度alpha
        alpha_view = findViewById(R.id.videoplayer_alpha);
        //底部播放当前的时间
        tv_currenttime = (TextView) findViewById(R.id.videopalyer_tv_currenttime);
        //底部播放时长
        tv_duration = (TextView) findViewById(R.id.videpalyer_tv_duration);
        //底部播放seekbar
        player_seekbar = (SeekBar) findViewById(R.id.videopalyer_player_seekbar);
        //底部返回键
        iv_back = (ImageView) findViewById(R.id.iv_back);
        //底部上一曲按键
        iv_pre = (ImageView) findViewById(R.id.iv_pre);
        //底部下一曲按键
        iv_next = (ImageView) findViewById(R.id.iv_next);
        //底部暂停按键
        iv_pause = (ImageView) findViewById(R.id.iv_pause);
        //获取顶部面板布局
        ll_top = (LinearLayout) findViewById(R.id.ll_top);
        //获取底部面板布局
        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        //全屏按钮
        iv_screen = (ImageView) findViewById(R.id.iv_screen);
        //正在加载中的布局
        ll_loding = (LinearLayout) findViewById(R.id.ll_loding_progress);
    }

    @Override
    public int getLayoutViewId() {
        return R.layout.activity_video_player;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //反注册广播接收者
        unregisterReceiver(receiver);
        //当activity销毁了，还在发送handler,会引发内存泄漏
        handler.removeCallbacksAndMessages(null);
    }

    /**手指滑动，改变屏幕的Aplha值，音量的大小*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getY();
                //获取手指压下的声音。
                currentVoice = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                //获取手指压下的Alpha。
                currentAlpha = ViewHelper.getAlpha(alpha_view);
                break;
            case MotionEvent.ACTION_MOVE:
                //手指划过屏幕的距离 = 手指当前位置 - 手指压下时的位置
                float CurrentY = event.getY();
                float OffsetY = CurrentY - startY;
                //获取屏幕的高度
                int height = getWindowManager().getDefaultDisplay().getHeight() / 2;
                //获取屏幕的宽度
                int width = getWindowManager().getDefaultDisplay().getWidth();
                //手指划过屏幕的百分比 =  手指划过屏幕的距离 / 屏幕高度
                float offsetBl = OffsetY / height;

                float offsetX = event.getX();
                if (offsetX < width / 2) {
                    //左边滑动，调屏幕的亮度。
                    //屏幕的亮度的变化百分比
                    moveAlpha(offsetBl);
                } else {
                    //右边滑动，调声音的变化。
                    //声音变化的百分比
                    moveVoice(offsetBl);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 透明度的变化
     *
     * @param offsetBl
     */
    private void moveAlpha(float offsetBl) {
        //变化的Alpha = 手指划过屏幕的百分比 * 最大Alpha值。
        float changeAlpha = offsetBl * 1;
        //最终的Alpha = 手指压下时的Alpha + 变化的Alpha。
        float finalAlpha = currentAlpha + changeAlpha;
        if (finalAlpha >= 0 && finalAlpha <= 1) {
            //设置屏幕的Alpha值
            ViewHelper.setAlpha(alpha_view, finalAlpha);
            LogUtils.LogD("透明度变化：" + finalAlpha);
        }
    }

    /**
     * 滑动屏幕声音的变化的百分比
     *
     * @param offsetBl
     */
    private void moveVoice(float offsetBl) {
        //变化的音量 = 手指划过屏幕的百分比 * 最大音量
        int changeVoice = (int) (voice_seekbar.getMax() * offsetBl);
        //最终的音量 = 手指压下时的音量 + 变化的音量
        int finalVoice = currentVoice + changeVoice;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, finalVoice, 0);
        voice_seekbar.setProgress(finalVoice);
    }

    @Override
    protected void processListener(int id) {
        switch (id) {
            case R.id.iv_pause:
                SwitchPauseState();
                break;
            case R.id.videopalyer_iv_voice:
                //切换是否静音
                SwitchVoiceState();
                break;
            case R.id.iv_back:
                //点击返回键的监听
                VideoPlayerActivity.this.finish();
                break;
            case R.id.iv_pre:
                //点击上一曲
                PlayerPre();
                break;
            case R.id.iv_next:
                //点击下一曲
                PlayerNext();
                break;
            case R.id.iv_screen:
                //切换全屏显示
                SwitchScreenShow();
                break;
        }
    }
    /**切换播放的状态，并实现播放、暂停功能，*/
    private void SwitchPauseState() {
        if (videoView.isPlaying()) {
            videoView.pause();
        } else {
            videoView.start();
        }
        // 播放的状态显示。
        SwitchState();
    }

    /**切换全屏显示*/
    private void SwitchScreenShow() {
        videoView.switchFullscreen();
        //更换全屏，默认显示按钮
        SwitchIshowingButton();
    }
    /**切换是否全屏，默认显示按钮*/
    private void SwitchIshowingButton() {
        if(videoView.isFullScreen()){
            //如果全屏，切换到默认状态
            iv_screen.setImageDrawable(getResources().getDrawable(R.drawable.btn_default_screen_normal));
        }else{
            //如果默认，切换到全屏状态
            iv_screen.setImageDrawable(getResources().getDrawable(R.drawable.btn_full_screen_normal));
        }
    }

    /**
     * 播放下一曲
     */
    private void PlayerNext() {
        if (postion != list.size() - 1) {
            postion++;
            PlayerItem();
        }
    }

    /**
     * 播放上一曲
     */
    private void PlayerPre() {
        if (postion != 0) {
            postion--;
            PlayerItem();
        }
    }

    /**
     * 更新上一首，下一首的按钮是否可用
     */
    private void UpdataPreAndNext() {
        iv_pre.setEnabled(postion != 0);
        iv_next.setEnabled(postion != list.size() - 1);
    }

    /**
     * 播放指定的Postion
     */
    private void PlayerItem() {
        VideoBean videobean = list.get(postion);
        LogUtils.LogD("cursor名称：" + videobean.title + "大小：" + videobean.size);
        //通过数据的路径播放视频。
        videoView.setVideoPath(videobean.data);
        tv_title.setText(videobean.title);
        //如果到第一首时，上一曲应该是灰色。如果是最后一首时，下一首是灰色。
        UpdataPreAndNext();
    }

    /**
     * 切换是否静音 如果当前的声音不为静音，则记录当前的声音，并设置为静音。如果当前的声音为0，则恢复声音
     */
    private void SwitchVoiceState() {
        if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {
            //不为静音，保存当前的音量。设置为静音，并修改进度条为0
            currentvoice = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            voice_seekbar.setProgress(0);
        } else {
            //静音，恢复音量
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentvoice, 0);
            voice_seekbar.setProgress(currentvoice);
        }
    }

    /**
     * 切换播放的状态
     */
    private void SwitchState() {
        if (videoView.isPlaying()){
            iv_pause.setImageDrawable(getResources().getDrawable(R.drawable.video_puase_selector));
            //正在播放，开始Hnalder更新Position.
            handler.sendEmptyMessage(UPDATA_PLAYER_TIME);
        }else{
            iv_pause.setImageDrawable(getResources().getDrawable(R.drawable.video_player_selector));
            //暂停时，取消handler更新的Postion.
            handler.removeMessages(UPDATA_PLAYER_TIME);
        }
    }

    /**
     * 自定义单击手势监听
     */
    class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        //单击时的回调
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //切换是否隐藏控制面板
            SwitchControlorState();
            return super.onSingleTapConfirmed(e);
        }
        //双击时的回调
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //双击时切换全屏，半屏的显示。
            SwitchScreenShow();
            return super.onDoubleTap(e);
        }
        //长按的回调
        @Override
        public void onLongPress(MotionEvent e) {
            //长按时，切换播放状态。
            SwitchPauseState();
            super.onLongPress(e);
        }
    }

    //切换是否隐藏控制面板显示
    private void SwitchControlorState() {
        if (isShowing) {
            //如果显示,隐藏控制面板
            HideControlor();
        } else {
            //如果隐藏，显示控制面板
            ShowControlor();
            //5秒后，自动隐藏控制面板
            handler.sendEmptyMessageDelayed(AUTO_HIDE_CONTRLOR,5000);
        }
    }

    /**
     * 显示控制面板
     */
    private void ShowControlor() {
        ViewPropertyAnimator.animate(ll_top).translationY(0);
        ViewPropertyAnimator.animate(ll_bottom).translationY(0);
        isShowing = true;
    }

    /**
     * 隐藏控制面板
     */
    private void HideControlor() {
        ViewPropertyAnimator.animate(ll_top).translationY(-ll_top.getHeight());
        ViewPropertyAnimator.animate(ll_bottom).translationY(ll_bottom.getHeight());
        isShowing = false;
    }

    /**
     * 自定义PreparedListener实现类
     */
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            //准备完成后，隐藏进度条显示
            ll_loding.setVisibility(View.GONE);
            //在mediaplayer准备完成之后，在开启播放。
            videoView.start();
            //更新暂停按钮使用的图片,并更新播放时间，播放进度。
            SwitchState();

            //获取时间总长度 ，在Mediaplayer 或VideoView 中可以获取视频总时长，当前的进度。
            int duration = mp.getDuration();
            tv_duration.setText(StringUtils.FormatDuration(duration));
            player_seekbar.setMax(duration);
            //开始更新时间进度
            StartUpdatePostion();
            //切换全屏，默认按钮。
            SwitchIshowingButton();
        }
    }

    /**
     * 开始更新时间的进度
     */
    private void StartUpdatePostion() {
        //获取当前的进度，并每500毫秒更新一次。
        int currentPosition = videoView.getCurrentPosition();
        UpdataPostion(currentPosition);
        handler.sendEmptyMessageDelayed(UPDATA_PLAYER_TIME, 500);
    }

    /**
     * 根据当前的Postion,更新位置
     */
    private void UpdataPostion(int currentPosition) {
        tv_currenttime.setText(StringUtils.FormatDuration(currentPosition));
        player_seekbar.setProgress(currentPosition);
    }

    /**
     * 通过系统电量，更新显示电量图片
     */
    private void UpdataBattery(int level) {
        if (level < 10) {
            iv_battery.setImageDrawable(getResources().getDrawable(R.drawable.ic_battery_0));
        } else if (level < 20) {
            iv_battery.setImageDrawable(getResources().getDrawable(R.drawable.ic_battery_10));
        } else if (level < 40) {
            iv_battery.setImageDrawable(getResources().getDrawable(R.drawable.ic_battery_20));
        } else if (level < 60) {
            iv_battery.setImageDrawable(getResources().getDrawable(R.drawable.ic_battery_40));
        } else if (level < 80) {
            iv_battery.setImageDrawable(getResources().getDrawable(R.drawable.ic_battery_60));
        } else if (level < 100) {
            iv_battery.setImageDrawable(getResources().getDrawable(R.drawable.ic_battery_80));
        } else {
            iv_battery.setImageDrawable(getResources().getDrawable(R.drawable.ic_battery_100));
        }
    }

    /**
     * 自定义广播接收者实现类
     */
    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取系统电量，更新电量显示图片
            int level = intent.getIntExtra("level", 0);

            UpdataBattery(level);
//            LogUtils.LogD("系统电量：" + level);
        }
    }

    /**自定义视频播放失败的监听*/
    class MyErrorListener implements MediaPlayer.OnErrorListener{

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayerActivity.this);
            builder.setTitle("提示");
            builder.setMessage("该视频无法播放");
            builder.setPositiveButton("确认退出？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
            return false;
        }
    }

    /**
     * 自定义视频结束的监听
     */
    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            //规避系统错误，修改播放结束时间为总时长时间。
            tv_currenttime.setText(StringUtils.FormatDuration(mp.getDuration()));
            //取消更新Postion位置的Handler
            handler.removeMessages(UPDATA_PLAYER_TIME);
            //更新一下视频播放的图片
            SwitchState();
        }
    }

    /**
     * 自定义seekbar变化的监听
     */
    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 当进度条发生变化时的回调
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //如果不是用户发起的操作，则不处理。
            if (!fromUser) {
                return;
            }
            switch (seekBar.getId()) {
                case R.id.videopalyer_voice_seekbar:
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    break;
                case R.id.videopalyer_player_seekbar:
                    videoView.seekTo(progress);
                    //在更新一下播放时间
                    UpdataPostion(progress);
                    break;
            }

        }

        /**
         * 当手指触摸seekbar时的回调
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //取消隐藏控制面板发送的消息
            handler.removeMessages(AUTO_HIDE_CONTRLOR);
        }

        /**
         * 当手指离开seekbar时的回调
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //继续5秒后自动隐藏控制面板
            handler.sendEmptyMessageDelayed(AUTO_HIDE_CONTRLOR,5000);
        }
    }
}
