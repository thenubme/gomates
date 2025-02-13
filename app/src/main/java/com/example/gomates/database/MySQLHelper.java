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
                    departure_time VARCHAR(50) NOT NULL,
                    fare DOUBLE NOT NULL,
                    available_seats INT NOT NULL,
                    user_id INT NOT NULL
                )
            """;
            
            try (PreparedStatement stmt = conn.prepareStatement(createRidesTable)) {
                stmt.execute();
            }

            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL
                )
            """;

            try (PreparedStatement stmt = conn.prepareStatement(createUsersTable)) {
                stmt.execute();
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error initializing database", e);
        }
    }

    // Insert a new ride
    public static long insertRide(Ride ride) {
        String sql = "INSERT INTO rides (origin, destination, departure_time, fare, available_seats, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, ride.getOrigin());
            stmt.setString(2, ride.getDestination());
            stmt.setString(3, ride.getDepartureTime());
            stmt.setDouble(4, ride.getFare());
            stmt.setInt(5, ride.getAvailableSeats());
            stmt.setInt(6, ride.getUserId());

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

    // Get all available rides
    public static List<Ride> getAllRides() {
        List<Ride> rides = new ArrayList<>();
        String sql = "SELECT r.*, u.name as driver_name FROM rides r " +
                    "JOIN users u ON r.user_id = u.id " +
                    "WHERE r.departure_time > NOW() " +
                    "ORDER BY r.departure_time ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Ride ride = new Ride();
                ride.setId(String.valueOf(rs.getInt("id")));
                ride.setOrigin(rs.getString("origin"));
                ride.setDestination(rs.getString("destination"));
                ride.setDepartureTime(rs.getString("departure_time"));
                ride.setFare(rs.getDouble("fare"));
                ride.setAvailableSeats(rs.getInt("available_seats"));
                ride.setUserId(rs.getInt("user_id"));
                ride.setDriverName(rs.getString("driver_name"));
                rides.add(ride);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting rides", e);
        }
        return rides;
    }

    // Get rides posted by a specific user
    public static List<Ride> getUserRides(int userId) {
        List<Ride> rides = new ArrayList<>();
        String sql = "SELECT r.*, u.name as driver_name FROM rides r " +
                    "JOIN users u ON r.user_id = u.id " +
                    "WHERE r.user_id = ? " +
                    "ORDER BY r.departure_time DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Ride ride = new Ride();
                    ride.setId(String.valueOf(rs.getInt("id")));
                    ride.setOrigin(rs.getString("origin"));
                    ride.setDestination(rs.getString("destination"));
                    ride.setDepartureTime(rs.getString("departure_time"));
                    ride.setFare(rs.getDouble("fare"));
                    ride.setAvailableSeats(rs.getInt("available_seats"));
                    ride.setUserId(rs.getInt("user_id"));
                    ride.setDriverName(rs.getString("driver_name"));
                    rides.add(ride);
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting user rides", e);
        }
        return rides;
    }

    // Update a ride's details
    public static boolean updateRide(Ride ride) {
        String sql = "UPDATE rides SET origin = ?, destination = ?, departure_time = ?, " +
                    "fare = ?, available_seats = ? WHERE id = ? AND user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ride.getOrigin());
            stmt.setString(2, ride.getDestination());
            stmt.setString(3, ride.getDepartureTime());
            stmt.setDouble(4, ride.getFare());
            stmt.setInt(5, ride.getAvailableSeats());
            stmt.setInt(6, Integer.parseInt(ride.getId()));
            stmt.setInt(7, ride.getUserId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error updating ride", e);
            return false;
        }
    }

    // Delete a ride
    public static boolean deleteRide(String rideId, int userId) {
        String sql = "DELETE FROM rides WHERE id = ? AND user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(rideId));
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error deleting ride", e);
            return false;
        }
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
                    ride.setDepartureTime(rs.getString("departure_time"));
                    ride.setFare(rs.getDouble("fare"));
                    ride.setAvailableSeats(rs.getInt("available_seats"));
                    ride.setUserId(rs.getInt("user_id"));
                    return ride;
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting ride by id", e);
        }
        return null;
    }
}