package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Provides a professional login interface for the Quiz Application.
 * Handles user authentication and admin verification with modern UI design.
 *
 * @author Tenzing Kunsang Sherpa
 * @version 3.0
 * @since 2024
 */
public class Login extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JPasswordField passwordField;

    // Modern color palette
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(13, 110, 253);
    private static final Color PRIMARY_HOVER = new Color(11, 94, 215);
    private static final Color TEXT_COLOR = new Color(33, 37, 41);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);

    // Shadow effect
    private static final int SHADOW_SIZE = 24;
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 50);

    // Admin credentials
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin";

    /**
     * Constructs the Login frame with professional UI components.
     */
    public Login() {
        initUI();
    }

    private void initUI() {
        setTitle("Quiz Application - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main content pane with modern background
        JPanel contentPane = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BACKGROUND_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        setContentPane(contentPane);

        // Create and add login card
        contentPane.add(createLoginCard());
    }

    /**
     * Creates the main login card with shadow effect.
     */
    private JPanel createLoginCard() {
        // Shadow panel
        JPanel shadowPanel = new JPanel(new GridBagLayout());
        shadowPanel.setOpaque(false);
        shadowPanel.setBorder(BorderFactory.createEmptyBorder(
                SHADOW_SIZE, SHADOW_SIZE, SHADOW_SIZE, SHADOW_SIZE));

        // Main card panel
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(CARD_COLOR);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)));

        // Add components to card
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // App logo/icon
        JLabel logoLabel = new JLabel("Quiz App", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoLabel.setForeground(PRIMARY_COLOR);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0);
        cardPanel.add(logoLabel, gbc);

        // Login form
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridy = 1;
        cardPanel.add(createFormField("Username", usernameField = createTextField()), gbc);

        gbc.gridy = 2;
        cardPanel.add(createFormField("Password", passwordField = createPasswordField()), gbc);

        // Login button
        JButton loginButton = createPrimaryButton("Sign In");
        loginButton.addActionListener(e -> performLogin());
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 0, 10, 0);
        cardPanel.add(loginButton, gbc);

        // Register link
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerPanel.setOpaque(false);
        JLabel registerLabel = new JLabel("Don't have an account?");
        JLabel registerLink = new JLabel("Register now");
        registerLink.setForeground(PRIMARY_COLOR);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Signup().setVisible(true);
                dispose();
            }
        });
        registerPanel.add(registerLabel);
        registerPanel.add(registerLink);
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 0, 0);
        cardPanel.add(registerPanel, gbc);

        // Add card to shadow panel
        shadowPanel.add(cardPanel);
        return shadowPanel;
    }

    /**
     * Creates a form field with label and input component.
     */
    private JPanel createFormField(String labelText, JComponent input) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);

        input.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        input.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        panel.add(label, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates a primary action button with hover effects.
     */
    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(PRIMARY_HOVER.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(PRIMARY_HOVER);
                } else {
                    g2.setColor(PRIMARY_COLOR);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();

                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                // No border
            }
        };

        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(0, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    /**
     * Creates a styled text field.
     */
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setBackground(CARD_COLOR);
        return field;
    }

    /**
     * Creates a styled password field.
     */
    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setBackground(CARD_COLOR);
        return field;
    }

    /**
     * Performs user authentication against database or admin credentials.
     */
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        // Check admin credentials
        if (ADMIN_USER.equals(username)) {
            if (ADMIN_PASS.equals(password)) {
                new AdminHome(username).setVisible(true);
                dispose();
                return;
            }
            showError("Invalid admin password");
            return;
        }

        // Check regular user credentials
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                new Home(username).setVisible(true);
                dispose();
            } else {
                showError("Invalid username or password");
            }
        } catch (Exception e) {
            showError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Shows an error message in a professional dialog.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center;'>" + message + "</div></html>",
                "Authentication Error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Main method for standalone execution.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Create and display the login form
                Login login = new Login();
                login.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}