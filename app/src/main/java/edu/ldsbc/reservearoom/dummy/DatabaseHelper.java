package edu.ldsbc.reservearoom.dummy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by higle on 5/10/2016.
 * Will be used to create, write to, read from, and otherwise maintain this app's SQLite database.
 *
 * You can retrieve a database instance by calling the getReadableDatabase() and getWritableDatabase() methods.
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Permanent database table names, field names, etc...
    private static final String DATABASE_NAME = "Rooms.db";
    private static final String TABLE_ROOMS = "table_rooms";
    private static final String COL_ID = "_ID";
    private static final String COL_NAME = "NAME";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_ROOMS
            + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NAME + " TEXT NOT NULL "
            +");";


    // constructor. borrowed from http://www.tutorialspoint.com/android/android_sqlite_database.htm.
    public DatabaseHelper(){

        super(App.getContext(), DATABASE_NAME,null,1);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_ROOMS + ";");
        onCreate(db);
    }

    public void insertRoom(String roomName) {
       SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        // put all fields except ID (which is autoincremented).
        cv.put(COL_NAME, roomName);

        db.insert(TABLE_ROOMS,null,cv);

        Log.i("Database", "inserting " + roomName + " into Database. ");
    }

    public ArrayList<String> getAllRooms() {
        ArrayList<String> list = new ArrayList<String>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(" SELECT * FROM " + TABLE_ROOMS + ";", null);
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            list.add(res.getString(res.getColumnIndex(COL_NAME)));

            Log.i("Database", "Loading " + res.getString(res.getColumnIndex(COL_NAME)) + " from database.");

            res.moveToNext();
        }
        res.close();
        return list;

    }

    public void deleteAllRooms() {
        SQLiteDatabase db = this.getWritableDatabase();
     //   Cursor res = db.rawQuery("DELETE FROM " + TABLE_ROOMS + ";", null);
     //   res.close();
        db.delete(TABLE_ROOMS, null, null); // deletes all rows in table.

        Log.i("Database", "deleting all records from database. ");
    }

    /**
     *  returns true if database has no rows, false if it has rows.
     */
    public boolean isEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ROOMS + ";", null);
        res.moveToFirst();
        String temp = res.getString(0);

        res.close();
        int amount = Integer.parseInt(temp);


        if (amount == 0) {
            Log.i("Database", "Database is empty. ");
            return (true);
        } else {
            Log.i("Database", "Database has rooms in it. ");
            return (false);
        }
    }

}
