/**
 * Liam Wild
 * CEN 3024 - Software Development 1
 * July 15, 2025
 * CheckoutManager.java
 * This class manages all SQLite database operations for the Audio Gear Checkout System.
 * It supports adding, deleting, loading, updating (returned status), and listing overdue gear records.
 */

import java.sql.*;
import java.util.ArrayList;

public class CheckoutManager {
    private Connection conn;

    // constructor
    // purpose: establishes SQLite connection using the user-supplied path
    // parameters: dbPath - full path to SQLite .db file
    // return: none
    public CheckoutManager(String dbPath) {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            createTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // method: createTableIfNotExists
    // purpose: creates the checkout_records table if it doesn't already exist
    // parameters: none
    // return: void
    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS checkout_records (" +
                "name TEXT NOT NULL," +
                "gear TEXT NOT NULL," +
                "checkoutDate TEXT NOT NULL," +
                "dueDate TEXT NOT NULL," +
                "returned TEXT NOT NULL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // method: addRecord
    // purpose: inserts a new checkout record into the database
    // parameters: name, gear, checkoutDate, dueDate, returned (all Strings)
    // return: void
    public void addRecord(String name, String gear, String checkoutDate, String dueDate, String returned) {
        String sql = "INSERT INTO checkout_records (name, gear, checkoutDate, dueDate, returned) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, gear);
            pstmt.setString(3, checkoutDate);
            pstmt.setString(4, dueDate);
            pstmt.setString(5, returned);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // method: loadAllRecords
    // purpose: retrieves all records from the database
    // parameters: none
    // return: ArrayList<String[]> containing all records
    public ArrayList<String[]> loadAllRecords() {
        ArrayList<String[]> records = new ArrayList<>();
        String sql = "SELECT name, gear, checkoutDate, dueDate, returned FROM checkout_records";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String[] row = {
                        rs.getString("name"),
                        rs.getString("gear"),
                        rs.getString("checkoutDate"),
                        rs.getString("dueDate"),
                        rs.getString("returned")
                };
                records.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    // method: deleteRecord
    // purpose: deletes a checkout record based on name, gear, and checkout date
    // parameters: name, gear, checkoutDate (all Strings)
    // return: void
    public void deleteRecord(String name, String gear, String checkoutDate) {
        String sql = "DELETE FROM checkout_records WHERE name = ? AND gear = ? AND checkoutDate = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, gear);
            pstmt.setString(3, checkoutDate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // method: listOverdueGear
    // purpose: returns a list of overdue gear items that have not been returned
    // parameters: today (String date in YYYY-MM-DD format)
    // return: String report of overdue gear
    public String listOverdueGear(String today) {
        StringBuilder result = new StringBuilder();
        String sql = "SELECT * FROM checkout_records WHERE dueDate < ? AND returned = 'No'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, today);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.append("Name: ").append(rs.getString("name"))
                        .append(", Gear: ").append(rs.getString("gear"))
                        .append(", Due: ").append(rs.getString("dueDate"))
                        .append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result.length() == 0) {
            return "No overdue gear found.";
        }
        return result.toString();
    }

    // method: updateReturnStatus
    // purpose: updates the 'returned' status to 'Yes' for a specific record
    // parameters: name, gear, checkoutDate, returned (all Strings)
    // return: void
    public void updateReturnStatus(String name, String gear, String checkoutDate, String returned) {
        String sql = "UPDATE checkout_records SET returned = ? WHERE name = ? AND gear = ? AND checkoutDate = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, returned);
            pstmt.setString(2, name);
            pstmt.setString(3, gear);
            pstmt.setString(4, checkoutDate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
