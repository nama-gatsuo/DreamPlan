package com.nama_gatsuo.dreamplan;


import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.nama_gatsuo.dreamplan.View.DateView;
import com.nama_gatsuo.dreamplan.dao.TaskDao;
import com.nama_gatsuo.dreamplan.model.Task;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;


public class TaskEditActivity extends FragmentActivity {
    private TaskDao taskDao;
    private SQLiteDatabase db;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private int taskID;
    private int projectID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        // IntentでのIDの受け取り
        // 初期値は1
        Intent i = getIntent();
        taskID = i.getIntExtra("TaskID", 1);
        projectID = i.getIntExtra("ProjectID", 1);

        // Databaseの接続
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        taskDao = new TaskDao(db);

        EditText taskName = (EditText)findViewById(R.id.taskName);
        final DateView startDate = (DateView)findViewById(R.id.startDate);
        final DateView endDate = (DateView)findViewById(R.id.endDate);
        Spinner status = (Spinner)findViewById(R.id.status);
        EditText description = (EditText)findViewById(R.id.description);

        // Taskが存在していたらViewに代入
        if (taskDao.exists(taskID)) {
            Task task = taskDao.findByTaskID(taskID);

            // Viewに値をセット
            taskName.setText(task.getName());

            DateTime sdt = new DateTime().withMillis(task.getStartDate());
            startDate.setText(sdt.toString(DateTimeFormat.longDate()));

            DateTime edt = new DateTime().withMillis(task.getEndDate());
            endDate.setText(edt.toString(DateTimeFormat.longDate()));

            status.setSelection(task.getStatus());
            description.setText(task.getDescription());
        }

        // StatusにListnerを設定
        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner sp = (Spinner)parent;
                Toast.makeText(TaskEditActivity.this,
                        String.format("%sを選択", sp.getSelectedItem()),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // startDateにbetterpickerのClickListnerを設定
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DateTime now = DateTime.now();
                CalendarDatePickerDialog cdpd = CalendarDatePickerDialog
                        .newInstance(startDate, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                cdpd.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });

        // endDateにbetterpickerのClickListnerを設定
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DateTime now = DateTime.now();
                CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(endDate, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });
    }

    // Save Button
    public void onClickSave(View v) {
        try {
            Task newTask = new Task();
            newTask.setTaskID(taskID);
            newTask.setProjectID(projectID);
            newTask.setName(((EditText) findViewById(R.id.taskName)).getText().toString());
            newTask.setDescription(((EditText) findViewById(R.id.description)).getText().toString());
            newTask.setStatus(((Spinner) findViewById(R.id.status)).getSelectedItemPosition());

            // 日付文字列をDateTime型を経てlong値に変換
            String sd = ((TextView)findViewById(R.id.startDate)).getText().toString();
            newTask.setStartDate(DateTimeFormat.forPattern("yyyy/MM/dd").parseDateTime(sd).getMillis());

            String ed = ((TextView)findViewById(R.id.endDate)).getText().toString();
            newTask.setEndDate(DateTimeFormat.forPattern("yyyy/MM/dd").parseDateTime(ed).getMillis());

            if (taskDao.save(newTask) < 0) {
                throw new Exception("could not save Task");
            }
            Toast.makeText(this, "保存しました", Toast.LENGTH_LONG).show();

            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "保存できませんでした", Toast.LENGTH_LONG).show();
        }
    }

    // Cancel Button
    public void onClickCancel(View v) {
        finish();
    }

    // Delete Button
    public void onClickDelete(View v) {
        try {
            if (taskDao.deleteByTaskID(taskID) < 0) {
                throw new Exception("could not delete Task");
            }
            Toast.makeText(this, "削除しました", Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "削除できませんでした", Toast.LENGTH_LONG).show();
        }
    }
}