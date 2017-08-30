package com.example.android.lemoblieplayer.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.lemoblieplayer.R;
import com.example.android.lemoblieplayer.activity.AudioPlayerActivity;
import com.example.android.lemoblieplayer.adapter.VideoPagerAdapter;
import com.example.android.lemoblieplayer.base.BasePager;
import com.example.android.lemoblieplayer.bean.MediaItem;
import com.example.android.lemoblieplayer.utils.MyDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 51477 on 2017/8/26.
 */

public class AudioPager extends BasePager implements VideoPagerAdapter.OnVideoItemClickListener {
    private RecyclerView rvVideoList;
    private TextView tvNoneVideo;
    private ProgressBar pbVideoLoading;

    private List<MediaItem> mMediaItemList;
    private VideoPagerAdapter mVideoPagerAdapter;

    public AudioPager(Context mContext) {
        super(mContext);
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mMediaItemList != null && mMediaItemList.size() > 0) {
                setMediaDate();
            } else {
                tvNoneVideo.setText("没有发现音乐...");
                tvNoneVideo.setVisibility(View.VISIBLE);
            }
            pbVideoLoading.setVisibility(View.GONE);
        }
    };

    private void setMediaDate() {
        mVideoPagerAdapter = new VideoPagerAdapter(mContext , false);
        rvVideoList.setAdapter(mVideoPagerAdapter);
        mVideoPagerAdapter.setVideoDate(mMediaItemList, this );
        rvVideoList.addItemDecoration(new MyDecoration(mContext, MyDecoration.VERTICAL_LIST));
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.video_pager, null);
        rvVideoList = (RecyclerView) view.findViewById(R.id.rv_video_list);
        tvNoneVideo = (TextView) view.findViewById(R.id.tv_none_video);
        pbVideoLoading = (ProgressBar) view.findViewById(R.id.pb_video_loading);
        rvVideoList.setLayoutManager(new LinearLayoutManager(mContext));
        rvVideoList.setHasFixedSize(true);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        getDataFromLocal();
    }

    private void getDataFromLocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mMediaItemList = new ArrayList<>();
                ContentResolver resolver = mContext.getContentResolver();

                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频文件在sdcard的名称
                        MediaStore.Audio.Media.DURATION,//视频总时长
                        MediaStore.Audio.Media.SIZE,//视频的文件大小
                        MediaStore.Audio.Media.DATA,//视频的绝对地址
                        MediaStore.Audio.Media.ARTIST,//歌曲的演唱者

                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaItem mediaItem = new MediaItem();

                        mMediaItemList.add(mediaItem);//写在上面

                        String name = cursor.getString(0);//视频的名称
                        mediaItem.setName(name);

                        long duration = cursor.getLong(1);//视频的时长
                        mediaItem.setDuration(duration);

                        long size = cursor.getLong(2);//视频的文件大小
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);//视频的播放地址
                        mediaItem.setData(data);

                        String artist = cursor.getString(4);//艺术家
                        mediaItem.setArtist(artist);
                    }
                    cursor.close();
                }
                //Handler发消息
                mHandler.sendEmptyMessage(10);
            }
        }.start();
    }

    @Override
    public void OnClick(MediaItem mediaItem, int position) {
        Intent intent = new Intent(mContext,AudioPlayerActivity.class);
        intent.putExtra("position",position);
        mContext.startActivity(intent);
    }
}
