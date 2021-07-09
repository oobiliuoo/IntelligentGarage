package com.oobiliuoo.intelligentgarageapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.oobiliuoo.intelligentgarageapp.adapter.NotifyListViewAdapter;
import com.oobiliuoo.intelligentgarageapp.bean.ControlCard;
import com.oobiliuoo.intelligentgarageapp.bean.MyMessage;
import com.oobiliuoo.intelligentgarageapp.bean.NotifyInfo;
import com.oobiliuoo.intelligentgarageapp.utils.MyUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotifyActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listView;
    private List<NotifyInfo> notifyInfos = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        setContentView(R.layout.activity_notify);
        MyMessage.NOTIFY_MODE_MAP.put("CAR-IN","有车辆入库");
        MyMessage.NOTIFY_MODE_MAP.put("CAR-OUT","有车辆出库");
        MyMessage.NOTIFY_MODE_MAP.put("WARING","警告：大门剧烈震动");

        initView();
    }

    private void initView() {

        toolbar = (Toolbar) findViewById(R.id.notify_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView = findViewById(R.id.notify_list);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                initPopWindow(view);
                MyUtils.mLog1("NA: Long click");
                return false;
            }
        });

    }

    private void initPopWindow(View v) {
        TextView tvTime = v.findViewById(R.id.lln_tv_time);
        String time = tvTime.getText().toString();
        View view =  LayoutInflater.from(NotifyActivity.this).inflate(R.layout.popupwindow_notify, null, false);
        Button btnDelete = view.findViewById(R.id.pn_delete);
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popWindow.showAsDropDown(v,400,-(v.getHeight() / 2));
       // popWindow.showAtLocation(v, Gravity.CENTER,0,0);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<NotifyInfo> notifyInfos = LitePal.where("time = ?", time).find(NotifyInfo.class);
                if(notifyInfos.size()>0){
                    notifyInfos.get(0).delete();
                    popWindow.dismiss();
                    initList();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        initList();
    }

    private void initList() {
        notifyInfos.clear();
        notifyInfos = LitePal.findAll(NotifyInfo.class);
        if(notifyInfos.size()<=0){
            notifyInfos = new ArrayList<>();
        }
        Collections.reverse(notifyInfos);
        NotifyListViewAdapter adapter = new NotifyListViewAdapter(NotifyActivity.this,R.layout.layout_it_notify,notifyInfos);
        listView.deferNotifyDataSetChanged();
        listView.setAdapter(adapter);

    }


}