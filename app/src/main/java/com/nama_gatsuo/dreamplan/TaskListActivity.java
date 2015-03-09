package com.nama_gatsuo.dreamplan;

import android.app.Activity;
import android.app.ExpandableListActivity;
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

import java.util.List;

public class TaskListActivity extends Activity {
    SQLiteDatabase db;
    TextView emptyTextView;
    AddFloatingActionButton afab;
    private int projectID;
    private List<Task> groups = null;
    private List<List<SubTask>> children = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        // IntentでのProjectIDの受け取り
        Intent i = getIntent();
        projectID = i.getIntExtra("projectID", 0);

        // FABの設定
        afab = new AddFloatingActionButton(this);
        afab = (AddFloatingActionButton)findViewById(R.id.fab);
        afab.setColorNormalResId(R.color.accent);
        afab.setColorPressedResId(R.color.accent_dark);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Database接続
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        TaskDao taskDao = new TaskDao(db);
        SubTaskDao subTaskDao = new SubTaskDao(db);

        ExpandableListView elv = (ExpandableListView)findViewById(R.id.elv);

        // アダプターに渡すためのListを準備
        groups = taskDao.findAll();
        for (Task task : groups) {
            List<SubTask> slist = subTaskDao.findByTaskID(task.getTaskID());
            if (slist.size() == 0) {
                slist.add(new SubTask());
            } else {
                children.add(slist);
            }
        }

        // アダプターを準備&設定
        MyExpandableListAdapter adapter = new MyExpandableListAdapter(this, groups, children);
        elv.setAdapter(adapter);

        // データが入っていない時の表示
        emptyTextView = (TextView)findViewById(R.id.emptyTextView);
        elv.setEmptyView(emptyTextView);
    }

    // FabによるTask作成
    public void onClickFab(View v) {
        Intent i = new Intent(this, TaskEditActivity.class);
        int taskID;

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        TaskDao taskDao = new TaskDao(db);

        // Taskがなければ最大のTaskIDに1を足す
        if (!taskDao.exists()) {
            taskID = 1;
            projectID = 1;
        } else {
            taskID = taskDao.getLastID() + 1;
        }

        i.putExtra("TaskID", taskID);
        i.putExtra("ProjectID", projectID);
        startActivity(i);
    }

}
