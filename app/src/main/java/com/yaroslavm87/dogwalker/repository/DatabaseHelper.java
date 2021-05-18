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
    private final String LOG_TAG = "myLogs";

    DatabaseHelper(Context context, int dbVersion) {

        super(context, DB_NAME, null, dbVersion);

        this.stringBuilder = new StringBuilder();

        Log.d(LOG_TAG, "DatabaseHelper() instance just created");

        //addTestData();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(LOG_TAG, "DatabaseHelper.onCreate() call");

        this.sqlStatement = stringBuilder.append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append(" (")
                .append(COLUMN_ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(COLUMN_NAME)
                .append(" TEXT);")


//                .append(" TEXT, ")
//                .append(COLUMN_IMAGE_RESOURCE_ID)
//                .append(" INTEGER, ")
//                .append(COLUMN_LAST_TIME_WALK)
//                .append(" INTEGER)")
                .toString();

        db.execSQL(sqlStatement);

//        sqlStatement = "";
//
//        this.sqlStatement = stringBuilder.append("INSERT INTO ")
//                .append(TABLE_NAME)
//                .append(" (")
//                .append(COLUMN_NAME)
//                .append(") VALUES ('Ami');")
//                .toString();
//
//        db.execSQL(sqlStatement);
//        Log.d(LOG_TAG, "Item added in table");
//
//
//        sqlStatement = "";
//
//        this.sqlStatement = stringBuilder.append("INSERT INTO ")
//                .append(TABLE_NAME)
//                .append(" (")
//                .append(COLUMN_NAME)
//                .append(") VALUES ('Feliz');")
//                .toString();
//
//        db.execSQL(sqlStatement);
//        Log.d(LOG_TAG, "Item added in table");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addTestData() {

        Log.d(LOG_TAG, "DatabaseHelper.addTestData() call");

        SQLiteDatabase db = getWritableDatabase();

        sqlStatement = "";

        this.sqlStatement = stringBuilder.append("INSERT INTO ")
                .append(TABLE_NAME)
                .append(" (")
                .append(COLUMN_NAME)
                .append(") VALUES ('Ami');")
                .toString();

        db.execSQL(sqlStatement);
    }

    StringBuilder getStringBuilder() {
        return stringBuilder;
    }
}
