package com.example.it.qq_player.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.it.qq_player.R;
import com.example.it.qq_player.bean.VideoBean;
import com.example.it.qq_player.utils.StringUtils;

/**
 * Created by lenovo on 2016/9/26.
 */
public class VideoCursorAdapter extends CursorAdapter {

    public VideoCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public VideoCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public VideoCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //创建新的View。
        View view = View.inflate(context, R.layout.fagment_video_item, null);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //填充数据。
        ViewHolder holder = (ViewHolder) view.getTag();
        //写个bean类，将cursor转换成Bean对象。
        VideoBean bean = VideoBean.InstantFromCursor(cursor);
        holder.tv_video_title.setText(bean.title);
        holder.tv_video_duration.setText(StringUtils.FormatDuration(bean.duration));
        holder.tv_video_size.setText(Formatter.formatFileSize(context, bean.size));

    }
    class ViewHolder{
        TextView tv_video_title,tv_video_duration,tv_video_size;
        public ViewHolder(View view){
            tv_video_title = (TextView) view.findViewById(R.id.tv_video_title);
            tv_video_size = (TextView) view.findViewById(R.id.tv_video_size);
            tv_video_duration = (TextView) view.findViewById(R.id.tv_video_duration);
        }
    }
}
