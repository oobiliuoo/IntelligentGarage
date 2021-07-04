package com.oobiliuoo.intelligentgarageapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oobiliuoo.intelligentgarageapp.bean.HostLocation;

import java.util.List;

public class HostLocationListViewAdapter  extends ArrayAdapter<HostLocation> {


    private int resourceId;
    public HostLocationListViewAdapter(@NonNull Context context, int resource, @NonNull List<HostLocation> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    class ViewHolder{

    }

}
