package com.example.android.lechat.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.lechat.R;
import com.example.android.lechat.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.exceptions.HyphenateException;

public class AddGroupActivity extends Activity {
    private static final String TAG = AddContactActivity.class.getSimpleName();
    private EditText etAddgroupName;
    private EditText etAddgroupDescription;
    private CheckBox cbAddgroupPublic;
    private CheckBox cbAddgroupInvite;
    private Button btnAddgroupAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        initView();

        initListener();
    }

    private void initListener() {
        btnAddgroupAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddGroupActivity.this, PickContactActivity.class);

                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            createGroup(data.getStringArrayExtra("members"));
        }
    }

    private void createGroup(final String[] memberses) {
        final String groupName = etAddgroupName.getText().toString();
        final String groupDesc = etAddgroupDescription.getText().toString();
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                EMGroupOptions options = new EMGroupOptions();

                options.maxUsers = 200;//群最多容纳多少人
                EMGroupManager.EMGroupStyle groupStyle = null;

                if (cbAddgroupPublic.isChecked()) {//公开
                    if (cbAddgroupInvite.isChecked()) {// 开放群邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                    } else {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                    }
                } else {
                    if (cbAddgroupInvite.isChecked()) {// 开放群邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                    } else {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                }

                options.style = groupStyle; // 创建群的类型

                try {
                    EMClient.getInstance().groupManager().createGroup(groupName, groupDesc, memberses, "申请加入群", options);
                    Log.e(TAG, "run: "+groupName );
                    Log.e(TAG, "run: "+groupDesc );
                    for (String s : memberses){
                        Log.e(TAG, "run: " +  s);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddGroupActivity.this, "创建群成功", Toast.LENGTH_SHORT).show();

                            // 结束当前页面
                            finish();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddGroupActivity.this, "创建群失败。", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initView() {
        etAddgroupName = (EditText) findViewById(R.id.et_addgroup_name);
        etAddgroupDescription = (EditText) findViewById(R.id.et_addgroup_description);
        cbAddgroupPublic = (CheckBox) findViewById(R.id.cb_addgroup_public);
        cbAddgroupInvite = (CheckBox) findViewById(R.id.cb_addgroup_invite);
        btnAddgroupAdd = (Button) findViewById(R.id.btn_addgroup_add);
    }
}
