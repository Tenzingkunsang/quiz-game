package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class Home extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private String username;

    private static final Color GRADIENT_START = new Color(52, 73, 94); // Dark Blue-Gray
    private static final Color GRADIENT_END = new Color(74, 98, 120);   // Lighter shade of blue-gray

    private static final Color TEXT_LIGHT = Color.WHITE; // White for main text on dark background
    private static final Color TEXT_DARK = new Color(44, 62, 80); // Dark gray for general text

    private static final Color BUTTON_PLAY = new Color(46, 204, 113); // Muted Emerald Green for Play
    private static final Color BUTTON_LEADERBOARD = new Color(52, 152, 219); // Muted Blue for Leaderboard
    private static final Color BUTTON_LOGOUT = new Color(192, 57, 43); // Desaturated Red for Logout

    private static final Color TABLE_HEADER_BG = new Color(52, 73, 94);
    private static final Color TABLE_ROW_EVEN = new Color(248, 248, 248);
    private static final Color TABLE_ROW_ODD = new Color(236, 240, 241);
    private static final Color BORDER_LIGHT = new Color(200, 200, 200);

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_app_db";
    private static final String DB_USER = "quiz_user";
    private static final String DB_PASSWORD = "secure_quiz_pw";

    /**
     * Constructor for the Home frame.
     * Initializes the UI components and sets up event listeners.
     * @param username The username of the logged-in user to personalize the page.
     */
    public Home(String username) {
        this.username = username;

        setTitle("Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, GRADIENT_START, getWidth(), getHeight(), GRADIENT_END);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setLayout(new GridBagLayout());
        setContentPane(contentPane);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel lblWelcome = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 38));
        lblWelcome.setForeground(TEXT_LIGHT);
        gbc.gridy = 0;
        gbc.ipady = 30;
        contentPane.add(lblWelcome, gbc);

        // Play Quiz Button
        JButton btnPlayQuiz = createStyledButton("Play Quiz", BUTTON_PLAY);
        gbc.gridy = 1;
        gbc.ipadx = 150;
        gbc.ipady = 25;
        contentPane.add(btnPlayQuiz, gbc);
        btnPlayQuiz.addActionListener(e -> showDifficultyDialog());

        // View Leaderboard Button
        JButton btnLeaderboard = createStyledButton("View Leaderboard", BUTTON_LEADERBOARD);
        gbc.gridy = 2;
        contentPane.add(btnLeaderboard, gbc);
        btnLeaderboard.addActionListener(e -> viewLeaderboard());

        // Logout Button
        JButton btnLogout = createStyledButton("Logout", BUTTON_LOGOUT);
        gbc.gridy = 3;
        contentPane.add(btnLogout, gbc);
        btnLogout.addActionListener(e -> logout());
    }

    /**
     * Helper method to create consistently styled JButtons with hover effects.
     * @param text The text to display on the button.
     * @param bgColor The background color of the button.
     * @return A styled JButton.
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setBackground(bgColor);
        button.setForeground(TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bgColor.darker(), 2),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Add mouse listener for hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter().brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    /**
     * Handles the logout process, prompting for confirmation and redirecting to the Login page.
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            Login loginPage = new Login(); // Assuming Login class is available
            loginPage.setVisible(true);
            dispose();
        }
    }

    /**
     * Opens a new JFrame to display the leaderboard (top scores).
     * Fetches scores from the database and displays them in a styled JTable.
     */
    private void viewLeaderboard() {
        JFrame leaderboardFrame = new JFrame("Quiz Leaderboard");
        leaderboardFrame.setSize(800, 500);
        leaderboardFrame.setLocationRelativeTo(this);
        leaderboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTable leaderboardTable = new JTable();
        leaderboardTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        leaderboardTable.setRowHeight(30);
        leaderboardTable.setFillsViewportHeight(true);

        // Style table header
        leaderboardTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        leaderboardTable.getTableHeader().setBackground(TABLE_HEADER_BG);
        leaderboardTable.getTableHeader().setForeground(TEXT_LIGHT);
        leaderboardTable.setGridColor(BORDER_LIGHT);
        leaderboardTable.setShowVerticalLines(false);
        leaderboardTable.setIntercellSpacing(new Dimension(0, 0));

        // Custom renderer for alternating row colors and cell padding
        leaderboardTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? TABLE_ROW_EVEN : TABLE_ROW_ODD);
                    c.setForeground(TEXT_DARK);
                } else {
                    c.setBackground(new Color(173, 216, 230));
                    c.setForeground(TEXT_DARK);
                }
                // Add padding to cell content
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        DefaultTableModel model = new DefaultTableModel(new String[]{"Rank", "Username", "Score", "Average Score"}, 0);
        leaderboardTable.setModel(model);

        String sql = "SELECT username, score, average_score FROM scores ORDER BY score DESC LIMIT 10";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            int rank = 1;
            while (resultSet.next()) {
                String user = resultSet.getString("username");
                int score = resultSet.getInt("score");
                double avgScoreAtSubmission = resultSet.getDouble("average_score");
                model.addRow(new Object[]{rank++, user, score, String.format("%.2f", avgScoreAtSubmission)});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(leaderboardFrame, "Error retrieving leaderboard: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        leaderboardFrame.add(scrollPane, BorderLayout.CENTER);

        leaderboardFrame.setVisible(true);
    }

    /**
     * Displays a modal dialog allowing the user to select a quiz difficulty level.
     */
    private void showDifficultyDialog() {
        JDialog difficultyDialog = new JDialog(this, "Select Difficulty", true);
        difficultyDialog.setSize(350, 250);
        difficultyDialog.setLocationRelativeTo(this);
        difficultyDialog.setLayout(new GridBagLayout());
        difficultyDialog.setResizable(false);
        difficultyDialog.getContentPane().setBackground(TABLE_ROW_EVEN);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Choose Difficulty Level", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(TEXT_DARK);
        gbc.gridy = 0;
        gbc.ipady = 10;
        difficultyDialog.add(lblTitle, gbc);

        // Difficulty buttons with action listeners to start quiz
        JButton btnEasy = createDifficultyButton("Easy", difficultyDialog);
        gbc.gridy = 1;
        gbc.ipady = 15;
        difficultyDialog.add(btnEasy, gbc);

        JButton btnMedium = createDifficultyButton("Medium", difficultyDialog);
        gbc.gridy = 2;
        difficultyDialog.add(btnMedium, gbc);

        JButton btnHard = createDifficultyButton("Hard", difficultyDialog);
        gbc.gridy = 3;
        difficultyDialog.add(btnHard, gbc);

        difficultyDialog.setVisible(true);
    }

    /**
     * Helper method to create styled buttons for the difficulty dialog.
     * @param text The text for the button.
     * @param dialog The parent dialog to be closed.
     * @return A styled JButton.
     */
    private JButton createDifficultyButton(String text, JDialog dialog) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(BUTTON_LEADERBOARD);
        button.setForeground(TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BUTTON_LEADERBOARD.darker(), 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_LEADERBOARD.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_LEADERBOARD);
            }
        });
        button.addActionListener(e -> startQuiz(text, dialog));
        return button;
    }

    /**
     * Starts the quiz game with the selected difficulty and closes the difficulty dialog.
     * @param difficulty The selected difficulty level.
     * @param dialog The difficulty selection dialog to be closed.
     */
    private void startQuiz(String difficulty, JDialog dialog) {
        dialog.dispose();
        Game gamePage = new Game(username, difficulty); // Assuming Game class is available
        gamePage.setVisible(true);
        dispose();
    }

    /**
     * Main method for testing the HomePage independently.
     * Ensures GUI updates are performed on the Event-Dispatching Thread (EDT).
     */
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Schedule GUI creation and display on the EDT
        EventQueue.invokeLater(() -> {
            try {
                Home home = new Home("TestUser"); // Pass a test username
                home.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
