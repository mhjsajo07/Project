package com.example.android.lechat.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.lechat.R;
import com.example.android.lechat.controller.adapter.GroupListAdapter;
import com.example.android.lechat.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

public class GroupListeActivity extends Activity implements GroupListAdapter.OnClickItemListener {
    private TextView tvGroupCreate;
    private RecyclerView rvGroupList;
    private GroupListAdapter groupListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_liste);
        initView();

        initData();

        initListener();
    }

    private void initListener() {
        tvGroupCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupListeActivity.this, AddGroupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        groupListAdapter = new GroupListAdapter(this, this);
        rvGroupList.setLayoutManager(new LinearLayoutManager(this));
        rvGroupList.setHasFixedSize(true);
        rvGroupList.setAdapter(groupListAdapter);
        getGroupFromServer();
    }

    private void getGroupFromServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<EMGroup> emGroups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshGroupList();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListeActivity.this, "加载群失败。", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void refreshGroupList() {
        groupListAdapter.setGroupListData(EMClient.getInstance().groupManager().getAllGroups());
    }

    private void initView() {
        tvGroupCreate = (TextView) findViewById(R.id.tv_group_create);
        rvGroupList = (RecyclerView) findViewById(R.id.rv_group_list);
    }

    @Override
    public void onClick(EMGroup emGroup) {
        Intent intent = new Intent(GroupListeActivity.this, ChatActivity.class);

        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);

        intent.putExtra(EaseConstant.EXTRA_USER_ID,emGroup.getGroupId());

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshGroupList();
    }
}
