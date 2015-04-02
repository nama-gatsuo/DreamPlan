package com.nama_gatsuo.dreamplan;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.nama_gatsuo.dreamplan.View.DateView;
import com.nama_gatsuo.dreamplan.dao.ProjectDao;
import com.nama_gatsuo.dreamplan.dao.SubTaskDao;
import com.nama_gatsuo.dreamplan.dao.TaskDao;
import com.nama_gatsuo.dreamplan.model.Project;
import com.nama_gatsuo.dreamplan.model.Task;

import org.joda.time.DateTime;


public class ProjectEditActivity extends FragmentActivity {

    private ProjectDao projectDao;
    private TaskDao taskDao;
    private SubTaskDao subTaskDao;
    private Project project;
    private SQLiteDatabase db;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_edit);

        // IntentでのProjectの受け取り
        project = (Project)getIntent().getSerializableExtra("Project");

        EditText pjName = (EditText)findViewById(R.id.pj_edit_name);
        final DateView startDate = (DateView)findViewById(R.id.pj_edit_startDate);
        final DateView endDate = (DateView)findViewById(R.id.pj_edit_endDate);
        Spinner status = (Spinner)findViewById(R.id.pj_edit_status);
        EditText description = (EditText)findViewById(R.id.pj_edit_description);

        // Database接続
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        projectDao = new ProjectDao(db);
        taskDao = new TaskDao(db);
        subTaskDao = new SubTaskDao(db);

        // Taskが既に存在していればViewに値をセット
        if(projectDao.exists(project.getProjectID())) {
            pjName.setText(project.getName());
            startDate.setDate(project.getStartDate());
            endDate.setDate(project.getEndDate());
            status.setSelection(project.getStatus());
            description.setText(project.getDescription());
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
            project.setName(((EditText) findViewById(R.id.pj_edit_name)).getText().toString());
            project.setDescription(((EditText) findViewById(R.id.pj_edit_description)).getText().toString());
            project.setStatus(((Spinner) findViewById(R.id.pj_edit_status)).getSelectedItemPosition());
            project.setStartDate(((DateView) findViewById(R.id.pj_edit_startDate)).getDate());
            project.setEndDate(((DateView) findViewById(R.id.pj_edit_endDate)).getDate());

            if (projectDao.save(project) < 0) {
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
        // 本当に削除するか確認
        try {
            if (projectDao.deleteByProjectID(project.getProjectID()) < 0 ||
                    taskDao.deleteByProjectID(project.getProjectID()) < 0 ||
                    subTaskDao.deleteByProjectID(project.getProjectID()) < 0) {
                throw new Exception("could not delete Task");
            }
            // 配下のSubTaskも削除
            Toast.makeText(this, "削除しました", Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "削除できませんでした", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        db.close();
    }
}
