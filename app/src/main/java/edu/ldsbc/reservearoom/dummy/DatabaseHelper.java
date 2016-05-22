package edu.ldsbc.reservearoom.dummy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Ross Higley on 5/10/2016.
 * Will be used to create, write to, read from, and otherwise maintain this app's SQLite database.
 * You can retrieve a database instance by calling the getReadableDatabase() and getWritableDatabase() methods.
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
            + ");";

    /**
     * Constructor. Borrowed from http://www.tutorialspoint.com/android/android_sqlite_database.htm.
     */
    public DatabaseHelper() {
        super(App.getContext(), DATABASE_NAME, null, 1);
    }

    /**
     * Overloaded constructor. Use the default instead.
     *
     * @param context context for database. use App.getContext();
     * @param name    name of Database. use DatabaseHelper.DATABASE_NAME.
     * @param factory Cursor factory. use null. it will create one for us.
     * @param version version number for this database. be consistent, or use 1.
     */
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * Creates new table from the SQL code above.
     *
     * @param db database to create table inside.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    /**
     * Upgrades table version, by deleting and recreating. Don't forget to insert new records.
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_ROOMS + ";");
        onCreate(db);
    }

    /**
     * Adds room to the database.
     *
     * @param roomName name of room to add.
     */
    public void insertRoom(String roomName) {
        SQLiteDatabase db = this.getWritableDatabase(); // get the database for this app.
        ContentValues cv = new ContentValues(); // prepare new record to add.

        // put all fields except ID (which is autoincremented) into record.
        cv.put(COL_NAME, roomName);

        // add that record to the table.
        db.insert(TABLE_ROOMS, null, cv);

        // if app is in Debug Mode, print to console.
        if (App.DEBUG_MODE) {
            Log.i("Database", "inserting " + roomName + " into Database. ");
        }
    }

    /**
     * Retrieves Names of all rooms in the table.
     *
     * @return
     */
    public ArrayList<String> getAllRooms() {
        ArrayList<String> list = new ArrayList<String>(); // create empty list

        SQLiteDatabase db = this.getReadableDatabase(); // get database.
        Cursor res = db.rawQuery(" SELECT * FROM " + TABLE_ROOMS + ";", null); // get all records from table.
        res.moveToFirst(); // move to first record.

        while (res.isAfterLast()) { // for all records in table.
            list.add(res.getString(res.getColumnIndex(COL_NAME))); // get name from record and add it to the list.

            // if App is in debug mode, print to console.
            if (App.DEBUG_MODE) {
                Log.i("Database", "Loading " + res.getString(res.getColumnIndex(COL_NAME)) + " from database.");
            }
            res.moveToNext(); // move to next record and restart loop.
        }
        res.close(); // close connection to database.
        return list; // return the list of names.

    }

    /**
     * removes all records from the table.
     */
    public void deleteAllRooms() {
        SQLiteDatabase db = this.getWritableDatabase(); // get database
        db.delete(TABLE_ROOMS, null, null); // deletes all rows in table.

        if (App.DEBUG_MODE) { // tell user if in debug mode.
            Log.i("Database", "deleting all records from database. ");
        }
    }

    /**
     * returns true if database has no records, false if it has records.
     */
    public boolean isEmpty() {
        SQLiteDatabase db = this.getReadableDatabase(); // get database
        Cursor res = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ROOMS + ";", null); // count how many records are in table.
        res.moveToFirst(); // go to first result (should only be one anyway).
        String temp = res.getString(0); // get contents of first result.

        res.close(); // close connection to table.
        int amount = Integer.parseInt(temp); // cast result to integer.

        if (App.DEBUG_MODE) { // if app is in debug mode, tell user the state of the database.
            if (amount == 0) {
                Log.i("Database", "Database is empty. ");
            } else {
                Log.i("Database", "Database has rooms in it. ");
            }
        }
        // if app is not in debug mode, do nothing.
        // either way, return true if count of records is 0, false if it's anything else.
        return (amount == 0);
    }

}
