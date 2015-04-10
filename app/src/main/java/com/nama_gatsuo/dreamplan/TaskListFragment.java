package com.nama_gatsuo.dreamplan;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.idunnololz.widgets.AnimatedExpandableListView;
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

    AnimatedExpandableListView elv;

    @Override
    public void onAttach(Activity activity) {
        // 親を変数として格納
        parent = (ShowProjectActivity) activity;
        projectID = parent.getProject().getProjectID();
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        // Database接続
        DatabaseHelper dbHelper = new DatabaseHelper(parent);
        db = dbHelper.getWritableDatabase();
        TaskDao taskDao = new TaskDao(db);
        SubTaskDao subTaskDao = new SubTaskDao(db);

        // ExpandableListViewの用意
        elv = (AnimatedExpandableListView) view.findViewById(R.id.elv);
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

        // LongClick to edit
        elv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    int childPosition = ExpandableListView.getPackedPositionChild(id);

                    SubTask subTask = adapter.getChild(groupPosition, childPosition);

                    Intent i = new Intent(parent.getContext(), SubtaskEditActivity.class);

                    i.putExtra("SubTask", subTask);
                    parent.getContext().startActivity(i);

                    return true;
                } else if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);

                    Task task = adapter.getGroup(groupPosition);

                    Intent i = new Intent(parent.getContext(), TaskEditActivity.class);

                    i.putExtra("Task", task);
                    parent.getContext().startActivity(i);
                    return true;
                }
                return false;
            }
        });

        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (elv.isGroupExpanded(groupPosition)) {
                    elv.collapseGroupWithAnimation(groupPosition);
                } else {
                    elv.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }

        });

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

                // 最大のTaskIDに1を足す
                task.setTaskID(taskDao.getLastID() + 1);
                task.setProjectID(projectID);

                i.putExtra("Task", task);

                v.getContext().startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        db.close();
    }

}
