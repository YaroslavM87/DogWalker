package com.yaroslavm87.dogwalker.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "DOGS_STORE_DB";
    public static final String TABLE_NAME = "DOGS";
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_IMAGE_RESOURCE_ID = "IMAGE_RESOURCE_ID";
    public static final String COLUMN_LAST_TIME_WALK = "LAST_TIME_WALK";
    private String sqlStatement;
    private StringBuilder stringBuilder;
    private final String LOG_TAG = "dbLogs";


    DatabaseHelper(Context context, int dbVersion) {
        super(context, DB_NAME, null, dbVersion);
        this.stringBuilder = new StringBuilder();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        this.sqlStatement = stringBuilder.append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append(" (")
                .append(COLUMN_ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(COLUMN_NAME)
                .append(" TEXT, ")
                .append(COLUMN_IMAGE_RESOURCE_ID)
                .append(" INTEGER, ")
                .append(COLUMN_LAST_TIME_WALK)
                .append(" INTEGER)")
                .toString();

        db.execSQL(sqlStatement);

        Log.d(LOG_TAG, "SQLiteDatabase.onCreate()");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    StringBuilder getStringBuilder() {
        return stringBuilder;
    }
}
