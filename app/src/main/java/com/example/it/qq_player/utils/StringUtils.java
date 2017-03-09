package com.example.it.qq_player.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 将int类的时间转换成01：01：01 或 01：01
 */
public class StringUtils {

    private static final int HOUR = 60 * 60 * 1000;
    private static final int MIN = 60 * 1000;
    private static final int SEC = 1000;

    /**
     * 将int类的时间转换成01：01：01 或 01：01
     */
    public static String FormatDuration(int duration) {

        //转换成小时：
        int hour = duration / HOUR;
        //转换成分钟：
        int min = duration % HOUR / MIN;
        //转换成秒：
        int sec = duration % MIN / SEC;
        if (hour == 0) {
            return String.format("%02d:%02d", min, sec);
        } else {
            return String.format("%02d:%02d:%02d", hour, min, sec);
        }
    }
    /**将系统的时间转换成：01：01：02：*/
    public static String FormatSystemTime(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    /**格式化字符串*/
    public static String FormatTitle(String title){
        if(title.contains(".")){
            return title.substring(0, title.indexOf("."));
        }
        return title;
    }
    /**格式化歌名*/
    public static String FormatSongs(String str){
        if(str.contains("[")){
            return str.substring(0,str.indexOf("["));
        }else if(str.contains("_")){
            return str.substring(0,str.indexOf("_"));
        }else {
            return str;
        }
    }
}
