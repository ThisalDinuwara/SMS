package student;

import dbConnection.MyConnection; // Add this import
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MarksSheet {

    private Connection con;
    private PreparedStatement ps;

    // ✅ Constructor to initialize the database connection
    public MarksSheet() {
        con = MyConnection.getConnection();
    }

    public boolean isIdExist(int sid) {
        try {
            ps = con.prepareStatement("SELECT * FROM score WHERE student_id = ?");
            ps.setInt(1, sid);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MarksSheet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void getScoreValue(JTable table, int sid) {
        String sql = "SELECT * FROM score WHERE student_id = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, sid);
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
                row[8] = rs.getString("Score3");
                row[9] = rs.getString("course4");
                row[10] = rs.getString("Score4");
                row[11] = rs.getString("course5");
                row[12] = rs.getString("Score5");
                row[13] = rs.getString("average");
                model.addRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MarksSheet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public double getCGPA(int sid){
        double cgpa = 0.0;
        Statement st;
        
        try {
            st = con.createStatement();
            ResultSet rs = st.executeQuery("select avg(average) from score where student_id = "+sid+"");
            if(rs.next()){
                cgpa = rs.getDouble(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MarksSheet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cgpa;
    }
}
