package org.example;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;

public class Main extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    private static final Color GRADIENT_START_MAIN = new Color(0, 0, 0);
    private static final Color GRADIENT_END_MAIN = new Color(0, 0, 50);

    private static final Color TEXT_LIGHT = Color.WHITE;
    private static final Color BUTTON_ACCENT = new Color(0, 123, 255);
    private static final Color BUTTON_ACCENT_HOVER = new Color(0, 86, 179);

    /**
     * Main method to run the Main frame independently.
     * Ensures GUI updates are performed on the Event-Dispatching Thread (EDT).
     */
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                Main frame = new Main();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Constructor for the Main frame.
     * Initializes the UI components and sets up event listeners for navigation.
     */
    public Main() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Welcome to Quiz Game");
        setSize(650, 550);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, GRADIENT_START_MAIN, getWidth(), getHeight(), GRADIENT_END_MAIN);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(50, 150, 255), 5),
                BorderFactory.createEmptyBorder(70, 70, 70, 70)));
        contentPane.setLayout(new GridBagLayout());
        setContentPane(contentPane);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 0, 30, 0);

        // Main Title
        JLabel lblTitle = new JLabel("QUIZ CHALLENGE");
        lblTitle.setFont(new Font("Arial Black", Font.BOLD, 64));
        lblTitle.setForeground(TEXT_LIGHT);
        gbc.ipady = 35;
        contentPane.add(lblTitle, gbc);

        // Subtitle/Tagline
        JLabel lblSubtitle = new JLabel("Test Your Knowledge!");
        lblSubtitle.setFont(new Font("Verdana", Font.PLAIN, 28));
        lblSubtitle.setForeground(TEXT_LIGHT.brighter());
        gbc.ipady = 0;
        gbc.insets = new Insets(0, 0, 70, 0);
        contentPane.add(lblSubtitle, gbc);

        // Panel for Buttons (to arrange them horizontally)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        buttonPanel.setOpaque(false);

        // Login Button
        JButton btnLogin = new JButton("LOGIN");
        styleButton(btnLogin);
        buttonPanel.add(btnLogin);
        btnLogin.addActionListener(e -> {
            dispose();
            Login loginPage = new Login();
            loginPage.setVisible(true);
        });

        // Signup Button
        JButton btnSignup = new JButton("SIGN UP");
        styleButton(btnSignup);
        buttonPanel.add(btnSignup);
        btnSignup.addActionListener(e -> {
            dispose();
            Signup signupPage = new Signup();
            signupPage.setVisible(true);
        });

        gbc.insets = new Insets(0, 0, 0, 0);
        contentPane.add(buttonPanel, gbc);
    }

    /**
     * Styles a JButton with a professional look, consistent with other UI components.
     * Applies font, colors, padding, and hover effects.
     * @param button The JButton to style.
     */
    private void styleButton(JButton button) {
        button.setFont(new Font("Verdana", Font.BOLD, 24));
        button.setBackground(BUTTON_ACCENT);
        button.setForeground(TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(280, 75));
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BUTTON_ACCENT_HOVER, 4),
                BorderFactory.createEmptyBorder(18, 50, 18, 50)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_ACCENT_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_ACCENT);
            }
        });
    }
}