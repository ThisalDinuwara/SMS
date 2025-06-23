package student;

import dbConnection.MyConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Student {
    private Connection con;
    private PreparedStatement ps;

    // Constructor to initialize the connection
    public Student() {
        con = MyConnection.getConnection();
        if (con == null) {
            System.out.println("Failed to connect to the database.");
        }
    }

    // Get max ID from student table
    public int getMax() {
        int id = 0;

        if (con == null) {
            System.out.println("Connection is null. Cannot fetch max ID.");
            return id;
        }

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT MAX(id) FROM student")) {

            if (rs.next()) {
                id = rs.getInt(1);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
        }

        return id + 1;
    }
}
