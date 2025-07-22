package org.example;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.LineBorder;
import java.sql.*;

public class AdminHome extends JFrame {

    private JPanel contentPane;
    private JTable table;
    private JButton btnAddQuestion, btnDeleteQuestion, btnUpdateQuestion, btnViewReports, btnViewQuestions, btnLogout;
    private String adminUsername;
    private JLabel lblAverageScore;
    private JScrollPane scrollPane;

    // Modern, professional color palette with improved contrast
    private static final Color PRIMARY_COLOR = new Color(44, 62, 80); // Darker Blue-Gray (Main elements, headers)
    private static final Color SECONDARY_COLOR = new Color(245, 248, 250); // Very light, almost white, cool gray (Backgrounds)
    private static final Color ACCENT_GREEN = new Color(39, 174, 96); // Vibrant Emerald Green (Add/Positive)
    private static final Color DANGER_RED = new Color(192, 57, 43); // Desaturated Red (Delete)
    private static final Color INFO_BLUE = new Color(41, 128, 185); // Professional Blue (Update/Info)
    private static final Color BUTTON_VIEW_COLOR = new Color(108, 122, 137); // Muted Gray-Blue for View buttons
    private static final Color BUTTON_LOGOUT_COLOR = new Color(200, 0, 0); // Clear Red for Logout

    private static final Color TEXT_DARK = new Color(44, 62, 80); // Very dark gray for text on light backgrounds
    private static final Color TEXT_LIGHT = Color.WHITE; // White for text on dark backgrounds
    private static final Color BORDER_SUBTLE = new Color(200, 200, 200); // Light gray for subtle borders

    // Table specific colors
    private static final Color TABLE_ROW_EVEN = SECONDARY_COLOR; // Very light background
    private static final Color TABLE_ROW_ODD = new Color(230, 235, 240); // Slightly darker for odd rows
    private static final Color TABLE_SELECTION_COLOR = new Color(173, 216, 230); // Light blue for selected row

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_app_db";
    private static final String DB_USER = "quiz_user";
    private static final String DB_PASSWORD = "secure_quiz_pw";

    /**
     * Constructor for the AdminHome frame.
     * Initializes the UI components and sets up event listeners.
     * @param username The username of the logged-in administrator.
     */
    public AdminHome(String username) {
        this.adminUsername = username;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Admin Dashboard - Quiz Application");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setResizable(true);

        // Menu Bar setup
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(PRIMARY_COLOR);
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(TEXT_LIGHT);
        fileMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setBackground(PRIMARY_COLOR.brighter());
        exitMenuItem.setForeground(TEXT_LIGHT);
        exitMenuItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Main content panel layout
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(25, 25));
        contentPane.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPane.setBackground(SECONDARY_COLOR);
        setContentPane(contentPane);

        // Top Panel for Welcome message and Logout button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(SECONDARY_COLOR);

        JLabel lblWelcome = new JLabel("Welcome, " + adminUsername + "!", SwingConstants.LEFT);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblWelcome.setForeground(PRIMARY_COLOR);
        topPanel.add(lblWelcome, BorderLayout.WEST);

        btnLogout = createStyledButton("Logout", BUTTON_LOGOUT_COLOR);
        btnLogout.setPreferredSize(new Dimension(100, 40));
        btnLogout.addActionListener(e -> logout());
        JPanel logoutWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Wrapper for right alignment
        logoutWrapper.setBackground(SECONDARY_COLOR);
        logoutWrapper.add(btnLogout);
        topPanel.add(logoutWrapper, BorderLayout.EAST);

        contentPane.add(topPanel, BorderLayout.NORTH);

        // Central container for action buttons and data display panels
        JPanel centralContainerPanel = new JPanel();
        centralContainerPanel.setLayout(new BoxLayout(centralContainerPanel, BoxLayout.Y_AXIS));
        centralContainerPanel.setBackground(SECONDARY_COLOR);
        centralContainerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        // Panel for action buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(SECONDARY_COLOR);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.insets = new Insets(15, 15, 15, 15); // Padding between buttons
        gbcButtons.fill = GridBagConstraints.BOTH;
        gbcButtons.weightx = 1.0;
        gbcButtons.weighty = 1.0;

        btnAddQuestion = createStyledButton("Add Question", ACCENT_GREEN);
        gbcButtons.gridx = 0; gbcButtons.gridy = 0;
        buttonPanel.add(btnAddQuestion, gbcButtons);
        btnAddQuestion.addActionListener(e -> openAddQuestionDialog());

        btnDeleteQuestion = createStyledButton("Delete Question", DANGER_RED);
        gbcButtons.gridx = 1; gbcButtons.gridy = 0;
        buttonPanel.add(btnDeleteQuestion, gbcButtons);
        btnDeleteQuestion.addActionListener(e -> openDeleteQuestionDialog());

        btnUpdateQuestion = createStyledButton("Update Question", INFO_BLUE);
        gbcButtons.gridx = 2; gbcButtons.gridy = 0;
        buttonPanel.add(btnUpdateQuestion, gbcButtons);
        btnUpdateQuestion.addActionListener(e -> openUpdateQuestionDialog());

        btnViewReports = createStyledButton("View User Reports", BUTTON_VIEW_COLOR);
        gbcButtons.gridx = 3; gbcButtons.gridy = 0;
        buttonPanel.add(btnViewReports, gbcButtons);
        btnViewReports.addActionListener(e -> viewReports());

        btnViewQuestions = createStyledButton("View All Questions", BUTTON_VIEW_COLOR);
        gbcButtons.gridx = 4; gbcButtons.gridy = 0;
        buttonPanel.add(btnViewQuestions, gbcButtons);
        btnViewQuestions.addActionListener(e -> viewQuestions());

        centralContainerPanel.add(buttonPanel);

        // Panel to hold the table and the average score label
        JPanel reportPanel = new JPanel(new BorderLayout(15, 15));
        reportPanel.setBackground(SECONDARY_COLOR);
        reportPanel.setBorder(BorderFactory.createLineBorder(BORDER_SUBTLE, 1));
        reportPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Table for displaying data
        table = new JTable();
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(TEXT_LIGHT);
        table.setGridColor(BORDER_SUBTLE);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Custom renderer for alternating row colors and padding
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? TABLE_ROW_EVEN : TABLE_ROW_ODD);
                    c.setForeground(TEXT_DARK);
                } else {
                    c.setBackground(TABLE_SELECTION_COLOR);
                    c.setForeground(TEXT_DARK);
                }
                // Add padding to cell content for better readability
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(PRIMARY_COLOR, 1),
                "Data View",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 18),
                PRIMARY_COLOR
        ));
        reportPanel.add(scrollPane, BorderLayout.CENTER);

        // Label to display the calculated average score
        lblAverageScore = new JLabel("Overall Average Score: N/A", SwingConstants.RIGHT);
        lblAverageScore.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblAverageScore.setForeground(PRIMARY_COLOR.darker());
        lblAverageScore.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 15));
        reportPanel.add(lblAverageScore, BorderLayout.SOUTH);

        centralContainerPanel.add(reportPanel);
        contentPane.add(centralContainerPanel, BorderLayout.CENTER);

        // Initially display user reports when the admin home loads
        viewReports();
    }

    /**
     * Helper method to create consistently styled JButtons with hover effects.
     * @param text The text to display on the button.
     * @param bgColor The background color of the button.
     * @return A styled JButton.
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 17));
        button.setBackground(bgColor);
        button.setForeground(TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.brighter().brighter());
            }

            @Override
            public void mouseExited(MouseEvent evt) {
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
            dispose(); // Close AdminHome
        }
    }

    /**
     * Opens a dialog to add a new question to the database.
     * Collects question details and inserts them into the 'questions' table.
     */
    private void openAddQuestionDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(SECONDARY_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField questionField = new JTextField(30);
        JTextField optionAField = new JTextField(20);
        JTextField optionBField = new JTextField(20);
        JTextField optionCField = new JTextField(20);
        JTextField optionDField = new JTextField(20);
        JTextField correctAnswerField = new JTextField(20);
        JComboBox<String> difficultyComboBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});

        // Apply consistent styling to dialog components
        styleDialogField(questionField);
        styleDialogField(optionAField);
        styleDialogField(optionBField);
        styleDialogField(optionCField);
        styleDialogField(optionDField);
        styleDialogField(correctAnswerField);
        styleDialogComboBox(difficultyComboBox);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createDialogLabel("Question:"), gbc);
        gbc.gridx = 1; panel.add(questionField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        panel.add(createDialogLabel("Option A:"), gbc);
        gbc.gridx = 1; panel.add(optionAField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        panel.add(createDialogLabel("Option B:"), gbc);
        gbc.gridx = 1; panel.add(optionBField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        panel.add(createDialogLabel("Option C:"), gbc);
        gbc.gridx = 1; panel.add(optionCField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        panel.add(createDialogLabel("Option D:"), gbc);
        gbc.gridx = 1; panel.add(optionDField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        panel.add(createDialogLabel("Correct Answer:"), gbc);
        gbc.gridx = 1; panel.add(correctAnswerField, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        panel.add(createDialogLabel("Difficulty:"), gbc);
        gbc.gridx = 1; panel.add(difficultyComboBox, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Question",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String question = questionField.getText().trim();
            String optionA = optionAField.getText().trim();
            String optionB = optionBField.getText().trim();
            String optionC = optionCField.getText().trim();
            String optionD = optionDField.getText().trim();
            String correctAnswer = correctAnswerField.getText().trim();
            String difficulty = (String) difficultyComboBox.getSelectedItem();

            if (question.isEmpty() || optionA.isEmpty() || optionB.isEmpty() ||
                    optionC.isEmpty() || optionD.isEmpty() || correctAnswer.isEmpty() ||
                    difficulty == null || difficulty.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required to add a question.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = "INSERT INTO questions (question, optionA, optionB, optionC, optionD, correctAnswer, difficulty) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, question);
                preparedStatement.setString(2, optionA);
                preparedStatement.setString(3, optionB);
                preparedStatement.setString(4, optionC);
                preparedStatement.setString(5, optionD);
                preparedStatement.setString(6, correctAnswer);
                preparedStatement.setString(7, difficulty);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Question Added Successfully!");
                    viewQuestions(); // Refresh question list
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding question: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /**
     * Helper to style JTextFields in dialogs for a consistent look.
     */
    private void styleDialogField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_SUBTLE, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8))); // Internal padding
    }

    /**
     * Helper to style JComboBoxes in dialogs for a consistent look.
     */
    private void styleDialogComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_SUBTLE, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    }

    /**
     * Helper to create styled JLabels for dialogs.
     */
    private JLabel createDialogLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setForeground(TEXT_DARK);
        return label;
    }

    /**
     * Opens a dialog to delete a question from the database by its ID.
     * Prompts for confirmation before deletion.
     */
    private void openDeleteQuestionDialog() {
        String questionIdStr = JOptionPane.showInputDialog(this, "Enter the Question ID to delete:", "Delete Question", JOptionPane.PLAIN_MESSAGE);

        if (questionIdStr != null && !questionIdStr.trim().isEmpty()) {
            try {
                int questionId = Integer.parseInt(questionIdStr);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete question ID " + questionId + "? This action cannot be undone.", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    String sql = "DELETE FROM questions WHERE id = ?";
                    try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        preparedStatement.setInt(1, questionId);
                        int result = preparedStatement.executeUpdate();
                        if (result > 0) {
                            JOptionPane.showMessageDialog(this, "Question Deleted Successfully!");
                            viewQuestions(); // Refresh question list
                        } else {
                            JOptionPane.showMessageDialog(this, "No question found with ID: " + questionId, "Not Found", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, "Error deleting question: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Question ID. Please enter a valid number.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Opens a dialog to update a question's difficulty in the database.
     * Requires the question ID and the new difficulty level.
     */
    private void openUpdateQuestionDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(SECONDARY_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField questionIdField = new JTextField(10);
        JComboBox<String> difficultyComboBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});

        // Apply consistent styling to dialog components
        styleDialogField(questionIdField);
        styleDialogComboBox(difficultyComboBox);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createDialogLabel("Question ID:"), gbc);
        gbc.gridx = 1; panel.add(questionIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createDialogLabel("New Difficulty:"), gbc);
        gbc.gridx = 1; panel.add(difficultyComboBox, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Question Difficulty",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String questionIdStr = questionIdField.getText().trim();
            String difficulty = (String) difficultyComboBox.getSelectedItem();

            if (questionIdStr.isEmpty() || difficulty == null || difficulty.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Question ID and difficulty are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int questionId = Integer.parseInt(questionIdStr);
                String updateSql = "UPDATE questions SET difficulty = ? WHERE id = ?";
                try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                     PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setString(1, difficulty);
                    updateStmt.setInt(2, questionId);
                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Question Updated Successfully!");
                        viewQuestions(); // Refresh question list
                    } else {
                        JOptionPane.showMessageDialog(this, "No question found with ID: " + questionId, "Not Found", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error updating question: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Question ID. Please enter a valid number.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Fetches all user scores from the database, displays them in a table,
     * and calculates and displays the overall average score.
     */
    private void viewReports() {
        // Update scroll pane title and visibility for average score
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(PRIMARY_COLOR, 1),
                "User Reports (Scores)",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 18),
                PRIMARY_COLOR
        ));
        lblAverageScore.setVisible(true);

        String sql = "SELECT username, score FROM scores ORDER BY score DESC";
        DefaultTableModel model = new DefaultTableModel(new String[]{"Username", "Score"}, 0);
        double averageScore = 0.0;

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            int totalScore = 0;
            int rowCount = 0;

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                int score = resultSet.getInt("score");
                model.addRow(new Object[]{username, score});
                totalScore += score;
                rowCount++;
            }
            table.setModel(model);

            if (rowCount > 0) {
                averageScore = (double) totalScore / rowCount;
                lblAverageScore.setText(String.format("Overall Average Score: %.2f", averageScore));
            } else {
                lblAverageScore.setText("Overall Average Score: N/A (No scores found)");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving reports: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            lblAverageScore.setText("Overall Average Score: Error");
            e.printStackTrace();
        }
    }

    /**
     * Fetches all quiz questions from the database and displays them in a table.
     * Hides the average score label as it's not relevant here.
     */
    private void viewQuestions() {
        // Update scroll pane title and hide average score label
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(PRIMARY_COLOR, 1),
                "Quiz Questions",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 18),
                PRIMARY_COLOR
        ));
        lblAverageScore.setVisible(false);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Question", "Option A", "Option B", "Option C", "Option D", "Correct Answer", "Difficulty"}, 0);

        String sql = "SELECT id, question, optionA, optionB, optionC, optionD, correctAnswer, difficulty FROM questions ORDER BY id ASC";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String questionText = resultSet.getString("question");
                String optionA = resultSet.getString("optionA");
                String optionB = resultSet.getString("optionB");
                String optionC = resultSet.getString("optionC");
                String optionD = resultSet.getString("optionD");
                String correctAnswer = resultSet.getString("correctAnswer");
                String difficulty = resultSet.getString("difficulty");
                model.addRow(new Object[]{id, questionText, optionA, optionB, optionC, optionD, correctAnswer, difficulty});
            }
            table.setModel(model);

            // Adjust column widths for better readability of questions
            table.getColumnModel().getColumn(0).setPreferredWidth(30); // ID
            table.getColumnModel().getColumn(1).setPreferredWidth(300); // Question
            table.getColumnModel().getColumn(6).setPreferredWidth(120); // Correct Answer
            table.getColumnModel().getColumn(7).setPreferredWidth(90); // Difficulty
            table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS); // Allow other columns to adjust


        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving questions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Main method for running the AdminHome frame independently (for testing purposes).
     */
    public static void main(String[] args) {
        // Set an anti-aliasing hint for better text rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // Use system look and feel

                AdminHome frame = new AdminHome("admin"); // Create frame with a dummy admin username
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}