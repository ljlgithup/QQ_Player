package com.example.it.qq_player.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore.Video.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.example.it.qq_player.R;
import com.example.it.qq_player.activity.VideoPlayerActivity;
import com.example.it.qq_player.adapter.VideoCursorAdapter;
import com.example.it.qq_player.bean.VideoBean;

import java.util.ArrayList;


/**
 * Created by lenovo on 2016/9/23.
 */
public class VideoFragment extends BaseFragment {

    private ListView lv_listView;
    private String TAG = "VideoFragment";
    private VideoCursorAdapter adapter;

    @Override
    public void initData() {
        ContentResolver resolver = getActivity().getContentResolver();

//        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID,Media.DATA, Media.TITLE, Media.SIZE, Media.DURATION}, null, null, null);
//        //打印Cursor
//        CursorUtils.PrintCursor(cursor);
//        //更新CursorAdapter参数中的cursor,并刷新adapter。
//        adapter.swapCursor(cursor);

        //查询数据库，获取数据的耗时的操作，应该放在子线程中去操作。而当前的线程为主线程，可以使用异步查询数据库。
        AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(resolver) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                CursorAdapter adapter = (CursorAdapter) cookie;
                adapter.swapCursor(cursor);
            }
        };
        /**String[] projection 查询那几列  String selection 查询条件 " thread_id=?" String[] selectionArgs new String[]{thread_id} orderBy 是否排序 */
        asyncQueryHandler.startQuery(0, adapter, Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID, Media.DATA, Media.TITLE, Media.SIZE, Media.DURATION}, null, null, null);

    }

    @Override
    public void initListener() {
        adapter = new VideoCursorAdapter(getContext(), null);
        lv_listView.setAdapter(adapter);
        //给listview设置条目点击事件
        lv_listView.setOnItemClickListener(new MyOnItemClickListener());
    }

    @Override
    public void initView() {
        lv_listView = (ListView) findViewById(R.id.lv_listView);
    }

    @Override
    public int getLayoutViewId() {
        return R.layout.fragment_video;
    }

    /**
     * 抽取listview条目点击事件
     */
    class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //获取当前的Cursor，跳转到视频播放的Actvity。
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
//            VideoBean bean = VideoBean.InstantFromCursor(cursor);
            //将cursor转换成List集合。
            ArrayList<VideoBean> list = VideoBean.TransformCursorToList(cursor);
            Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
            intent.putExtra("videobean",list);
            intent.putExtra("postion",position);
            startActivity(intent);
        }
    }

}
