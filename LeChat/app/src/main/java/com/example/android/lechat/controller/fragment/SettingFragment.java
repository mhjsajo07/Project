package com.example.android.lechat.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.lechat.R;
import com.example.android.lechat.controller.activity.LoginActivity;
import com.example.android.lechat.model.Model;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

/**
 * Created by 51477 on 2017/8/22.
 */

public class SettingFragment extends Fragment implements View.OnClickListener {
    private Button btnMainExit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_setting, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        btnMainExit = (Button) view.findViewById(R.id.btn_main_exit);
        initData();
        initListener();
    }

    private void initData() {
        btnMainExit.setText("退出登录(" + EMClient.getInstance().getCurrentUser() + ")");
    }

    private void initListener() {
        btnMainExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnMainExit) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    EMClient.getInstance().logout(true, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "退出成功。", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            });
                        }

                        @Override
                        public void onError(int i, String s) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "退出失败。", Toast.LENGTH_SHORT).show();
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
    }
}
