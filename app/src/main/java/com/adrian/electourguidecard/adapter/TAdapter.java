package com.adrian.electourguidecard.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by adrian on 16-5-31.
 */
public class TAdapter<T> extends BaseAdapter {

    private List<T> list;
    protected Context ctx;

    public TAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    public void addItem(T t) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(t);
        notifyDataSetChanged();
    }

    public void addList(List<T> l) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.addAll(l);
        notifyDataSetChanged();
    }

    public void setList(List<T> l) {
        list = l;
        notifyDataSetChanged();
    }

    public void delItem(T t) {
        if (list != null && list.size() > 0) {
            list.remove(t);
            notifyDataSetChanged();
        }
    }

    public void delList(List<T> l) {
        if (list != null && list.size() > 0) {
            list.removeAll(l);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        if (list != null) {
            list.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
