package com.nama_gatsuo.dreamplan;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nama_gatsuo.dreamplan.model.SubTask;
import com.nama_gatsuo.dreamplan.model.Task;

import java.util.ArrayList;
import java.util.List;

public class GroupListAdapter extends BaseAdapter {
    private Context context;
    private List<Task> group;
    private ArrayList<List<SubTask>> children;
    private int resource;

    public GroupListAdapter(Context context, List<Task> group, ArrayList<List<SubTask>> children, int resource) {
        this.context = context;
        this.group = group;
        this.children = children;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return group.size();
    }

    @Override
    public Task getItem(int position) {
        return group.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getTaskID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Activity activity = (Activity)context;
        LinearLayout v = (LinearLayout) activity.getLayoutInflater().inflate(resource, null);

        // タスク名をセット
        TextView task_name = (TextView) v.findViewById(R.id.div_task_name);
        task_name.setText(getItem(position).getName());

        // リストを表示
        ListView child_list = (ListView) v.findViewById(R.id.div_child_list);
        ChildListAdapter cla = new ChildListAdapter(context, children.get(position), R.layout.division_child);
        child_list.setAdapter(cla);

        return v;
    }
}
