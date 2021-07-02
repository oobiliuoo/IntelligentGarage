package com.oobiliuoo.intelligentgarageapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.GridView;

import com.oobiliuoo.intelligentgarageapp.adapter.MyGridViewAdapter;
import com.oobiliuoo.intelligentgarageapp.bean.ControlCard;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;
    private List<ControlCard> controlCardList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.Snow2));


        initView();

    }

    private void initView() {
        initCardInfo();
        MyGridViewAdapter adapter = new MyGridViewAdapter(MainActivity.this,R.layout.layout_it_contral,controlCardList);

        gridView = findViewById(R.id.main_gridview);
        gridView.deferNotifyDataSetChanged();
        gridView.setAdapter(adapter);


    }

    private void initCardInfo() {
        controlCardList.clear();
        ControlCard card1 = new ControlCard("大厅","吊灯","ON",R.drawable.light_on);
        controlCardList.add(card1);
        ControlCard card2 = new ControlCard("室外","大灯","OFF",R.drawable.light_off);
        controlCardList.add(card2);
        ControlCard card3 = new ControlCard("大厅","门","ON",R.drawable.door_open2);
        controlCardList.add(card3);
        ControlCard card4 = new ControlCard("车库","门","OFF",R.drawable.door_closed);
        controlCardList.add(card4);
    }
}