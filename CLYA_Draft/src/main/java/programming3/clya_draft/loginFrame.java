package programming3.clya_draft;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author markjimuel
 */
public class loginFrame extends JFrame {
    private JTextField txtUsername;
    private JButton btnSubmit;
    private JLabel lblName;
    private JLabel lblLogo;
    
    public loginFrame() {
        // 1. Disable the layout manager for custom positioning
        this.setLayout(null);
        
        // 2. Fetch the safe usable screen boundary dimensions for this display
        Rectangle safeBounds = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();
        
        int screenWidth = safeBounds.width;
        
        // 3. Load image and calculate its horizontal center position
        ImageIcon image = new ImageIcon(loginFrame.class.getResource("/C-LYA LOGO.png"));
        int logoWidth = image.getIconWidth();
        int logoHeight = image.getIconHeight();
        
        // Center Formula: (Screen Width / 2) - (Component Width / 2)
        int logoX = (screenWidth / 2) - (logoWidth / 2);
        
        lblLogo = new JLabel(image);
        lblLogo.setBounds(logoX, 50, logoWidth, logoHeight); // Placed 50 pixels from the top
        this.add(lblLogo);
        
        // 4. Center your Input Elements
        // Define widths so we can align them symmetrically around the center vertical line
        int inputLabelWidth = 80;
        int textFieldWidth = 200;
        int componentGap = 10; // Space between label and text field
        int totalRowWidth = inputLabelWidth + componentGap + textFieldWidth;
        
        // Find where the combined row should start to look centered
        int rowStartX = (screenWidth / 2) - (totalRowWidth / 2);
        int inputRowY = 50 + logoHeight + 40; // Placed 40 pixels below the bottom of your logo
        
        lblName = new JLabel("Teacher Name:");
        lblName.setBounds(rowStartX, inputRowY, inputLabelWidth, 25);
        this.add(lblName);

        txtUsername = new JTextField();
        txtUsername.setBounds(rowStartX + inputLabelWidth + componentGap, inputRowY, textFieldWidth, 25);
        this.add(txtUsername);

        // 5. Center the Database Submit Button
        int buttonWidth = 150;
        int buttonHeight = 30;
        int buttonX = (screenWidth / 2) - (buttonWidth / 2);
        int buttonY = inputRowY + 50; // Placed 50 pixels below the text input row
        
        btnSubmit = new JButton("Save to DB");
        btnSubmit.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        this.add(btnSubmit);

        // Action Listener Event routing
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDataToDatabase();
            }
        });
        
        // Window Configuration Matrix Properties
        this.setTitle("CLYA | Classroom Seat Arranger");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setIconImage(image.getImage());
        
        // Background Color Configuration Property
        this.getContentPane().setBackground(new Color(0xFFF4D5));
        
        // Force the layout engine execution to maximize and reveal safely LAST
        this.setBounds(safeBounds);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
    }

    // Database operation handler method
    private void saveDataToDatabase() {
        String username = txtUsername.getText().trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username!");
            return;
        }

        String sql = "INSERT INTO users (username) VALUES (?)";

        try (Connection conn = Login.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            int rowsInserted = pstmt.executeUpdate();
            
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Data successfully saved to MySQL!");
                txtUsername.setText(""); 
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), 
                                          "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}