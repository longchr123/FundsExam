package com.example.administrator.jijin.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.administrator.jijin.MainActivity;
import com.example.administrator.jijin.R;
import com.example.administrator.jijin.service.MyService;
import com.example.administrator.jijin.util.ConfigUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

public class WelcomeActivity extends Activity {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        final Intent intent = new Intent(WelcomeActivity.this, MyService.class);
        startService(intent);
        sp = getSharedPreferences(ConfigUtil.spSave, Activity.MODE_PRIVATE);
        editor=sp.edit();
        number = sp.getString("number", "");
        final Intent intent1 = new Intent(this, MainActivity.class);
        final Intent intent2 = new Intent(this,LoginActivity.class);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(sp.getBoolean("isFirstCome",true)){
                    startActivity(new Intent(WelcomeActivity.this,GuideActivity.class));
                    editor.putBoolean("isFirstCome",false).commit();
                }else {
                if (!number.equals("")) {
                    startActivity(intent1);
                } else {
                    startActivity(intent2);
                }
                }
                finish();
            }
        };
        timer.schedule(task, 1000 * 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        JPushInterface.onPause(this);
    }
}
