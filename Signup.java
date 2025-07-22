package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Provides user registration functionality for the Quiz Application.
 * Validates and stores new user credentials in the database.
 *
 * @author Tenzing Kunsang Sherpa
 * @version 2.0
 * @since 2024
 */
public class Signup extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField usernameField, countryField;
    private JPasswordField passwordField;

    // UI Constants
    private static final Color GRADIENT_START = new Color(70, 80, 90);
    private static final Color GRADIENT_END = new Color(100, 120, 140);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color BUTTON_COLOR = new Color(52, 152, 219);

    /**
     * Constructs the Signup frame and initializes UI components.
     */
    public Signup() {
        setTitle("Quiz Application - Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 550);
        setLocationRelativeTo(null);
        initComponents();
    }

    /**
     * Initializes and arranges all UI components.
     */
    private void initComponents() {
        // Gradient background panel
        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, GRADIENT_START, getWidth(), getHeight(), GRADIENT_END);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setLayout(new GridBagLayout());
        setContentPane(contentPane);

        // Signup form panel
        JPanel signupPanel = createSignupPanel();
        contentPane.add(signupPanel);
    }

    /**
     * Creates the signup form panel with all form elements.
     *
     * @return Configured JPanel containing signup form
     */
    private JPanel createSignupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title label
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Username field
        gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(createLabel("Username:"), gbc);
        usernameField = createTextField();
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createLabel("Password:"), gbc);
        passwordField = createPasswordField();
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Country field
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createLabel("Country:"), gbc);
        countryField = createTextField();
        gbc.gridx = 1;
        panel.add(countryField, gbc);

        // Signup button
        JButton signupBtn = createButton("Sign Up", BUTTON_COLOR);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(signupBtn, gbc);
        signupBtn.addActionListener(e -> performSignup());

        // Login link
        JLabel loginLink = createLinkLabel(
                "Already have an account? Login Here");
        gbc.gridy = 5;
        panel.add(loginLink, gbc);
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Login().setVisible(true);
                dispose();
            }
        });

        return panel;
    }

    /**
     * Validates and processes user registration.
     */
    private void performSignup() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String country = countryField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || country.isEmpty()) {
            showError("All fields are required");
            return;
        }

        if (username.equalsIgnoreCase("admin")) {
            showError("Username 'admin' is reserved");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (isUsernameTaken(conn, username)) {
                showError("Username already exists");
                return;
            }

            String sql = "INSERT INTO users (username, password, country) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, country);

            int result = stmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this,
                        "Registration successful! Please login.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                new Login().setVisible(true);
                dispose();
            }
        } catch (Exception e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Checks if username already exists in database.
     *
     * @param conn Active database connection
     * @param username Username to check
     * @return true if username exists, false otherwise
     * @throws SQLException if database error occurs
     */
    private boolean isUsernameTaken(Connection conn, String username)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    // Helper methods for UI components (similar to Login.java)
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        return field;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 40, 12, 40));
        return button;
    }

    private JLabel createLinkLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR.darker());
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return label;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message,
                "Registration Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Main method for standalone execution.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new Signup().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}