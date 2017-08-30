package com.example.android.lemoblieplayer.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.lemoblieplayer.base.BasePager;

/**
 * Created by 51477 on 2017/8/26.
 */

public class ReplaceFragment extends Fragment {
    private BasePager currPager;


    public ReplaceFragment(BasePager pager) {
        this.currPager = pager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return currPager.rootView;
    }
}
