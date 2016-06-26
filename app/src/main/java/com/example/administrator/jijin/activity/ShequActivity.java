package com.example.administrator.jijin.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

import com.example.administrator.jijin.R;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.sdkmanager.LoginSDKManager;
import com.umeng.simplify.ui.fragments.CommunityMainFragment;
import com.umeng.simplify.ui.presenter.impl.LoginSimplify;
import com.umeng.simplify.ui.presenter.impl.LoginSuccessStrategory;

/**
 * Created by wangfei on 16/4/27.
 */
public class ShequActivity extends FragmentActivity {

    CommunitySDK mCommSDK = null;
    String topicId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /**以下代码为demo中使用，开发者使用不需要添加**/
        Constants.isCheck = true;


        /**以上代码为demo中使用，开发者使用不需要添加**/
        // 1、初始化友盟微社区
        mCommSDK = CommunityFactory.getCommSDK(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // ==================== 注意 ===================
        // 开发者如果想将友盟微社区作为ViewPager中的一个页面集成到应用中时，请将您的ViewPager替换为CommunityViewPager，
        // 避免滑动时间冲突导致问题
        // CommunityViewPager viewPager = (CommunityViewPager)
        // findViewById(R.id.viewPager);
        // // 设置ViewPager的Adapter
        // viewPager.setAdapter(new
        // FragmentTabAdapter(getSupportFragmentManager()));
        // ===============================================

        // Get access token before showing UI
        // 需要先请求access token,才能请求其他网络接口

//                    // 2、单纯Fragment使用方式
        /**如果使用android6.0适配，需要加入以下代码，获取对应权限*/
//        String[] mPermissionList = new String[]{Manifest.permission.CHANGE_CONFIGURATION,Manifest.permission.CHANGE_WIFI_STATE,Manifest.permission.WAKE_LOCK,Manifest.permission.WRITE_SETTINGS,Manifest.permission.VIBRATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_LOGS,Manifest.permission.READ_PHONE_STATE};
//        if(Build.VERSION.SDK_INT>=23){
//            requestPermissions(mPermissionList,100);
//        }
        CommunityMainFragment fragment = new CommunityMainFragment();
        fragment.setBackButtonVisibility(View.GONE);

        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        LoginSDKManager.getInstance().addAndUse(new LoginSimplify());
        CommConfig.getConfig().setLoginResultStrategy(new LoginSuccessStrategory());

    }


//    }
}
