package com.example.administrator.jijin.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.jijin.R;
import com.example.administrator.jijin.adapter.ChapterListAdapter;
import com.example.administrator.jijin.bean.ProgressItem;
import com.example.administrator.jijin.util.ConfigUtil;
import com.example.administrator.jijin.util.SQLiteUtil;
import com.umeng.analytics.MobclickAgent;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChapterActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ListView lv;
    private List<ProgressItem> list = new ArrayList<>();
    private ChapterListAdapter adapter;
    private TextView tv_exam;
    private ImageView iv_back;
    private String exam, sqLitePath;//数据库位置
    private int examPosition;//第几个考试类型的数据库
    private SQLiteDatabase sqLiteDatabase;
    private boolean isXiTi;//是否为习题或者错题
    private Dialog dialog;
    private int changedPosition;// 更新有变化的位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chaper);
        getIntentData();
        initView();
        initData();
        initListener();
    }

    private void getIntentData() {
        exam = getIntent().getStringExtra("chapter");
        examPosition = getIntent().getIntExtra("position", 0);
        isXiTi = getIntent().getBooleanExtra("isXiTi", true);
    }

    private void initListener() {
        lv.setOnItemClickListener(this);
        iv_back.setOnClickListener(this);
    }

    private void initData() {
        if (isXiTi) {
            sqLitePath = ConfigUtil.path + ConfigUtil.getXiSqLite(examPosition);
        } else {
            sqLitePath = ConfigUtil.path + ConfigUtil.getCuoSqLite(examPosition);
        }
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqLitePath, ConfigUtil.mi_ma, null);
        list = SQLiteUtil.getProgressItem(sqLiteDatabase,isXiTi);
        adapter = new ChapterListAdapter(list, this,isXiTi);
        lv.setAdapter(adapter);
        tv_exam.setText(exam);
        dialog = new AlertDialog.Builder(this).setTitle("加载中...").setCancelable(false).create();
    }

    private void initView() {
        lv = (ListView) findViewById(R.id.lv);
        tv_exam = (TextView) findViewById(R.id.tv_exam);
        iv_back = (ImageView) findViewById(R.id.iv_back);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dialog.show();
        if (isXiTi) {
            Intent intent = new Intent(this, XiTiAnswerActivity.class);
            intent.putExtra("sqLitePath", sqLitePath);
            intent.putExtra("cuosqLitePath", ConfigUtil.path + ConfigUtil.getCuoSqLite(examPosition));
            intent.putExtra("position", position);
            intent.putExtra("examPosition", examPosition);
            intent.putExtra("title", list.get(position).getTitle());
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, AnswerActivity.class);
            intent.putExtra("sqLitePath", sqLitePath);
            intent.putExtra("position", position);
            intent.putExtra("title", list.get(position).getTitle());
            startActivity(intent);
        }
        dialog.dismiss();
        changedPosition=position;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isXiTi) {
            String tableName = SQLiteUtil.getTableEnglishName(sqLiteDatabase).get(changedPosition);
            Cursor cursor = sqLiteDatabase.query("tableNames", new String[]{"read"}, "EnglishName=?", new String[]{tableName}, null, null, null, null);
            int readPosition = 0;
            if (cursor.moveToFirst()) {
                readPosition = cursor.getInt(cursor.getColumnIndex("read"));
            }
            cursor.close();
            list.get(changedPosition).setCurrent(readPosition);
            adapter = new ChapterListAdapter(list, this, isXiTi);
//        adapter.notifyDataSetChanged();
            lv.setAdapter(adapter);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
