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

import com.oobiliuoo.intelligentgarageapp.R;
import com.oobiliuoo.intelligentgarageapp.bean.ControlCard;

import java.util.List;

/**
 * @author biliu
 */
public class MyGridViewAdapter extends ArrayAdapter<ControlCard> {

    private int resourceId;

    public MyGridViewAdapter(@NonNull Context context, int resource, @NonNull List<ControlCard> objects) {
        super(context, resource, objects);
        resourceId = resource;
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

        if ("ON".equals(controlCard.getState())) {
            viewHolder.aSwitch.setChecked(true);
            viewHolder.tvStates.setText(controlCard.getState());
            if ("吊灯".equals(controlCard.getType())) {
                controlCard.setImg(R.drawable.ceiling_lamp_on);
                viewHolder.ivImg.setImageResource(controlCard.getImg());
            }

            if ("大灯".equals(controlCard.getType())) {
                controlCard.setImg(R.drawable.light_on);
                viewHolder.ivImg.setImageResource(controlCard.getImg());
            }
            if ("门".equals(controlCard.getType())) {
                controlCard.setImg(R.drawable.door_open2);
                viewHolder.ivImg.setImageResource(controlCard.getImg());
            }
        }

        if ("OFF".equals(controlCard.getState())) {
            viewHolder.aSwitch.setChecked(false);
            viewHolder.tvStates.setText(controlCard.getState());
            if ("吊灯".equals(controlCard.getType())) {
                controlCard.setImg(R.drawable.ceiling_lamp_off);
                viewHolder.ivImg.setImageResource(controlCard.getImg());
            }
            if ("大灯".equals(controlCard.getType())) {
                controlCard.setImg(R.drawable.light_off);
                viewHolder.ivImg.setImageResource(controlCard.getImg());
            }
            if ("门".equals(controlCard.getType())) {
                controlCard.setImg(R.drawable.door_closed);
                viewHolder.ivImg.setImageResource(controlCard.getImg());
            }
        }

        viewHolder.aSwitch.setEnabled("1".equals(controlCard.getEnable()));

        viewHolder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    controlCard.setState("ON");
                    viewHolder.tvStates.setText(controlCard.getState());
                    if ("吊灯".equals(controlCard.getType())) {
                        controlCard.setImg(R.drawable.ceiling_lamp_on);
                        viewHolder.ivImg.setImageResource(controlCard.getImg());
                    }

                    if ("大灯".equals(controlCard.getType())) {
                        controlCard.setImg(R.drawable.light_on);
                        viewHolder.ivImg.setImageResource(controlCard.getImg());
                    }
                    if ("门".equals(controlCard.getType())) {
                        controlCard.setImg(R.drawable.door_open2);
                        viewHolder.ivImg.setImageResource(controlCard.getImg());
                    }

                } else {
                    controlCard.setState("OFF");
                    viewHolder.tvStates.setText(controlCard.getState());
                    if ("吊灯".equals(controlCard.getType())) {
                        controlCard.setImg(R.drawable.ceiling_lamp_off);
                        viewHolder.ivImg.setImageResource(controlCard.getImg());
                    }
                    if ("大灯".equals(controlCard.getType())) {
                        controlCard.setImg(R.drawable.light_off);
                        viewHolder.ivImg.setImageResource(controlCard.getImg());
                    }
                    if ("门".equals(controlCard.getType())) {
                        controlCard.setImg(R.drawable.door_closed);
                        viewHolder.ivImg.setImageResource(controlCard.getImg());
                    }
                }
            }
        });

        return view;
    }

    private void ON_style() {

    }


    class ViewHolder {
        ImageView ivImg;
        TextView tvLoc;
        TextView tvType;
        TextView tvStates;
        Switch aSwitch;
    }
}
