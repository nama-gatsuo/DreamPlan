package com.nama_gatsuo.dreamplan;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.nama_gatsuo.dreamplan.dao.SubTaskDao;
import com.nama_gatsuo.dreamplan.dao.TaskDao;
import com.nama_gatsuo.dreamplan.model.SubTask;
import com.nama_gatsuo.dreamplan.model.Task;

import com.getbase.floatingactionbutton.AddFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.paralloid.Parallaxor;

public class TaskListActivity extends Activity {
    private SQLiteDatabase db;
    private TextView emptyTextView;
    private AddFloatingActionButton afab;
    private int projectID;
    private List<Task> groups = null;
    private List<List<SubTask>> children = null;
    private MyExpandableListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        // IntentでのProjectIDの受け取り
        Intent i = getIntent();
        projectID = i.getIntExtra("projectID", 1);

        // FABの設定
        afab = (AddFloatingActionButton) findViewById(R.id.fab);
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
    }

    @Override
    public void onResume() {
        super.onResume();

        // Database接続
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        TaskDao taskDao = new TaskDao(db);
        SubTaskDao subTaskDao = new SubTaskDao(db);

        // ExpandableListViewの用意
        ExpandableListView elv = (ExpandableListView) findViewById(R.id.elv);
        elv.setVerticalScrollBarEnabled(false);
        elv.setDivider(null);
        elv.setSelector(R.color.transparent);

        // アダプターに渡すためのListを準備
        groups = taskDao.findAll();
        children = new ArrayList<List<SubTask>>();

        // モデルをリストに格納
        for (Task task : groups) {
            List<SubTask> slist = subTaskDao.findByTaskID(task.getTaskID());
            // ボタン列追加のため余分にSubTaskを作成してリストに渡す
            SubTask subTask = new SubTask();
            subTask.setTaskID(task.getTaskID());
            subTask.setProjectID(this.projectID);

            slist.add(subTask);
            children.add(slist);
        }

        // アダプターを準備&設定
        adapter = new MyExpandableListAdapter(this, groups, children);
        elv.setAdapter(adapter);

        // データが入っていない時の表示
        emptyTextView = (TextView) findViewById(R.id.emptyTextView);
        elv.setEmptyView(emptyTextView);
    }

    @Override
    public void onPause() {
        super.onPause();
        db.close();
    }
}