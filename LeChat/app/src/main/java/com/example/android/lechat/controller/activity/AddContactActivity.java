package com.example.android.lechat.controller.activity;

import android.app.Activity;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.lechat.R;
import com.example.android.lechat.model.Model;
import com.example.android.lechat.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class AddContactActivity extends Activity implements View.OnClickListener {
    private static final String TAG  = AddContactActivity.class.getSimpleName();

    private EditText etAddName;
    private TextView tvAddName;
    private Button btnAddAdd;
    private LinearLayout llAddFriend;
    private Button btnAddFind;
    private ProgressBar pbAddLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        initView();
        initListener();
    }

    private void initListener() {
        btnAddFind.setOnClickListener(this);
        btnAddAdd.setOnClickListener(this);
    }

    private void initView() {
        etAddName = (EditText) findViewById(R.id.et_add_name);
        tvAddName = (TextView) findViewById(R.id.tv_add_name);
        btnAddAdd = (Button) findViewById(R.id.btn_add_add);
        llAddFriend = (LinearLayout) findViewById(R.id.ll_add_friend);
        btnAddFind = (Button) findViewById(R.id.btn_add_find);
        pbAddLoading = (ProgressBar) findViewById(R.id.pb_add_loading);
    }

    @Override
    public void onClick(View view) {
        if (view == btnAddFind) {
            pbAddLoading.setVisibility(View.VISIBLE);
            final String name = etAddName.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(this, "查找不能为空。", Toast.LENGTH_SHORT).show();
                return;
            }
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pbAddLoading.setVisibility(View.INVISIBLE);
                            llAddFriend.setVisibility(View.VISIBLE);
                            tvAddName.setText(name);
                        }
                    });
                }
            });
        } else if (view == btnAddAdd) {
            final String name = etAddName.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(this, "添加不能为空。", Toast.LENGTH_SHORT).show();
                return;
            }
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    final UserInfo userInfo = new UserInfo(name);
                    try {
                        EMClient.getInstance().contactManager().addContact(userInfo.getHxid(), "交个朋友吧。");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "run: "+userInfo.getHxid() );
                                Toast.makeText(AddContactActivity.this, "好友请求成功。", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddContactActivity.this, "好友请求失败。", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }
}
