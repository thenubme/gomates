package com.example.gomates.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gomates.models.Ride;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GoMatesDB";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_RIDES = "rides";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_ORIGIN = "origin";
    private static final String KEY_DESTINATION = "destination";
    private static final String KEY_FARE = "fare";
    private static final String KEY_DRIVER_ID = "driver_id";
    private static final String KEY_TIMESTAMP = "timestamp";

    // Create table statements
    private static final String CREATE_RIDES_TABLE = "CREATE TABLE " + TABLE_RIDES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_ORIGIN + " TEXT,"
            + KEY_DESTINATION + " TEXT,"
            + KEY_FARE + " REAL,"
            + KEY_DRIVER_ID + " TEXT,"
            + KEY_TIMESTAMP + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RIDES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIDES);
        // Create tables again
        onCreate(db);
    }

    // CRUD Operations for Rides

    public long insertRide(Ride ride) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ORIGIN, ride.getOrigin());
        values.put(KEY_DESTINATION, ride.getDestination());
        values.put(KEY_FARE, ride.getFare());
        values.put(KEY_DRIVER_ID, ride.getDriverId());
        values.put(KEY_TIMESTAMP, ride.getTimestamp());

        long id = db.insert(TABLE_RIDES, null, values);
        db.close();
        return id;
    }

    public List<Ride> getAllRides() {
        List<Ride> rides = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RIDES + " ORDER BY " + KEY_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Ride ride = new Ride();
                ride.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                ride.setOrigin(cursor.getString(cursor.getColumnIndex(KEY_ORIGIN)));
                ride.setDestination(cursor.getString(cursor.getColumnIndex(KEY_DESTINATION)));
                ride.setFare(cursor.getDouble(cursor.getColumnIndex(KEY_FARE)));
                ride.setDriverId(cursor.getString(cursor.getColumnIndex(KEY_DRIVER_ID)));
                ride.setTimestamp(cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP)));
                rides.add(ride);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return rides;
    }

    public Ride getRide(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RIDES, null, KEY_ID + "=?",
                new String[]{id}, null, null, null, null);

        Ride ride = null;
        if (cursor != null && cursor.moveToFirst()) {
            ride = new Ride();
            ride.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
            ride.setOrigin(cursor.getString(cursor.getColumnIndex(KEY_ORIGIN)));
            ride.setDestination(cursor.getString(cursor.getColumnIndex(KEY_DESTINATION)));
            ride.setFare(cursor.getDouble(cursor.getColumnIndex(KEY_FARE)));
            ride.setDriverId(cursor.getString(cursor.getColumnIndex(KEY_DRIVER_ID)));
            ride.setTimestamp(cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP)));
            cursor.close();
        }
        db.close();
        return ride;
    }

    public int updateRide(Ride ride) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ORIGIN, ride.getOrigin());
        values.put(KEY_DESTINATION, ride.getDestination());
        values.put(KEY_FARE, ride.getFare());
        values.put(KEY_DRIVER_ID, ride.getDriverId());
        values.put(KEY_TIMESTAMP, ride.getTimestamp());

        int rowsAffected = db.update(TABLE_RIDES, values, KEY_ID + "=?",
                new String[]{String.valueOf(ride.getId())});
        db.close();
        return rowsAffected;
    }

    public void deleteRide(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RIDES, KEY_ID + "=?", new String[]{id});
        db.close();
    }
}
