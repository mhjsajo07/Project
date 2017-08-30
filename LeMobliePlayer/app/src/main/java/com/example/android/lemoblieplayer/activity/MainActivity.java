package com.example.android.lemoblieplayer.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.android.lemoblieplayer.R;
import com.example.android.lemoblieplayer.base.BasePager;
import com.example.android.lemoblieplayer.pager.AudioPager;
import com.example.android.lemoblieplayer.pager.NetAudioPager;
import com.example.android.lemoblieplayer.pager.NetVideoPager;
import com.example.android.lemoblieplayer.pager.VideoPager;
import com.example.android.lemoblieplayer.utils.ReplaceFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {
    private FrameLayout flMainContent;
    private RadioGroup rbMainBottomButton;
    private List<BasePager> mBasePagerList;

    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initListener();
        rbMainBottomButton.check(R.id.rb_main_video);
    }

    private void initListener() {
        rbMainBottomButton.setOnCheckedChangeListener(this);
    }

    private void initData() {
        mBasePagerList.add(new VideoPager(this));
        mBasePagerList.add(new AudioPager(this));
        mBasePagerList.add(new NetVideoPager(this));
        mBasePagerList.add(new NetAudioPager(this));
    }

    private void initView() {
        flMainContent = (FrameLayout) findViewById(R.id.fl_main_content);
        rbMainBottomButton = (RadioGroup) findViewById(R.id.rb_main_bottom_button);
        mBasePagerList = new ArrayList<>();
    }

    public static boolean isGrantExternalRW(Activity activity) {
        isReadStorage = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
            return false;
        }
        return true;
    }

    public static boolean isReadStorage = false;

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        Fragment fragment = null;
        switch (i) {
            case R.id.rb_main_video:
                if(!isReadStorage) {
                    isGrantExternalRW(MainActivity.this);
                }
                position = 0;
                break;
            case R.id.rb_main_radio:
                position = 1;
                break;
            case R.id.rb_main_net_video:
                position = 2;
                break;
            case R.id.rb_main_net_radio:
                position = 3;
                break;
        }
        switchFragment();
    }

    private void switchFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main_content, new ReplaceFragment(getBasePager())).commit();
    }

    public BasePager getBasePager() {
        BasePager basePage = mBasePagerList.get(position);
        if (basePage != null && !basePage.isInitData) {
            basePage.initData();
            basePage.isInitData = true;
        }
        return basePage;
    }

    /**
     * 是否已经退出
     */
    private boolean isExit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (position != 0) {//不是第一页面
                position = 0;
                rbMainBottomButton.check(R.id.rb_main_video);//首页
                return true;
            } else if (!isExit) {
                isExit = true;
                Toast.makeText(MainActivity.this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
