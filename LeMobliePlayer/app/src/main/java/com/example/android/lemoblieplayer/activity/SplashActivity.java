package com.example.android.lemoblieplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.MotionEvent;

import com.example.android.lemoblieplayer.R;

public class SplashActivity extends Activity {
    private boolean isStartMainActivity = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private void toMainActivity() {
        if (!isStartMainActivity) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            isStartMainActivity = true;
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toMainActivity();
            }
        }, 2000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        toMainActivity();
        isStartMainActivity = true;
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
