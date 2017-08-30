package com.example.android.lechat.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.lechat.R;
import com.example.android.lechat.controller.adapter.GroupDetailAdapter;
import com.example.android.lechat.model.Model;
import com.example.android.lechat.model.bean.UserInfo;
import com.example.android.lechat.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailActivity extends Activity {
    private EaseTitleBar titlebar;
    private GridView gvGroupDetail;
    private TextView tvGroupId;
    private Button btnGroup;
    private EMGroup mGroup;
    private LocalBroadcastManager mLBM;
    private GroupDetailAdapter.OnGroupDetailListener onGroupDetailListener = new GroupDetailAdapter.OnGroupDetailListener() {
        @Override
        public void addMembers() {
            Intent intent = new Intent(GroupDetailActivity.this, PickContactActivity.class);
            intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());
            startActivityForResult(intent, 2);
        }

        @Override
        public void deleteMembers(final UserInfo user) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().removeUserFromGroup(mGroup.getGroupId(), user.getHxid());

                        getMembersFromServer();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除成功。", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除失败。", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };
    private List<UserInfo> userInfoList;
    private GroupDetailAdapter groupDetailAdapter;
    private int memberCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        initView();
        getData();
        initData();
        tvGroupId.setText(mGroup.getGroupId());
        initListener();
    }

    private void initListener() {
        gvGroupDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(groupDetailAdapter.isDeleteModel()){
                            groupDetailAdapter.setDeleteModel(false);
                            groupDetailAdapter.notifyDataSetChanged();
                        }
                        break;

                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            final String[] memberses = data.getStringArrayExtra("members");

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().addUsersToGroup(mGroup.getGroupId(), memberses);
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "邀请失败。", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initData() {
        initButtonDisplay();
        initGridView();
        getMembersFromServer();
    }

    private void getMembersFromServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMGroup emGroup = EMClient.getInstance().groupManager().getGroupFromServer(mGroup.getGroupId());
                    memberCount = emGroup.getMemberCount();
                    List<String> members = emGroup.getMembers();
                    userInfoList = new ArrayList<UserInfo>();
                    if (members != null && members.size() >= 0) {
                        for (String hxid : members) {
                            UserInfo userInfo = new UserInfo(hxid);
                            userInfoList.add(userInfo);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            groupDetailAdapter.setUserData(userInfoList);
                            titlebar.setTitle("聊天信息(" + memberCount + ")");
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupDetailActivity.this, "获取群成员失败。", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initGridView() {
        boolean isCanmodify = mGroup.getOwner().equals(EMClient.getInstance().getCurrentUser()) || mGroup.isPublic();
        groupDetailAdapter = new GroupDetailAdapter(this, isCanmodify, onGroupDetailListener);
        gvGroupDetail.setAdapter(groupDetailAdapter);
    }

    private void initButtonDisplay() {
        if (mGroup.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
            btnGroup.setText("解散该群");
            btnGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager().destroyGroup(mGroup.getGroupId());

                                exitGroupBroatCast();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群成功。", Toast.LENGTH_SHORT).show();

                                        finish();
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群失败。", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        } else {
            btnGroup.setText("退出并删除群");
            btnGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager().leaveGroup(mGroup.getGroupId());

                                exitGroupBroatCast();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群成功。", Toast.LENGTH_SHORT).show();

                                        finish();
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群失败。", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }

    private void exitGroupBroatCast() {
        mLBM = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent(Constant.EXIT_GROUP);
        intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());

        mLBM.sendBroadcast(intent);
    }


    private void getData() {
        Intent intent = getIntent();
        String groupId = intent.getStringExtra(Constant.GROUP_ID);
        if (groupId == null) {
            return;
        } else {
            mGroup = EMClient.getInstance().groupManager().getGroup(groupId);
        }
    }

    private void initView() {
        titlebar = (EaseTitleBar) findViewById(R.id.titlebar);
        gvGroupDetail = (GridView) findViewById(R.id.gv_group_detail);
        tvGroupId = (TextView) findViewById(R.id.tv_group_id);
        btnGroup = (Button) findViewById(R.id.btn_group_);
    }
}
