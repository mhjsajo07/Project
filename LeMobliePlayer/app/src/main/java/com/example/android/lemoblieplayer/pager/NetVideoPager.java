package com.example.android.lemoblieplayer.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.lemoblieplayer.R;
import com.example.android.lemoblieplayer.activity.SystemVideoPlayer;
import com.example.android.lemoblieplayer.adapter.NetVideoPagerAdapter;
import com.example.android.lemoblieplayer.base.BasePager;
import com.example.android.lemoblieplayer.bean.MediaItem;
import com.example.android.lemoblieplayer.utils.CacheUtils;
import com.example.android.lemoblieplayer.utils.Constants;
import com.example.android.lemoblieplayer.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.example.android.lemoblieplayer.R.id.tv_none_net;

/**
 * Created by 51477 on 2017/8/26.
 */

public class NetVideoPager extends BasePager {


    private TextView textView;
    @ViewInject(R.id.rv_video_list)
    private XListView rvVideoList;
    @ViewInject(tv_none_net)
    private TextView tvNoneNet;
    @ViewInject(R.id.pb_loading)
    private ProgressBar pbVideoLoading;

    private List<MediaItem> mediaItemList = new ArrayList<>();
    private NetVideoPagerAdapter netVideoPagerAdapter;

    private Handler mHandler = new Handler();
    private boolean isLoadMore;


    public NetVideoPager(Context mContext) {
        super(mContext);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.netvideo_pager, null);
        x.view().inject(NetVideoPager.this, view);
        rvVideoList.setOnItemClickListener(new MyOnItemClickListener());
        rvVideoList.setPullLoadEnable(true);
        rvVideoList.setXListViewListener(new MyIXListViewListener());
        return view;
    }

    private class MyIXListViewListener implements XListView.IXListViewListener {
        @Override
        public void onRefresh() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getDataFromNet();
                }
            }, 2000);
        }

        @Override
        public void onLoadMore() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getMoreDataFromNet();
                }
            }, 2000);
        }

    }

    private void getMoreDataFromNet() {
        //联网
        //视频内容
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功==" + result);

                isLoadMore = true;
                //主线程
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败==" + ex.getMessage());
                isLoadMore = false;
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
                isLoadMore = false;
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
                isLoadMore = false;
            }
        });
    }

    public void onLoad() {
        rvVideoList.stopRefresh();
        rvVideoList.stopLoadMore();
        rvVideoList.setRefreshTime(getSysteTime());
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //3.传递列表数据-对象-序列化
            Intent intent = new Intent(mContext, SystemVideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", (Serializable) mediaItemList);
            intent.putExtras(bundle);
            intent.putExtra("position", position - 1);
            mContext.startActivity(intent);

        }
    }

    @Override
    public void initData() {
        super.initData();
        Log.e(TAG, "initData: " + "网络视频数据初始化完成。");
        String json = CacheUtils.getString(mContext, Constants.NET_URL);
        if (!json.isEmpty()) {
            processData(json);
        }
        getDataFromNet();
    }

    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result);
                CacheUtils.putString(mContext, Constants.NET_URL, result);

                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: " + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                Log.e(TAG, "onFinished: ");
            }
        });
    }

    private void processData(String result) {
        if (!isLoadMore) {
            mediaItemList = parseJson(result);
            if (mediaItemList != null && mediaItemList.size() > 0) {
                setMediaDate();
                onLoad();
            } else {
                tvNoneNet.setVisibility(View.VISIBLE);
            }
        } else {
            mediaItemList.addAll(parseJson(result));
            netVideoPagerAdapter.notifyDataSetChanged();

            onLoad();
        }
    }

    private void setMediaDate() {
        netVideoPagerAdapter = new NetVideoPagerAdapter(mContext, mediaItemList);
        rvVideoList.setAdapter(netVideoPagerAdapter);
    }

    private List<MediaItem> parseJson(String result) {
        List<MediaItem> mediaItems = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray trailers = jsonObject.optJSONArray("trailers");
            if (trailers != null && trailers.length() > 0) {
                for (int i = 0; i < trailers.length(); i++) {
                    JSONObject o = (JSONObject) trailers.get(i);
                    if (o != null) {
                        MediaItem mediaItem = new MediaItem();

                        String moiveName = o.getString("movieName");
                        mediaItem.setName(moiveName);
                        String videoTitle = o.getString("videoTitle");
                        mediaItem.setMovieDesc(videoTitle);
                        String videoLength = o.getString("videoLength");
                        mediaItem.setDuration(Long.parseLong(videoLength));
                        String hightUrl = o.getString("hightUrl");
                        mediaItem.setData(hightUrl);
                        String coverImg = o.getString("coverImg");
                        mediaItem.setImageUrl(coverImg);

                        mediaItems.add(mediaItem);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mediaItems;
    }

    public String getSysteTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date());
    }
}
