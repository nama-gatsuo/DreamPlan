package com.nama_gatsuo.dreamplan;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.inqbarna.tablefixheaders.adapters.BaseTableAdapter;
import com.nama_gatsuo.dreamplan.dao.SubTaskDao;
import com.nama_gatsuo.dreamplan.dao.TaskDao;
import com.nama_gatsuo.dreamplan.model.SubTask;
import com.nama_gatsuo.dreamplan.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nagamatsuayumu on 15/03/27.
 */
public class GanttTableAdapter extends BaseTableAdapter {
    private float scale;
    private ShowProjectActivity activity;
    private SQLiteDatabase db;
    private List<Task> groups = null;
    private ArrayList<List<SubTask>> children = null;

    public GanttTableAdapter(Context context) {
        this.activity = (ShowProjectActivity)context;
        scale = context.getResources().getDisplayMetrics().density;
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public View getView(int row, int column, View convertView, ViewGroup parent) {
        final View view;
        switch (getItemViewType(row, column)) {
            case 0:
                view = getProjectView(row, column, convertView, parent);
                break;
            case 1:
                view = getCalendarView(row, column, convertView, parent);
                break;
            case 2:
                view = getListView(row, column, convertView, parent);
                break;
            case 3:
                view = getGanttView(row, column, convertView, parent);
                break;
            default:
                throw new RuntimeException("wtf?");
        }
        return view;
    }

    private View getProjectView(int row, int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_table_pj, null);
        }
        ((TextView) convertView.findViewById(R.id.project_name)).setText(activity.getProject().getName());
        return convertView;
    }

    private View getCalendarView(int row, int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_table_cal, null);
        }
        String test = "test";
        ((TextView) convertView.findViewById(R.id.calendar)).setText(test);
        return convertView;
    }

    private View getListView(int row, int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_table_list, null);
        }

        // Database接続
        DatabaseHelper dbHelper = new DatabaseHelper(activity);
        db = dbHelper.getWritableDatabase();
        TaskDao taskDao = new TaskDao(db);
        SubTaskDao subTaskDao = new SubTaskDao(db);

        // アダプターに渡すためのListを準備
        groups = taskDao.findByProjectID(activity.getProject().getProjectID());
        children = new ArrayList<List<SubTask>>();
        for (Task task : groups) {
            List<SubTask> slist = subTaskDao.findByTaskID(task.getTaskID());
            children.add(slist);
        }

        // リスト部分の表示
        ListView group_list = (ListView) convertView.findViewById(R.id.group_list);
        GroupListAdapter gla = new GroupListAdapter(activity, groups, children, R.layout.division_group);
        group_list.setAdapter(gla);
        return convertView;
    }

    private View getGanttView(int row, int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_table_gantt, null);
        }
        String test = "test";
        ((TextView) convertView.findViewById(R.id.gantt)).setText(test);
        return convertView;
    }


    @Override
    public int getWidth(int column) {
        if (column == -1) {
            int width = 160;
            return Math.round(width * scale);
        } else if (column == 0) {
            int width = 1000;
            return Math.round(width * scale);
        } else {
            throw new RuntimeException("wtf?");
        }
    }

    @Override
    public int getHeight(int row) {
        if (row == -1) {
            int height = 32;
            return Math.round(height * scale);
        } else if (row == 0) {
            int height = 1000;
            return Math.round(height * scale);
        } else {
            throw new RuntimeException("wtf?");
        }
    }

    @Override
    public int getItemViewType(int row, int column) {
        final int itemViewType;
        if (row == -1 && column == -1) {
            itemViewType = 0;
        } else if (row == -1 && column == 0) {
            itemViewType = 1;
        } else if (row == 0 && column == -1) {
            itemViewType = 2;
        } else if (row == 0 && column == 0) {
            itemViewType = 3;
        } else {
            throw new RuntimeException("wtf?");
        }
        return itemViewType;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }
}
