package com.nama_gatsuo.dreamplan;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.nama_gatsuo.dreamplan.View.DateView;
import com.nama_gatsuo.dreamplan.dao.ProjectDao;
import com.nama_gatsuo.dreamplan.dao.SubTaskDao;
import com.nama_gatsuo.dreamplan.dao.TaskDao;
import com.nama_gatsuo.dreamplan.model.Project;

import org.joda.time.DateTime;

import java.io.File;

import uk.co.chrisjenx.paralloid.views.ParallaxScrollView;


public class ProjectEditActivity extends FragmentActivity {

    private ProjectDao projectDao;
    private TaskDao taskDao;
    private SubTaskDao subTaskDao;
    private Project project;
    private SQLiteDatabase db;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    static final int REQUEST_PICK_CONTENT = 0;
    static final int REQUEST_KITKAT_PICK_CONTENT = 1;

    private String imagePath;

    ViewHolder holder;

    private class ViewHolder {
        ImageView imageView;
        EditText pjName;
        DateView startDate;
        DateView endDate;
        Spinner status;
        EditText description;

        ParallaxScrollView scrollView;
        View change_pic;

        public ViewHolder() {
            this.imageView = (ImageView) findViewById(R.id.image_view);
            this.pjName = (EditText)findViewById(R.id.pj_edit_name);
            this.startDate = (DateView)findViewById(R.id.pj_edit_startDate);
            this.endDate = (DateView)findViewById(R.id.pj_edit_endDate);
            this.status = (Spinner)findViewById(R.id.pj_edit_status);
            this.description = (EditText)findViewById(R.id.pj_edit_description);

            this.scrollView = (ParallaxScrollView) findViewById(R.id.scroll_view);
            this.change_pic = findViewById(R.id.change_pic);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_edit);

        holder = new ViewHolder();

        // Get the Parallax View
        holder.scrollView.parallaxViewBy(holder.imageView, 0.5f);

        // get Project from Intent
        project = (Project)getIntent().getSerializableExtra("Project");

        // Connect Database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        projectDao = new ProjectDao(db);
        taskDao = new TaskDao(db);
        subTaskDao = new SubTaskDao(db);

        // Projectが既に存在していればViewに値をセット
        if(projectDao.exists(project.getProjectID())) {
            holder.pjName.setText(project.getName());
            holder.startDate.setDate(project.getStartDate());
            holder.endDate.setDate(project.getEndDate());
            holder.status.setSelection(project.getStatus());
            holder.description.setText(project.getDescription());

            imagePath = project.getImagePath();
            if (imagePath != null) {
                Uri uri = Uri.parse(imagePath);
                try {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    holder.imageView.setImageBitmap(bmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        holder.change_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_KITKAT_PICK_CONTENT);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_PICK_CONTENT);
                }

            }
        });

        // startDateにbetterpickerのClickListnerを設定
        holder.startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DateTime now = DateTime.now().withTimeAtStartOfDay();
                CalendarDatePickerDialog cdpd = CalendarDatePickerDialog
                        .newInstance(holder.startDate, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                cdpd.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });

        // endDateにbetterpickerのClickListnerを設定
        holder.endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DateTime now = DateTime.now().withTimeAtStartOfDay();
                CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(holder.endDate, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });
    }

    // Save Button
    public void onClickSave(View v) {
        try {
            project.setName(holder.pjName.getText().toString());
            project.setDescription(holder.description.getText().toString());
            project.setStatus(holder.status.getSelectedItemPosition());
            project.setStartDate(holder.startDate.getDate());
            project.setEndDate(holder.endDate.getDate());
            project.setImagePath(imagePath);

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
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            Uri uri = data.getData();
            Bitmap bmp;
            imagePath = uri.toString();
            if (requestCode == REQUEST_KITKAT_PICK_CONTENT) {
                // Check for the freshest data.
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    holder.imageView.setImageBitmap(bmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == REQUEST_PICK_CONTENT) {
                String[] columns = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(uri, columns, null, null, null);
                c.moveToFirst();
                int index = c.getColumnIndex(MediaStore.Images.Media.DATA);
                String path = c.getString(index);
                bmp = BitmapFactory.decodeFile(path);
                holder.imageView.setImageBitmap(bmp);
            }
        }
    }
}
