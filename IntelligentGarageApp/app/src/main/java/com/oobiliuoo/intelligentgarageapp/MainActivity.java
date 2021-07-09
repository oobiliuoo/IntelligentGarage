package com.oobiliuoo.intelligentgarageapp;

import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oobiliuoo.intelligentgarageapp.adapter.MyGridViewAdapter;
import com.oobiliuoo.intelligentgarageapp.bean.ControlCard;
import com.oobiliuoo.intelligentgarageapp.bean.MyMessage;
import com.oobiliuoo.intelligentgarageapp.utils.MyUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * @author biliu
 */
public class MainActivity extends BaseActivity {

    private boolean showConnectToast = true;
    private GridView gridView;
    private List<ControlCard> controlCardList = new ArrayList<>();
    private ImageButton btnSet;
    private ImageButton btnNotify;
    private ProgressBar pbBrightness;
    private TextView tvHostName;
    private ImageView ivHome;
    private TextView tvHint;

    /**
     * 收到的信息
     */
    private String RECEIVE_MESSAGE = "";


    @Override
    public void init() {
        setContentView(R.layout.activity_main);

        initView();
        MyUtils.initImgMap();
        MyUtils.resetControlCardTable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCardInfo();
        initData();
        tvHostName.setText(host.getLocationName());
        MyUtils.mLog1("MA: OnResume host:" + host.getLocationName());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void initHandleMessage(Message msg) {
        switch (msg.what) {
            case MyUtils.CONNECT_SUCCESS:
                doOnConnectSuccess();
                break;
            case MyUtils.CONNECT_FAIL:
                doOnConnectFail();
                break;
            case MyUtils.RECEIVE_DATA:
                RECEIVE_MESSAGE = msg.obj.toString();
                doOnReceiveData();
                MyUtils.showControlCardAll();

                break;
            default:
        }
    }

    @Override
    public void initClick(View v) {
        switch (v.getId()) {
            case R.id.main_btn_set:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.main_btn_notify:
                startActivity(new Intent(MainActivity.this,NotifyActivity.class));
                break;
            default:
        }
    }

    @Override
    protected boolean checkChanged(ControlCard card, boolean isChecked) {
        if (isChecked) {
            card.setState("ON");
        } else {
            card.setState("OFF");
        }
        MyMessage message = new MyMessage();
        message.setDataMode();
        message.setContext(card);
        MyUtils.mLog1("MA: checkChanged  " + message.getMessage());

        myBinder.sendMyMessage(message);

        return isChecked;
    }


    private void initView() {

        btnSet = (ImageButton) findViewById(R.id.main_btn_set);
        btnSet.setOnClickListener(new MyLister());

        btnNotify = (ImageButton) findViewById(R.id.main_btn_notify);
        btnNotify.setOnClickListener(new MyLister());

        gridView = findViewById(R.id.main_gridview);

        pbBrightness = (ProgressBar) findViewById(R.id.main_pb_brightness);

        tvHostName = (TextView) findViewById(R.id.main_tv_hostname);
        tvHostName.setText(host.getLocationName());

        ivHome = (ImageView) findViewById(R.id.main_iv_home);

        tvHint = (TextView) findViewById(R.id.main_tv_hint);

    }

    private void initCardInfo() {
        controlCardList.clear();
        List<ControlCard> list = LitePal.findAll(ControlCard.class);
        if (list.size() > 0) {
            MyUtils.mLog1("表建立成功");
            controlCardList = list;
        } else {
            ControlCard card1 = new ControlCard("LED1", "车库", "吊灯", "OFF", "1");
            controlCardList.add(card1);
            card1.save();
            ControlCard card2 = new ControlCard("LED2", "车库外", "大灯", "OFF", "1");
            controlCardList.add(card2);
            card2.save();
            ControlCard card3 = new ControlCard("DOOR1", "车库", "门", "OFF", "1");
            controlCardList.add(card3);
            card3.save();

        }

        MyGridViewAdapter adapter = new MyGridViewAdapter(MainActivity.this, R.layout.layout_it_control, controlCardList, new MyLister());
        gridView.deferNotifyDataSetChanged();
        gridView.setAdapter(adapter);
    }

    private void doOnConnectSuccess() {

        if(showConnectToast){
            MyUtils.showToast(MainActivity.this, "连接成功");
            showConnectToast = false;
        }
        ControlCard controlCard = new ControlCard();
        controlCard.setEnable("1");
        controlCard.setState("OFF");
        controlCard.updateAll();
        initCardInfo();
        ivHome.setImageResource(R.drawable.home_on);
        tvHint.setText("智能网关监控中");

    }

    private void doOnConnectFail() {
        if(showConnectToast){
            MyUtils.showToast(MainActivity.this, "连接失败");
            showConnectToast = false;
        }
        ControlCard controlCard = new ControlCard();
        controlCard.setEnable("0");
        controlCard.setState("OFF");
        controlCard.updateAll();
        initCardInfo();
        ivHome.setImageResource(R.drawable.home_off);
        tvHint.setText("未连接");
    }


    private void doOnReceiveData() {

        MyMessage message = new MyMessage(RECEIVE_MESSAGE);
        switch (message.getIntMode()) {
            case 0:
                ArrayList<String[]> dataList = message.getDataContext();
                for (String[] data : dataList) {
                    if("LIGHT".equals(data[0])){
                        if(MyUtils.isNumeric(data[1])){
                            pbBrightness.setProgress(Integer.valueOf(data[1]).intValue());
                        }
                    }
                    List<ControlCard> controlCards = LitePal.where("name = ?", data[0]).find(ControlCard.class);
                    if (controlCards.size() > 0) {
                        if ("ON".equals(data[1]) || "OFF".equals(data[1])) {
                            ControlCard card = controlCards.get(0);
                            card.setState(data[1]);
                            card.save();
                        }
                    }
                }
                break;
            default:
        }

        initCardInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        MyUtils.mLog1("MainActivity onDestroy");
    }

}