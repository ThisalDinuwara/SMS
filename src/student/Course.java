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

public class Course {

    private Connection con;
    private PreparedStatement ps;

    public Course() {
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
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT MAX(id) FROM course")) {
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Course.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id + 1;
    }

    // Fetch student by ID
    public boolean getId(int id) {
        try {
            ps = con.prepareStatement("SELECT * FROM student WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Home.jTextField11.setText(String.valueOf(rs.getInt(1)));
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Student ID doesn't exist.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Course.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // Count number of semesters for a student
    public int countSemester(int id) {
        int total = 0;
        try {
            ps = con.prepareStatement("SELECT COUNT(*) AS total FROM course WHERE student_id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
            if (total == 8) {
                JOptionPane.showMessageDialog(null, "This student has completed all the courses.");
                return -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Course.class.getName()).log(Level.SEVERE, null, ex);
        }
        return total;
    }

    //check whether the student has already taken this semester or not
    public boolean isSemesterExist(int sid, int semesterNo) {
        try {
            ps = con.prepareStatement("SELECT * FROM course WHERE student_id = ? and semester = ?");
            ps.setInt(1, sid);
            ps.setInt(2, semesterNo);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(Course.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    //check whether the student has already taken this course or not
    public boolean isCourseExist(int sid, String courseNo, String course) {
        String sql = "select * from course where student_id = ? and " + courseNo + " = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, sid);
            ps.setString(2, course);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(Course.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // Insert data into course table
    public void insert(int id, int sid, int semester, String course1, String course2,
            String course3, String course4, String course5) {
        String sql = "INSERT INTO course VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setInt(2, sid);
            ps.setInt(3, semester);
            ps.setString(4, course1);
            ps.setString(5, course2);
            ps.setString(6, course3);
            ps.setString(7, course4);
            ps.setString(8, course5);

            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Course added successfully");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Course.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Load course data into JTable
    public void getCourseValue(JTable table, String searchValue) {
        String sql = "SELECT * FROM course WHERE CONCAT(id, student_id, semester) LIKE ? ORDER BY id DESC";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + searchValue + "%");
            ResultSet rs = ps.executeQuery();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            Object[] row;

            while (rs.next()) {
                row = new Object[8];
                row[0] = rs.getInt("id");
                row[1] = rs.getInt("student_id");
                row[2] = rs.getInt("semester");
                row[3] = rs.getString("course1");
                row[4] = rs.getString("course2");
                row[5] = rs.getString("course3");
                row[6] = rs.getString("course4");
                row[7] = rs.getString("course5");

                model.addRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Course.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
