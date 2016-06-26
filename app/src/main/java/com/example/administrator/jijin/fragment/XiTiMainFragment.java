package com.example.administrator.jijin.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.administrator.jijin.MainActivity;
import com.example.administrator.jijin.R;


public class XiTiMainFragment extends Fragment {

    private Button mXiTiBtn,mCuoTiBtn;

    private XiTiFragment mXiTiFragment;
    private CuoTiFragment mCuoTiFragment;

    public static final int MESSAGE_FRAGMENT_TYPE = 1;
    public static final int CALL_FRAGMENT_TYPE = 2;
    public int currentFragmentType = -1;
    private View view;
    private MainActivity activity;


    public XiTiMainFragment(Context context) {
        activity=(MainActivity)context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_xi_ti_main, container, false);
        initViews();
        initDatas();
        return view;
    }

    private void initViews() {
        mXiTiBtn = (Button)view.findViewById(R.id.btn_xiti);
        mCuoTiBtn = (Button)view.findViewById(R.id.btn_cuoti);
        mXiTiBtn.setOnClickListener(onClicker);
        mCuoTiBtn.setOnClickListener(onClicker);
    }

    private void initDatas() {
        loadFragment(MESSAGE_FRAGMENT_TYPE);
    }

    private View.OnClickListener onClicker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_xiti:
                    mXiTiBtn.setTextColor(Color.parseColor("#df3031"));
                    mCuoTiBtn.setTextColor(Color.WHITE);
                    mXiTiBtn
                            .setBackgroundResource(R.drawable.baike_btn_pink_left_f_96);
                    mCuoTiBtn
                            .setBackgroundResource(R.drawable.baike_btn_trans_right_f_96);
                    switchFragment(MESSAGE_FRAGMENT_TYPE);

                    break;
                case R.id.btn_cuoti:

                    mXiTiBtn.setTextColor(Color.WHITE);
                    mCuoTiBtn.setTextColor(Color.parseColor("#df3031"));
                    mXiTiBtn
                            .setBackgroundResource(R.drawable.baike_btn_trans_left_f_96);
                    mCuoTiBtn
                            .setBackgroundResource(R.drawable.baike_btn_pink_right_f_96);
                    switchFragment(CALL_FRAGMENT_TYPE);

                    break;

            }
        }
    };
    private void switchFragment(int type) {
        switch (type) {
            case MESSAGE_FRAGMENT_TYPE:
                loadFragment(MESSAGE_FRAGMENT_TYPE);
                break;
            case CALL_FRAGMENT_TYPE:
                loadFragment(CALL_FRAGMENT_TYPE);
                break;
        }

    }

    private void loadFragment(int type) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (type == CALL_FRAGMENT_TYPE) {
            if (mCuoTiFragment == null) {
                mCuoTiFragment = new CuoTiFragment();

                transaction.add(R.id.fl_content, mCuoTiFragment, "zhishi");
            } else {
                transaction.show(mCuoTiFragment);
            }
            if (mXiTiFragment != null) {
                transaction.hide(mXiTiFragment);
            }
            currentFragmentType = MESSAGE_FRAGMENT_TYPE;
        } else {
            if (mXiTiFragment == null) {
                mXiTiFragment = new XiTiFragment();
                transaction.add(R.id.fl_content, mXiTiFragment, "wenda");
            } else {
                transaction.show(mXiTiFragment);
            }
            if (mCuoTiFragment != null) {
                transaction.hide(mCuoTiFragment);
            }
            currentFragmentType = CALL_FRAGMENT_TYPE;
        }
        transaction.commitAllowingStateLoss();
    }
}
