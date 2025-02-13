package com.example.gomates.database;

import android.util.Log;
import com.example.gomates.models.Ride;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLHelper {
    private static final String TAG = "MySQLHelper";
    private static final String HOST = "sql12.freesqldatabase.com";
    private static final String DATABASE = "sql12762099";
    private static final String USER = "sql12762099";
    private static final String PASSWORD = "fVlHYMv9cA";
    private static final int PORT = 3306;

    private static Connection getConnection() throws SQLException {
        String url = String.format("jdbc:mysql://%s:%d/%s", HOST, PORT, DATABASE);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(url, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "MySQL JDBC Driver not found.", e);
            throw new SQLException("MySQL JDBC Driver not found.");
        }
    }

    // Create tables if they don't exist
    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            String createRidesTable = """
                CREATE TABLE IF NOT EXISTS rides (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    origin VARCHAR(255) NOT NULL,
                    destination VARCHAR(255) NOT NULL,
                    fare DOUBLE NOT NULL,
                    driver_id VARCHAR(255) NOT NULL,
                    timestamp VARCHAR(50) NOT NULL
                )
            """;
            
            try (PreparedStatement stmt = conn.prepareStatement(createRidesTable)) {
                stmt.execute();
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error initializing database", e);
        }
    }

    // Insert a new ride
    public static long insertRide(Ride ride) {
        String sql = "INSERT INTO rides (origin, destination, fare, driver_id, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, ride.getOrigin());
            stmt.setString(2, ride.getDestination());
            stmt.setDouble(3, ride.getFare());
            stmt.setString(4, ride.getDriverId());
            stmt.setString(5, ride.getTimestamp());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error inserting ride", e);
        }
        return -1;
    }

    // Get all rides
    public static List<Ride> getAllRides() {
        List<Ride> rides = new ArrayList<>();
        String sql = "SELECT * FROM rides ORDER BY timestamp DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
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
            Log.e(TAG, "Error getting rides", e);
        }
        return rides;
    }

    // Get a specific ride by ID
    public static Ride getRide(String id) {
        String sql = "SELECT * FROM rides WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, Integer.parseInt(id));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Ride ride = new Ride();
                    ride.setId(String.valueOf(rs.getInt("id")));
                    ride.setOrigin(rs.getString("origin"));
                    ride.setDestination(rs.getString("destination"));
                    ride.setFare(rs.getDouble("fare"));
                    ride.setDriverId(rs.getString("driver_id"));
                    ride.setTimestamp(rs.getString("timestamp"));
                    return ride;
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting ride by id", e);
        }
        return null;
    }
}
