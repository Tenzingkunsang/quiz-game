package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game extends JFrame {

    /**
     * The main quiz gameplay interface where users answer questions.
     */    private String username;
    private String difficulty;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int totalQuestions = 0;

    // UI Components
    private JLabel lblQuestion, lblScore;
    private JRadioButton rbOptionA, rbOptionB, rbOptionC, rbOptionD;
    private ButtonGroup buttonGroup;
    private JButton btnNext;

    private static final Color BACKGROUND_PRIMARY = new Color(228, 242, 250); // Soft light blue for main background
    private static final Color CARD_BACKGROUND = new Color(255, 255, 255);   // Pure white for question/option cards
    private static final Color TEXT_DARK = new Color(34, 49, 63);            // Deep blue-gray for primary text
    private static final Color ACCENT_BUTTON_BRIGHT = new Color(0, 66, 255);
    private static final Color ACCENT_BUTTON_HOVER = new Color(39, 174, 96);
    private static final Color SCORE_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_FEEDBACK = new Color(39, 174, 96);
    private static final Color ERROR_FEEDBACK = new Color(231, 76, 60);
    private static final Color BORDER_SUBTLE = new Color(200, 200, 200);

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quiz_app_db";
    private static final String DB_USER = "quiz_user";
    private static final String DB_PASSWORD = "secure_quiz_pw";

    /**
     * Constructs the Game frame for the specified user and difficulty.
     * @param username The player's username
     * @param difficulty The selected difficulty level
     */
    public Game(String username, String difficulty) {
        this.username = username;
        this.difficulty = difficulty;

        setTitle("Quiz Game - " + difficulty);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);

        initComponents();
        fetchQuestions();
        displayQuestion();
    }

    /**
     * Initializes and lays out the GUI components for the game.
     * Uses a clean, card-based layout with generous padding and vibrant buttons.
     */
    private void initComponents() {
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(35, 35));
        contentPane.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        contentPane.setBackground(BACKGROUND_PRIMARY);
        setContentPane(contentPane);

        // Header Panel (Top section: Welcome, Score, and Question)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BACKGROUND_PRIMARY);

        JPanel topRowPanel = new JPanel(new BorderLayout());
        topRowPanel.setBackground(BACKGROUND_PRIMARY);

        JLabel lblWelcome = new JLabel("Hello, " + username + "!", SwingConstants.LEFT);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblWelcome.setForeground(TEXT_DARK);
        topRowPanel.add(lblWelcome, BorderLayout.WEST);

        lblScore = new JLabel("Score: 0 / 0", SwingConstants.RIGHT);
        lblScore.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblScore.setForeground(SCORE_COLOR);
        topRowPanel.add(lblScore, BorderLayout.EAST);

        headerPanel.add(topRowPanel);
        headerPanel.add(Box.createVerticalStrut(25));

        // Question "Card" Panel
        JPanel questionCard = new JPanel(new BorderLayout(20, 20));
        questionCard.setBackground(CARD_BACKGROUND);
        questionCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_SUBTLE, 1),
                BorderFactory.createEmptyBorder(35, 35, 35, 35)
        ));

        lblQuestion = new JLabel("Loading question...", SwingConstants.CENTER);
        lblQuestion.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblQuestion.setForeground(TEXT_DARK);
        lblQuestion.setVerticalAlignment(SwingConstants.CENTER);
        lblQuestion.setHorizontalAlignment(SwingConstants.CENTER);
        questionCard.add(lblQuestion, BorderLayout.CENTER);
        headerPanel.add(questionCard);

        contentPane.add(headerPanel, BorderLayout.NORTH);

        // Options Panel
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(4, 1, 20, 20));
        optionsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_SUBTLE, 1),
                BorderFactory.createEmptyBorder(30, 60, 30, 60)
        ));
        optionsPanel.setBackground(CARD_BACKGROUND);

        rbOptionA = new JRadioButton("Option A");
        rbOptionB = new JRadioButton("Option B");
        rbOptionC = new JRadioButton("Option C");
        rbOptionD = new JRadioButton("Option D");

        styleRadioButton(rbOptionA);
        styleRadioButton(rbOptionB);
        styleRadioButton(rbOptionC);
        styleRadioButton(rbOptionD);

        buttonGroup = new ButtonGroup();
        buttonGroup.add(rbOptionA);
        buttonGroup.add(rbOptionB);
        buttonGroup.add(rbOptionC);
        buttonGroup.add(rbOptionD);

        optionsPanel.add(rbOptionA);
        optionsPanel.add(rbOptionB);
        optionsPanel.add(rbOptionC);
        optionsPanel.add(rbOptionD);
        contentPane.add(optionsPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_PRIMARY);

        btnNext = new JButton("Next Question");
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 22));
        btnNext.setBackground(ACCENT_BUTTON_BRIGHT);
        btnNext.setForeground(Color.BLACK);
        btnNext.setFocusPainted(false);
        btnNext.setBorderPainted(false);
        btnNext.setBorder(BorderFactory.createEmptyBorder(18, 45, 18, 45));
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnNext.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                btnNext.setBackground(ACCENT_BUTTON_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                btnNext.setBackground(ACCENT_BUTTON_BRIGHT);
            }
        });
        btnNext.addActionListener(e -> checkAnswerAndNextQuestion());
        buttonPanel.add(btnNext);

        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Applies consistent styling to JRadioButtons, including a hover effect.
     * @param rb The JRadioButton to style.
     */
    private void styleRadioButton(JRadioButton rb) {
        rb.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        rb.setBackground(CARD_BACKGROUND);
        rb.setForeground(TEXT_DARK);
        rb.setFocusPainted(false);
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rb.setOpaque(true);

        for (MouseListener listener: rb.getMouseListeners()) {
            if (listener instanceof MouseAdapter) {
                rb.removeMouseListener(listener);
            }
        }

        rb.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                if (rb.getBackground().equals(CARD_BACKGROUND)) {
                    rb.setBackground(BACKGROUND_PRIMARY.brighter());
                }
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                if (rb.getBackground().equals(BACKGROUND_PRIMARY.brighter())) {
                    rb.setBackground(CARD_BACKGROUND);
                }
            }
        });
    }

    /**
     * Fetches questions from the database based on the selected difficulty.
     * Limits to 10 random questions and shuffles them. Provides a fallback to dummy questions.
     */
    private void fetchQuestions() {
        questions = new ArrayList<>();
        String sql = "SELECT question, optionA, optionB, optionC, optionD, correctAnswer FROM questions WHERE difficulty = ? ORDER BY RAND() LIMIT 10";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, difficulty);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String questionText = resultSet.getString("question");
                    String optionA = resultSet.getString("optionA");
                    String optionB = resultSet.getString("optionB");
                    String optionC = resultSet.getString("optionC");
                    String optionD = resultSet.getString("optionD");
                    String correctAnswer = resultSet.getString("correctAnswer");
                    questions.add(new Question(questionText, optionA, optionB, optionC, optionD, correctAnswer));
                }
            }
            Collections.shuffle(questions);
            this.totalQuestions = questions.size();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching questions from database: " + e.getMessage() + "\nLoading dummy questions instead.", "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            addDummyQuestions();
        }

        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions found for difficulty: " + difficulty + ". Adding dummy questions.", "Info", JOptionPane.INFORMATION_MESSAGE);
            addDummyQuestions();
        }
        if (this.totalQuestions == 0) {
            this.totalQuestions = questions.size();
        }
        lblScore.setText(String.format("Score: %d / %d", score, totalQuestions));
    }

    /**
     * Adds a few predefined dummy questions to the list.
     * This is used as a fallback if database fetching fails or returns no questions.
     */
    private void addDummyQuestions() {
        questions.add(new Question("What is 2 + 2?", "3", "4", "5", "6", "4"));
        questions.add(new Question("What is the capital of France?", "Berlin", "Madrid", "Paris", "Rome", "Paris"));
        questions.add(new Question("Which planet is known as the Red Planet?", "Earth", "Mars", "Jupiter", "Venus", "Mars"));
        questions.add(new Question("What is the largest ocean on Earth?", "Atlantic", "Indian", "Arctic", "Pacific", "Pacific"));
        questions.add(new Question("How many continents are there?", "5", "6", "7", "8", "7"));
        Collections.shuffle(questions);
    }

    /**
     * Displays the current question and its options in the GUI.
     * If all questions are answered, it calls the `endGame()` method.
     */
    private void displayQuestion() {
        if (currentQuestionIndex < questions.size()) {
            resetRadioButtonStyles();
            buttonGroup.clearSelection();

            Question currentQuestion = questions.get(currentQuestionIndex);
            lblQuestion.setText("Q" + (currentQuestionIndex + 1) + ": " + currentQuestion.getQuestion());
            rbOptionA.setText("A) " + currentQuestion.getOptionA());
            rbOptionB.setText("B) " + currentQuestion.getOptionB());
            rbOptionC.setText("C) " + currentQuestion.getOptionC());
            rbOptionD.setText("D) " + currentQuestion.getOptionD());

            btnNext.setText("Next Question");
            btnNext.setEnabled(true);
            setRadioButtonsEnabled(true);
        } else {
            endGame();
        }
    }

    /**
     * Checks the player's selected answer, updates the score, provides visual feedback,
     * and then proceeds to the next question or ends the game.
     */
    private void checkAnswerAndNextQuestion() {
        JRadioButton selectedRadioButton = null;
        if (rbOptionA.isSelected()) selectedRadioButton = rbOptionA;
        else if (rbOptionB.isSelected()) selectedRadioButton = rbOptionB;
        else if (rbOptionC.isSelected()) selectedRadioButton = rbOptionC;
        else if (rbOptionD.isSelected()) selectedRadioButton = rbOptionD;

        if (selectedRadioButton == null) {
            JOptionPane.showMessageDialog(this, "Please select an answer to proceed!", "No Answer Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        setRadioButtonsEnabled(false);
        btnNext.setEnabled(false);

        String selectedAnswerText = selectedRadioButton.getText().substring(3);
        Question currentQuestion = questions.get(currentQuestionIndex);
        String correctAnswer = currentQuestion.getCorrectAnswer();

        if (selectedAnswerText.equals(correctAnswer)) {
            selectedRadioButton.setBackground(SUCCESS_FEEDBACK);
            selectedRadioButton.setForeground(Color.WHITE);
            score++;
            lblScore.setText(String.format("Score: %d / %d", score, totalQuestions));
            JOptionPane.showMessageDialog(this, "Correct Answer!", "Result", JOptionPane.INFORMATION_MESSAGE);
        } else {
            selectedRadioButton.setBackground(ERROR_FEEDBACK);
            selectedRadioButton.setForeground(Color.WHITE);
            if (rbOptionA.getText().substring(3).equals(correctAnswer)) highlightCorrectOption(rbOptionA);
            else if (rbOptionB.getText().substring(3).equals(correctAnswer)) highlightCorrectOption(rbOptionB);
            else if (rbOptionC.getText().substring(3).equals(correctAnswer)) highlightCorrectOption(rbOptionC);
            else if (rbOptionD.getText().substring(3).equals(correctAnswer)) highlightCorrectOption(rbOptionD);

            JOptionPane.showMessageDialog(this, "Wrong Answer! The correct answer was: " + correctAnswer, "Result", JOptionPane.ERROR_MESSAGE);
        }

        currentQuestionIndex++;
        btnNext.setEnabled(true);
        displayQuestion();
    }

    /**
     * Helper method to style the correct option when the user has chosen an incorrect answer.
     * @param rb The JRadioButton that represents the correct answer.
     */
    private void highlightCorrectOption(JRadioButton rb) {
        rb.setBackground(SUCCESS_FEEDBACK.darker());
        rb.setForeground(Color.WHITE);
    }

    /**
     * Enables or disables all quiz option radio buttons.
     * Used to prevent interaction during feedback display.
     * @param enabled `true` to enable, `false` to disable.
     */
    private void setRadioButtonsEnabled(boolean enabled) {
        rbOptionA.setEnabled(enabled);
        rbOptionB.setEnabled(enabled);
        rbOptionC.setEnabled(enabled);
        rbOptionD.setEnabled(enabled);
    }

    /**
     * Resets the visual styles of all radio buttons to their default (unselected) state.
     * This is called at the beginning of displaying each new question.
     */
    private void resetRadioButtonStyles() {
        styleRadioButton(rbOptionA);
        styleRadioButton(rbOptionB);
        styleRadioButton(rbOptionC);
        styleRadioButton(rbOptionD);
    }

    /**
     * Concludes the game sequence. Saves the player's score, displays a summary,
     * and navigates back to the Home page.
     */
    private void endGame() {
        saveScore();

        JOptionPane.showMessageDialog(this,
                String.format("Quiz Finished! Your final score is: %d out of %d.", score, questions.size()),
                "Quiz End", JOptionPane.INFORMATION_MESSAGE);

        // Transition back to the Home page (assuming 'Home' class exists and handles username)
        Home homePage = new Home(username);
        homePage.setVisible(true);
        dispose();
    }

    /**
     * Saves the player's final score to the 'scores' table in the database.
     * Also calculates and updates the user's running average score.
     */
    private void saveScore() {
        double currentUserAverageScore = 0.0;
        int existingScoresCount = 0;
        int existingScoresSum = 0;

        // Step 1: Fetch existing scores for accurate average calculation
        String selectSql = "SELECT SUM(score), COUNT(score) FROM scores WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {

            selectStmt.setString(1, username);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    existingScoresSum = rs.getInt(1);
                    existingScoresCount = rs.getInt(2);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching existing scores for average calculation: " + e.getMessage());
            e.printStackTrace();
        }

        // Calculate the new average score including the current quiz submission
        int totalScoresIncludingCurrent = existingScoresSum + this.score;
        int totalCountIncludingCurrent = existingScoresCount + 1;

        if (totalCountIncludingCurrent > 0) {
            currentUserAverageScore = (double) totalScoresIncludingCurrent / totalCountIncludingCurrent;
        } else {
            currentUserAverageScore = this.score;
        }

        // Step 2: Insert the current score and the newly calculated average into the database
        String insertSql = "INSERT INTO scores (username, score, submission_time, average_score) VALUES (?, ?, NOW(), ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {

            insertStmt.setString(1, username);
            insertStmt.setInt(2, this.score);
            insertStmt.setDouble(3, currentUserAverageScore);

            insertStmt.executeUpdate();
            System.out.println("Score saved successfully for " + username + ": " + this.score +
                    " (User Average at Submission: " + String.format("%.2f", currentUserAverageScore) + ")");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving score: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Inner static class to represent a single quiz question.
     * Encapsulates the question text, its four options, and the correct answer.
     */
    private static class Question {
        private String question;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private String correctAnswer;

        public Question(String question, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
            this.question = question;
            this.optionA = optionA;
            this.optionB = optionB;
            this.optionC = optionC;
            this.optionD = optionD;
            this.correctAnswer = correctAnswer;
        }

        // Getters for question properties
        public String getQuestion() { return question; }
        public String getOptionA() { return optionA; }
        public String getOptionB() { return optionB; }
        public String getOptionC() { return optionC; }
        public String getOptionD() { return optionD; }
        public String getCorrectAnswer() { return correctAnswer; }
    }

    /**
     * Main method for testing the Game frame independently.
     * Ensures GUI updates are performed on the Event-Dispatching Thread (EDT).
     */
    public static void main(String[] args) {
        // Set system properties for better text rendering (anti-aliasing)
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Schedule GUI creation and display on the EDT
        EventQueue.invokeLater(() -> {
            try {
                Game game = new Game("PlayerOne", "Easy"); // Example: Launch game for a test user
                game.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
