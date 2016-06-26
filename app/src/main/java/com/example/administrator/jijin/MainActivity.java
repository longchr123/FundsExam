package com.example.administrator.jijin;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.jijin.fragment.MoreFragment;
import com.example.administrator.jijin.fragment.NewsFragment;
import com.example.administrator.jijin.fragment.XiTiMainFragment;
import com.example.administrator.jijin.fragment.ZixunFragment;
import com.example.administrator.jijin.util.ConfigUtil;
import com.example.administrator.jijin.util.ExampleUtil;
import com.umeng.analytics.MobclickAgent;
import com.example.administrator.jijin.activity.ShequActivity;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.Calendar;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {
    private RadioGroup rg;
    private FragmentManager manager;
    private XiTiMainFragment xiTiMainFragment;
    private NewsFragment newsFragment;
    private MoreFragment moreFragment;
    private ZixunFragment zixunFragment;
    private Fragment mCurrentFragment;
    private FragmentTransaction transaction;
    private SharedPreferences sp;
    SharedPreferences.Editor editor;
    private int alarmHour, alarmMinute;
    private boolean openAlarm;
    private TextView tv_title;
    public static boolean isForeground=false;
    private CircleImageView civ;
    protected MainListener mMainListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteDatabase.loadLibs(this);
        initView();
        initDate();
        initListener();
        //开启闹铃
        if (openAlarm) {
            setAlarm(alarmHour, alarmMinute);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        isForeground=true;
        MobclickAgent.onResume(this);
        alarmHour = sp.getInt("alarmHour", 20);
        alarmMinute = sp.getInt("alarmMinute", 0);
        openAlarm = sp.getBoolean("openAlarm", false);
    }

    private void setAlarm(int hour, int minute) {
        long systemTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然会有8个小时的时间差
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 选择的定时时间
        long selectTime = calendar.getTimeInMillis();
        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if (systemTime > selectTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            selectTime = calendar.getTimeInMillis();
        }
        // 计算现在时间到设定时间的时间差
        long time = selectTime - systemTime;
        long firstTime = systemTime + time;
        setAlarmTime(this, firstTime);
    }

    private void initDate() {
        manager = getFragmentManager();
        zixunFragment=new ZixunFragment(this);
        xiTiMainFragment = new XiTiMainFragment(this);
        newsFragment = new NewsFragment();
        moreFragment = new MoreFragment();
        transaction = manager.beginTransaction();
        transaction.add(R.id.lin, zixunFragment).commit();
        mCurrentFragment = zixunFragment;
        sp = this.getSharedPreferences(ConfigUtil.spSave, Activity.MODE_PRIVATE);
    }

    private void initListener() {
        rg.setOnCheckedChangeListener(this);
        civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, ShequActivity.class);
                startActivityForResult(it, 0);
                overridePendingTransition(R.anim.push_up_in,
                        R.anim.push_up_out);
            }
        });
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        rg = (RadioGroup) findViewById(R.id.rg);
        civ= (CircleImageView) findViewById(R.id.civ);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        transaction = manager.beginTransaction();
        switch (checkedId) {
            case R.id.rb_zixun:
                changeFragment(zixunFragment);
                tv_title.setText("资讯");
                tv_title.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_xiti:
                changeFragment(xiTiMainFragment);
//                tv_title.setText("习题");
                tv_title.setVisibility(View.GONE);
                break;
            case R.id.rb_news:
                changeFragment(newsFragment);
                tv_title.setText("直播课");
                tv_title.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_cuoti:
                Intent intent =new Intent(MainActivity.this, ShequActivity.class);
                startActivity(intent);
                tv_title.setText(" ");
                break;
            case R.id.rb_more:
                changeFragment(moreFragment);
                tv_title.setText("更多");
                tv_title.setVisibility(View.VISIBLE);
                break;
        }
    }

    //要先hide再show
    public void changeFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        transaction = manager.beginTransaction();
        if (mCurrentFragment != null) {
            transaction.hide(mCurrentFragment);
        }
        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            transaction.add(R.id.lin, fragment);
        }
        transaction.commit();
        mCurrentFragment = fragment;
    }

    private void setAlarmTime(Context context, long firstTime) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("android.alarm.yijian.action");
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        int interval = 60 * 1000 * 60 * 24;//闹铃时间间隔
        am.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, interval, sender);
    }

    private long mExitTime;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
//                android.os.Process.killProcess(android.os.Process.myPid());
            }
//            Toast.makeText(MainActivity.this,"killProcess",Toast.LENGTH_SHORT).show();
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mMainListener!=null)
            mMainListener.xiTiRestart();
    }

    public interface MainListener {
        void xiTiRestart();
    }

    public void setMainListener(MainListener listener){
        mMainListener=listener;
    }

        @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        isForeground=false;
    }



    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
            }
        }
    }


}
