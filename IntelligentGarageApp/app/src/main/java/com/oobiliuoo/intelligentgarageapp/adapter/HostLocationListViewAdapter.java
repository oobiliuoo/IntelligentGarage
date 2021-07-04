package com.oobiliuoo.intelligentgarageapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oobiliuoo.intelligentgarageapp.R;
import com.oobiliuoo.intelligentgarageapp.bean.HostLocation;
import com.oobiliuoo.intelligentgarageapp.utils.MyUtils;

import java.util.List;

public class HostLocationListViewAdapter  extends ArrayAdapter<HostLocation> {


    HostListViewOnClickListener listener;
    private int resourceId;
    public HostLocationListViewAdapter(@NonNull Context context, int resource, @NonNull List<HostLocation> objects, HostListViewOnClickListener listener) {
        super(context, resource, objects);
        resourceId = resource;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        HostLocation hostLocation = getItem(position);

        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvLocation = view.findViewById(R.id.dlli_tv_location_name);
            viewHolder.btnCancel = view.findViewById(R.id.dlli_btn_cancel);
            viewHolder.btnSave = view.findViewById(R.id.dlli_btn_save);
            viewHolder.etLocation = view.findViewById(R.id.dlli_et_location_name);
            viewHolder.etIp = view.findViewById(R.id.dlli_et_location_ip);
            viewHolder.etPort = view.findViewById(R.id.dlli_et_location_port);
            viewHolder.llInfo = view.findViewById(R.id.dlli_ll_info);
            viewHolder.llTitle = view.findViewById(R.id.dlli_ll_title);
            viewHolder.ibtnSelect = view.findViewById(R.id.dlli_ibtn_1);
            viewHolder.ibtnRemove = view.findViewById(R.id.dlli_ibtn_remove);
            viewHolder.ibtnEdit = view.findViewById(R.id.dlli_ibtn_edit);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tvLocation.setText(hostLocation.getLocationName());

        viewHolder.btnCancel.setVisibility(View.GONE);
        viewHolder.btnSave.setVisibility(View.GONE);
        viewHolder.llInfo.setVisibility(View.GONE);

        viewHolder.etLocation.setText(hostLocation.getLocationName());
        viewHolder.etLocation.setEnabled(false);
        viewHolder.etIp.setText(hostLocation.getIpAddress());
        viewHolder.etIp.setEnabled(false);
        viewHolder.etPort.setText(hostLocation.getPort());
        viewHolder.etPort.setEnabled(false);

        viewHolder.llTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.etLocation.setEnabled(false);
                viewHolder.etIp.setEnabled(false);
                viewHolder.etPort.setEnabled(false);

                // 列表被展开
                if(viewHolder.isTitleClick){
                    MyUtils.mLog1("test","title:" + viewHolder.isTitleClick);
                    viewHolder.llInfo.setVisibility(View.GONE);
                    viewHolder.ibtnEdit.setVisibility(View.GONE);
                    viewHolder.ibtnSelect.setVisibility(View.VISIBLE);
                    viewHolder.isTitleClick = false;
                }else{
                    MyUtils.mLog1("test","title:" + viewHolder.isTitleClick);
                    viewHolder.llInfo.setVisibility(View.VISIBLE);
                    if(!viewHolder.isDelete){
                        MyUtils.mLog1("test","isdelete:" + viewHolder.isTitleClick);
                        viewHolder.ibtnSelect.setVisibility(View.GONE);
                        viewHolder.ibtnEdit.setVisibility(View.VISIBLE);
                    }else {
                        viewHolder.ibtnEdit.setVisibility(View.GONE);
                    }
                    viewHolder.isTitleClick = true;

                }

                MyUtils.mLog1("HLVA: touch title");
            }

        });
        viewHolder.llTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                viewHolder.ibtnEdit.setVisibility(View.GONE);
                viewHolder.ibtnRemove.setVisibility(View.VISIBLE);
                viewHolder.isDelete = true;
                MyUtils.mLog1("HLVA: long touch title");
                return false;
            }

        });

        viewHolder.ibtnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.isDelete){
                   viewHolder.ibtnRemove.setVisibility(View.GONE);
                   viewHolder.isDelete = false;
                }else {
                    listener.HostListOnClick(v);
                }
            }
        });

        viewHolder.etLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.etLocation.setEnabled(true);
            }
        });

        viewHolder.ibtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.etLocation.setEnabled(true);
                viewHolder.etIp.setEnabled(true);
                viewHolder.etPort.setEnabled(true);
            }
        });


        return view;
    }

    class ViewHolder{
        boolean isTitleClick = false;
        boolean isDelete = false;
        TextView tvLocation ;
        Button btnCancel;
        Button btnSave;
        EditText etLocation;
        EditText etIp;
        EditText etPort;
        LinearLayout llInfo;
        LinearLayout llTitle;
        ImageButton ibtnSelect;
        ImageButton ibtnRemove;
        ImageButton ibtnEdit;

    }

    /**通过接口回调，使得在活动中控制监听事件*/
    public interface HostListViewOnClickListener{
        public void HostListOnClick(View v);
        public void HostListOnLongClick(View v);

    }

}
