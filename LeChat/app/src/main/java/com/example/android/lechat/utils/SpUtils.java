package com.example.android.lechat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.android.lechat.IMApplication;

/**
 * Created by 51477 on 2017/8/22.
 */

public class SpUtils {

    public static final String IS_NEW_INVITE = "is_new_invite";
    private static SpUtils instance = new SpUtils();
    private static SharedPreferences mSp;

    private SpUtils() {
    }

    public static SpUtils getInstance() {
        if (mSp == null) {
            mSp = IMApplication.getGlobalApplication().getSharedPreferences("im", Context.MODE_PRIVATE);
        }
        return instance;
    }

    // 保存
    public void save(String key, Object value) {

        if (value instanceof String) {
            mSp.edit().putString(key, (String) value).commit();
        } else if (value instanceof Boolean) {
            mSp.edit().putBoolean(key, (Boolean) value).commit();
        } else if (value instanceof Integer) {
            mSp.edit().putInt(key, (Integer) value).commit();
        }
    }

    // 获取数据的方法
    public String getString(String key, String defValue) {
        return mSp.getString(key, defValue);
    }

    // 获取boolean数据
    public boolean getBoolean(String key, boolean defValue) {
        return mSp.getBoolean(key, defValue);
    }

    // 获取int类型数据
    public int getInt(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }
}
