package com.yaroslavm87.dogwalker.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yaroslavm87.dogwalker.commands.PassValToSubscriber;
import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Observable;
import com.yaroslavm87.dogwalker.notifications.Publisher;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDbAdapter implements Repository<List<Dog>>, Observable {

    private final SQLiteDb dbHelper;
    private SQLiteDatabase database;
    private final ContentValues contentValues;

    private final Publisher publisher;

    // TODO: убрать буффер добавив метод получения из БД объекта (запрос + десериализация)
    private Dog lastDogMovedBuffer;

    private final String LOG_TAG;

    {
        this.publisher = Publisher.INSTANCE;
        this.LOG_TAG = "myLogs";
    }


    public SQLiteDbAdapter(Context context){

        Log.d(LOG_TAG, "SQLiteDbAdapter() constructor call");

        Log.d(LOG_TAG, "SQLiteDb() constructor call from SQLiteDbAdapter");
        dbHelper = new SQLiteDb(context.getApplicationContext(), 1);
        contentValues = new ContentValues();
    }

    @Override
    public ArrayList<Dog> read() {

        Log.d(LOG_TAG, "SQLiteDbAdapter.read() call");

        databaseOpen();

        //database.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_NAME);

        ArrayList<Dog> dogList = new ArrayList<>();

        Cursor cursor = getAllEntries();

        while (cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndex(SQLiteDb.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(SQLiteDb.COLUMN_NAME));
//            int imageResId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_RESOURCE_ID));
//            int lastTimeWalk = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_LAST_TIME_WALK));
            Dog dog = new Dog(name);
            dog.setId(id);
            dogList.add(dog);

            //dogList.add(new Dog(id, name, imageResId, lastTimeWalk));
        }

        cursor.close();
        databaseClose();

        return dogList;
    }

    @Override
    public void add(Dog d) {

        Log.d(LOG_TAG, "SQLiteDbAdapter.add() call");

        databaseOpen();

        contentValues.clear();
        contentValues.put(SQLiteDb.COLUMN_NAME, d.getName());
//        contentValues.put(DatabaseHelper.COLUMN_IMAGE_RESOURCE_ID, dog.getImageResId());
//        contentValues.put(DatabaseHelper.COLUMN_LAST_TIME_WALK, dog.getLastTimeWalk());

        database.insert(SQLiteDb.TABLE_NAME, null, contentValues);

        databaseClose();

        this.setLastDogMovedBuffer(d);

        this.publisher.notifyEventHappened(this, Event.REPO_LIST_DOGS_ITEM_ADDED);
    }

    @Override
    public void update(Dog dog) {

        Log.d(LOG_TAG, "SQLiteDbAdapter.update() call");

        String whereClause = dbHelper.getStringBuilder()
                .append(SQLiteDb.COLUMN_ID)
                .append("=")
                .append(dog.getId())
                .toString();

        contentValues.clear();
        contentValues.put(SQLiteDb.COLUMN_NAME, dog.getName());
        contentValues.put(SQLiteDb.COLUMN_IMAGE_RESOURCE_ID, dog.getImageResId());
        contentValues.put(SQLiteDb.COLUMN_LAST_TIME_WALK, dog.getLastTimeWalk());

        database.update(SQLiteDb.TABLE_NAME, contentValues, whereClause, null);
    }

    @Override
    public void delete(Dog d) {

        Log.d(LOG_TAG, "SQLiteDbAdapter.delete() call");

        databaseOpen();

        String whereClause = "NAME = ?";

        String[] whereArgs = new String[] {d.getName()};

        database.delete(SQLiteDb.TABLE_NAME, whereClause, whereArgs);

        databaseClose();

        this.setLastDogMovedBuffer(d);

        this.publisher.notifyEventHappened(this, Event.REPO_LIST_DOGS_ITEM_DELETED);
    }

    public long getCount(){

        return DatabaseUtils.queryNumEntries(database, SQLiteDb.TABLE_NAME);
    }


//    public void addTestData() {
//
//        Log.d(LOG_TAG, "DatabaseAdapter.addTestData() call");
//
//        StringBuilder sb = dbHelper.getStringBuilder();
//        String sqlStatement = "";
//
//        databaseOpen();
//
//        sqlStatement = sb.append("INSERT INTO ")
//                .append(DatabaseHelper.TABLE_NAME)
//                .append(" (")
//                .append(DatabaseHelper.COLUMN_NAME)
//                .append(") VALUES ('Ami');")
//                .toString();
//
//        database.execSQL(sqlStatement);
//
//        databaseClose();
//    }

    private void databaseOpen(){

        Log.d(LOG_TAG, "SQLiteDbAdapter.databaseOpen() call");

        database = dbHelper.getWritableDatabase();
    }

    private void databaseClose(){

        Log.d(LOG_TAG, "SQLiteDbAdapter.databaseClose() call");

        dbHelper.close();

    }

    private Cursor getAllEntries(){

        Log.d(LOG_TAG, "SQLiteDbAdapter.getAllEntries() call");

        String[] columns = new String[] {
                SQLiteDb.COLUMN_ID,
                SQLiteDb.COLUMN_NAME
//                DatabaseHelper.COLUMN_IMAGE_RESOURCE_ID,
//                DatabaseHelper.COLUMN_LAST_TIME_WALK
        };

        return  database.query(
                SQLiteDb.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public PassValToSubscriber prepareCommandForUpdate(Event event) {

        return getAppropriateCommand(event);
    }

    private PassValToSubscriber getAppropriateCommand(Event event) {

        switch (event) {

//            case MODEL_LIST_DOGS_CHANGED:
//                return (observable, subscriber) -> subscriber.receiveUpdate(event, this.read());

            case REPO_LIST_DOGS_ITEM_ADDED:
            case REPO_LIST_DOGS_ITEM_DELETED:

                Log.d(LOG_TAG, "SQLiteDbAdapter.getAppropriateCommand() call");

                return (observable, subscriber) -> subscriber.receiveUpdate(event, this.getLastDogMovedBuffer());

            default:
                return null;
        }
    }

    private void setLastDogMovedBuffer(Dog d) {
        this.lastDogMovedBuffer = d;
    }

    private Dog getLastDogMovedBuffer() {

        Dog result = this.lastDogMovedBuffer;

        this.lastDogMovedBuffer = null;

        return result;
    }
}
