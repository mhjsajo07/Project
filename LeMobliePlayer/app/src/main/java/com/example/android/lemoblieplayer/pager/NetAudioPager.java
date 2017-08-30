package com.example.android.lemoblieplayer.pager;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.lemoblieplayer.R;
import com.example.android.lemoblieplayer.adapter.NetAudioPagerAdapter;
import com.example.android.lemoblieplayer.base.BasePager;
import com.example.android.lemoblieplayer.bean.NetAudioPagerData;
import com.example.android.lemoblieplayer.utils.CacheUtils;
import com.example.android.lemoblieplayer.utils.Constants;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by 51477 on 2017/8/26.
 */

public class NetAudioPager extends BasePager {
    private ListView rvJokeList;
    private ProgressBar pbJokeLoading;
    private TextView tvNone;
    private List<NetAudioPagerData.ListEntity> datas;

    public NetAudioPager(Context mContext) {
        super(mContext);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.net_audio_pager, null);
        rvJokeList = (ListView) view.findViewById(R.id.rv_joke_list);
        pbJokeLoading = (ProgressBar) view.findViewById(R.id.pb_joke_loading);
        tvNone = (TextView) view.findViewById(R.id.tv_none);


        return view;
    }

    private void getData() {
        NetAudioPagerAdapter jokeAdapter = new NetAudioPagerAdapter(mContext, datas);
        rvJokeList.setAdapter(jokeAdapter);
    }

    @Override
    public void initData() {
        super.initData();
        pbJokeLoading.setVisibility(View.VISIBLE);
        String json = CacheUtils.getString(mContext, Constants.ALL_RES_URL);
        if (json != null && !json.isEmpty()) {
            processData(json);
        }
        getDataFromNet();
        Log.e(TAG, "initData: " + "网络音乐数据初始化完成。");
    }

    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.ALL_RES_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                processData(result);
                CacheUtils.putString(mContext, Constants.ALL_RES_URL, result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                pbJokeLoading.setVisibility(View.GONE);
                tvNone.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                pbJokeLoading.setVisibility(View.GONE);
            }
        });
    }

    private void processData(String result) {
        NetAudioPagerData jokeData = parsedJson(result);
        datas = jokeData.getList();
        if (datas != null && datas.size() > 0) {
            getData();
        } else {
            tvNone.setVisibility(View.VISIBLE);
        }
        pbJokeLoading.setVisibility(View.GONE);
    }

    private NetAudioPagerData parsedJson(String json) {
        return new Gson().fromJson(json, NetAudioPagerData.class);
    }
}
