package com.adrian.electourguidecard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adrian.electourguidecard.R;

/**
 * Created by adrian on 16-5-31.
 */
public class IdsAdapter extends TAdapter {

    public IdsAdapter(Context ctx) {
        super(ctx);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(ctx).inflate(R.layout.layout_id_item, null, false);
            holder = new ViewHolder();
            holder.mIdTV = (TextView) convertView.findViewById(R.id.tv_id);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String item = (String) getItem(position);
        holder.mIdTV.setText(item);
        return convertView;
    }

    class ViewHolder {
        TextView mIdTV;
    }
}
