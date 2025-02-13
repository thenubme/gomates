package com.example.gomates.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gomates.models.Ride;
import com.example.gomates.models.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GoMatesDB";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_RIDES = "rides";

    // Common column names
    private static final String KEY_ID = "id";

    // Users Table Columns
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_NAME = "name";

    // Rides Table Columns
    private static final String KEY_ORIGIN = "origin";
    private static final String KEY_DESTINATION = "destination";
    private static final String KEY_DEPARTURE_TIME = "departure_time";
    private static final String KEY_FARE = "fare";
    private static final String KEY_AVAILABLE_SEATS = "available_seats";
    private static final String KEY_USER_ID = "user_id";

    // Create table statements
    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_EMAIL + " TEXT UNIQUE NOT NULL,"
            + KEY_PASSWORD + " TEXT NOT NULL,"
            + KEY_NAME + " TEXT NOT NULL"
            + ")";

    private static final String CREATE_RIDES_TABLE = "CREATE TABLE " + TABLE_RIDES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_ORIGIN + " TEXT NOT NULL,"
            + KEY_DESTINATION + " TEXT NOT NULL,"
            + KEY_DEPARTURE_TIME + " TEXT NOT NULL,"
            + KEY_FARE + " REAL NOT NULL,"
            + KEY_AVAILABLE_SEATS + " INTEGER NOT NULL,"
            + KEY_USER_ID + " INTEGER NOT NULL,"
            + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + ")"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_RIDES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIDES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // User operations
    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_NAME, user.getName());

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, KEY_EMAIL + "=?",
                new String[]{email}, null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
            user.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            cursor.close();
        }
        db.close();
        return user;
    }

    // Ride operations
    public long insertRide(Ride ride) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ORIGIN, ride.getOrigin());
        values.put(KEY_DESTINATION, ride.getDestination());
        values.put(KEY_DEPARTURE_TIME, ride.getDepartureTime());
        values.put(KEY_FARE, ride.getFare());
        values.put(KEY_AVAILABLE_SEATS, ride.getAvailableSeats());
        values.put(KEY_USER_ID, ride.getUserId());

        long id = db.insert(TABLE_RIDES, null, values);
        db.close();
        return id;
    }

    public List<Ride> getAllRides() {
        List<Ride> rides = new ArrayList<>();
        String selectQuery = "SELECT r.*, u." + KEY_NAME + " as driver_name FROM " + TABLE_RIDES + " r"
                + " INNER JOIN " + TABLE_USERS + " u ON r." + KEY_USER_ID + " = u." + KEY_ID
                + " ORDER BY " + KEY_DEPARTURE_TIME + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Ride ride = new Ride();
                ride.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                ride.setOrigin(cursor.getString(cursor.getColumnIndex(KEY_ORIGIN)));
                ride.setDestination(cursor.getString(cursor.getColumnIndex(KEY_DESTINATION)));
                ride.setDepartureTime(cursor.getString(cursor.getColumnIndex(KEY_DEPARTURE_TIME)));
                ride.setFare(cursor.getDouble(cursor.getColumnIndex(KEY_FARE)));
                ride.setAvailableSeats(cursor.getInt(cursor.getColumnIndex(KEY_AVAILABLE_SEATS)));
                ride.setUserId(cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)));
                ride.setDriverName(cursor.getString(cursor.getColumnIndex("driver_name")));
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
                new String[]{id}, null, null, null);

        Ride ride = null;
        if (cursor != null && cursor.moveToFirst()) {
            ride = new Ride();
            ride.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
            ride.setOrigin(cursor.getString(cursor.getColumnIndex(KEY_ORIGIN)));
            ride.setDestination(cursor.getString(cursor.getColumnIndex(KEY_DESTINATION)));
            ride.setDepartureTime(cursor.getString(cursor.getColumnIndex(KEY_DEPARTURE_TIME)));
            ride.setFare(cursor.getDouble(cursor.getColumnIndex(KEY_FARE)));
            ride.setAvailableSeats(cursor.getInt(cursor.getColumnIndex(KEY_AVAILABLE_SEATS)));
            ride.setUserId(cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)));
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
        values.put(KEY_DEPARTURE_TIME, ride.getDepartureTime());
        values.put(KEY_FARE, ride.getFare());
        values.put(KEY_AVAILABLE_SEATS, ride.getAvailableSeats());

        int rowsAffected = db.update(TABLE_RIDES, values, KEY_ID + "=? AND " + KEY_USER_ID + "=?",
                new String[]{String.valueOf(ride.getId()), String.valueOf(ride.getUserId())});
        db.close();
        return rowsAffected;
    }

    public void deleteRide(String id, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RIDES, KEY_ID + "=? AND " + KEY_USER_ID + "=?",
                new String[]{id, String.valueOf(userId)});
        db.close();
    }
}