package com.oobiliuoo.intelligentgarageapp.adapter;

import android.content.Context;
import android.sax.TextElementListener;
import android.view.LayoutInflater;
import android.view.VerifiedInputEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oobiliuoo.intelligentgarageapp.R;
import com.oobiliuoo.intelligentgarageapp.bean.MyMessage;
import com.oobiliuoo.intelligentgarageapp.bean.NotifyInfo;
import com.oobiliuoo.intelligentgarageapp.utils.MyUtils;

import java.util.List;

public class NotifyListViewAdapter  extends ArrayAdapter<NotifyInfo> {


    private int resourceId;

    public NotifyListViewAdapter(@NonNull Context context, int resource, @NonNull List<NotifyInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        NotifyInfo notifyInfo = getItem(position);

        View view;
        ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.llMax = view.findViewById(R.id.lln_ll_max);
            viewHolder.tvTime = view.findViewById(R.id.lln_tv_time);
            viewHolder.tvTitle = view.findViewById(R.id.lln_tv_title);
            viewHolder.tvContext = view.findViewById(R.id.lln_tv_context);
            viewHolder.ibtVideo = view.findViewById(R.id.lln_ibtn_video);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.llMax.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        viewHolder.tvTime.setText(notifyInfo.getTime());
        viewHolder.tvTitle.setText(MyMessage.NOTIFY_MODE_MAP.get(notifyInfo.getMode()));
        viewHolder.tvContext.setText(notifyInfo.getText());

        return view;
    }

    class ViewHolder{
        LinearLayout llMax;
        TextView tvTime;
        TextView tvTitle;
        TextView tvContext;
        ImageButton ibtVideo;

    }
}
