package student;

import dbConnection.MyConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Student {
    private Connection con;
    private PreparedStatement ps;

    public Student() {
        con = MyConnection.getConnection();
        if (con == null) {
            System.out.println("Failed to connect to the database.");
        }
    }

    // Get next available ID
    public int getMax() {
        int id = 0;
        if (con == null) return id;
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

    // Insert new student
    public void insert(int id, String sname, String date, String gender, String email,
                       String phone, String father, String mother,
                       String address1, String address2, String imagePath) {
        String sql = "INSERT INTO student VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setString(2, sname);
            ps.setString(3, date);
            ps.setString(4, gender);
            ps.setString(5, email);
            ps.setString(6, phone);
            ps.setString(7, father);
            ps.setString(8, mother);
            ps.setString(9, address1);
            ps.setString(10, address2);
            ps.setString(11, imagePath);

            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "New student added successfully");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Update student info
    public void update(int id, String sname, String date, String gender,
                       String email, String phone, String father, String mother,
                       String address1, String address2, String imagePath) {
        String sql = "UPDATE student SET name=?, date_of_birth=?, gender=?, email=?, phone=?, "
                   + "father_name=?, mother_name=?, address1=?, address2=?, image_path=? WHERE id=?";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, sname);
            ps.setString(2, date);
            ps.setString(3, gender);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.setString(6, father);
            ps.setString(7, mother);
            ps.setString(8, address1);
            ps.setString(9, address2);
            ps.setString(10, imagePath);
            ps.setInt(11, id);

            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Student data updated successfully");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Check if email exists
    public boolean isEmailExist(String email) {
        try {
            ps = con.prepareStatement("SELECT * FROM student WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // Check if phone number already exists
    public boolean isPhoneExist(String phone) {
        try {
            ps = con.prepareStatement("SELECT * FROM student WHERE phone = ?");
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    // Check if phone number already exists
    public boolean isIdExist(int id) {
        try {
            ps = con.prepareStatement("SELECT * FROM student WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // Load student data into JTable
    public void getStudentValue(JTable table, String searchValue) {
        String sql = "SELECT * FROM student WHERE CONCAT(name, email, phone) LIKE ? ORDER BY id DESC";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + searchValue + "%");
            ResultSet rs = ps.executeQuery();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            Object[] row;

            while (rs.next()) {
                row = new Object[11];
                row[0] = rs.getInt("id");
                row[1] = rs.getString("name");
                row[2] = rs.getString("date_of_birth");
                row[3] = rs.getString("gender");
                row[4] = rs.getString("email");
                row[5] = rs.getString("phone");
                row[6] = rs.getString("father_name");
                row[7] = rs.getString("mother_name");
                row[8] = rs.getString("address1");
                row[9] = rs.getString("address2");
                row[10] = rs.getString("image_path");

                model.addRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //student data delete
    public void delete(int id){
        int yesOrNo = JOptionPane.showConfirmDialog(null, "Course and score records will also be deleted","Student Delete",JOptionPane.OK_CANCEL_OPTION,0);
        if(yesOrNo == JOptionPane.OK_OPTION){
            try {
                ps = con.prepareStatement("delete from student where id = ?");
                ps.setInt(1, id);
                if(ps.executeUpdate()>0){
                    JOptionPane.showMessageDialog(null, "Student data delete successfully");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
}


