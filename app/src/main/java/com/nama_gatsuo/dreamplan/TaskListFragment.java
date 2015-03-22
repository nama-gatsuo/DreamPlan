package com.nama_gatsuo.dreamplan;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.nama_gatsuo.dreamplan.dao.SubTaskDao;
import com.nama_gatsuo.dreamplan.dao.TaskDao;
import com.nama_gatsuo.dreamplan.model.SubTask;
import com.nama_gatsuo.dreamplan.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    private SQLiteDatabase db;
    private ShowProjectActivity parent;
    private int projectID;
    private List<Task> groups = null;
    private List<List<SubTask>> children = null;
    private MyExpandableListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        // Database接続
        DatabaseHelper dbHelper = new DatabaseHelper(parent);
        db = dbHelper.getWritableDatabase();
        TaskDao taskDao = new TaskDao(db);
        SubTaskDao subTaskDao = new SubTaskDao(db);

        // ExpandableListViewの用意
        ExpandableListView elv = (ExpandableListView) view.findViewById(R.id.elv);
        elv.setVerticalScrollBarEnabled(false);
        elv.setDivider(null);
        elv.setSelector(R.color.transparent);

        // アダプターに渡すためのListを準備
        groups = taskDao.findByProjectID(projectID);
        children = new ArrayList<List<SubTask>>();

        // モデルをリストに格納
        for (Task task : groups) {
            List<SubTask> slist = subTaskDao.findByTaskID(task.getTaskID());
            // ボタン列追加のため余分にSubTaskを作成してリストに渡す
            SubTask subTask = new SubTask();
            subTask.setTaskID(task.getTaskID());
            subTask.setProjectID(projectID);

            slist.add(subTask);
            children.add(slist);
        }

        // アダプターを準備&設定
        adapter = new MyExpandableListAdapter(parent, groups, children);
        elv.setAdapter(adapter);

        // Indicationのアイコンを変更
        // elv.setGroupIndicator();

        // リストを開く
        int groupCount = adapter.getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            elv.expandGroup(i);
        }

        // データが入っていない時の表示
        TextView emptyTextView = (TextView) view.findViewById(R.id.emptyTextView);
        elv.setEmptyView(emptyTextView);

        // FABの設定
        AddFloatingActionButton afab = (AddFloatingActionButton) view.findViewById(R.id.fab);
        afab.setColorNormalResId(R.color.accent);
        afab.setColorPressedResId(R.color.accent_dark);
        afab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), TaskEditActivity.class);
                DatabaseHelper dbHelper = new DatabaseHelper(v.getContext());
                db = dbHelper.getWritableDatabase();
                TaskDao taskDao = new TaskDao(db);

                Task task = new Task();

                int taskID;
                // Taskがなければ最大のTaskIDに1を足す
                if (!taskDao.exists()) {
                    taskID = 1;
                } else {
                    taskID = taskDao.getLastID() + 1;
                }
                task.setTaskID(taskID);
                task.setProjectID(projectID);

                i.putExtra("Task", task);

                v.getContext().startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        // 親を変数として格納
        parent = (ShowProjectActivity) activity;
        this.projectID = parent.getProject().getProjectID();
        super.onAttach(activity);
    }

    @Override
    public void onPause() {
        super.onPause();
        db.close();
    }

}
