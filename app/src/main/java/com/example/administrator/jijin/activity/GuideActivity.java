package com.example.administrator.jijin.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.administrator.jijin.MainActivity;
import com.example.administrator.jijin.R;
import com.example.administrator.jijin.adapter.GuideAdapter;
import com.example.administrator.jijin.util.ConfigUtil;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends Activity implements OnClickListener, OnPageChangeListener{

    private ViewPager vp;
    private GuideAdapter vpAdapter;
    private List<View> views;
    private Button btn_go;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    //引导图片资源
    private static final int[] pics = { R.mipmap.welcome1,
            R.mipmap.welcome2, R.mipmap.welcome3,
            R.mipmap.welcome4,R.mipmap.welcome5 };

    //底部小店图片
    private ImageView[] dots ;

    //记录当前选中位置
    private int currentIndex;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.activity_guide);

        views = new ArrayList<View>();

        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        //初始化引导图片列表
        for(int i=0; i<pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setImageResource(pics[i]);
            views.add(iv);
        }
        vp = (ViewPager) findViewById(R.id.viewpager);
        //初始化Adapter
        vpAdapter = new GuideAdapter(views);
        vp.setAdapter(vpAdapter);
        //绑定回调
        vp.setOnPageChangeListener(this);

        //初始化底部小点
        initDots();

    }

    private void initDots() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
        btn_go= (Button) findViewById(R.id.btn_go);
        sp = getSharedPreferences(ConfigUtil.spSave, Activity.MODE_PRIVATE);
        editor=sp.edit();
        final String number = sp.getString("number", "");
        final Intent intent1 = new Intent(this, MainActivity.class);
        final Intent intent2 = new Intent(this,LoginActivity.class);
        btn_go.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!number.equals("")) {
                    startActivity(intent1);
                } else {
                    startActivity(intent2);
                }
            }
        });
        dots = new ImageView[pics.length];

        //循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            dots[i] = (ImageView) ll.getChildAt(i);
                dots[i].setEnabled(true);//都设为灰色
                dots[i].setOnClickListener(this);
                dots[i].setTag(i);//设置位置tag，方便取出与当前位置对应
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(false);//设置为白色，即选中状态
    }

    /**
     *设置当前的引导页
     */
    private void setCurView(int position)
    {
        if (position < 0 || position >= pics.length) {
            return;
        }

        vp.setCurrentItem(position);
    }

    /**
     *这只当前引导小点的选中
     */
    private void setCurDot(int positon)
    {
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
            return;
        }
            dots[positon].setEnabled(false);
            dots[currentIndex].setEnabled(true);

        currentIndex = positon;
    }

    //当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    //当当前页面被滑动时调用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    //当新的页面被选中时调用
    @Override
    public void onPageSelected(int arg0) {
        //设置底部小点选中状态
        setCurDot(arg0);
        if (arg0==4){
            btn_go.setVisibility(View.VISIBLE);
        }else {
            btn_go.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int position = (Integer)v.getTag();
        setCurView(position);
        setCurDot(position);
    }
}
