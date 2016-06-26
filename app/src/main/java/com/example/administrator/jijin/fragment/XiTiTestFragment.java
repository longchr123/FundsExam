package com.example.administrator.jijin.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.jijin.R;
import com.example.administrator.jijin.activity.XiTiAnswerActivity;
import com.example.administrator.jijin.adapter.TestListAdapter;
import com.example.administrator.jijin.view.MyListView;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/26.
 */
public class XiTiTestFragment extends Fragment implements AdapterView.OnItemClickListener {
    private XiTiAnswerActivity answerActivity;
    private View view;
    private TextView tv_tigan, tv_answer, tv_analysis;
    private MyListView lv;
    private List<String> list;
    private TestListAdapter adapter;
    private LinearLayout lin_answer;
    private String A, B, C, D, E, answer, analysis, tigan;
    private boolean flag = true;
    private int testId;
    private FragmentCallBack callBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        answerActivity = (XiTiAnswerActivity) getActivity();
        callBack = answerActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.from(answerActivity).inflate(R.layout.fragment_test, null);
        getBundle();
        initView();
        initData();
        initListener();
        return view;
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("XiTiTestFragment");
    }

    private void getBundle() {
        Bundle bundle = getArguments();
        testId = bundle.getInt("id");
        A = bundle.getString("A");
        B = bundle.getString("B");
        C = bundle.getString("C");
        D = bundle.getString("D");
        E = bundle.getString("E");
        answer = bundle.getString("answer");
        analysis = bundle.getString("analysis");
        tigan = bundle.getString("tigan");
    }

    private void initListener() {
        lv.setOnItemClickListener(this);
    }

    private void initData() {
        list = new ArrayList<>();
        list.add(A);
        list.add(B);
        list.add(C);
        list.add(D);
        if (!E.equals("E. 暂无") && !E.equals("E. ")) {
            list.add(E);
        }
        adapter = new TestListAdapter(answerActivity, list);
        lv.setAdapter(adapter);
        tv_answer.setText(answer);
        tv_analysis.setText(analysis);
        tv_tigan.setText(tigan);
    }

    private void initView() {
        tv_tigan = (TextView) view.findViewById(R.id.tv_tigan);
        tv_answer = (TextView) view.findViewById(R.id.tv_answer);
        tv_analysis = (TextView) view.findViewById(R.id.tv_analysis);
        lv = (MyListView) view.findViewById(R.id.lv);
        lin_answer = (LinearLayout) view.findViewById(R.id.lin_answer);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (flag) {
            adapter.changeTextColor(position, getPosition(answer));
            lin_answer.setVisibility(View.VISIBLE);
            if (list.get(position).substring(0, 1).equals(answer)) {
                Toast.makeText(answerActivity, "恭喜回答正确咯", Toast.LENGTH_SHORT).show();
            } else {
                callBack.addToSql(testId);
            }
            flag = false;
            switch (testId-answerActivity.firstInPosition){
                case 5:
                case 10:
                case 15:
                case 20:
                case 25:
                case 30:
                    showModelDialog((testId-answerActivity.firstInPosition)/5);
                    break;

            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("XiTiTestFragment");
        flag = true;
    }

    public interface FragmentCallBack {
        void addToSql(int testId);
    }

    public void show() {
        if (lin_answer != null) {
            lin_answer.setVisibility(View.VISIBLE);
        }
    }

    private int getPosition(String answer) {
        if (answer.equals("A")) {
            return 0;
        } else if (answer.equals("B")) {
            return 1;
        } else if (answer.equals("C")) {
            return 2;
        } else if (answer.equals("D")) {
            return 3;
        } else if (answer.equals("E")) {
            return 4;
        }
        return 0;
    }

    WebView mWebView;
    ImageView mImageView;
    private void showModelDialog(int read){
        //布局文件转换为view对象
        LayoutInflater inflaterDl = LayoutInflater.from(answerActivity);
        RelativeLayout layout = (RelativeLayout)inflaterDl.inflate(R.layout.dialog_medal, null);
        mWebView = (WebView) layout.findViewById(R.id.wv);
        //设置编码
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        //支持js
        mWebView.getSettings().setJavaScriptEnabled(true);
        //设置背景颜色 透明
        mWebView.setBackgroundColor(Color.argb(0, 0, 0, 0));
        //设置本地调用对象及其接口
//        mWebView.addJavascriptInterface(new JavaScriptObject(mContext), "myObj");
        //载入js
        mWebView.loadUrl("file:///android_asset/fireworks.html");
        //对话框
        final Dialog dialog = new AlertDialog.Builder(answerActivity).create();
        mImageView= (ImageView) layout.findViewById(R.id.iv_medal);
        getImageByReflect("lv"+read);
        dialog.show();
        dialog.getWindow().setContentView(layout);

        //关闭按钮
        ImageButton btnClose = (ImageButton) layout.findViewById(R.id.dialog_close);
        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /** $表示内部类的意思
        所以com.example.administrator.jijin.R$mipmap表示:
        mipmap是com.example.administrator.jijin.R的内部类
     */
    private void getImageByReflect(String imageName){
        try {
            Field field = Class.forName("com.example.administrator.jijin.R$mipmap").getField(imageName);
            mImageView.setImageResource(field.getInt(field));
        } catch (Exception e) {
            Log.e("reflect", "-------------"+e.toString());
        }
    }
}
