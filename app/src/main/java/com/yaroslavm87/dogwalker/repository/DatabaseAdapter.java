package com.yaroslavm87.dogwalker.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.yaroslavm87.dogwalker.model.Dog;
import java.util.ArrayList;

public class DatabaseAdapter implements Repository {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private ContentValues contentValues;

    public DatabaseAdapter(Context context){
        dbHelper = new DatabaseHelper(context.getApplicationContext(), 1);
        contentValues = new ContentValues();
    }

    @Override
    public ArrayList<Dog> read() {

        databaseOpen();

        ArrayList<Dog> dogs = new ArrayList<>();

        Cursor cursor = getAllEntries();

        while (cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            int imageResId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_RESOURCE_ID));
            int lastTimeWalk = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_LAST_TIME_WALK));

            dogs.add(new Dog(id, name, imageResId, lastTimeWalk));
        }

        cursor.close();
        databaseClose();

        return dogs;
    }

    @Override
    public void add(Dog dog) {

        contentValues.clear();
        contentValues.put(DatabaseHelper.COLUMN_NAME, dog.getName());
        contentValues.put(DatabaseHelper.COLUMN_IMAGE_RESOURCE_ID, dog.getImageResId());
        contentValues.put(DatabaseHelper.COLUMN_LAST_TIME_WALK, dog.getLastTimeWalk());

        database.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
    }

    @Override
    public void update(Dog dog) {
        String whereClause = dbHelper.getStringBuilder()
                .append(DatabaseHelper.COLUMN_ID)
                .append("=")
                .append(dog.getId())
                .toString();

        contentValues.clear();
        contentValues.put(DatabaseHelper.COLUMN_NAME, dog.getName());
        contentValues.put(DatabaseHelper.COLUMN_IMAGE_RESOURCE_ID, dog.getImageResId());
        contentValues.put(DatabaseHelper.COLUMN_LAST_TIME_WALK, dog.getLastTimeWalk());

        database.update(DatabaseHelper.TABLE_NAME, contentValues, whereClause, null);
    }

    @Override
    public void delete(Dog dog) {

        String whereClause = "_id = ?";

        String[] whereArgs = new String[] {String.valueOf(dog.getId())};

        database.delete(DatabaseHelper.TABLE_NAME, whereClause, whereArgs);
    }

        public long getCount(){
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE_NAME);
    }

    private void databaseOpen(){
        database = dbHelper.getWritableDatabase();
    }

    private void databaseClose(){
        dbHelper.close();
    }

    private Cursor getAllEntries(){

        String[] columns = new String[] {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_NAME,
                DatabaseHelper.COLUMN_IMAGE_RESOURCE_ID,
                DatabaseHelper.COLUMN_LAST_TIME_WALK
        };

        return  database.query(
                DatabaseHelper.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );
    }
}
