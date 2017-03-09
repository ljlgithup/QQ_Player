package com.example.it.qq_player.lyrics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.it.qq_player.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/10/14.
 */
public class LyricView extends TextView {

    private Paint mPaint;
    private float normal_size;
    private float light_size;
    private int light_color;
    private int normal_color;
    private int screentWidth_half;
    private int screentHight_half;
    private int currentPosition;
    private int lineHight;
    private int endPosition;
    private int mPlayerPostion;
    private int mDuration;
    private List<LyricBean> list;

    public LyricView(Context context) {
        super(context);
        TextView();
    }

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextView();
    }

    public LyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TextView();
    }

    /**
     * 初始化画笔
     */
    private void TextView() {

        //获取普通行歌词、高亮歌词的大小、行的高度。
        normal_size = getResources().getDimension(R.dimen.lyrics_normal_size);
        light_size = getResources().getDimension(R.dimen.lyrics_light_size);
        lineHight = getResources().getDimensionPixelSize(R.dimen.lyrics_Line_hight);
        //歌词的颜色
        light_color = Color.GREEN;
        normal_color = Color.WHITE;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);//设置抗锯齿
        mPaint.setTextSize(light_size);
        mPaint.setColor(light_color);

        //封装假数据
        /*list = new ArrayList<LyricBean>();
        for (int i = 0; i < 100; i++) {
            list.add(new LyricBean(2000 * i, "我是歌词数据" + i));
        }

        currentPosition = 0;*/
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (list == null || list.size() == 0) {
            //绘制单行的歌词的文本
            DrawSingleLyricText(canvas);

        } else {
            //绘制多行的歌词文本
            DrawMulitLyricText(canvas);

        }

        //绘制多行的歌词文本
        //DrawMulitLyricText(canvas);
    }

    /**绘制多行歌词文本*/
    private void DrawMulitLyricText(Canvas canvas) {

//      平滑滚动的算法。
//      偏移位置 = 经过的时间百分比  *  行高
//      经过的时间百分比 = 经过的时间 / 行可用的时间
//      经过的时间 = 已播放时间 - 行起始时间
//      行可用时间 = 下一行起始时间 - 当前行起始时间
        //获取当前行的歌词
        LyricBean lyric = list.get(currentPosition);

        int nextStartPosition ;
        //获取下一行的歌词
        if(currentPosition == list.size()-1){
            //最后一行
            nextStartPosition = mDuration;
        }else{
            LyricBean lyricNext = list.get(currentPosition + 1);
            nextStartPosition = lyricNext.getStartPosition();

        }

        int lineTime = nextStartPosition - lyric.getStartPosition();
        int pastTime = mPlayerPostion - lyric.getStartPosition();
        float pastParcent = pastTime / (float)lineTime;
        int offsetY =(int) (lineHight * pastParcent);

        canvas.translate(0, -offsetY);

        //绘制一行歌词的文本
        String text = list.get(currentPosition).getContent();

        //通过Rect获取文本的宽高
        Rect bounds = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), bounds);

        int text_halfhight = bounds.height() / 2;
        int centerY = screentHight_half + text_halfhight;//中心位置加上偏移的位置，为平滑的滚动歌词(+offsetY);

        for (int i = 0; i < list.size(); i++) {
            //修改文字的高亮显示
            if (i == currentPosition) {
                //高亮显示
                mPaint.setColor(light_color);
                mPaint.setTextSize(light_size);
            } else {
                //普通显示
                mPaint.setColor(normal_color);
                mPaint.setTextSize(normal_size);
            }
            // x = 水平居中使用的x
            // y = 居中行的Y位置 + (绘制行的行数 - 高亮行的行数) * 行高
            int drawH = centerY + (i - currentPosition) * lineHight;
            DrawHorizontalText(canvas, list.get(i).getContent(), drawH);
        }

    }

    /**
     * 绘制单行歌词的文本
     */
    private void DrawSingleLyricText(Canvas canvas) {
        //绘制一行歌词的文本
        String text = "";
        //通过Rect获取文本的宽高
        Rect bounds = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), bounds);

        int text_halfhight = bounds.height() / 2;
        int drawH = screentHight_half + text_halfhight;

        //绘制水平居中的文本
        DrawHorizontalText(canvas, text, drawH);
    }

    /**
     * 根据当前的播放时间选择高亮行
     */
    public void Roll(int position, int duration) {
        mPlayerPostion = position;
        mDuration = duration;
        //如果当前播放的时间大于 本行的开始时间，小于下行的开始时间，当前播放的时间的行就是高亮行
        for (int i = 0; i < list.size(); i++) {
            //获取当前行的歌词
            LyricBean lyric = list.get(i);

            if (i == list.size() - 1) {
                //最后一行
                endPosition = duration;

            } else {
                //获取下一行的歌词
                LyricBean lyricNext = list.get(i + 1);
                endPosition = lyricNext.getStartPosition();

            }

            if (lyric.getStartPosition() < position && endPosition > position) {
                currentPosition = i;
                break;
            }
        }
        invalidate();
    }

    /**解析歌词文件成歌词列表*/
    public void SetLyricFile(File file){
        list = LyricsParser.parserFromFile(file);
        currentPosition = 0;
    }

    /**
     * 绘制水平居中的歌词
     */
    private void DrawHorizontalText(Canvas canvas, String text, int drawH) {
        //通过Rect获取文本的宽高
        Rect bounds = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), bounds);

        int text_halfwidth = bounds.width() / 2;
        //使用mPaint.measureText(text)获取文本的宽度
        //int text_halfhight = (int)mPaint.measureText(text,0,text.length())/2;

        //计算高亮歌词要显示的位置
        // x = view一半宽度 - 文字的一半宽度
        // y = view一半高度 + 文字的一半高度
        int drawX = screentWidth_half - text_halfwidth;

        canvas.drawText(text, drawX, drawH, mPaint);
    }

    /**
     * 获取屏幕的宽高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screentWidth_half = w / 2;
        screentHight_half = h / 2;
    }
}
