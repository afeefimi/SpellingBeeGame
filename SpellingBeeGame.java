import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import java.sql.*;
import java.io.IOException;

//Name = Muhammad Afeef Imran Mughal 
//Matriculation Number = 31705   

public class SpellingBeeGame extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vocabulary";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Barcelonalm10!";
    private static final int MAX_TIME = 15;
    private static final int MAX_TRIES = 3;
    private int timeLeft = MAX_TIME;
    
    private JLabel wordLabel;
    private JTextField inputField;
    private JButton submitButton;
    private JLabel resultLabel;
    private int score;
    private int tries;
    private String currentWord;
    private Timer timer;
    private DatabaseConnection dbConnection;


    public SpellingBeeGame() {
        super("Spelling Bee Game");

        dbConnection = new DatabaseConnection(DB_URL, DB_USER, DB_PASSWORD);
        currentWord = dbConnection.getRandomWord();
        score = 0;
        tries = 0;

        wordLabel = new JLabel(currentWord);
        wordLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        wordLabel.setHorizontalAlignment(JLabel.CENTER);

        inputField = new JTextField();
        inputField.setFont(new Font("Serif", Font.PLAIN, 24));
        inputField.setHorizontalAlignment(JTextField.CENTER);

        submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkAnswer();
            }
        });

        resultLabel = new JLabel("Score: " + score);
        resultLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        resultLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 1));
        mainPanel.add(wordLabel);
        mainPanel.add(inputField);
        mainPanel.add(submitButton);
        mainPanel.add(resultLabel);

        setContentPane(mainPanel);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTimer();
            }
        });
        timer.start();
        
    }

    private void checkAnswer() {
    if (inputField.getText().equalsIgnoreCase(currentWord)) {
        score++;
        resultLabel.setText("Score: " + score);
        currentWord = dbConnection.getRandomWord();
        wordLabel.setText(currentWord);
        inputField.setText("");
        tries = 0;
    } else {
        tries++;
        if (tries == MAX_TRIES) {
            //currentWord = dbConnection.getRandomWord();
            //wordLabel.setText(currentWord);
            //inputField.setText("");
            //tries = 0;
            gameOver();
        }
    }
    timeLeft = MAX_TIME;
    timer.start();
}

    private void updateTimer() {
    
        timeLeft--;
    
    if (timeLeft == 0) {
        gameOver();
        if (tries == MAX_TRIES) {
            gameOver();
        } else {
            currentWord = dbConnection.getRandomWord();
            wordLabel.setText(currentWord);
            inputField.setText("");
            tries = 0;
            timeLeft = MAX_TIME;
        }
    
    } else if (timeLeft > MAX_TIME - 3) {
        wordLabel.setText(currentWord);
    
        if (timeLeft == 14) {
    try {
        String[] cmd = {"say", currentWord};
        Runtime.getRuntime().exec(cmd);
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    } else {
        wordLabel.setText(Integer.toString(timeLeft));
    }
}




private void gameOver() {
    timer.stop();
    updateTimer(); // add this line
    inputField.setEditable(false);
    int option;
    if (tries == MAX_TRIES) {
        option = JOptionPane.showOptionDialog(
            this,
            "Game Over!",
            "Spelling Bee Game",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new Object[] {"Retry", "Close"},
            null
        );
        
    } else {
        option = JOptionPane.showOptionDialog(
            this,
            "Time's up!",
            "Spelling Bee Game",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new Object[] {"Retry", "Close"},
            null
        );
    }
    
    if (option == JOptionPane.YES_OPTION) {
        restartGame();
    } else {
        dbConnection.close();
        System.exit(0);
    }
}




private void restartGame() {
    score = 0;
    tries = 0;
    currentWord = dbConnection.getRandomWord();
    wordLabel.setText(currentWord);
    inputField.setText("");
    inputField.setEditable(true);
    resultLabel.setText("Score: " + score);
    timeLeft = MAX_TIME;
    timer.start();
}



    public static void main(String[] args) {
        new SpellingBeeGame();
    }

}

class DatabaseConnection {
    private Connection conn;
    private Random random;

    public DatabaseConnection(String url, String user, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conn = DriverManager.getConnection(url, user, password);
            this.random = new Random();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public void close() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public String getRandomWord() {
        String randomWord = null;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT word FROM word_list ORDER BY RAND() LIMIT 1");
            if (rs.next()) {
                randomWord = rs.getString("word");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return randomWord;
    }
    
}

