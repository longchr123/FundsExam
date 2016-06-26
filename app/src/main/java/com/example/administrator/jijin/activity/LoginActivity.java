package com.example.administrator.jijin.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.administrator.jijin.MainActivity;
import com.example.administrator.jijin.R;
import com.example.administrator.jijin.util.ConfigUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.login.LoginListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText et_phone, et_mima;
    private TextView tv_forget;
    private Button bt_login, bt_create;
    private SharedPreferences sp;
    private RequestQueue mQueue;
    private StringRequest stringRequest;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.activity_login);
        initView();
        initListener();
    }

    private void initListener() {
        bt_create.setOnClickListener(this);
        tv_forget.setOnClickListener(this);
        bt_login.setOnClickListener(this);
    }

    private void initView() {
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_mima = (EditText) findViewById(R.id.et_mima);
        tv_forget = (TextView) findViewById(R.id.tv_forget);
        bt_create = (Button) findViewById(R.id.bt_create);
        bt_login = (Button) findViewById(R.id.bt_login);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_create:
                Intent intent = new Intent(this, CreateActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_forget:
                Intent intentForget = new Intent(this, ChangeMiMaActivity.class);
                startActivity(intentForget);
                break;
            case R.id.bt_login:
                phone=et_phone.getText().toString().trim();
                LoginUmengCom();
                break;
        }
    }

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

    private void login() {
        mQueue = Volley.newRequestQueue(this);
        stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.contains("ï»¿")){
                        response=response.replace("ï»¿","");
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    String resultCode = jsonObject.getString("resultCode");
                    String result = jsonObject.getString("result");
                    if (resultCode.equals("0")) {
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        sp = getSharedPreferences(ConfigUtil.spSave, Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("number",phone);
                        editor.commit();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "网络连接有问题", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("username", phone);
                map.put("password", et_mima.getText().toString().trim());
                return map;
            }
        };
        mQueue.add(stringRequest);
    }

    private void LoginUmengCom(){
        //创建CommUser前必须先初始化CommunitySDK
        CommunitySDK sdk = CommunityFactory.getCommSDK(this);
        CommUser user = new CommUser();
        user.name =phone;
        user.id = phone;
        sdk.loginToUmengServerBySelfAccount(this, user, new LoginListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(int stCode, CommUser commUser) {
                Log.d("tag", "login result is" + stCode);          //获取登录结果状态码
                if (ErrorCode.NO_ERROR == stCode) {
                    MobclickAgent.onProfileSignIn("urse20");
                    login();
                }
            }
        });
    }
}
