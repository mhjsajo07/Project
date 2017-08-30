package com.example.android.lechat.controller.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.android.lechat.R;
import com.example.android.lechat.controller.adapter.InviteAdapter;
import com.example.android.lechat.model.Model;
import com.example.android.lechat.model.bean.InvationInfo;
import com.example.android.lechat.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

public class InviteActivity extends Activity implements InviteAdapter.InviteOnClickListener {
    private RecyclerView rvListInvite;
    private InviteAdapter inviteAdapter;
    private List<InvationInfo> invationInfoList;
    private LocalBroadcastManager mLBM;
    private BroadcastReceiver contactInviteChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    };
    private BroadcastReceiver GroupInviteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        initView();
        initData();

        mLBM = LocalBroadcastManager.getInstance(this);
        mLBM.registerReceiver(contactInviteChangeReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(GroupInviteReceiver, new IntentFilter(Constant.GROUP_INVITE_CHANGED));
    }

    private void initData() {
        invationInfoList = new ArrayList<>();
        invationInfoList = Model.getInstance().getDbManager().getInviteTableDao().getInvitations();
        inviteAdapter.setInviteData(invationInfoList);
    }

    private void initView() {
        rvListInvite = (RecyclerView) findViewById(R.id.rv_list_invite);
        inviteAdapter = new InviteAdapter(this, this);
        rvListInvite.setLayoutManager(new LinearLayoutManager(this));
        rvListInvite.setHasFixedSize(true);
        rvListInvite.setAdapter(inviteAdapter);
    }


    @Override
    public void AgreeInvite(final InvationInfo invationInfo) {
        if (invationInfo.getUser() != null) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().acceptInvitation(invationInfo.getUser().getHxid());

                        Model.getInstance().getDbManager().getInviteTableDao().updateInvitationStatus(InvationInfo.InvitationStatus.INVITE_ACCEPT, invationInfo.getUser().getHxid());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受好友申请成功。", Toast.LENGTH_SHORT).show();
                                initData();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受申请失败。", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        } else {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().acceptInvitation(invationInfo.getGroup().getGroupId(), invationInfo.getGroup().getInvatePerson());

                        Model.getInstance().getDbManager().getInviteTableDao().updateInvitationStatus(InvationInfo.InvitationStatus.GROUP_ACCEPT_INVITE, invationInfo.getGroup().getInvatePerson());


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "您接受了群邀请。", Toast.LENGTH_SHORT).show();
                                initData();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受群邀请失败。", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void RefuseInvite(final InvationInfo invationInfo) {
        if (invationInfo.getUser() != null) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().declineInvitation(invationInfo.getUser().getHxid());

                        // 数据库变化
                        Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(invationInfo.getUser().getHxid());

                        // 页面变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝成功了", Toast.LENGTH_SHORT).show();

                                // 刷新页面
                                initData();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝失败了", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        } else {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().declineInvitation(invationInfo.getGroup().getGroupId(), invationInfo.getGroup().getInvatePerson(), "对方拒绝群邀请");

                        Model.getInstance().getDbManager().getInviteTableDao().updateInvitationStatus(InvationInfo.InvitationStatus.GROUP_REJECT_INVITE, invationInfo.getGroup().getInvatePerson());


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "您拒绝了群邀请。", Toast.LENGTH_SHORT).show();
                                initData();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝群邀请失败。", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void AgreeApplication(final InvationInfo invationInfo) {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().acceptApplication(invationInfo.getGroup().getGroupId(), invationInfo.getGroup().getInvatePerson());

                    Model.getInstance().getDbManager().getInviteTableDao().updateInvitationStatus(InvationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION, invationInfo.getGroup().getInvatePerson());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InviteActivity.this, "您接受了群申请。", Toast.LENGTH_SHORT).show();
                            initData();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InviteActivity.this, "接受群申请失败。", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void RefuseApplication(final InvationInfo invationInfo) {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().declineApplication(invationInfo.getGroup().getGroupId(), invationInfo.getGroup().getInvatePerson(), "对方拒绝群申请");

                    Model.getInstance().getDbManager().getInviteTableDao().updateInvitationStatus(InvationInfo.InvitationStatus.GROUP_REJECT_APPLICATION, invationInfo.getGroup().getInvatePerson());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InviteActivity.this, "您拒绝了群申请。", Toast.LENGTH_SHORT).show();
                            initData();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InviteActivity.this, "拒绝群申请失败。", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(contactInviteChangeReceiver);
    }
}
