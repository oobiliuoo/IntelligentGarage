package com.oobiliuoo.intelligentgarageapp;

import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

import com.oobiliuoo.intelligentgarageapp.utils.MyUtils;

public class SettingActivity extends BaseActivity {

    private String TGA = "SettingActivity";

    private Toolbar toolbar;
    private Button btnConnect;


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

    }


    @Override
    public void initClick() {
        switch (CLICK_ID) {
            case R.id.set_btn_connect:
                MyUtils.mLog1(TGA, "click reconnect");
                myBinder.reConnect(MyUtils.HOST_IP_ADDRESS, 8080);
                break;

            default:
                break;
        }
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
                btnConnect.setBackground(getDrawable(R.drawable.button_drawable1));
                break;
            default:
        }
    }

}