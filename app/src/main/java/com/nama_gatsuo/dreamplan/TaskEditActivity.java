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
    private Task task;
    private SQLiteDatabase db;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        // IntentでのTaskの受け取り
        task = (Task)getIntent().getSerializableExtra("Task");

        EditText taskName = (EditText)findViewById(R.id.task_edit_name);
        final DateView startDate = (DateView)findViewById(R.id.task_edit_startDate);
        final DateView endDate = (DateView)findViewById(R.id.task_edit_endDate);
        Spinner status = (Spinner)findViewById(R.id.task_edit_status);
        EditText description = (EditText)findViewById(R.id.task_edit_description);

        // Database接続
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        taskDao = new TaskDao(db);

        // Taskが既に存在していればViewに値をセット
        if(taskDao.exists(task.getTaskID())) {
            taskName.setText(task.getName());
            startDate.setDate(task.getStartDate());
            endDate.setDate(task.getEndDate());
            status.setSelection(task.getStatus());
            description.setText(task.getDescription());
        }

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
            task.setName(((EditText) findViewById(R.id.task_edit_name)).getText().toString());
            task.setDescription(((EditText) findViewById(R.id.task_edit_description)).getText().toString());
            task.setStatus(((Spinner) findViewById(R.id.task_edit_status)).getSelectedItemPosition());
            task.setStartDate(((DateView)findViewById(R.id.task_edit_startDate)).getDate());
            task.setEndDate(((DateView)findViewById(R.id.task_edit_endDate)).getDate());

            if (taskDao.save(task) < 0) {
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
            if (taskDao.deleteByTaskID(task.getTaskID()) < 0) {
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