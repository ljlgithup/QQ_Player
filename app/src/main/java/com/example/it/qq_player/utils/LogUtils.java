package com.example.it.qq_player.utils;

import android.util.Log;

/**
 * Created by lenovo on 2016/9/22.
 */
public class LogUtils {

    public static final boolean ENABLE = true;

    /**
     * 打印debug级别的Log，使用当前的类名为TAG。
     * @param msg
     */
    public static void LogD(String msg){
        if (ENABLE){
            Log.d("uipower",msg);
        }
    }/**
     * 打印error级别的Log，使用当前的类名为TAG。
     * @param msg
     */
    public static void LogE(String msg){
        if (ENABLE){
            Log.e("uipower",msg);
        }
    }
}
