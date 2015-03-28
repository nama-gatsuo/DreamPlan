package com.nama_gatsuo.dreamplan.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nama_gatsuo.dreamplan.model.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nagamatsuayumu on 15/03/01.
 */
public class ProjectDao {
    public static final String TABLE_NAME = "project";

    // DBカラム名
    static final String COLUMN_projectID = "projectID";
    static final String COLUMN_projectName = "project_name";
    static final String COLUMN_projectDescription = "project_description";
    static final String COLUMN_projectStatus = "project_status";
    static final String COLUMN_projectStartDate = "project_startDate";
    static final String COLUMN_projectEndDate = "project_endDate";

    // カラム名の配列を静的メンバとして用意
    static final String[] COLUMNS = {
            COLUMN_projectID, COLUMN_projectName, COLUMN_projectDescription,
            COLUMN_projectStatus, COLUMN_projectStartDate, COLUMN_projectEndDate
    };

    // CREATE TABLE文を静的メンバとして用意
    public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_projectID + " INTEGER PRIMARY KEY, "
            + COLUMN_projectName + " TEXT, "
            + COLUMN_projectDescription + " TEXT, "
            + COLUMN_projectStatus + " INTEGER, "
            + COLUMN_projectStartDate + " INTEGER, "
            + COLUMN_projectEndDate + " INTEGER)";

    SQLiteDatabase db;

    // Constructor
    public ProjectDao(SQLiteDatabase db) {
        this.db = db;
    }

    // 全てのProjectをリストで取得するメソッド
    public List<Project> findAll() {
        List<Project> list = new ArrayList<Project>();
        // 全件抽出のqueryを生成
        Cursor c = db.query(TABLE_NAME, COLUMNS, null, null, null, null, COLUMN_projectID);
        // 一行ずつfetch
        while (c.moveToNext()) {
            Project pj = new Project();
            pj.setProjectID(c.getColumnIndex(COLUMN_projectID));
            pj.setName(c.getString(c.getColumnIndex(COLUMN_projectName)));
            pj.setDescription(c.getString(c.getColumnIndexOrThrow(COLUMN_projectDescription)));
            pj.setStatus(c.getInt(c.getColumnIndex(COLUMN_projectStatus)));
            pj.setStartDate(c.getInt(c.getColumnIndex(COLUMN_projectStartDate)));
            pj.setEndDate(c.getInt(c.getColumnIndex(COLUMN_projectEndDate)));
            list.add(pj);
        }
        // Cursorのclose
        c.close();
        return list;
    }

    // ProjectIDを指定して取り出すメソッド
    public Project findByProjectID(int projectID) {
        // WHERE句でCOLUMN_projectIDを指定してqueryを生成
        Cursor c = db.query(TABLE_NAME, COLUMNS, COLUMN_projectID + " = ?",
                new String[]{String.valueOf(projectID)}, null, null, COLUMN_projectID);
        Project pj = null;

        // 一行だけfetch
        if (c.moveToFirst()) {
            pj = new Project();
            pj.setProjectID(c.getInt(c.getColumnIndex(COLUMN_projectID)));
            pj.setName(c.getString(c.getColumnIndex(COLUMN_projectName)));
            pj.setDescription(c.getString(c.getColumnIndexOrThrow(COLUMN_projectDescription)));
            pj.setStatus(c.getInt(c.getColumnIndex(COLUMN_projectStatus)));
            pj.setStartDate(c.getLong(c.getColumnIndex(COLUMN_projectStartDate)));
            pj.setEndDate(c.getLong(c.getColumnIndex(COLUMN_projectEndDate)));
        }
        // Cursorのclose
        c.close();
        return pj;
    }

    // SubTaskを保存するメソッド
    public long save(Project pj) {
        if (!pj.validate()) {
            return -1;
        }
        // 値設定
        ContentValues values = new ContentValues();
        values.put(COLUMN_projectName, pj.getName());
        values.put(COLUMN_projectDescription, pj.getDescription());
        values.put(COLUMN_projectStatus, pj.getStatus());
        values.put(COLUMN_projectStartDate, pj.getStartDate());
        values.put(COLUMN_projectEndDate, pj.getEndDate());

        // 同じSubTaskIDが存在するなら更新
        if (exists(pj.getProjectID())) {
            String where = COLUMN_projectID + " = ?";
            String[] arg = { String.valueOf(pj.getProjectID()) };
            return db.update(TABLE_NAME, values, where, arg);
        } else {
            // データがまだ無いなら挿入
            values.put(COLUMN_projectID, pj.getProjectID());
            return db.insert(TABLE_NAME, null, values);
        }
    }

    // SubTaskIDを指定してSubTaskのデータを削除するメソッド
    public long deleteByProjectID(int subTaskID) {
        // 値設定
        String where = COLUMN_projectID + " = ?";
        String[] arg = { String.valueOf(subTaskID) };
        return db.delete(TABLE_NAME, where, arg);
    }

    // ID値の最大を返すメソッド
    public int getLastID() {
        int lastTaskID = 0;

        // SELECT句の用意
        String[] column = { "MAX(" + COLUMN_projectID + ")" };

        Cursor c = db.query(TABLE_NAME, column, null, null, null, null, null );

        // 一行だけfetch
        if (c.moveToFirst()) {
            lastTaskID = c.getInt(c.getColumnIndex(column[0]));
        }
        // Cursorのclose
        c.close();

        return lastTaskID;
    }

    // SubTaskIDの存在をチェックするメソッド
    public boolean exists(int projectID) {
        return findByProjectID(projectID) != null;
    }

    // データの存在をチェックするメソッド
    public boolean exists() {
        return findAll().size() > 0;
    }

}
