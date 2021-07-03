package com.oobiliuoo.intelligentgarageapp;

import android.content.Intent;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageButton;

import com.oobiliuoo.intelligentgarageapp.adapter.MyGridViewAdapter;
import com.oobiliuoo.intelligentgarageapp.bean.ControlCard;
import com.oobiliuoo.intelligentgarageapp.utils.MyUtils;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author biliu
 */
public class MainActivity extends BaseActivity {

    private GridView gridView;
    private List<ControlCard> controlCardList = new ArrayList<>();
    private ImageButton btnSet;



    @Override
    public void init() {
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.Snow2));
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCardInfo();
    }

    @Override
    protected void initHandleMessage() {
        switch (MESSAGE.what){
            case MyUtils.CONNECT_SUCCESS:
                MyUtils.showToast(MainActivity.this,"连接成功");
                doOnConnectSuccess();
                break;
            case MyUtils.CONNECT_FAIL:
                MyUtils.showToast(MainActivity.this,"连接失败");
                doOnConnectFail();
                break;
            default:
        }
    }

    @Override
    public void initClick() {
        switch (CLICK_ID){
            case R.id.main_btn_set:
                Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(intent);
                break;
            default:
        }
    }

    private void initView() {

        btnSet = (ImageButton) findViewById(R.id.main_btn_set);
        btnSet.setOnClickListener(new MyLister());
        //initCardInfo();
        //MyGridViewAdapter adapter = new MyGridViewAdapter(MainActivity.this,R.layout.layout_it_contral,controlCardList);
        gridView = findViewById(R.id.main_gridview);
        //gridView.deferNotifyDataSetChanged();
        //gridView.setAdapter(adapter);


    }

    private void initCardInfo() {
        controlCardList.clear();

        List<ControlCard> list = LitePal.findAll(ControlCard.class);
        if(list.size() > 0){
            MyUtils.mLog1("表建立成功");
            controlCardList = list;
        }else {
            controlCardList = null;
        }

        MyGridViewAdapter adapter = new MyGridViewAdapter(MainActivity.this,R.layout.layout_it_contral,controlCardList);
        gridView.deferNotifyDataSetChanged();
        gridView.setAdapter(adapter);
    }

    private void doOnConnectSuccess(){
        ControlCard controlCard = new ControlCard();
        controlCard.setEnable("1");
        controlCard.setState("OFF");
        controlCard.updateAll();
        initCardInfo();
    }

    private void doOnConnectFail(){

        ControlCard controlCard = new ControlCard();
        controlCard.setEnable("0");
        controlCard.setState("OFF");
        controlCard.updateAll();

        initCardInfo();

    }

}