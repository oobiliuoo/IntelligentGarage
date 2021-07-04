package com.oobiliuoo.intelligentgarageapp;

import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.oobiliuoo.intelligentgarageapp.adapter.HostLocationListViewAdapter;
import com.oobiliuoo.intelligentgarageapp.bean.ControlCard;
import com.oobiliuoo.intelligentgarageapp.bean.HostLocation;
import com.oobiliuoo.intelligentgarageapp.utils.MyUtils;
import com.oobiliuoo.intelligentgarageapp.utils.ScreenSizeUtils;

import org.litepal.LitePal;

import java.util.List;

public class SettingActivity extends BaseActivity {

    private String TGA = "SettingActivity";

    private Toolbar toolbar;
    private Button btnConnect;
    private LinearLayout llLocation;


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

        llLocation = (LinearLayout) findViewById(R.id.set_ll_location);
        llLocation.setOnClickListener(new MyLister());

    }


    @Override
    public void initClick(View v) {
        switch (v.getId()) {
            case R.id.set_btn_connect:
                MyUtils.mLog1(TGA, "click reconnect");
                myBinder.reConnect(MyUtils.HOST_IP_ADDRESS, 8080);
                break;
            case R.id.set_ll_location:
                locationDialog();
            default:
                break;
        }
    }

    @Override
    protected boolean checkChanged(ControlCard card, boolean isChecked) {
        return false;
    }

    @Override
    protected void initHostListOnClick(View v) {

    }

    @Override
    protected boolean initHostLocationLongClick(View v) {
        return false;
    }

    @Override
    protected void initHandleMessage() {
        switch (MESSAGE.what) {
            case MyUtils.CONNECT_SUCCESS:
                MyUtils.showToast(SettingActivity.this, "连接成功");
                btnConnect.setBackground(getDrawable(R.drawable.button_drawable2));
                break;
            case MyUtils.CONNECT_FAIL:
                MyUtils.showToast(SettingActivity.this, "连接失败");
                btnConnect.setBackground(getDrawable(R.drawable.button_drawable3));
                break;
            default:
        }
    }

    private void locationDialog(){

         MyUtils.resetHostLocationTable();
        List<HostLocation> list = LitePal.findAll(HostLocation.class);
        if(list.size() <= 0){
            HostLocation hostLocation = new HostLocation("Biliu Garage",MyUtils.HOST_IP_ADDRESS, "8080");
            hostLocation.save();
            list.add(hostLocation);
        }

        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.dialog_location, null);
        ListView listView = (ListView) view.findViewById(R.id.location_list);
        HostLocationListViewAdapter adapter =
                new HostLocationListViewAdapter(SettingActivity.this,R.layout.dialog_location_list_it,list, new MyLister());
        listView.deferNotifyDataSetChanged();
        listView.setAdapter(adapter);
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

}