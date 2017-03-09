package com.example.it.qq_player.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.it.qq_player.R;
import com.example.it.qq_player.bean.AduioBean;
import com.example.it.qq_player.lyrics.LyricLoader;
import com.example.it.qq_player.lyrics.LyricView;
import com.example.it.qq_player.service.AudioPlayerService;
import com.example.it.qq_player.utils.LogUtils;
import com.example.it.qq_player.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;

public class AudioPlayerActivity extends BaseActivity {


    private ArrayList<AduioBean> list;
    private MyServiceConnection serviceConnection;
    private ImageView iv_audio_pause;
    private AudioPlayerService.MyIBinder myIBinder;
    private MyBroadcastReceiver myBroadcastReceiver;
    private ImageView iv_audio_back;
    private TextView tv_audio_song;
    private TextView tv_audio_arties;
    private ImageView iv_audio_wave;
    private TextView tv_player_time;
    private SeekBar player_seekbar;
    private ImageView iv_player_pre;
    private ImageView iv_player_next;
    private ImageView iv_player_mode;
    private LyricView lyric;
    private static final int UPDATA_CURRENT_POSITION = 0;
    private static final int UPDATA_LYRIC = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATA_CURRENT_POSITION:
                    UpdataPlayerDuration();
                    break;
                case UPDATA_LYRIC:
                    SwitchLineUpdata();
                    break;
            }
        }
    };

    @Override
    public void initData() {

        Intent service = new Intent(getIntent());
        service.setClass(this, AudioPlayerService.class);
        //开启服务
        serviceConnection = new MyServiceConnection();
        bindService(service, serviceConnection, Service.BIND_AUTO_CREATE);
        startService(service);

        //开启示波器动画
        AnimationDrawable animation = (AnimationDrawable) iv_audio_wave.getDrawable();
        animation.start();

    }

    @Override
    public void initListener() {
        iv_audio_pause.setOnClickListener(this);
        iv_audio_back.setOnClickListener(this);
        //注册一个广播接收者，接收service发过来的广播
        IntentFilter filter = new IntentFilter("com.uipower.audioplayer.prepared");
        myBroadcastReceiver = new MyBroadcastReceiver();
        registerReceiver(myBroadcastReceiver, filter);

        //给seekbar设置进度条变化的监听
        player_seekbar.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
        //给上一首，下一首设置点击事件
        iv_player_pre.setOnClickListener(this);
        iv_player_next.setOnClickListener(this);
        //给切换播放音乐模式，设置点击事件
        iv_player_mode.setOnClickListener(this);
    }

    @Override
    public void initView() {
        iv_audio_pause = (ImageView) findViewById(R.id.iv_audio_pause);
        //音频界面，返回按钮
        iv_audio_back = (ImageView) findViewById(R.id.iv_audio_back);
        //音频界面，歌名
        tv_audio_song = (TextView) findViewById(R.id.tv_audio_song);
        //音频界面，歌手
        tv_audio_arties = (TextView) findViewById(R.id.tv_audio_arties);
        //音频界面，示波器
        iv_audio_wave = (ImageView) findViewById(R.id.iv_audio_wave);
        //当前播放时长，时间更新
        tv_player_time = (TextView) findViewById(R.id.tv_player_time);
        //当前播放的进度条
        player_seekbar = (SeekBar) findViewById(R.id.adudio_player_seekbar);
        //播放上一首的按钮
        iv_player_pre = (ImageView) findViewById(R.id.iv_audioplayer_pre);
        //播放下一首的按钮
        iv_player_next = (ImageView) findViewById(R.id.iv_audioPlayer_next);
        //切换播放模式
        iv_player_mode = (ImageView) findViewById(R.id.iv_audioplayer_playermode);
        //歌词
        lyric = (LyricView) findViewById(R.id.lyric);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        unregisterReceiver(myBroadcastReceiver);
        //为了防止内存泄漏，在activity销毁时，应该取消所有的消息的发送
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public int getLayoutViewId() {
        //取消标题显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        return R.layout.activity_aduio_player;
    }

    @Override
    protected void processListener(int id) {
        switch (id) {
            case R.id.iv_audio_pause:
                //切换播放的功能
                SwitchPlayerPause();
                break;
            case R.id.iv_audio_back:
                //点击返回键，关闭当前的界面
                AudioPlayerActivity.this.finish();
                break;
            case R.id.iv_audioplayer_pre:
                //点击上一首的按钮
                PlayerPre();
                break;
            case R.id.iv_audioPlayer_next:
                //点击下一首的按钮
                PlayerNext();
                break;
            case R.id.iv_audioplayer_playermode:
                //点击切换播放模式
                SwitchPlayMode();
                break;
        }
    }
    /**更改播放模式*/
    private void SwitchPlayMode() {
        myIBinder.SwitchPlayerMode();
        //根据当前的播放模式，更新模式按钮
        UpdataModeBtn();
    }

    /**根据当前的播放模式，更新模式按钮*/
    private void UpdataModeBtn() {
        switch (myIBinder.GetPlayerMode()){
            case AudioPlayerService.PLAYER_MODE_ALL:
                iv_player_mode.setImageResource(R.drawable.audio_playermode_repeat_selector);
            break;
            case AudioPlayerService.PLAYER_MODE_RANDOM:
                iv_player_mode.setImageResource(R.drawable.audio_playermode_random_selector);
            break;
            case AudioPlayerService.PLAYER_MODE_SINGLE:
                iv_player_mode.setImageResource(R.drawable.audio_playermode_singlerepeat_selector);
            break;
        }
    }

    /**播放下一首歌曲*/
    private void PlayerNext() {
        myIBinder.PlayNext();
    }
    /**播放上一首歌曲*/
    private void PlayerPre() {
        myIBinder.PlayPre();
    }

    /**
     * 切换播放的功能
     */
    private void SwitchPlayerPause() {
        if (myIBinder.isPlaying()) {
            //正在播放时，暂停播放
            myIBinder.Pause();
        } else {
            //暂停时，开始播放
            myIBinder.Start();
        }

        //更新图片按钮
        SwitchPlayerBtn();
    }

    /**
     * 切换播放，暂停的按钮显示
     */
    private void SwitchPlayerBtn() {
        if (myIBinder.isPlaying()) {
            //正在播放时，切换成显示暂停按钮。
            iv_audio_pause.setImageResource(R.drawable.audio_pause_selector);
        } else {
            //正在暂停时，切换成显示播放按钮
            iv_audio_pause.setImageResource(R.drawable.audio_player_selector);
        }
    }

    /**
     * 自定义ServiceConnection
     */
    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myIBinder = (AudioPlayerService.MyIBinder) service;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    /**
     * 自定义广播接收者
     */
    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //更新播放、暂停按钮
            SwitchPlayerBtn();

            //获取service传过来的当前音频对象
            AduioBean aduiobean = (AduioBean) intent.getSerializableExtra("aduiobean");
            LogUtils.LogD("---" + aduiobean.title + "=====" + aduiobean.artist);
            //更新歌名，歌手
            tv_audio_song.setText(StringUtils.FormatSongs(aduiobean.title));
            tv_audio_arties.setText(aduiobean.artist);

            //更新播放进度，时长
            UpdataPlayerDuration();

            //根据当前的播放模式，更新模式按钮
            UpdataModeBtn();

            //开始更新换行歌词
//            File file = new File(Environment.getExternalStorageDirectory(),"DCIM/audio/"+aduiobean.title+".lrc");
            String title = StringUtils.FormatTitle(aduiobean.title);
            LogUtils.LogD("---------------------------歌名--------------------------"+title);
            File file = LyricLoader.loadLyricFile(title);
            lyric.SetLyricFile(file);
            SwitchLineUpdata();

        }
    }

    /**更新高亮行换行*/
    private void SwitchLineUpdata() {
        lyric.Roll(myIBinder.getPosition(), myIBinder.getDuration());
        handler.sendEmptyMessage(UPDATA_LYRIC);
    }

    /**
     * 更新播放时长，当前的进度
     */
    private void UpdataPlayerDuration() {
        //获取当前播放的进度
        int position = myIBinder.getPosition();

        //更新当前的播放位置，时长
        UpdataCurrentPlayerInfo(position);

        //通过handler实现不断的循环，更新时间进度
        handler.sendEmptyMessageDelayed(UPDATA_CURRENT_POSITION, 500);

    }

    /**
     * 更新当前的播放位置，时长
     * @param position
     */
    private void UpdataCurrentPlayerInfo(int position) {
        //获取当前的播放总时长
        int duration = myIBinder.getDuration();
        //更新seekbar
        player_seekbar.setMax(duration);
        player_seekbar.setProgress(position);

        String totalDuration = StringUtils.FormatDuration(duration);
        String curretnPosition = StringUtils.FormatDuration(position);
        tv_player_time.setText(curretnPosition + "/" + totalDuration);
    }

    /**
     * 自定义进度条变化的监听
     */
    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 进度条变化的监听
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //如果不是用户操作的，直接退出。
            if (!fromUser) {
                return;
            }
            myIBinder.SeekTo(progress);
            //更新一下时间进度。
            UpdataCurrentPlayerInfo(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
