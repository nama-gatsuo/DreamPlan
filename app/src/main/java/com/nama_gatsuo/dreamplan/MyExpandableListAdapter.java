package com.nama_gatsuo.dreamplan;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.nama_gatsuo.dreamplan.View.StatusView;
import com.nama_gatsuo.dreamplan.dao.SubTaskDao;
import com.nama_gatsuo.dreamplan.model.SubTask;
import com.nama_gatsuo.dreamplan.model.Task;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.HashMap;
import java.util.List;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private List<Task> groups;
    private List<List<SubTask>> children;
    private Context context = null;

    private class ViewHolder {
        private HashMap<Integer, View> storedViews = new HashMap<Integer, View>();
        Animation anim;

        ViewHolder(Context context) {
            if (anim == null) {
                anim = AnimationUtils.loadAnimation(context, R.anim.item_motion);
            }
        }

        public ViewHolder addView(View view) {
            int id = view.getId();
            storedViews.put(id, view);
            return this;
        }

        public View getView(int id) {
            return storedViews.get(id);
        }
    }

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
    public Task getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public SubTask getChild(int groupPosition, int childPosition) {
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

        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.task_line_item, null);

            TextView task_name = (TextView) convertView.findViewById(R.id.task_name);
            TextView start_date = (TextView) convertView.findViewById(R.id.task_startDate);
            TextView end_date = (TextView) convertView.findViewById(R.id.task_endDate);
            StatusView task_status = (StatusView) convertView.findViewById(R.id.task_status);

            holder = new ViewHolder(context);
            holder.addView(task_name);
            holder.addView(start_date);
            holder.addView(end_date);
            holder.addView(task_status);

            convertView.setTag(holder);
        } else {
            // ViewHolderからID参照
            holder = (ViewHolder) convertView.getTag();
        }

        final Task task = groups.get(groupPosition);

        // 名前のセット
        TextView task_name = (TextView)holder.getView(R.id.task_name);
        task_name.setText(task.getName());
        task_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Task編集
                Intent i = new Intent(v.getContext(), TaskEditActivity.class);

                i.putExtra("Task", task);
                v.getContext().startActivity(i);
            }
        });

        // 開始日のセット
        TextView start_date = (TextView)holder.getView(R.id.task_startDate);
        DateTime sdt = new DateTime().withMillis(task.getStartDate());
        start_date.setText(sdt.toString(DateTimeFormat.forPattern("yyyy/MM/dd")));

        // 終了日のセット
        TextView end_date = (TextView)holder.getView(R.id.task_endDate);
        DateTime edt = new DateTime().withMillis(task.getEndDate());
        end_date.setText(edt.toString(DateTimeFormat.forPattern("yyyy/MM/dd")));

        // ステータスのセット
        StatusView sv = (StatusView)holder.getView(R.id.task_status);
        sv.setStatus(task.getStatus());

        // アニメーションの開始
        // convertView.startAnimation(holder.anim);

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {

        // Group内の最後の子要素に対してはボタンの行とする
        if (isLastChild) {
            View lastChild = LayoutInflater.from(context).inflate(R.layout.subtask_line_item_add, null);

            Button button = (Button) lastChild.findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // SubTask作成
                    Intent i = new Intent(v.getContext(), SubtaskEditActivity.class);

                    // Database接続
                    DatabaseHelper dbHelper = new DatabaseHelper(v.getContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    SubTaskDao subTaskDao = new SubTaskDao(db);

                    SubTask subTask = new SubTask();

                    // SubTaskがあれば最大のTaskIDに1を足す
                    if (!subTaskDao.exists()) {
                        subTask.setSubTaskID(1);;
                    } else {
                        subTask.setSubTaskID(subTaskDao.getLastID() + 1);
                    }

                    subTask.setTaskID(getGroup(groupPosition).getTaskID());
                    subTask.setProjectID(getGroup(groupPosition).getProjectID());

                    i.putExtra("SubTask", subTask);

                    v.getContext().startActivity(i);
                }
            });

            return lastChild;
        } else {
            ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.subtask_line_item, null);

                TextView subTask_name = (TextView) convertView.findViewById(R.id.subTask_name);
                TextView start_date = (TextView) convertView.findViewById(R.id.subTask_startDate);
                TextView end_date = (TextView) convertView.findViewById(R.id.subTask_endDate);
                StatusView subTask_status = (StatusView) convertView.findViewById(R.id.subTask_status);

                holder = new ViewHolder(context);
                holder.addView(subTask_name);
                holder.addView(start_date);
                holder.addView(end_date);
                holder.addView(subTask_status);

                convertView.setTag(holder);
            } else {
                // ViewHolderからID参照
                holder = (ViewHolder) convertView.getTag();
            }

            final SubTask subTask = children.get(groupPosition).get(childPosition);

            // 名前のセット
            TextView subtask_name = (TextView)holder.getView(R.id.subTask_name);
            subtask_name.setText(subTask.getName());
            subtask_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // SubTask編集
                    Intent i = new Intent(v.getContext(), SubtaskEditActivity.class);

                    i.putExtra("SubTask", subTask);
                    v.getContext().startActivity(i);
                }
            });

            // 開始日のセット
            TextView start_date = (TextView)holder.getView(R.id.subTask_startDate);
            DateTime sdt = new DateTime().withMillis(subTask.getStartDate());
            start_date.setText(sdt.toString(DateTimeFormat.forPattern("yyyy/MM/dd")));

            // 終了日のセット
            TextView end_date = (TextView)holder.getView(R.id.subTask_endDate);
            DateTime edt = new DateTime().withMillis(subTask.getEndDate());
            end_date.setText(edt.toString(DateTimeFormat.forPattern("yyyy/MM/dd")));

            // ステータスのセット
            StatusView sv = (StatusView)holder.getView(R.id.subTask_status);
            sv.invalidate();
            sv.setStatus(subTask.getStatus());

            // アニメーションの開始
            // convertView.startAnimation(holder.anim);

            return convertView;
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
