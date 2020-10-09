package com.example.dttt.idiary.Database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static String SQL_CREATE_ENTRIES_M =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                    DataContract.DiaryEntry.TABLE_NAME,
                    DataContract.DiaryEntry._ID,
                    DataContract.DiaryEntry.COLUMN_NAME_TITLE,
                    DataContract.DiaryEntry.COLUMN_NAME_CONTENTS,
                    DataContract.DiaryEntry.COLUMN_NAME_IMAGE,
                    DataContract.DiaryEntry.COLUMN_NAME_DATE,
                    DataContract.DiaryEntry.COLUMN_NAME_EMAIL);

    public static String SQL_DELETE_ENTRIES_M = "DROP TABLE IF EXISTS " + DataContract.DiaryEntry.TABLE_NAME;

    public static String SQL_CREATE_ENTRIES_R =
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT)",
                    DataContract.RegistEntry.TABLE_NAME,
                    DataContract.RegistEntry._ID,
                    DataContract.RegistEntry.COLUMN_NAME_USEREMAIL,
                    DataContract.RegistEntry.COLUMN_NAME_USERNAME,
                    DataContract.RegistEntry.COLUMN_NAME_PASSWORD);

    public static String SQL_DELETE_ENTRIES_R =
            "DROP TABLE IF EXISTS " + DataContract.RegistEntry.TABLE_NAME;

    public DBHelper(@Nullable Context context) {
        super(context, "diary_DB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_M);
        db.execSQL(SQL_CREATE_ENTRIES_R);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_M);
        db.execSQL(SQL_DELETE_ENTRIES_R);
        onCreate(db);
    }
}
