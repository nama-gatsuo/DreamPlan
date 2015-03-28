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

import com.inqbarna.tablefixheaders.TableFixHeaders;
import com.inqbarna.tablefixheaders.adapters.BaseTableAdapter;
import com.nama_gatsuo.dreamplan.dao.SubTaskDao;
import com.nama_gatsuo.dreamplan.dao.TaskDao;
import com.nama_gatsuo.dreamplan.model.SubTask;
import com.nama_gatsuo.dreamplan.model.Task;

import java.util.ArrayList;
import java.util.List;


public class TaskGanttFragment extends Fragment {

    private ShowProjectActivity parent;

    @Override
    public void onAttach(Activity activity) {
        // 親を変数として格納
        parent = (ShowProjectActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_gantt, container, false);

        TableFixHeaders tableFixHeaders = (TableFixHeaders) view.findViewById(R.id.table);
        GanttTableAdapter baseTableAdapter = new GanttTableAdapter(parent);
        tableFixHeaders.setAdapter(baseTableAdapter);
        tableFixHeaders.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        return view;
    }
}
