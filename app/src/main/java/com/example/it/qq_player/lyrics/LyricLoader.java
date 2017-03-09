package com.example.it.qq_player.lyrics;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2015/11/17.
 */
public class LyricLoader {

    private static final File root = new File(Environment.getExternalStorageDirectory(),"DCIM/audio/");

    /** 尝试多种方式获取歌词文件 */
    public static File loadLyricFile(String title){
        // 本地查找lrc文件
        File lrcFile = new File(root,title+".lrc");
        if (lrcFile.exists()){
            return lrcFile;
        }

        // 本地查找txt文件
        File txtFile = new File(root,title+".txt");
        if (txtFile.exists()){
            return txtFile;
        }

        // 查找服务器
        // ......

        return null;
    }
}
