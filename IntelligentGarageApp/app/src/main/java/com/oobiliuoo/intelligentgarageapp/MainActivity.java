package com.oobiliuoo.intelligentgarageapp;

import android.content.Intent;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;

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

    private GridView gridView;
    private List<ControlCard> controlCardList = new ArrayList<>();
    private ImageButton btnSet;
    private ProgressBar pbBrightness;

    /**
     * 收到的信息
     */
    private String RECEIVE_MESSAGE = "";


    @Override
    public void init() {
        setContentView(R.layout.activity_main);

        initView();
        MyUtils.initImgMap();
        // MyUtils.resetControlCardTable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCardInfo();
    }

    @Override
    protected void initHandleMessage() {
        switch (MESSAGE.what) {
            case MyUtils.CONNECT_SUCCESS:
                MyUtils.showToast(MainActivity.this, "连接成功");
                doOnConnectSuccess();
                break;
            case MyUtils.CONNECT_FAIL:
                MyUtils.showToast(MainActivity.this, "连接失败");
                doOnConnectFail();
                break;
            case MyUtils.RECEIVE_DATA:
                RECEIVE_MESSAGE = MESSAGE.obj.toString();
                MyUtils.showToast(MainActivity.this, "MA 收到数据: " + MESSAGE.obj.toString());
                doOnReceiveData();
                MyUtils.showControlCardAll();

                break;
            default:
        }
    }

    @Override
    public void initClick() {
        switch (CLICK_ID) {
            case R.id.main_btn_set:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
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

        return IS_CHECKED;
    }

    private void initView() {

        btnSet = (ImageButton) findViewById(R.id.main_btn_set);
        btnSet.setOnClickListener(new MyLister());
        gridView = findViewById(R.id.main_gridview);

        pbBrightness = (ProgressBar) findViewById(R.id.main_pb_brightness);


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
        ControlCard controlCard = new ControlCard();
        controlCard.setEnable("1");
        controlCard.setState("OFF");
        controlCard.updateAll();
        initCardInfo();
    }

    private void doOnConnectFail() {
        ControlCard controlCard = new ControlCard();
        controlCard.setEnable("0");
        controlCard.setState("OFF");
        controlCard.updateAll();
        initCardInfo();

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