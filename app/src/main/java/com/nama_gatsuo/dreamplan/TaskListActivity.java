package com.nama_gatsuo.dreamplan;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.nama_gatsuo.dreamplan.dao.SubTaskDao;
import com.nama_gatsuo.dreamplan.dao.TaskDao;
import com.nama_gatsuo.dreamplan.model.SubTask;
import com.nama_gatsuo.dreamplan.model.Task;

import com.getbase.floatingactionbutton.AddFloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskListActivity extends Activity {
    SQLiteDatabase db;
    TextView emptyTextView;
    AddFloatingActionButton afab;
    private int projectID;

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

        // アダプターに渡すためのArrayListを準備
        ArrayList<Map<String, String>> task_list = new ArrayList<Map<String, String>>();
        ArrayList<List<Map<String, String>>> subTask_list = new ArrayList<List<Map<String, String>>>();

        // TaskをDaoから取得
        List<Task> tlist = taskDao.findAll();

        // Taskの個数だけ名前取り出しとリストへの名前格納を繰り返す
        for (Task task : tlist) {
            String taskName = task.getName();
            HashMap<String, String> tgroup = new HashMap<String, String>();
            tgroup.put("task_name", taskName);
            // 配列リストにKey, Valueを格納
            task_list.add(tgroup);

            List<SubTask> slist = subTaskDao.findByTaskID(task.getTaskID());
            // SubTaskの個数だけデータ取り出しとリストへのデータ格納を繰り返す
            ArrayList<Map<String, String>> sglist = new ArrayList<Map<String, String>>();
            for (SubTask subTask : slist) {
                String subTaskName = subTask.getName();
                HashMap<String, String> sngroup = new HashMap<String, String>();
                sngroup.put("subTask_name", subTaskName);
                sglist.add(sngroup);

                String subTaskStatus = null;
                switch (subTask.getStatus()) {
                    case 0: subTaskStatus = "0%"; break;
                    case 1: subTaskStatus = "10%"; break;
                    case 2: subTaskStatus = "20%"; break;
                    case 3: subTaskStatus = "30%"; break;
                    case 4: subTaskStatus = "40%"; break;
                    case 5: subTaskStatus = "50%"; break;
                    case 6: subTaskStatus = "60%"; break;
                    case 7: subTaskStatus = "70%"; break;
                    case 8: subTaskStatus = "80%"; break;
                    case 9: subTaskStatus = "90%"; break;
                    case 10: subTaskStatus = "DONE"; break;
                }
                HashMap<String, String> ssgroup = new HashMap<String, String>();
                ssgroup.put("subTask_status", subTaskStatus);
                sglist.add(ssgroup);

                subTask_list.add(sglist);
            }
        }

        // アダプターを準備&設定
        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                this,
                task_list,
                android.R.layout.simple_expandable_list_item_1,
                new String[]{ "task_name" },
                new int []{ android.R.id.text1 },
                subTask_list,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{ "subTask_name", "subTask_status" },
                new int[]{ android.R.id.text1, android.R.id.text2 }
        );
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
