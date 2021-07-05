package com.oobiliuoo.intelligentgarageapp;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oobiliuoo.intelligentgarageapp.adapter.HostLocationListViewAdapter;
import com.oobiliuoo.intelligentgarageapp.bean.ControlCard;
import com.oobiliuoo.intelligentgarageapp.bean.HostLocation;
import com.oobiliuoo.intelligentgarageapp.utils.MyUtils;
import com.oobiliuoo.intelligentgarageapp.utils.ScreenSizeUtils;

import org.litepal.LitePal;

import java.util.Collections;
import java.util.List;

public class SettingActivity extends BaseActivity {

    private String TGA = "SettingActivity";

    private Toolbar toolbar;
    private Button btnConnect;
    private Button btnQuit;
    private LinearLayout llLocation;
    private TextView tvLocationName;
    private TextView tvIp;
    private TextView tvPort;
    private Dialog dialog;
    private boolean showConnectToast = false;


    @Override
    public void init() {

        setContentView(R.layout.activity_setting);
        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        MyUtils.mLog1("SettingActivity onDestroy");
    }


    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.set_toorbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnConnect = (Button) findViewById(R.id.set_btn_connect);
        btnConnect.setOnClickListener(new MyLister());

        btnQuit = (Button) findViewById(R.id.set_btn_quit);
        btnQuit.setOnClickListener(new MyLister());

        llLocation = (LinearLayout) findViewById(R.id.set_ll_location);
        llLocation.setOnClickListener(new MyLister());

        tvLocationName = (TextView) findViewById(R.id.set_tv_locationName);
        tvLocationName.setText(host.getLocationName());
        tvIp = (TextView) findViewById(R.id.set_tv_ip);
        tvIp.setText(host.getIpAddress());
        tvPort = (TextView) findViewById(R.id.set_tv_port);
        tvPort.setText(host.getPort());

        boolean isConnect = pref.getBoolean("connectSuccess",false);
        if(isConnect){
            btnConnect.setBackground(getDrawable(R.drawable.button_drawable2));
        }



    }


    @Override
    public void initClick(View v) {
        switch (v.getId()) {
            case R.id.set_btn_connect:
                showConnectToast = true;
                MyUtils.mLog1(TGA, "click reconnect");
                myBinder.reConnect(host.getIpAddress(), host.getIntPort());
                break;
            case R.id.set_ll_location:
                locationDialog();

            case R.id.set_btn_quit:
                break;
            default:
                break;
        }
    }

    @Override
    protected boolean checkChanged(ControlCard card, boolean isChecked) {
        return false;
    }


    @Override
    protected void initHandleMessage(Message msg) {
        switch (msg.what) {
            case MyUtils.CONNECT_SUCCESS:
                if(showConnectToast){
                     MyUtils.showToast(SettingActivity.this, "连接成功");
                     showConnectToast = false;
                }
                btnConnect.setBackground(getDrawable(R.drawable.button_drawable2));

                break;
            case MyUtils.CONNECT_FAIL:
                if(showConnectToast){
                    MyUtils.showToast(SettingActivity.this, "连接失败");
                    showConnectToast = false;
                }
                btnConnect.setBackground(getDrawable(R.drawable.button_drawable3));
                break;
            default:
        }
    }

    private void locationDialog(){

        // MyUtils.resetHostLocationTable();
        List<HostLocation> list = LitePal.findAll(HostLocation.class);
        if(list.size() <= 0){
            HostLocation hostLocation = new HostLocation("Biliu Garage",MyUtils.HOST_IP_ADDRESS, "8080");
            hostLocation.save();
            list.add(hostLocation);
        }

        //final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.dialog_location, null);

        ListView listView = (ListView) view.findViewById(R.id.location_list);
        HostLocationListViewAdapter adapter =
                new HostLocationListViewAdapter(SettingActivity.this,R.layout.dialog_location_list_it,list, new SettingLister());
        listView.deferNotifyDataSetChanged();
        listView.setAdapter(adapter);

        ImageButton ibtnAdd = view.findViewById(R.id.location_ibtn_add);
        ibtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HostLocation hostLocation = new HostLocation("添加名称","127.0.0.1", "8080");
                list.add(0,hostLocation);
                HostLocationListViewAdapter adapter2 =  new HostLocationListViewAdapter(SettingActivity.this,R.layout.dialog_location_list_it,list, new SettingLister());
                listView.setAdapter(adapter2);

            }
        });

        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(true);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.90f);
        lp.height = (int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.45f);
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);
        dialog.show();

    }

    class SettingLister extends MyLister implements HostLocationListViewAdapter.HostListViewOnClickListener{

        /**
         * 专门处理 list 选择的点击事件
         */
        @Override
        public void HostListOnClick(View v, HostLocation h) {
            tvLocationName.setText(h.getLocationName());
            tvIp.setText(h.getIpAddress());
            tvPort.setText(h.getPort());
            host = h;
            prefEditor.putString("hostname",host.getLocationName());
            prefEditor.putString("ip",host.getIpAddress());
            prefEditor.putString("port",host.getPort());
            prefEditor.apply();
            myBinder.reConnect(host.getIpAddress(),host.getIntPort());
            dialog.dismiss();

        }
        /**
         * 专门处理 HostLocationList 中长按事件
         * 提供给子类的长按事件函数
         * v 点击控件
         */
        @Override
        public void HostListOnLongClick(View v) {

        }
    }



}