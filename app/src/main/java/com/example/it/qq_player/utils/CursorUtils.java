package com.example.it.qq_player.utils;

import android.database.Cursor;
import android.util.Log;

/**
 * Created by lenovo on 2016/9/26.
 */
public class CursorUtils {
    /**打印Cursor的工具类*/
    public static void PrintCursor(Cursor cursor){
        LogUtils.LogD( "有多少个数据：" + cursor.getCount());
        while (cursor.moveToNext()){
            LogUtils.LogD("---------------------------");
            for (int i = 0; i <cursor.getColumnCount() ; i++) {
                LogUtils.LogD("name:" + cursor.getColumnName(i) + "---value:" + cursor.getString(i));
            }
        }
    }
}
