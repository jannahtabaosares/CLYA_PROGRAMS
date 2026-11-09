/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package programming3.clya_draft;

import java.sql.*;
/**
 *
 * @author markjimuel
 */
public class Login {
    
    private static final String URL = "jdbc:mysql://localhost:3306/clya"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = "MySQL_MarkJimuelroot18"; 

    // 2. If you want a constructor, it needs to open and close properly
    public Login() {
        // You can put initialization code for your Swing GUI here later
    }

    // 3. Methods must be declared directly inside the class
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

    
