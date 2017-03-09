package com.example.it.qq_player.bean;

import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;

import com.example.it.qq_player.utils.LogUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * bean对象要做为参数传输，要实现Serrializable.
 */
public class AduioBean implements Serializable {
    public String title;
    public String data;
    public String artist;

    public static AduioBean InstantFromCursor(Cursor cursor) {
        AduioBean bean = new AduioBean();
        if (cursor == null || cursor.getCount() == 0) {
            return bean;
        }

        bean.title = cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME));
        bean.artist = cursor.getString(cursor.getColumnIndex(Media.ARTIST));
        bean.data = cursor.getString(cursor.getColumnIndex(Media.DATA));
        return bean;
    }

    /**
     * 将Cursor转换成ArrayList集合
     */
    public static ArrayList<AduioBean> TransformCursorToList(Cursor cursor) {
        ArrayList<AduioBean> list = new ArrayList<>();
        //如果cursor为空，直接返回null的集合。
        if(cursor==null || cursor.getCount()==0){
            return list;
        }
        //把游标移动到列表的头部
        cursor.moveToPosition(-1);
        //将cursor转换成aduioBean对象。
        while (cursor.moveToNext()){
            AduioBean aduioBean = InstantFromCursor(cursor);
            LogUtils.LogD("名称："+aduioBean.title +"大小："+aduioBean.artist+"路径："+aduioBean.data);
            list.add(aduioBean);
        }
        return list;
    }

}
