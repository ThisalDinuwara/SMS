package student;

import dbConnection.MyConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Score {

    private Connection con;
    private PreparedStatement ps;

    public Score() {
        con = MyConnection.getConnection();
        if (con == null) {
            System.out.println("Failed to connect to the database.");
        }
    }

    // Get next available ID
    public int getMax() {
        int id = 0;
        if (con == null) {
            return id;
        }
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT MAX(id) FROM score")) {
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Score.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id + 1;
    }

    // Fetch student by ID
    public boolean getDetails(int sid, int semesterNo) {
        try {
            ps = con.prepareStatement("SELECT * FROM course WHERE student_id = ? and semester = ?");
            ps.setInt(1, sid);
            ps.setInt(2, semesterNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Home.jTextField12.setText(String.valueOf(rs.getInt(2)));
                Home.jTextField16.setText(String.valueOf(rs.getInt(3)));
                Home.jTextCourse1.setText(rs.getString(4));
                Home.jTextCourse2.setText(rs.getString(5));
                Home.jTextCourse3.setText(rs.getString(6));
                Home.jTextCourse4.setText(rs.getString(7));
                Home.jTextCourse5.setText(rs.getString(8));
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Student ID or semester number doesn't exist.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Score.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // Check score id is already exists
    public boolean isIdExist(int id) {
        try {
            ps = con.prepareStatement("SELECT * FROM score WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(Score.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // Check whether the student id or semester number exist ot not
    public boolean isSidSemesterNoExist(int sid, int semesterNo) {
        try {
            ps = con.prepareStatement("SELECT * FROM score WHERE student_id = ? AND semester = ?");
            ps.setInt(1, sid);
            ps.setInt(2, semesterNo);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(Score.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // Insert score data into score table
    public void insert(int id, int sid, int semester, String course1, String course2,
            String course3, String course4, String course5, double score1, double score2,
            double score3, double score4, double score5, double average) {
        String sql = "INSERT INTO score VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setInt(2, sid);
            ps.setInt(3, semester);
            ps.setString(4, course1);
            ps.setDouble(5, score1);
            ps.setString(6, course2);
            ps.setDouble(7, score2);
            ps.setString(8, course3);
            ps.setDouble(9, score3);
            ps.setString(10, course4);
            ps.setDouble(11, score4);
            ps.setString(12, course5);
            ps.setDouble(13, score5);
            ps.setDouble(14, average);

            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Score added successfully");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Score.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Load score data into JTable
    public void getScoreValue(JTable table, String searchValue) {
        String sql = "SELECT * FROM score WHERE CONCAT(id, student_id, semester) LIKE ? ORDER BY id DESC";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + searchValue + "%");
            ResultSet rs = ps.executeQuery();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            Object[] row;
            while (rs.next()) {
                row = new Object[14];
                row[0] = rs.getInt("id");
                row[1] = rs.getInt("student_id");
                row[2] = rs.getInt("semester");
                row[3] = rs.getString("course1");
                row[4] = rs.getString("Score1");
                row[5] = rs.getString("course2");
                row[6] = rs.getString("Score2");
                row[7] = rs.getString("course3");
                row[8] = getIntOrString(rs, "Score3");  // Safe conversion
                row[9] = getIntOrString(rs, "course4"); // Safe conversion
                row[10] = getIntOrString(rs, "Score4"); // Safe conversion
                row[11] = getIntOrString(rs, "course5"); // Safe conversion
                row[12] = getIntOrString(rs, "Score5"); // Safe conversion
                row[13] = getIntOrString(rs, "average"); // Safe conversion
                model.addRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Score.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

// Helper method to safely convert database values
    private Object getIntOrString(ResultSet rs, String columnName) {
        try {
            String value = rs.getString(columnName);
            if (value == null || value.trim().isEmpty()) {
                return 0; // or return empty string ""
            }

            // Try to parse as integer
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                // If it's not a number, return as string
                return value;
            }
        } catch (SQLException e) {
            Logger.getLogger(Score.class.getName()).log(Level.WARNING,
                    "Error reading column " + columnName, e);
            return ""; // or return 0
        }
    }

// Alternative: If you want to handle specific non-numeric values
    private Object getScoreValue(ResultSet rs, String columnName) {
        try {
            String value = rs.getString(columnName);
            if (value == null || value.trim().isEmpty()) {
                return 0;
            }

            value = value.trim();

            // Handle special cases
            switch (value.toUpperCase()) {
                case "AI":
                    return "AI"; // Keep as string
                case "N/A":
                case "NULL":
                    return 0;
                default:
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        return value; // Return as string if not numeric
                    }
            }
        } catch (SQLException e) {
            Logger.getLogger(Score.class.getName()).log(Level.WARNING,
                    "Error reading column " + columnName, e);
            return 0;
        }
    }

    // Update score
    public void update(int id, double score1, double score2, double score3, double score4, double score5, double average) {
        String sql = "UPDATE score SET score1=?, score2=?, score3=?, score4=?, score5=?, average=? WHERE id=?";
        try {
            ps = con.prepareStatement(sql);
            ps.setDouble(1, score1);
            ps.setDouble(2, score2);
            ps.setDouble(3, score3);
            ps.setDouble(4, score4);
            ps.setDouble(5, score5);
            ps.setDouble(6, average);
            ps.setInt(7, id);
            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Score updated successfully");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Score.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
