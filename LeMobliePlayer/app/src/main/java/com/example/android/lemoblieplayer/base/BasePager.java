package com.example.android.lemoblieplayer.base;

import android.content.Context;
import android.view.View;

/**
 * Created by 51477 on 2017/8/26.
 */

public abstract class BasePager {
    public final Context mContext;
    public View rootView;

    public boolean isInitData = false;

    public BasePager(Context mContext) {
        this.mContext = mContext;
        rootView = initView();
    }

    public abstract View initView();

    public void initData(){

    }
}
