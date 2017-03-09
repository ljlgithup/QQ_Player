package com.example.it.qq_player.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.it.qq_player.R;
import com.example.it.qq_player.activity.AudioPlayerActivity;
import com.example.it.qq_player.bean.AduioBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class AudioPlayerService extends Service {

    private ArrayList<AduioBean> list;
    private int position;
    private MyIBinder myIBinder;
    public static final int PLAYER_MODE_ALL = 0;
    public static final int PLAYER_MODE_RANDOM = 1;
    public static final int PLAYER_MODE_SINGLE = 2;
    private int player_mode = PLAYER_MODE_ALL;
    private SharedPreferences sp;
    private Notification notification;
    private String NOTIFYCATION_TYPE = "notifycation_type";
    private static final int NOTIFYCATION_TYPE_PRE = 0;
    private static final int NOTIFYCATION_TYPE_NEXT = 1;
    private static final int NOTIFYCATION_TYPE_CONTENT = 2;
    private int CurretnPostion = -1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myIBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myIBinder = new MyIBinder();
        sp = getSharedPreferences("audio_mode", MODE_PRIVATE);
        player_mode = sp.getInt("player_mode", PLAYER_MODE_ALL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //获取启动Service的通知
        int notify_type = intent.getIntExtra(NOTIFYCATION_TYPE, -1);

        switch (notify_type) {
            case NOTIFYCATION_TYPE_PRE:
                //播放上一首
                myIBinder.PlayPre();
                break;
            case NOTIFYCATION_TYPE_NEXT:
                //播放下一首
                myIBinder.PlayNext();
                break;
            case NOTIFYCATION_TYPE_CONTENT:
                //通知AudioPlayerActivity更新界面UI
                UpdataAudioPlayerActivityUI();
                break;
            default:
                //不是从Notification通知的intent,是在AudioPlayerActivity中启动的intent。
                //获取在音频界面传递过来的集合
                list = (ArrayList<AduioBean>) intent.getSerializableExtra("aduioBeans");
                //获取点击的位置
                position = intent.getIntExtra("position", -1);

                if (CurretnPostion == position) {
                    //点击的是同一首歌，刷新一下界面继续播放
                    UpdataAudioPlayerActivityUI();
                    //如果当前的歌曲暂停了，点击后继续播放
                    if (!myIBinder.isPlaying()) {
                        myIBinder.Start();
                    }

                } else {
                    //点击的不是同一首歌
                    this.CurretnPostion = position;
                    myIBinder.Play();
                }

                break;
        }
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 自定义Binder
     */
    public class MyIBinder extends Binder {

        private MediaPlayer mediaPlayer;

        /**
         * 自定义准备完成的监听
         */
        class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

            @Override
            public void onPrepared(MediaPlayer mp) {
                //准备完成，开始播放
                mediaPlayer.start();

                //在MediaPlayer准备完成后，显示通知
                ShowNotification();

                //通知ACTVITY界面,更新播放、暂停按钮
                UpdataAudioPlayerActivityUI();
            }
        }

        /**
         * 自定义MediaPlayer播放完成的监听
         */
        class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

            @Override
            public void onCompletion(MediaPlayer mp) {
                //根据当前的播放模式，更新播放位置，播放下一首歌曲
                switch (GetPlayerMode()) {
                    case PLAYER_MODE_ALL:
                        //循环列表播放模式 到最后一首，播放第一首
                        if (position == list.size() - 1) {
                            position = 0;
                        } else {
                            position++;
                        }
                        break;
                    case PLAYER_MODE_RANDOM:
                        //随机播放模式
                        position = new Random().nextInt(list.size());
                        break;
                    case PLAYER_MODE_SINGLE:
                        //单曲循环播放模式
                        break;
                }

                Play();
            }
        }

        /**
         * 播放
         */
        public void Play() {
            AduioBean bean = list.get(position);

            if (mediaPlayer != null) {
                mediaPlayer.reset();
            }

            //使用MediaPlayer开始播放音频
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(bean.data);
                //MediaPlayer准备完成的监听
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                //MediaPlayer播放完成的监听
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 开始播放
         */
        public void Start() {
            mediaPlayer.start();
            //重新播放时，在显示通知
            ShowNotification();
        }

        /**
         * 暂停
         */
        public void Pause() {
            mediaPlayer.pause();
            //暂停时取消通知
            CancelNotify();
        }

        /**
         * 是否正在播放
         */
        public boolean isPlaying() {
            return mediaPlayer.isPlaying();
        }

        /**
         * 获取总时长
         */
        public int getDuration() {
            return mediaPlayer.getDuration();
        }

        /**
         * 获取当前播放的时间
         */
        public int getPosition() {
            return mediaPlayer.getCurrentPosition();
        }

        /**
         * 设置对当前进度seekTo
         */
        public void SeekTo(int pos) {
            mediaPlayer.seekTo(pos);
        }

        /**
         * 播放上一曲
         */
        public void PlayPre() {
            if (position != 0) {
                position--;
                Play();
            } else {
                //提示用户已经是第一首歌了
                Toast.makeText(getApplicationContext(), "已经是第一首歌了", Toast.LENGTH_LONG).show();
            }
        }

        /**
         * 播放下一曲
         */
        public void PlayNext() {
            if (position < list.size() - 1) {
                position++;
                Play();
            } else {
                //提示用户已经是最后一首歌了
                Toast.makeText(getApplicationContext(), "已经是最后一首歌了", Toast.LENGTH_LONG).show();
            }
        }

        /**
         * 切换播放模式 列表模式 、随机模式、单曲循环模式
         */
        public void SwitchPlayerMode() {
            switch (player_mode) {
                case PLAYER_MODE_ALL:
                    player_mode = PLAYER_MODE_RANDOM;
                    break;
                case PLAYER_MODE_RANDOM:
                    player_mode = PLAYER_MODE_SINGLE;
                    break;
                case PLAYER_MODE_SINGLE:
                    player_mode = PLAYER_MODE_ALL;
                    break;
            }
            //保存当前的播放模式
            sp.edit().putInt("player_mode", player_mode).commit();

        }

        /**
         * 返回当前的播放模式 {@link #PLAYER_MODE_ALL},{@link #PLAYER_MODE_RANDOM},{@link #PLAYER_MODE_SINGLE}
         */
        public int GetPlayerMode() {
            return player_mode;
        }
    }

    /**
     * 通知AudioPlayerActivity更新界面数据
     */
    private void UpdataAudioPlayerActivityUI() {
        AduioBean bean = list.get(position);
        Intent intent = new Intent("com.uipower.audioplayer.prepared");
        intent.putExtra("aduiobean", bean);
        sendBroadcast(intent);
    }

    /**
     * 显示通知
     */
    private void ShowNotification() {
        //当系统版本大于11 2.3 使用新的API下的Notification。
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            //获取新API下的Notification
//            Notification notification = GetNewAPINotification();
            //获取自定义通知
            notification = GetCustomNormalBuilderApI();
        } else {
            //获取低版本的Notification 当兼容低版本时使用低版本的下Notification。
            notification = GetCommonNotification();
        }

        //显示通知
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, notification);

    }

    /**
     * 获取普通的通知
     */
    private Notification GetCommonNotification() {
        Notification notification = new Notification(R.drawable.notification_music_playing, "正在播放：滴答", System.currentTimeMillis());
//        notification.setLatestEventInfo(this, "滴答", "调侃", getPendingIntent());
        //让通知一直存在。
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        return notification;
    }

    /**
     * 获取自定义Notification
     */
    private Notification GetCustomNormalBuilderApI() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.icon);
        builder.setTicker("正在播放:" + list.get(position).title);
//        builder.setWhen(System.currentTimeMillis());
        builder.setContent(getRemoteView());
        //让通知一直存在
        builder.setOngoing(true);
        return builder.getNotification();
    }

    /**
     * 取消通知
     */
    private void CancelNotify() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(0);
    }

    /**
     * 获取新API下Notification通知
     */
    private Notification GetNewAPINotification() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.notification_music_playing);
        builder.setTicker("正在播放：滴答");
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle("滴答");
        builder.setContentText("调侃");
        builder.setContentIntent(getPendingIntent());
        builder.setOngoing(true);
        return builder.getNotification();
    }

    /**
     * 点击通知空白处的响应
     */
    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, AudioPlayerService.class);
        intent.putExtra("msg", "我是从通知栏过来的通知");
        return PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public RemoteViews getRemoteView() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.audio_notfy);

        // 填充文字
        remoteViews.setTextViewText(R.id.audio_notify_tv_title, list.get(position).title);
        remoteViews.setTextViewText(R.id.audio_notify_tv_arties, list.get(position).artist);

        // 设置点击事件
        remoteViews.setOnClickPendingIntent(R.id.audio_notify_iv_pre, getPreIntent());
        remoteViews.setOnClickPendingIntent(R.id.audio_notify_iv_next, getNextIntent());
        remoteViews.setOnClickPendingIntent(R.id.audio_notify_layout, getContentIntent());

        return remoteViews;
    }

    /**
     * 点击通知空白部分的响应
     */
    private PendingIntent getContentIntent() {
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra(NOTIFYCATION_TYPE, NOTIFYCATION_TYPE_CONTENT);

        return PendingIntent.getActivity(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * 点击下一曲的响应
     */
    private PendingIntent getNextIntent() {
        Intent intent = new Intent(this, AudioPlayerService.class);
        intent.putExtra(NOTIFYCATION_TYPE, NOTIFYCATION_TYPE_NEXT);

        return PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * 点击上一曲的响应
     */
    private PendingIntent getPreIntent() {
        Intent intent = new Intent(this, AudioPlayerService.class);
        intent.putExtra(NOTIFYCATION_TYPE, NOTIFYCATION_TYPE_PRE);

        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
