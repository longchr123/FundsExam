package com.example.administrator.jijin.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.administrator.jijin.bean.RealTime;
import com.example.administrator.jijin.util.ConfigUtil;
import com.example.administrator.jijin.util.JsonUtil;
import com.example.administrator.jijin.view.swipe.SwipeFlingAdapterView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/26.
 */
public class NewsFragment extends Fragment implements SwipeFlingAdapterView.onFlingListener,
        SwipeFlingAdapterView.OnItemClickListener{
    private MainActivity mainActivity;
    private View view;
    private List<RealTime> list = new ArrayList<>();
    private RequestQueue mQueue;
    private SharedPreferences sp;
    private String username;

    private int cardWidth;
    private int cardHeight;

    private SwipeFlingAdapterView swipeView;
    private InnerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        mQueue = Volley.newRequestQueue(mainActivity);
        sp = mainActivity.getSharedPreferences(ConfigUtil.spSave, Activity.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.from(mainActivity).inflate(R.layout.fragment_news, null);
        initView();
        getDataFromNet();
        username=sp.getString("number", "").toString().trim();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("NewsFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("NewsFragment");
    }

    //预约
    private void order(final int position,final View view) {
        StringRequest request = new StringRequest(Request.Method.POST, Config.YYORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("result");
                            if (result.equals("预约成功")){
                                RealTime realTime=list.get(position);
//                                for (RealTime item:list) {
//                                    if(item.getTitle().equals(realTime.getTitle()))
//                                        item.setYuyue(true);
//                                }
                                realTime.setYuyue(true);
                                view.setBackgroundResource(R.mipmap.yiyuyue);
                                new AlertDialog.Builder(mainActivity)
                                        .setTitle("您已预约" + list.get(position).getTeacher() + "老师的" + list.get(position).getTitle())
                                        .setMessage("电话，微信号")
                                        .setNegativeButton("立即拨打", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:12345678910"));
                                                startActivity(intent);
                                            }
                                        })
                                        .setPositiveButton("取消", null)
                                        .show();
                            }else {
                                Toast.makeText(mainActivity, result, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("username",username);
                map.put("yid", list.get(position).getYid());
                return map;
            }
        };
        mQueue.add(request);
    }


    private void getDataFromNet() {
        StringRequest request = new StringRequest(Config.YYLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        list = JsonUtil.parseJsonReal(response);
                        adapter.addAll(list);
                        swipeView.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        mQueue.add(request);
    }

    private void initView() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;
        cardWidth = (int) (dm.widthPixels - (2 * 18 * density));
        cardHeight = (int) (dm.heightPixels - (338 * density));

        swipeView = (SwipeFlingAdapterView) view.findViewById(R.id.swipe_view);
        //swipeView.setIsNeedSwipe(true);
        swipeView.setFlingListener(this);
        swipeView.setOnItemClickListener(this);

        adapter = new InnerAdapter();
        swipeView.setAdapter(adapter);
    }

    @Override
    public void onItemClicked(MotionEvent event, View v, Object dataObject) {

    }

    @Override
    public void removeFirstObjectInAdapter() {
        adapter.remove(0);
    }

    @Override
    public void onLeftCardExit(Object dataObject) {

    }

    @Override
    public void onRightCardExit(Object dataObject) {

    }

    @Override
    public void onAdapterAboutToEmpty(int itemsInAdapter) {
        //当只剩下2个时就会重新加载数据，进行循环
        if (itemsInAdapter == 2) {
            getDataFromNet();
        }
    }

    @Override
    public void onScroll(float progress, float scrollXProgress) {

    }

    private class InnerAdapter extends BaseAdapter {

        private boolean isFirstEnter=true;

        public InnerAdapter() {
        }

        public void addAll(Collection<RealTime> collection) {
            if (isFirstEnter) {
                list.addAll(collection);
                notifyDataSetChanged();
                isFirstEnter=false;
            } else {
                list.addAll(collection);
            }
        }

        public void clear() {
            list.clear();
            notifyDataSetChanged();
        }

        public boolean isEmpty() {
            return list.isEmpty();
        }

        public void remove(int index) {
            if (index > -1 && index < list.size()) {
                list.remove(index);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public RealTime getItem(int position) {
            if(list==null ||list.size()==0) return null;
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        // TODO: getView
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.yuyue_item, parent, false);
                holder = new ViewHolder();
                convertView.setTag(holder);
                convertView.getLayoutParams().width = cardWidth;
            }else {
                holder= (ViewHolder) convertView.getTag();
            }
            holder.portraitView = (ImageView) convertView.findViewById(R.id.portrait);
            //holder.portraitView.getLayoutParams().width = cardWidth;
            holder.portraitView.getLayoutParams().height = cardHeight;
            holder.titleView = (TextView) convertView.findViewById(R.id.tv_title);
            holder.teacherView = (TextView) convertView.findViewById(R.id.tv_teacher);
            holder.timeView = (TextView) convertView.findViewById(R.id.tv_time);
            holder.yuyueView = (TextView) convertView.findViewById(R.id.yiyuyue);
            holder.collectView = (Button) convertView.findViewById(R.id.favorite);
            if (list.get(position).getYuyue()){
                holder.collectView.setBackgroundResource(R.mipmap.yiyuyue);
            }else {
                holder.collectView.setBackgroundResource(R.mipmap.yuyue);
            }
//            position=position%list.size();
            holder.collectView.setOnClickListener(new buttonListener(position));
            holder.titleView.setText(list.get(position).getTitle());
            holder.teacherView.setText(list.get(position).getTeacher());
            holder.timeView.setText(list.get(position).getDate());
            holder.yuyueView.setText(list.get(position).getNum()+"人已预约");
            return convertView;
        }

    }

    private static class ViewHolder {
        ImageView portraitView;
        TextView titleView;
        TextView teacherView;
        TextView timeView;
        TextView yuyueView;
        Button collectView;

    }

    class buttonListener implements View.OnClickListener {
        private int position;

        buttonListener(int pos) {
            position = pos;
        }

        @Override
        public void onClick(View v) {
                if (!username.equals("")) {
                    order(position, v);
                } else {
                    Toast.makeText(mainActivity, "需要先登录才能预约哦", Toast.LENGTH_SHORT).show();
                }
        }
    }
}
