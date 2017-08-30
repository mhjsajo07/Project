package com.example.android.lechat.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.lechat.R;
import com.example.android.lechat.controller.activity.AddContactActivity;
import com.example.android.lechat.controller.activity.ChatActivity;
import com.example.android.lechat.controller.activity.GroupListeActivity;
import com.example.android.lechat.controller.activity.InviteActivity;
import com.example.android.lechat.model.Model;
import com.example.android.lechat.model.bean.UserInfo;
import com.example.android.lechat.utils.Constant;
import com.example.android.lechat.utils.SpUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 51477 on 2017/8/22.
 */

public class ContactsFragment extends EaseContactListFragment implements View.OnClickListener {
    private ImageView redPoint;
    private LinearLayout llContactInvite;
    private String mHxid;
    private LinearLayout llGroup;

    private LocalBroadcastManager mLBM;
    private BroadcastReceiver ContactInviteChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // 更新红点显示
            redPoint.setVisibility(View.VISIBLE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

        }
    };
    private BroadcastReceiver ContactChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getContactFromServer();
        }
    };
    private BroadcastReceiver groupInviteChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 更新红点显示
            redPoint.setVisibility(View.VISIBLE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
        }
    };

    @Override
    protected void initView() {
        super.initView();
        titleBar.setRightImageResource(R.drawable.em_add);
        View headerView = View.inflate(getActivity(), R.layout.fragment_header_contact, null);

        redPoint = (ImageView) headerView.findViewById(R.id.red_point);
        llContactInvite = (LinearLayout) headerView.findViewById(R.id.ll_contact_invite);
        llGroup = (LinearLayout) headerView.findViewById(R.id.ll_group);

        listView.addHeaderView(headerView);

        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {
                if (user == null) {
                    return;
                }

                Intent intent = new Intent(getActivity(), ChatActivity.class);

                intent.putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername());

                startActivity(intent);
            }
        });

        llGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GroupListeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void setUpView() {
        super.setUpView();
        titleBar.setRightLayoutClickListener(this);
        // 初始化红点显示
        boolean isNewInvite = SpUtils.getInstance().getBoolean(SpUtils.IS_NEW_INVITE, false);
        redPoint.setVisibility(isNewInvite ? View.VISIBLE : View.GONE);
        mLBM = LocalBroadcastManager.getInstance(getActivity());
        mLBM.registerReceiver(ContactInviteChangeReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(ContactChangedReceiver, new IntentFilter(Constant.CONTACT_CHANGED));
        mLBM.registerReceiver(groupInviteChangedReceiver,new IntentFilter(Constant.GROUP_INVITE_CHANGED));
        initListener();

        getContactFromServer();

        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;

        EaseUser easeUser = (EaseUser) listView.getItemAtPosition(position);

        mHxid = easeUser.getUsername();
        getActivity().getMenuInflater().inflate(R.menu.delete, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            deleteContact();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteContact() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().deleteContact(mHxid);
                    Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(mHxid);
                    Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(mHxid);

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "删除好友成功。", Toast.LENGTH_SHORT).show();
                                refreshContact();
                            }
                        });
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "删除好友失败。", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    private void getContactFromServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> hxids = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    if (hxids != null && hxids.size() >= 0) {
                        List<UserInfo> contacts = new ArrayList<UserInfo>();

                        for (String hxid : hxids) {
                            UserInfo userInfo = new UserInfo(hxid);
                            contacts.add(userInfo);
                        }

                        Model.getInstance().getDbManager().getContactTableDao().saveContacts(contacts, true);

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshContact();
                                }
                            });
                        }
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void refreshContact() {
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();
        if (contacts != null && contacts.size() >= 0) {

            Map<String, EaseUser> contactMap = new HashMap<>();
            for (UserInfo contact : contacts) {
                EaseUser easeUser = new EaseUser(contact.getHxid());
                contactMap.put(contact.getName(), easeUser);
            }
            setContactsMap(contactMap);

            refresh();
        }
    }

    private void initListener() {
        llContactInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InviteActivity.class);
                startActivity(intent);
                redPoint.setVisibility(View.INVISIBLE);
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, false);
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), AddContactActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(ContactInviteChangeReceiver);
        mLBM.unregisterReceiver(ContactChangedReceiver);
        mLBM.unregisterReceiver(groupInviteChangedReceiver);
    }
}
