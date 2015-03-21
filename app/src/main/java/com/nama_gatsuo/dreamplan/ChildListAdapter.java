package com.nama_gatsuo.dreamplan;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nama_gatsuo.dreamplan.model.SubTask;

import java.util.List;

public class ChildListAdapter extends BaseAdapter {
    private Context context;
    private List<SubTask> children;
    private int resource;

    public ChildListAdapter(Context context, List<SubTask> children, int resource) {
        this.context = context;
        this.children = children;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return children.size();
    }

    @Override
    public SubTask getItem(int position) {
        return children.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getSubTaskID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Activity activity = (Activity)context;
        LinearLayout v = (LinearLayout) activity.getLayoutInflater().inflate(resource, null);

        // サブタスク名をセット
        TextView subtask_name = (TextView) v.findViewById(R.id.div_subtask_name);
        subtask_name.setText(getItem(position).getName());

        return v;
    }
}
