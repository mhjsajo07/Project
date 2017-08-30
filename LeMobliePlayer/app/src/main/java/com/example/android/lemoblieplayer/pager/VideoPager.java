package com.example.android.lemoblieplayer.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.lemoblieplayer.R;
import com.example.android.lemoblieplayer.activity.SystemVideoPlayer;
import com.example.android.lemoblieplayer.adapter.VideoPagerAdapter;
import com.example.android.lemoblieplayer.base.BasePager;
import com.example.android.lemoblieplayer.bean.MediaItem;
import com.example.android.lemoblieplayer.utils.MyDecoration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by 51477 on 2017/8/28.
 */

public class VideoPager extends BasePager implements VideoPagerAdapter.OnVideoItemClickListener {
    private RecyclerView rvVideoList;
    private TextView tvNoneVideo;
    private ProgressBar pbVideoLoading;

    private List<MediaItem> mMediaItemList;
    private VideoPagerAdapter mVideoPagerAdapter;

    public VideoPager(Context mContext) {
        super(mContext);
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mMediaItemList != null && mMediaItemList.size() > 0) {
                setMediaDate();
            } else {
                tvNoneVideo.setVisibility(View.VISIBLE);
            }
            pbVideoLoading.setVisibility(GONE);
        }
    };

    private void setMediaDate() {
        mVideoPagerAdapter = new VideoPagerAdapter(mContext , true);
        rvVideoList.setAdapter(mVideoPagerAdapter);
        mVideoPagerAdapter.setVideoDate(mMediaItemList, this);
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

                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,//视频文件在sdcard的名称
                        MediaStore.Video.Media.DURATION,//视频总时长
                        MediaStore.Video.Media.SIZE,//视频的文件大小
                        MediaStore.Video.Media.DATA,//视频的绝对地址
                        MediaStore.Video.Media.ARTIST,//歌曲的演唱者

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
        Intent intent = new Intent(mContext,SystemVideoPlayer.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("videolist", (Serializable) mMediaItemList);
        intent.putExtras(bundle);
        intent.putExtra("position",position);
        mContext.startActivity(intent);
    }
}
