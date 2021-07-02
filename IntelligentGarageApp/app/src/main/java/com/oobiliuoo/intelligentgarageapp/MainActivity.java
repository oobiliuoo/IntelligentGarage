package com.oobiliuoo.intelligentgarageapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
        ControlCard card2 = new ControlCard("s","吊灯","OFF",R.drawable.light_on);
        controlCardList.add(card2);
    }
}