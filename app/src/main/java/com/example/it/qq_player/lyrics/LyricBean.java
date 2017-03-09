package com.example.it.qq_player.lyrics;

/**
 * Created by lenovo on 2016/10/14.
 */
public class LyricBean implements Comparable<LyricBean> {
    private int StartPosition;
    private String Content;

    public LyricBean(int startPosition,String content){
        this.StartPosition = startPosition;
        this.Content = content;
    }

    private void setStartPosition(int startPosition){
        this.StartPosition = startPosition;
    }

    public int getStartPosition(){
        return StartPosition;
    }

    private void setContent(String content){
        this.Content = content;
    }

    public String getContent(){
        return Content;
    }

    @Override
    public int compareTo(LyricBean another) {
        return this.getStartPosition()-another.getStartPosition();
    }
}
