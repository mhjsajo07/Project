package com.example.android.lechat.controller.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.android.lechat.R;
import com.example.android.lechat.utils.Constant;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;

public class ChatActivity extends FragmentActivity {
    private static boolean isReadStorage = false;
    private String mHxid;
    private EaseChatFragment easeChatFragment;
    private LocalBroadcastManager mLBM;

    private int mChatType;
    private BroadcastReceiver groupDestoryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mHxid.equals(intent.getStringExtra(Constant.GROUP_ID))) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        isGrantExternalRW(this);

        initData();

        initListener();
    }

    public static boolean isGrantExternalRW(Activity activity) {
        isReadStorage = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
            }, 1);
            return false;
        }
        return true;
    }

    private void initListener() {
        easeChatFragment.setChatFragmentHelper(new EaseChatFragment.EaseChatFragmentHelper() {
            @Override
            public void onSetMessageAttributes(EMMessage message) {

            }

            @Override
            public void onEnterToChatDetails() {
                Intent intent = new Intent(ChatActivity.this, GroupDetailActivity.class);
                intent.putExtra(Constant.GROUP_ID, mHxid);

                startActivity(intent);
            }

            @Override
            public void onAvatarClick(String username) {

            }

            @Override
            public void onAvatarLongClick(String username) {

            }

            @Override
            public boolean onMessageBubbleClick(EMMessage message) {
                return false;
            }

            @Override
            public void onMessageBubbleLongClick(EMMessage message) {

            }

            @Override
            public boolean onExtendMenuItemClick(int itemId, View view) {
                return false;
            }

            @Override
            public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
                return null;
            }
        });
        if (mChatType == EaseConstant.CHATTYPE_GROUP) {
            mLBM.registerReceiver(groupDestoryReceiver, new IntentFilter(Constant.EXIT_GROUP));
        }
    }

    private void initData() {
        easeChatFragment = new EaseChatFragment();
        mHxid = getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID);
        easeChatFragment.setArguments(getIntent().getExtras());
        mChatType = getIntent().getExtras().getInt(EaseConstant.EXTRA_CHAT_TYPE);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_chat, easeChatFragment).commit();
        mLBM = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(groupDestoryReceiver);
    }
}
