package com.example.android.lechat.controller.activity;


import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.example.android.lechat.R;
import com.example.android.lechat.controller.fragment.ChatListFragment;
import com.example.android.lechat.controller.fragment.ContactsFragment;
import com.example.android.lechat.controller.fragment.SettingFragment;

public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {
    private RelativeLayout flMainContent;
    private RadioGroup rgMain;
    private ChatListFragment chatListFragment;
    private ContactsFragment contactsFragment;
    private SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        initData();
        rgMain.check(R.id.rb_main_chat);
    }

    private void initListener() {
        rgMain.setOnCheckedChangeListener(this);
    }

    private void initData() {
        chatListFragment = new ChatListFragment();
        contactsFragment = new ContactsFragment();
        settingFragment = new SettingFragment();
    }

    private void initView() {
        flMainContent = (RelativeLayout) findViewById(R.id.fl_main_content);
        rgMain = (RadioGroup) findViewById(R.id.rg_main);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        Fragment fragment = null;
        switch (i) {
            case R.id.rb_main_chat:
                fragment = chatListFragment;
                break;
            case R.id.rb_main_contact:
                fragment = contactsFragment;
                break;
            case R.id.rb_main_setting:
                fragment = settingFragment;
                break;
        }
        switchFragment(fragment);
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_main_content, fragment);
        transaction.commit();
    }
}

