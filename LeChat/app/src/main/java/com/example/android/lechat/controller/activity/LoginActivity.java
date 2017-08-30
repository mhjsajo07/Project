package com.example.android.lechat.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.lechat.R;
import com.example.android.lechat.model.Model;
import com.example.android.lechat.model.bean.UserInfo;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText etLoginName;
    private EditText etLoginPwd;
    private Button btnLoginLogin;
    private Button btnLoginRegister;
    private ProgressBar pbLoginLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initListener();
    }

    private void register() {
        final String registerName = etLoginName.getText().toString();
        final String registerPwd = etLoginPwd.getText().toString();
        if (TextUtils.isEmpty(registerName) || TextUtils.isEmpty(registerPwd)) {
            Toast.makeText(this, "用户名或密码不能为空。", Toast.LENGTH_SHORT).show();
            pbLoginLoading.setVisibility(View.INVISIBLE);
            return;
        }
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(registerName, registerPwd);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册成功。", Toast.LENGTH_SHORT).show();
                            pbLoginLoading.setVisibility(View.INVISIBLE);
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册失败。", Toast.LENGTH_SHORT).show();
                            pbLoginLoading.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }

    private void login() {
        final String loginName = etLoginName.getText().toString();
        final String loginPwd = etLoginPwd.getText().toString();
        if (TextUtils.isEmpty(loginName) || TextUtils.isEmpty(loginPwd)) {
            Toast.makeText(this, "用户名或密码不能为空。", Toast.LENGTH_SHORT).show();
            pbLoginLoading.setVisibility(View.INVISIBLE);
            return;
        }
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().login(loginName, loginPwd, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        Model.getInstance().loginSuccess(new UserInfo(loginName));

                        Model.getInstance().getUserAccountDao().addAccount(new UserInfo(loginName));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pbLoginLoading.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onError(int i, String s) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登录失败。", Toast.LENGTH_SHORT).show();
                                pbLoginLoading.setVisibility(View.INVISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });
    }

    private void initListener() {
        btnLoginLogin.setOnClickListener(this);
        btnLoginRegister.setOnClickListener(this);
    }

    private void initView() {
        etLoginName = (EditText) findViewById(R.id.et_login_name);
        etLoginPwd = (EditText) findViewById(R.id.et_login_pwd);
        btnLoginLogin = (Button) findViewById(R.id.btn_login_login);
        btnLoginRegister = (Button) findViewById(R.id.btn_login_register);
        pbLoginLoading = (ProgressBar) findViewById(R.id.pb_login_loading);
    }

    @Override
    public void onClick(View view) {
        if (view == btnLoginLogin) {
            pbLoginLoading.setVisibility(View.VISIBLE);
            login();
        } else if (view == btnLoginRegister) {
            pbLoginLoading.setVisibility(View.VISIBLE);
            register();
        }
    }
}
