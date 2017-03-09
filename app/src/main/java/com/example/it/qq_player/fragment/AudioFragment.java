package com.example.it.qq_player.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.example.it.qq_player.R;
import com.example.it.qq_player.activity.AudioPlayerActivity;
import com.example.it.qq_player.adapter.AudioCursorAdapter;
import com.example.it.qq_player.bean.AduioBean;
import com.example.it.qq_player.utils.CursorUtils;

import java.util.ArrayList;

/**
 * Created by lenovo on 2016/9/23.
 */
public class AudioFragment extends BaseFragment {

    private ListView lv_listView;
    private AudioCursorAdapter adapter;

    @Override
    public void initData() {
        ContentResolver resolver = getActivity().getContentResolver();

//        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID, Media.DATA,Media.DISPLAY_NAME,Media.ARTIST}, null, null, null);
//        //打印Cursor
//        CursorUtils.PrintCursor(cursor);
//       //更新CursorAdapter参数中的cursor,并刷新adapter。
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

        asyncQueryHandler.startQuery(0, adapter,Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID, Media.DATA, Media.DISPLAY_NAME, Media.ARTIST}, null, null, null);
    }

    @Override
    public void initListener() {
        adapter = new AudioCursorAdapter(getContext(), null);
        lv_listView.setAdapter(adapter);
        //给listView设置条目点击事件
        lv_listView.setOnItemClickListener(new MyOnItemClickListener());
    }

    @Override
    public void initView() {
        //初始化Listview
        lv_listView = (ListView) findViewById(R.id.lv_listView);
    }

    @Override
    public int getLayoutViewId() {
        return R.layout.fragment_audio;
    }

    /**自定义ListView条目点击事件*/
    class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            ArrayList<AduioBean> aduioBeans = AduioBean.TransformCursorToList(cursor);
            Intent intent = new Intent(getContext(),AudioPlayerActivity.class);
            intent.putExtra("aduioBeans",aduioBeans);
            intent.putExtra("position",position);
            startActivity(intent);
        }
    }
}
