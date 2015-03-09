package com.nama_gatsuo.dreamplan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nama_gatsuo.dreamplan.model.SubTask;
import com.nama_gatsuo.dreamplan.model.Task;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

/**
 * Created by nagamatsuayumu on 15/03/08.
 */
public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private List<Task> groups;
    private List<List<SubTask>> children;
    private Context context = null;

    // Constructor
    public MyExpandableListAdapter (Context context, List<Task> groups, List<List<SubTask>> children) {
        this.context = context;
        this.groups = groups;
        this.children = children;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return children.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (children.isEmpty()) {
            return null;
        }
        return children.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        View groupView = LayoutInflater.from(context).inflate(R.layout.task_line_item, null);
        Task task = groups.get(groupPosition);

        TextView task_name = (TextView) groupView.findViewById(R.id.task_name);
        task_name.setText(task.getName());

        TextView start_date = (TextView) groupView.findViewById(R.id.task_startDate);
        DateTime sdt = new DateTime().withMillis(task.getStartDate());
        start_date.setText(sdt.toString(DateTimeFormat.longDate()));

        TextView end_date = (TextView) groupView.findViewById(R.id.task_endDate);
        DateTime edt = new DateTime().withMillis(task.getStartDate());
        end_date.setText(edt.toString(DateTimeFormat.longDate()));

        // Statusの表示をおこなう

        return groupView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (childPosition == getChildrenCount(groupPosition) - 1) {
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
            AbsListView.LayoutParams lp2 = new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            LinearLayout ll = new LinearLayout(context);
            ll.setLayoutParams(lp);

            Button button = new Button(context);
            button.setText("追加");
            button.setLayoutParams(lp2);
            ll.addView(button);
            return ll;
        } else {
            View childView = LayoutInflater.from(context).inflate(R.layout.subtask_line_item, null);
            SubTask subTask = children.get(groupPosition).get(childPosition);

            TextView task_name = (TextView) childView.findViewById(R.id.task_name);
            task_name.setText(subTask.getName());

            TextView start_date = (TextView) childView.findViewById(R.id.subTask_startDate);
            DateTime sdt = new DateTime().withMillis(subTask.getStartDate());
            start_date.setText(sdt.toString(DateTimeFormat.longDate()));

            TextView end_date = (TextView) childView.findViewById(R.id.subTask_endDate);
            DateTime edt = new DateTime().withMillis(subTask.getStartDate());
            end_date.setText(edt.toString(DateTimeFormat.longDate()));

            // Statusの表示をおこなう

            return childView;
        }

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
