package com.example.it.qq_player.bean;

import android.database.Cursor;
import android.provider.MediaStore.Video.Media;

import com.example.it.qq_player.utils.LogUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * bean对象要做为参数传输，要实现Serrializable.
 */
public class VideoBean implements Serializable {
    public String title;
    public String data;
    public int size;
    public int duration;

    /**
     * 将cursor转换成bean对象
     */
    public static VideoBean InstantFromCursor(Cursor cursor) {
        VideoBean bean = new VideoBean();
        if (cursor == null || cursor.getCount() == 0) {
            return bean;
        }

        bean.title = cursor.getString(cursor.getColumnIndex(Media.TITLE));
        bean.duration = cursor.getInt(cursor.getColumnIndex(Media.DURATION));
        bean.size = cursor.getInt(cursor.getColumnIndex(Media.SIZE));
        bean.data = cursor.getString(cursor.getColumnIndex(Media.DATA));
        return bean;
    }

    /**
     * 将Cursor转换成ArrayList集合
     */
    public static ArrayList<VideoBean> TransformCursorToList(Cursor cursor) {
        ArrayList<VideoBean> list = new ArrayList<>();
        //如果cursor为空，直接返回null的集合。
        if (cursor == null || cursor.getCount() == 0) {
            return list;
        }
        //把游标移动到列表的头部
        cursor.moveToPosition(-1);
        //将cursor转换成VideoBean对象。
        while (cursor.moveToNext()) {
            VideoBean videoBean = InstantFromCursor(cursor);
            LogUtils.LogD("名称：" + videoBean.title + "大小：" + videoBean.size);
            list.add(videoBean);
        }
        return list;
    }

}
