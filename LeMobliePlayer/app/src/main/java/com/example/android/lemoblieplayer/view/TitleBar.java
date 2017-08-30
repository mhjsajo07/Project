package com.example.android.lemoblieplayer.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SearchEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.lemoblieplayer.activity.SearchActivity;

/**
 * Created by 51477 on 2017/8/26.
 */

public class TitleBar extends LinearLayout implements View.OnClickListener {
    private View tvMainSearch;
    private View rlGame;
    private View ivMainRecord;

    private Context mContext;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvMainSearch = getChildAt(1);
        rlGame = getChildAt(2);
        ivMainRecord = getChildAt(3);

        tvMainSearch.setOnClickListener(this);
        rlGame.setOnClickListener(this);
        ivMainRecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == tvMainSearch){
            Intent intent = new Intent(mContext, SearchActivity.class);
            mContext.startActivity(intent);
        }else if(view == rlGame){
            Toast.makeText(mContext, "点击了游戏", Toast.LENGTH_SHORT).show();
        }else if(view == ivMainRecord){
            Toast.makeText(mContext, "点击了历史", Toast.LENGTH_SHORT).show();
        }
    }
}
