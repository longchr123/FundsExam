package com.example.administrator.jijin.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.jijin.MainActivity;
import com.example.administrator.jijin.R;
import com.example.administrator.jijin.bean.ZiXunItem;

import java.util.ArrayList;
import java.util.List;


public class ZixunFragment extends Fragment implements ViewPager.OnPageChangeListener,View.OnClickListener{

    private View view;
    private LinearLayout mTitleLayout;
    private ViewPager mViewPager;
    private List<ZiXunItem> mItems;
    private List<ZixunItemFragment> mFragmentItems;
    private List<TextView> mTextViews;
    private FragmentPagerAdapter mFragmentAdapter;
    private FragmentManager fragmentManager;
    private MainActivity activity;
    private int count=-1;


    public ZixunFragment(Context context) {
        activity=(MainActivity)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_zixun, container, false);
        initViews();
        initDatas();
        return view;
    }

    private void initViews() {
        mTitleLayout= (LinearLayout) view.findViewById(R.id.ll_title);
        mViewPager= (ViewPager) view.findViewById(R.id.vp_zx);
    }

    private void initDatas() {
        mItems=new ArrayList<ZiXunItem>();
        mFragmentItems=new ArrayList<ZixunItemFragment>();
        mTextViews=new ArrayList<TextView>();
        mTitleLayout.setVisibility(View.GONE);
        mItems.add(new ZiXunItem("", "http://www.zhongyuedu.com/tgm/test/test4/"));
        for (ZiXunItem item:  mItems) {
            count++;
            TextView tv=new TextView(activity);
            tv.setText(item.getTitle());
            tv.setBackgroundResource(R.drawable.zixun_selector);
            tv.setTextColor(R.color.colorWeiChoose);
            tv.setTextSize(18);
            tv.setPadding(6, 4, 6, 4);
            tv.setTag(count);
            tv.setOnClickListener(ZixunFragment.this);
            mTitleLayout.addView(tv);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tv.getLayoutParams();
            lp.setMargins(14, 6, 14, 6);
            tv.setLayoutParams(lp);
            mTextViews.add(tv);
            ZixunItemFragment fragment=new ZixunItemFragment(item.getUrl());
            mFragmentItems.add(fragment);
        }
        mFragmentAdapter = new FragmentPagerAdapter(activity.getSupportFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int i) {
                return mFragmentItems.get(i);
            }

            @Override
            public int getCount() {
                return mFragmentItems.size();
            }
        };
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setCurrentItem(1);
        resetTextView(1);
        mViewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        mViewPager.setCurrentItem(i);
        resetTextView(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private void resetTextView(int i) {
        int count=mTextViews.size();
        for (int j=0;j<count;j++){
            TextView tv=mTextViews.get(j);
            if (i==j){
                tv.setTextColor(R.color.colorBackground);
                tv.setSelected(true);
            }else {
                tv.setTextColor(R.color.colorWeiChoose);
                tv.setSelected(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i= (int) v.getTag();
        mViewPager.setCurrentItem(i);
        resetTextView(i);
    }
}
