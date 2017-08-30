package com.example.android.lemoblieplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import io.vov.vitamio.widget.*;

/**
 * Created by 51477 on 2017/8/26.
 */

public class VitamioVideoView extends io.vov.vitamio.widget.VideoView {
    public VitamioVideoView(Context context) {
        this(context, null);
    }

    public VitamioVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VitamioVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    public void setVideoSize(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = widthMeasureSpec;
        layoutParams.height = heightMeasureSpec;
        setLayoutParams(layoutParams);
    }
}
