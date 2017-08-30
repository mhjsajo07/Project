package com.example.android.lechat;

import android.app.Application;
import android.content.Context;

import com.example.android.lechat.model.Model;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;


/**
 * Created by 51477 on 2017/8/22.
 */

public class IMApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化环信EaseUI
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        options.setAutoAcceptGroupInvitation(false);
        EaseUI.getInstance().init(this, options);
        context = this;
        Model.getInstance().init(this);//初始化数据模型层类
    }

    public static Context getGlobalApplication() {
        return context;
    }
}
