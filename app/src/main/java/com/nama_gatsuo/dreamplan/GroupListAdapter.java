package com.nama_gatsuo.dreamplan;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nama_gatsuo.dreamplan.model.SubTask;
import com.nama_gatsuo.dreamplan.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupListAdapter extends BaseAdapter {
    private Context context;
    private List<Task> group;
    private ArrayList<List<SubTask>> children;
    private int resource;


    private class ViewHolder {
        private HashMap<Integer, View> storedViews = new HashMap<Integer, View>();

        public ViewHolder addView(View view) {
            int id = view.getId();
            storedViews.put(id, view);
            return this;
        }
        public View getView(int id) {
            return storedViews.get(id);
        }
    }

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
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = activity.getLayoutInflater().inflate(resource, null);

            TextView task_name = (TextView)convertView.findViewById(R.id.div_task_name);
            LinearLayout child_list = (LinearLayout)convertView.findViewById(R.id.div_child_list);

            holder = new ViewHolder();
            holder.addView(task_name);
            holder.addView(child_list);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        // タスク名をセット
        TextView task_name = (TextView)holder.getView(R.id.div_task_name);
        task_name.setText(getItem(position).getName());

        // リストを表示
        LinearLayout child_list = (LinearLayout)holder.getView(R.id.div_child_list);
        child_list.removeAllViews();

        for (SubTask subTask : children.get(position)) {
            View child = activity.getLayoutInflater().inflate(R.layout.division_child, null);

            TextView subTask_name = (TextView)child.findViewById(R.id.div_subtask_name);
            subTask_name.setText(subTask.getName());

            child_list.addView(child);
        }
        return convertView;
    }
}
