package com.nama_gatsuo.dreamplan;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.nama_gatsuo.dreamplan.dao.SubTaskDao;
import com.nama_gatsuo.dreamplan.dao.TaskDao;
import com.nama_gatsuo.dreamplan.model.SubTask;
import com.nama_gatsuo.dreamplan.model.Task;

import java.util.ArrayList;
import java.util.List;


public class TaskGanttFragment extends Fragment {

    private SQLiteDatabase db;
    private ShowProjectActivity parent;
    private List<Task> groups = null;
    private ArrayList<List<SubTask>> children = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_gantt, container, false);

        // Database接続
        DatabaseHelper dbHelper = new DatabaseHelper(parent);
        db = dbHelper.getWritableDatabase();
        TaskDao taskDao = new TaskDao(db);
        SubTaskDao subTaskDao = new SubTaskDao(db);

        // プロジェクト名をセット
        TextView pjname = (TextView) view.findViewById(R.id.div_project_name);
        pjname.setText(parent.getProject().getName());

        // アダプターに渡すためのListを準備
        groups = taskDao.findByProjectID(parent.getProject().getProjectID());
        children = new ArrayList<List<SubTask>>();
        for (Task task : groups) {
            List<SubTask> slist = subTaskDao.findByTaskID(task.getTaskID());
            children.add(slist);
        }

        // リスト部分の表示
        ListView group_list = (ListView) view.findViewById(R.id.group_list);
        GroupListAdapter gla = new GroupListAdapter(parent, groups, children, R.layout.division_group);
        group_list.setAdapter(gla);
        group_list.setDivider(null);

        // グラフ初期化

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        // 親を変数として格納
        parent = (ShowProjectActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onPause() {
        super.onPause();
        db.close();
    }
}
