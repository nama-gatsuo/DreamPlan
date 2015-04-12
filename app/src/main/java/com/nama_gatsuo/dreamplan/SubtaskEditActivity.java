package com.nama_gatsuo.dreamplan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.nama_gatsuo.dreamplan.View.DateView;
import com.nama_gatsuo.dreamplan.dao.SubTaskDao;
import com.nama_gatsuo.dreamplan.model.SubTask;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class SubtaskEditActivity extends ActionBarActivity {
    private SubTaskDao subTaskDao;
    private SubTask subTask;
    private SQLiteDatabase db;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subtask_edit);

        // IntentでのSubTaskの受け取り
        subTask = (SubTask)getIntent().getSerializableExtra("SubTask");

        EditText taskName = (EditText)findViewById(R.id.subTask_edit_name);
        final DateView startDate = (DateView)findViewById(R.id.subTask_edit_startDate);
        final DateView endDate = (DateView)findViewById(R.id.subTask_edit_endDate);
        Spinner status = (Spinner)findViewById(R.id.subTask_edit_status);
        EditText description = (EditText)findViewById(R.id.subTask_edit_description);

        // Database接続
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        subTaskDao = new SubTaskDao(db);

        // SubTaskが既に存在していればViewに値をセット
        if(subTaskDao.exists(subTask.getSubTaskID())) {
            taskName.setText(subTask.getName());
            startDate.setDate(subTask.getStartDate());
            endDate.setDate(subTask.getEndDate());
            status.setSelection(subTask.getStatus());
            description.setText(subTask.getDescription());
        }

        // startDateにbetterpickerのClickListnerを設定
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DateTime now = DateTime.now().withTimeAtStartOfDay();
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
                DateTime now = DateTime.now().withTimeAtStartOfDay();
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
            subTask.setName(((EditText) findViewById(R.id.subTask_edit_name)).getText().toString());
            subTask.setDescription(((EditText) findViewById(R.id.subTask_edit_description)).getText().toString());
            subTask.setStatus(((Spinner) findViewById(R.id.subTask_edit_status)).getSelectedItemPosition());
            subTask.setStartDate(((DateView)findViewById(R.id.subTask_edit_startDate)).getDate());
            subTask.setEndDate(((DateView)findViewById(R.id.subTask_edit_endDate)).getDate());

            if (subTaskDao.save(subTask) < 0) {
                throw new Exception("could not save Task");
            }
            Toast.makeText(this, R.string.toast_success_save, Toast.LENGTH_LONG).show();

            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.toast_fail_save, Toast.LENGTH_LONG).show();
        }
    }

    // Cancel Button
    public void onClickCancel(View v) {
        finish();
    }

    // Delete Button
    public void onClickDelete(View v) {
        // Show confirming message.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.edit_alert_title);
        alertDialogBuilder.setMessage(R.string.subtask_edit_alert_message);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (subTaskDao.deleteBySubTaskID(subTask.getSubTaskID()) < 0) {
                                throw new Exception("could not delete Subtask");
                            }
                            Toast.makeText(SubtaskEditActivity.this, R.string.toast_success_delete, Toast.LENGTH_LONG).show();
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(SubtaskEditActivity.this, R.string.toast_fail_delete, Toast.LENGTH_LONG).show();
                        }
                        finish();
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        db.close();
    }
}