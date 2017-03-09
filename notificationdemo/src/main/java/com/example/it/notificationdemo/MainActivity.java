package com.example.it.notificationdemo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MainActivity extends Activity {

    private RemoteViews remoteView;
    private Notification notification;
    private Notification notification1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String msg = getIntent().getStringExtra("msg");
        if(msg!=null){
            Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
        }
    }


    public void StartNotification(View view){
        ShowNotification();
    }

    public void CancelNotification(View view){
        CancalNotify();
    }


    /**显示通知*/
    private void ShowNotification() {
        //当系统版本大于11 2.3 使用新的API下的Notification。
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
            //获取新API下的Notification
            notification = GetNewAPINotification();
        }else {
            //获取低版本的Notification 当兼容低版本时使用低版本的下Notification。
            notification = GetCommonNotification();
        }

        //获取自定义通知
        Notification notification1 = GetCustomNormalBuilderApI();

        //显示通知
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,notification1);

    }

    /**获取普通的通知*/
    private Notification GetCommonNotification() {
        Notification notification = new Notification(R.drawable.notification_music_playing,"正在播放：滴答", System.currentTimeMillis());
        notification.setLatestEventInfo(this, "滴答", "调侃", getPendingIntent());
        //让通知一直存在。
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        return notification;
    }

    /**获取自定义Notification*/
    private Notification GetCustomNormalBuilderApI() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.icon);
        builder.setTicker("正在播放： beijingbeijing");
//        builder.setWhen(System.currentTimeMillis());
        builder.setContent(getRemoteView());
        builder.setOngoing(true);
        return builder.getNotification();
    }

    /**取消通知*/
    private void CancalNotify() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(0);
    }
    /**获取新API下Notification通知*/
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
    /**点击通知空白处的响应*/
    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("msg","我是从通知栏过来的通知");
        return PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public RemoteViews getRemoteView() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.audio_notfy);

        // 填充文字
        remoteViews.setTextViewText(R.id.audio_notify_tv_title, "北北北京");
        remoteViews.setTextViewText(R.id.audio_notify_tv_arties, "王峰峰");

        // 设置点击事件
        remoteViews.setOnClickPendingIntent(R.id.audio_notify_iv_pre, getPreIntent());
        remoteViews.setOnClickPendingIntent(R.id.audio_notify_iv_next, getNextIntent());
        remoteViews.setOnClickPendingIntent(R.id.audio_notify_layout, getContentIntent());

        return remoteViews;
    }

    /** 点击通知空白部分的响应 */
    private PendingIntent getContentIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("msg", "我是从通知栏启动的啊!");

        return PendingIntent.getActivity(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /** 点击下一曲的响应 */
    private PendingIntent getNextIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("msg", "我是下一曲!");

        return PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /** 点击上一曲的响应 */
    private PendingIntent getPreIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("msg", "我是上一曲!");

        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
