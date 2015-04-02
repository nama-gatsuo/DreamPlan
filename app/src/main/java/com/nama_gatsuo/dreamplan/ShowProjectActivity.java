package com.nama_gatsuo.dreamplan;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.nama_gatsuo.dreamplan.dao.ProjectDao;
import com.nama_gatsuo.dreamplan.model.Project;

public class ShowProjectActivity extends FragmentActivity {

    private Project project;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private boolean isPortrait = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_project);

        // IntentでのProjectIDの受け取り
        project = (Project) getIntent().getSerializableExtra("Project");

        // Fragment表示のための準備
        manager = getFragmentManager();
    }

    @Override
    protected void onResume() {
        transaction = manager.beginTransaction();

        // 縦置きか横置きかで表示するFragmentを決定
        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // TaskListFragmentを表示
            TaskListFragment tlf = new TaskListFragment();
            transaction.add( R.id.container, tlf, "list");
            isPortrait = true;
        } else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // ActionBarを非表示にする
            ActionBar actionBar = getActionBar();
            actionBar.hide();

            // TaskGanttFragmentを表示
            TaskGanttFragment tgf = new TaskGanttFragment();
            transaction.add(R.id.container, tgf, "gantt");
            isPortrait = false;
        }
        transaction.commit();

        super.onResume();
    }

    @Override
    protected void onPause() {
        // onPause時にFragmentを削除
        transaction = manager.beginTransaction();
        if (isPortrait) {
            transaction.remove(manager.findFragmentByTag("list"));
            isPortrait = false;
        } else {
            transaction.remove(manager.findFragmentByTag("gantt"));
            isPortrait = true;
        }
        transaction.commit();

        super.onPause();
    }

    public Project getProject() {
        if (project == null) {
            Project newproject = new Project();
            newproject.setProjectID(1);
            return newproject;
        } else {
            return this.project;
        }
    }
}