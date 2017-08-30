package com.example.android.lechat.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.android.lechat.R;
import com.example.android.lechat.controller.adapter.PickContactAdapter;
import com.example.android.lechat.model.Model;
import com.example.android.lechat.model.bean.PickContactInfo;
import com.example.android.lechat.model.bean.UserInfo;
import com.example.android.lechat.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

public class PickContactActivity extends Activity implements PickContactAdapter.OnItemClickListener {
    private static final String TAG = PickContactActivity.class.getSimpleName();
    private Button btnPickTrue;
    private RecyclerView rvPickContact;
    private PickContactAdapter pickContactAdapter;
    private List<PickContactInfo> pickContactInfoList;
    private List<String> mExistmembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);
        initView();

        getData();

        initData();

        initListener();
    }

    private void getData() {
        String groupId = getIntent().getStringExtra(Constant.GROUP_ID);
        if (groupId != null) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            mExistmembers = group.getMembers();
        }
        if (mExistmembers == null) {
            mExistmembers = new ArrayList<>();
        }
    }

    private void initListener() {
        btnPickTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> names = pickContactAdapter.getPickContacts();

                Intent intent = new Intent(PickContactActivity.this, AddGroupActivity.class);

                intent.putExtra("members", names.toArray(new String[0]));

                for (String s : names) {
                    Log.e(TAG, "onClick: " + s);
                }

                setResult(RESULT_OK, intent);

                finish();
            }
        });
    }

    private void initData() {
        pickContactAdapter = new PickContactAdapter(this, this,mExistmembers);
        rvPickContact.setLayoutManager(new LinearLayoutManager(this));
        rvPickContact.setHasFixedSize(true);
        rvPickContact.setAdapter(pickContactAdapter);
        getContacts();
    }

    private void getContacts() {
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();

        pickContactInfoList = new ArrayList<>();

        for (UserInfo userInfo : contacts) {
            PickContactInfo pickContactInfo = new PickContactInfo(userInfo, false);
            pickContactInfoList.add(pickContactInfo);
        }
        pickContactAdapter.setPickContactData(pickContactInfoList);
    }

    private void initView() {
        btnPickTrue = (Button) findViewById(R.id.btn_pick_true);
        rvPickContact = (RecyclerView) findViewById(R.id.rv_pick_contact);
    }

    @Override
    public void onClick(PickContactInfo pickContactInfo) {
        pickContactInfo.setIsChecked(!pickContactInfo.isChecked());
        pickContactAdapter.notifyDataSetChanged();
    }
}
