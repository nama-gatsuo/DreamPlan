package com.nama_gatsuo.dreamplan;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nama_gatsuo.dreamplan.dao.ProjectDao;
import com.nama_gatsuo.dreamplan.dao.SubTaskDao;
import com.nama_gatsuo.dreamplan.dao.TaskDao;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 3;

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // CREATE TABLE
        db.execSQL(ProjectDao.CREATE_SQL);
        db.execSQL(TaskDao.CREATE_SQL);
        db.execSQL(SubTaskDao.CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if( oldVersion == 1 && newVersion == 2 ){
            db.execSQL(ProjectDao.CREATE_SQL);
        }
        if( oldVersion == 2 && newVersion == 3) {
            db.execSQL("alter table " + ProjectDao.TABLE_NAME + " add " + ProjectDao.COLUMN_imagePath + " TEXT");
        }
    }
}

