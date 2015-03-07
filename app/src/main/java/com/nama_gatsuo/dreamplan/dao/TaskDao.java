package com.nama_gatsuo.dreamplan.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nama_gatsuo.dreamplan.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nagamatsuayumu on 15/02/07.
 */
public class TaskDao {
    public static final String TABLE_NAME = "task";

    // DBカラム名
    static final String COLUMN_taskID = "taskID";
    static final String COLUMN_projectID = "projectID";
    static final String COLUMN_taskName = "task_name";
    static final String COLUMN_taskDescription = "task_description";
    static final String COLUMN_taskStatus = "task_status";
    static final String COLUMN_taskStartDate = "task_startDate";
    static final String COLUMN_taskEndDate = "task_endDate";

    // カラム名の配列を静的メンバとして用意
    static final String[] COLUMNS = {
            COLUMN_taskID, COLUMN_projectID, COLUMN_taskName, COLUMN_taskDescription, COLUMN_taskStatus, COLUMN_taskStartDate, COLUMN_taskEndDate
    };

    // CREATE TABLE文を静的メンバとして用意
    public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_taskID + " INTEGER PRIMARY KEY, "
            + COLUMN_projectID + " INTEGER, "
            + COLUMN_taskName + " TEXT, "
            + COLUMN_taskDescription + " TEXT, "
            + COLUMN_taskStatus + " INTEGER, "
            + COLUMN_taskStartDate + " INTEGER, "
            + COLUMN_taskEndDate + " INTEGER)";

    SQLiteDatabase db;

    // Constructor
    public TaskDao(SQLiteDatabase db) {
        this.db = db;
    }

    // 全てのTaskをリストで取得するメソッド
    public List<Task> findAll() {
        List<Task> list = new ArrayList<Task>();
        // 全件抽出のqueryを生成
        Cursor c = db.query(TABLE_NAME, COLUMNS, null, null, null, null, COLUMN_taskID);
        // 一行ずつfetch
        while (c.moveToNext()) {
            Task task = new Task();
            task.setTaskID(c.getInt(c.getColumnIndex(COLUMN_taskID)));
            task.setProjectID(c.getInt(c.getColumnIndex(COLUMN_projectID)));
            task.setName(c.getString(c.getColumnIndex(COLUMN_taskName)));
            task.setDescription(c.getString(c.getColumnIndexOrThrow(COLUMN_taskDescription)));
            task.setStatus(c.getInt(c.getColumnIndex(COLUMN_taskStatus)));
            task.setStartDate(c.getInt(c.getColumnIndex(COLUMN_taskStartDate)));
            task.setEndDate(c.getInt(c.getColumnIndex(COLUMN_taskEndDate)));
            list.add(task);
        }
        // Cursorのclose
        c.close();
        return list;
    }

    // 特定のProjectID配下のTaskを取得するメソッド
    public List<Task> findByProjectID(int projectID) {
        List<Task> list = new ArrayList<Task>();
        // Where句でProjectIDを指定してquery生成
        String where = COLUMN_projectID + " = " + projectID;
        Cursor c = db.query(TABLE_NAME, COLUMNS, where, null, null, null, COLUMN_taskID);
        // 一行ずつfetch
        while (c.moveToNext()) {
            Task task = new Task();
            task.setTaskID(c.getInt(c.getColumnIndex(COLUMN_taskID)));
            task.setProjectID(c.getInt(c.getColumnIndex(COLUMN_projectID)));
            task.setName(c.getString(c.getColumnIndex(COLUMN_taskName)));
            task.setDescription(c.getString(c.getColumnIndexOrThrow(COLUMN_taskDescription)));
            task.setStatus(c.getInt(c.getColumnIndex(COLUMN_taskStatus)));
            task.setStartDate(c.getInt(c.getColumnIndex(COLUMN_taskStartDate)));
            task.setEndDate(c.getInt(c.getColumnIndex(COLUMN_taskEndDate)));
            list.add(task);
        }
        // Cursorのclose
        c.close();
        return list;
    }

    // TasKIDを指定して取り出すメソッド
    public Task findByTaskID(int taskID) {
        // WHERE句でCOLUMN_taskIDを指定してqueryを生成
        Cursor c = db.query(TABLE_NAME, COLUMNS, COLUMN_taskID + " = ?",
                new String[] { String.valueOf(taskID) }, null, null, COLUMN_taskID );
        Task task = null;

        // 一行だけfetch
        if (c.moveToFirst()) {
            task = new Task();
            task.setTaskID(c.getInt(c.getColumnIndex(COLUMN_taskID)));
            task.setProjectID(c.getInt(c.getColumnIndex(COLUMN_projectID)));
            task.setName(c.getString(c.getColumnIndex(COLUMN_taskName)));
            task.setDescription(c.getString(c.getColumnIndexOrThrow(COLUMN_taskDescription)));
            task.setStatus(c.getInt(c.getColumnIndex(COLUMN_taskStatus)));
            task.setStartDate(c.getInt(c.getColumnIndex(COLUMN_taskStartDate)));
            task.setEndDate(c.getInt(c.getColumnIndex(COLUMN_taskEndDate)));
        }
        // Cursorのclose
        c.close();
        return task;
    }

    // Taskを保存するメソッド
    public long save(Task task) {
        if (!task.validate()) {
            return -1;
        }
        // 値設定
        ContentValues values = new ContentValues();
        values.put(COLUMN_projectID, task.getProjectID());
        values.put(COLUMN_taskName, task.getName());
        values.put(COLUMN_taskDescription, task.getDescription());
        values.put(COLUMN_taskStatus, task.getStatus());
        values.put(COLUMN_taskStartDate, task.getStartDate());
        values.put(COLUMN_taskEndDate, task.getEndDate());

        // 同じTaskIDが存在するなら更新
        if (exists(task.getTaskID())) {
            String where = COLUMN_taskID + " = ?";
            String[] arg = { String.valueOf(task.getTaskID()) };
            return db.update(TABLE_NAME, values, where, arg);
        } else {
            // データがまだ無いなら挿入
            values.put(COLUMN_taskID, task.getTaskID());
            return db.insert(TABLE_NAME, null, values);
        }
    }

    // ProjectIDを指定してTaskのデータを削除するメソッド
    public long deleteByProjectID(int projectID) {
        // 値設定
        String where = COLUMN_projectID + " = ?";
        String[] arg = { String.valueOf(projectID) };
        return db.delete(TABLE_NAME, where, arg);
    }

    // TaskIDを指定してTaskのデータを削除するメソッド
    public long deleteByTaskID(int taskID) {
        // 値設定
        String where = COLUMN_taskID + " = ?";
        String[] arg = { String.valueOf(taskID) };
        return db.delete(TABLE_NAME, where, arg);
    }

    // ID値の最大を返すメソッド
    public int getLastID() {
        ArrayList<Task> tlist = new ArrayList<Task>();
        tlist.addAll(findAll());
        int lastTaskID = (tlist.get(tlist.size() - 1)).getTaskID();
        return lastTaskID;
    }

    // TaskIDの存在をチェックするメソッド
    public boolean exists(int taskID) {
        return findByTaskID(taskID) != null;
    }

    // データの存在をチェックするメソッド
    public boolean exists() {
        return findAll().size() > 0;
    }
}
