package com.example.gomates.database;

import android.util.Log;
import com.example.gomates.models.Ride;
import com.example.gomates.models.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLDatabaseHelper {
    private static final String TAG = "MySQLDatabaseHelper";
    private static final String HOST = "sql12.freesqldatabase.com";
    private static final String DATABASE = "sql12762099";
    private static final String USER = "sql12762099";
    private static final String PASSWORD = "fVlHYMv9cA";
    private static final int PORT = 3306;

    private static Connection getConnection() throws SQLException {
        String url = String.format("jdbc:mysql://%s:%d/%s", HOST, PORT, DATABASE);
        return DriverManager.getConnection(url, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            try (Connection conn = getConnection()) {
                // Create Users table
                String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "email VARCHAR(255) UNIQUE NOT NULL,"
                        + "password VARCHAR(255) NOT NULL,"
                        + "name VARCHAR(255) NOT NULL)";
                
                // Create Rides table
                String createRidesTable = "CREATE TABLE IF NOT EXISTS rides ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "origin VARCHAR(255) NOT NULL,"
                        + "destination VARCHAR(255) NOT NULL,"
                        + "fare DOUBLE NOT NULL,"
                        + "driver_id VARCHAR(255) NOT NULL,"
                        + "timestamp VARCHAR(255) NOT NULL)";
                
                conn.createStatement().execute(createUsersTable);
                conn.createStatement().execute(createRidesTable);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database", e);
        }
    }

    // User Operations
    public static long insertUser(User user) {
        String sql = "INSERT INTO users (email, password, name) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getName());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error inserting user", e);
        }
        return -1;
    }

    public static User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(String.valueOf(rs.getInt("id")));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setName(rs.getString("name"));
                return user;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting user by email", e);
        }
        return null;
    }

    // Ride Operations
    public static long insertRide(Ride ride) {
        String sql = "INSERT INTO rides (origin, destination, fare, driver_id, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, ride.getOrigin());
            stmt.setString(2, ride.getDestination());
            stmt.setDouble(3, ride.getFare());
            stmt.setString(4, ride.getDriverId());
            stmt.setString(5, ride.getTimestamp());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error inserting ride", e);
        }
        return -1;
    }

    public static List<Ride> getAllRides() {
        List<Ride> rides = new ArrayList<>();
        String sql = "SELECT * FROM rides ORDER BY timestamp DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Ride ride = new Ride();
                ride.setId(String.valueOf(rs.getInt("id")));
                ride.setOrigin(rs.getString("origin"));
                ride.setDestination(rs.getString("destination"));
                ride.setFare(rs.getDouble("fare"));
                ride.setDriverId(rs.getString("driver_id"));
                ride.setTimestamp(rs.getString("timestamp"));
                rides.add(ride);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting all rides", e);
        }
        return rides;
    }
}
