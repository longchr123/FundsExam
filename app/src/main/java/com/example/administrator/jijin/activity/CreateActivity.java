package com.example.administrator.jijin.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.jijin.Config.Config;
import com.example.administrator.jijin.R;
import com.example.administrator.jijin.util.ConfigUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.comm.core.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

public class CreateActivity extends Activity implements View.OnClickListener {
    private EditText et_phone, et_yanzheng;
    private TextView tv_get, tv_remind;
    private Button bt_ok,bt_cacle;
    private RequestQueue mQueue;
    private StringRequest stringRequest;
    private SharedPreferences sp;
    private int time = 60;
    private EditText et_pwd1,et_pwd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.activity_create);
        initView();
        initListener();
        sp = getSharedPreferences(ConfigUtil.spSave, Activity.MODE_PRIVATE);
        SMSSDK.initSDK(this, Config.SMSAPPKEY, Config.SMSAPPSECRET);
        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eh);
    }

    private void initListener() {
        tv_get.setOnClickListener(this);
        bt_ok.setOnClickListener(this);
        bt_cacle.setOnClickListener(this);
    }

    private void initView() {
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_yanzheng = (EditText) findViewById(R.id.et_yanzheng);
        tv_get = (TextView) findViewById(R.id.tv_get);
        bt_ok = (Button) findViewById(R.id.bt_ok);
        bt_cacle = (Button) findViewById(R.id.bt_cacle);
        tv_remind = (TextView) findViewById(R.id.tv_remind);
        et_pwd1= (EditText) findViewById(R.id.et_pwd1);
        et_pwd2= (EditText) findViewById(R.id.et_pwd2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_cacle:
                finish();
                break;
            case R.id.tv_get:
                if (!TextUtils.isEmpty(et_phone.getText().toString().trim())) {
                    if (et_phone.getText().toString().trim().length() == 11) {
                        tv_get.setVisibility(View.GONE);
                        SMSSDK.getVerificationCode("86", et_phone.getText().toString().trim());
                    } else {
                        Toast.makeText(CreateActivity.this, "请输入完整手机号码", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreateActivity.this, "请输入您的手机号码", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_ok:
                if(et_pwd1.getText().toString().trim().equals(et_pwd2.getText().toString().trim())) {
//                    dialog = new AlertDialog.Builder(this).setTitle("注册中...")
//                            .setCancelable(false).show();
                    SMSSDK.submitVerificationCode("86", et_phone.getText().toString().trim(), et_yanzheng.getText().toString().trim());
                }else {
                    Toast.makeText(this,"两次密码输入不一致",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            System.out.println("--------result---0" + event + "--------*" + result + "--------" + data);
            if (result == SMSSDK.RESULT_COMPLETE) {
                System.out.println("--------result---1" + event + "--------*" + result + "--------" + data);
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
                    create();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //已经验证
                    Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
                    reminderText();
                }
            } else {
                tv_get.setVisibility(View.VISIBLE);
                tv_remind.setVisibility(View.INVISIBLE);
                int status = 0;
                try {
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    JSONObject object = new JSONObject(throwable.getMessage());
                    String des = object.optString("detail");
                    status = object.optInt("status");
                    if (!TextUtils.isEmpty(des)) {
                        Toast.makeText(CreateActivity.this, des, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    SMSLog.getInstance().w(e);
                }
            }
        }
    };

    //验证码送成功后提示文字
    private void reminderText() {
        tv_remind.setVisibility(View.VISIBLE);
        handlerText.sendEmptyMessageDelayed(1, 1000);
    }

    Handler handlerText = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (time > 0) {
                    tv_remind.setText("验证码已发送" + time + "秒");
                    time--;
                    handlerText.sendEmptyMessageDelayed(1, 1000);
                } else {
                    tv_remind.setText("提示信息");
                    time = 60;
                    tv_remind.setVisibility(View.GONE);
                    tv_get.setVisibility(View.VISIBLE);
                }
            } else {
                et_yanzheng.setText("");
                tv_remind.setText("提示信息");
                time = 60;
                tv_remind.setVisibility(View.GONE);
                tv_get.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void create() {
        mQueue = Volley.newRequestQueue(this);
        String httpUrl = Config.REGISTER;//公司服务器
        stringRequest = new StringRequest(Request.Method.POST, httpUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("ï»¿")){
                    response=response.replace("ï»¿","");
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String resultCode = jsonObject.getString("resultCode");
                    String result = jsonObject.getString("result");
                    if (resultCode.equals("0")) {
                        finish();
                        //友盟社区登录
                        Toast.makeText(CreateActivity.this, CommonUtils.isLogin(CreateActivity.this)+"", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateActivity.this, result, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CreateActivity.this, "网络连接有问题", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("username", et_phone.getText().toString().trim());
                map.put("password", et_pwd1.getText().toString().trim());
                return map;
            }
        };
        mQueue.add(stringRequest);
    }
}
