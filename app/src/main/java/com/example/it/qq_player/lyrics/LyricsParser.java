package com.example.it.qq_player.lyrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2015/11/17.
 */
public class LyricsParser {

    /**
     * 从歌词文件解析出歌词数据列表
     */
    public static List<LyricBean> parserFromFile(File lyricsFile) {
        List<LyricBean> lyricsList = new ArrayList<>();
        // 数据可用性检查
        if (lyricsFile == null || !lyricsFile.exists()) {
            for (int i = 0; i < 100; i++) {
                lyricsList.add(new LyricBean(2000 * i, "我是歌词数据" + i));
            }
//            lyricsList.add(new LyricBean(0, "没有找到歌词文件。"));
            return lyricsList;
        }

        // 按行解析歌词
        try {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(lyricsFile),"GBK"));
            String line  = buffer.readLine();
            while (line!=null){
                List<LyricBean> lineList = parserLine(line);
                lyricsList.addAll(lineList);

                line = buffer.readLine();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 歌词排序
        Collections.sort(lyricsList);
        return lyricsList;
    }

    /** 解析一行歌词  [01:45.51][02:58.62]整理好心情再出发 */
    private static List<LyricBean> parserLine(String line) {
        List<LyricBean> lineList = new ArrayList<>();

        String[] arr = line.split("]");
        // [01:45.51 [02:58.62 整理好心情再出发
        String content = arr[arr.length-1];

        // [01:45.51 [02:58.62
        for (int i = 0; i < arr.length - 1; i++) {
            int startPoint = parserStartPoint(arr[i]);
            lineList.add(new LyricBean(startPoint, content));
        }

        return lineList;
    }

    /** 解析出一行歌词的起始时间 [01:45.51 */
    private static int parserStartPoint(String startPoint) {

        int time = 0;
        String[] arr = startPoint.split(":");
        // [01 45.51
        String minStr = arr[0].substring(1);

        // 45.51
        arr = arr[1].split("\\.");

        // 45 51
        String secStr = arr[0];
        String mSecStr = arr[1];

        time = Integer.parseInt(minStr) * 60 * 1000
                + Integer.parseInt(secStr) *  1000
                + Integer.parseInt(mSecStr) * 10 ;


        return time;
    }
}
