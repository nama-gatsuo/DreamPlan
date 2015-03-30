package com.nama_gatsuo.dreamplan;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inqbarna.tablefixheaders.adapters.BaseTableAdapter;
import com.nama_gatsuo.dreamplan.View.BarView;
import com.nama_gatsuo.dreamplan.View.CalendarView;
import com.nama_gatsuo.dreamplan.View.ScaleView;
import com.nama_gatsuo.dreamplan.dao.SubTaskDao;
import com.nama_gatsuo.dreamplan.dao.TaskDao;
import com.nama_gatsuo.dreamplan.model.Project;
import com.nama_gatsuo.dreamplan.model.SubTask;
import com.nama_gatsuo.dreamplan.model.Task;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by nagamatsuayumu on 15/03/27.
 */
public class GanttTableAdapter extends BaseTableAdapter {
    private float scale;
    private ShowProjectActivity activity;
    private Project project;
    private SQLiteDatabase db;
    private TaskDao taskDao;
    private SubTaskDao subTaskDao;

    private DateTime minDate;
    private DateTime maxDate;
    private int nx = 0;
    private int dx = 16;

    private List<Task> groups = null;
    private ArrayList<List<SubTask>> children = null;
    private int ny = 0;
    private int dy = 30;




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

    public GanttTableAdapter(Context context) {
        activity = (ShowProjectActivity)context;
        project = activity.getProject();
        scale = context.getResources().getDisplayMetrics().density;
        dx = (int)(dx * scale);
        dy = (int)(dy * scale);

        // Database接続
        DatabaseHelper dbHelper = new DatabaseHelper(activity);
        db = dbHelper.getWritableDatabase();
        taskDao = new TaskDao(db);
        subTaskDao = new SubTaskDao(db);

        // Listを準備
        groups = taskDao.findByProjectID(activity.getProject().getProjectID());
        children = new ArrayList<List<SubTask>>();
        for (Task task : groups) {
            List<SubTask> slist = subTaskDao.findByTaskID(task.getTaskID());
            children.add(slist);
        }

        // Listの個数(ny)を求める
        int taskCnt = groups.size();
        int subtaskCnt = 0;
        for (List<SubTask> subTasks : children) {
            subtaskCnt = subTasks.size() + subtaskCnt;
        }
        ny = taskCnt + subtaskCnt;

        // 表示する日付の決定
        DateTime pjsd = new DateTime().withMillis(project.getStartDate());
        int s = pjsd.getDayOfWeek() - 1;
        if (s == 0) {
            minDate = pjsd;
        } else {
            minDate = pjsd.minusDays(s);
        }

        DateTime pjed  = new DateTime().withMillis(project.getEndDate());
        int e = 7 - pjed.getDayOfWeek();
        if (e == 0) {
            maxDate = pjed;
        } else {
            maxDate = pjed.plusDays(e);
        }

        // 日付の個数(nx)を求める
        Duration d = new Duration(minDate, maxDate);
        nx = (int)d.getStandardDays() + 1;

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
                view = getProjectView(convertView);
                break;
            case 1:
                view = getCalendarView(convertView);
                break;
            case 2:
                view = getListView(convertView);
                break;
            case 3:
                view = getGanttView(convertView);
                break;
            default:
                throw new RuntimeException("wtf?");
        }
        return view;
    }

    private View getProjectView(View convertView) {
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_table_pj, null);
        }
        ((TextView) convertView.findViewById(R.id.project_name)).setText(activity.getProject().getName());
        return convertView;
    }

    private View getCalendarView(View convertView) {
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_table_cal, null);
        }
        CalendarView cv = ((CalendarView) convertView.findViewById(R.id.calendar));
        cv.setRange(minDate, maxDate);
        cv.setXAxis(nx, dx);

        return convertView;
    }

    private View getListView(View convertView) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_table_list, null);

            LinearLayout list = (LinearLayout) convertView.findViewById(R.id.group_list);

            holder = new ViewHolder();
            holder.addView(list);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        // リスト部分の表示
        LinearLayout list = (LinearLayout) holder.getView(R.id.group_list);
        list.removeAllViews();

        for (int i = 0; i < groups.size(); i++) {
            Task _task = groups.get(i);

            TextView task = (TextView)LayoutInflater.from(activity).inflate(R.layout.division_group, null);
            task.setText(_task.getName());

            list.addView(task);

            List<SubTask> _children = children.get(i);

            for (int j = 0; j < _children.size(); j++) {

                TextView subTask = (TextView)LayoutInflater.from(activity).inflate(R.layout.division_child, null);
                subTask.setText(_children.get(j).getName());

                list.addView(subTask);
            }
        }
        return convertView;
    }

    private View getGanttView(View convertView) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_table_gantt, null);

            ScaleView scale = (ScaleView) convertView.findViewById(R.id.scale);
            LinearLayout gantt = (LinearLayout) convertView.findViewById(R.id.group_bars);

            holder = new ViewHolder();
            holder.addView(scale);
            holder.addView(gantt);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        // Display scale as background
        ScaleView scale = (ScaleView) holder.getView(R.id.scale);
        scale.setRange(minDate, maxDate);
        scale.setXAxis(nx, dx);

        // Display Gantt Graph
        LinearLayout bars = (LinearLayout) holder.getView(R.id.group_bars);
        bars.removeAllViews();

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dy);

        for (int i = 0; i < groups.size(); i++) {
            Task _task = groups.get(i);
            BarView task_bar = new BarView(activity);
            task_bar.setMinimumHeight(dy);
            task_bar.setRange(minDate, maxDate);
            task_bar.setDimen(dx, dy);
            task_bar.setTask(_task);

            bars.addView(task_bar, lp);

            List<SubTask> _children = children.get(i);

            for (int j = 0; j < _children.size(); j++) {
                Task _subtask = _children.get(j);

                BarView subtask_bar = new BarView(activity);
                subtask_bar.setMinimumHeight(dy);
                subtask_bar.setRange(minDate, maxDate);
                subtask_bar.setDimen(dx, dy);
                subtask_bar.setTask(_subtask);

                bars.addView(subtask_bar, lp);
            }
        }
        return convertView;
    }


    @Override
    public int getWidth(int column) {
        int width;
        switch (column) {
            case -1:
                width = Math.round(160 * scale);
                break;
            case 0:
                width = nx * dx;
                break;
            default:
                throw new RuntimeException("wtf?");
        }
        return width;
    }

    @Override
    public int getHeight(int row) {
        int height;
        switch (row) {
            case -1:
                height = Math.round(32 * scale);
                break;
            case 0:
                int listHeight = Math.round((ny * dy + (10 * scale)));

                WindowManager wm = activity.getWindowManager();
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                // Display size minus proj
                int dispHeight = size.y - Math.round(32 * scale);

                if (dispHeight > listHeight) {
                    height = dispHeight;
                } else {
                    height = listHeight;
                }
                break;
            default:
                throw new RuntimeException("wtf?");
        }
        return height;
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
