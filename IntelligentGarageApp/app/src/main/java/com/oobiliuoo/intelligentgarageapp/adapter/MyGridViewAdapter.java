package com.oobiliuoo.intelligentgarageapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oobiliuoo.intelligentgarageapp.BaseActivity;
import com.oobiliuoo.intelligentgarageapp.R;
import com.oobiliuoo.intelligentgarageapp.bean.ControlCard;
import com.oobiliuoo.intelligentgarageapp.bean.MyMessage;
import com.oobiliuoo.intelligentgarageapp.utils.MyUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author biliu
 */
public class MyGridViewAdapter extends ArrayAdapter<ControlCard> {

    private int resourceId;
    MyOnClickListener clickListener;


    public MyGridViewAdapter(@NonNull Context context, int resource, @NonNull List<ControlCard> objects,MyOnClickListener myOnClickListener) {
        super(context, resource, objects);
        resourceId = resource;
        clickListener = myOnClickListener;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ControlCard controlCard = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivImg = view.findViewById(R.id.control_iv_img);
            viewHolder.tvLoc = view.findViewById(R.id.control_tv_loc);
            viewHolder.tvType = view.findViewById(R.id.control_tv_type);
            viewHolder.tvStates = view.findViewById(R.id.control_tv_states);
            viewHolder.aSwitch = view.findViewById(R.id.control_switch);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.ivImg.setImageResource(controlCard.getImg());
        viewHolder.tvLoc.setText(controlCard.getLocation());
        viewHolder.tvType.setText(controlCard.getType());
        viewHolder.tvStates.setText(controlCard.getState());

        choseImg(controlCard);
        viewHolder.ivImg.setImageResource(controlCard.getImg());
        viewHolder.aSwitch.setChecked("ON".equals(controlCard.getState()));
        viewHolder.aSwitch.setEnabled("1".equals(controlCard.getEnable()));


        viewHolder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(clickListener.MyCheckChanged(controlCard,isChecked)){
                    controlCard.setState("ON");
                }else{
                    controlCard.setState("OFF");
                }

                viewHolder.tvStates.setText(controlCard.getState());
                choseImg(controlCard);
                viewHolder.ivImg.setImageResource(controlCard.getImg());
                controlCard.save();

            }
        });



        return view;
    }

    /**选择合适图片，图片数据保存在MyUtils的IMG——MAP中*/
    private void choseImg(ControlCard card) {
        int i[] = MyUtils.IMG_MAP.get(card.getType());
        if("ON".equals(card.getState())){
            card.setImg(i[1]);
        }else {
            card.setImg(i[0]);
        }

    }


    class ViewHolder {
        ImageView ivImg;
        TextView tvLoc;
        TextView tvType;
        TextView tvStates;
        Switch aSwitch;
    }

    /**通过接口回调，使得在活动中控制监听事件*/
    public interface MyOnClickListener{
        public boolean MyCheckChanged(ControlCard card, boolean isChecked);
    }
}
