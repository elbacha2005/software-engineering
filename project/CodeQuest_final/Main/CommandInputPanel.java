package CodeQuest.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// UI panel for command input and output display
public class CommandInputPanel extends JPanel {

    private CommandParser commandParser; // Parser for executing commands
    private JTextField commandField; // Input field for commands
    private JTextArea outputArea; // Output display area
    private JButton executeButton; // Execute command button
    private JButton helpButton; // Show help button
    private JButton clearButton; // Clear queue button
    private JLabel queueLabel; // Shows queue size
    private JLabel hintLabel; // Shows command hints
    
    // Constructor initializes command input panel
    public CommandInputPanel(CommandParser commandParser) {
        this.commandParser = commandParser;
        initializeUI();
    }

    // Initializes UI components
    private void initializeUI() {
        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(350, 640));
        setBackground(new Color(45, 45, 48));
        
        JPanel topSection = new JPanel(new BorderLayout(5, 5));
        topSection.setBackground(new Color(45, 45, 48));
        
        hintLabel = new JLabel("💡 Try: for i in range(5): move up");
        hintLabel.setForeground(new Color(106, 153, 85));
        hintLabel.setFont(new Font("Consolas", Font.ITALIC, 11));
        hintLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBackground(new Color(45, 45, 48));
        
        commandField = new JTextField();
        commandField.setFont(new Font("Consolas", Font.PLAIN, 14));
        commandField.setBackground(new Color(30, 30, 30));
        commandField.setForeground(Color.WHITE);
        commandField.setCaretColor(Color.WHITE);
        commandField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        commandField.addActionListener(e -> executeCommand());
        
        commandField.addKeyListener(new KeyAdapter() {
            private int hintIndex = 0;
            private String[] hints = {
                "💡 Try: for i in range(5): move up",
                "💡 Try: if x < 200: move right",
                "💡 Try: move up; move right; move down",
                "💡 Try: walk 10 up",
                "💡 Try: for i in range(3): walk 5 right"
            };
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    e.consume();
                    hintIndex = (hintIndex + 1) % hints.length;
                    hintLabel.setText(hints[hintIndex]);
                }
            }
        });
        
        JLabel pythonLabel = new JLabel(">>> ");
        pythonLabel.setForeground(new Color(86, 156, 214));
        pythonLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        
        inputPanel.add(pythonLabel, BorderLayout.WEST);
        inputPanel.add(commandField, BorderLayout.CENTER);
        
        topSection.add(hintLabel, BorderLayout.NORTH);
        topSection.add(inputPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        buttonPanel.setBackground(new Color(45, 45, 48));
        
        executeButton = createStyledButton("▶ Execute", new Color(0, 120, 215));
        helpButton = createStyledButton("? Help", new Color(100, 100, 100));
        clearButton = createStyledButton("⊗ Clear", new Color(160, 50, 50));
        
        executeButton.addActionListener(e -> executeCommand());
        helpButton.addActionListener(e -> showHelp());
        clearButton.addActionListener(e -> clearQueue());
        
        queueLabel = new JLabel("Queue: 0 commands");
        queueLabel.setForeground(Color.LIGHT_GRAY);
        queueLabel.setFont(new Font("Consolas", Font.PLAIN, 12));
        
        buttonPanel.add(executeButton);
        buttonPanel.add(helpButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(queueLabel);
        
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        outputArea.setBackground(new Color(30, 30, 30));
        outputArea.setForeground(Color.LIGHT_GRAY);
        outputArea.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        outputArea.setRows(5);
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        scrollPane.setPreferredSize(new Dimension(800, 120));
        
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(new Color(45, 45, 48));
        topContainer.add(topSection, BorderLayout.CENTER);
        topContainer.add(buttonPanel, BorderLayout.SOUTH);
        
        add(topContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        appendOutput("🐍 Python-Style CodeQuest Ready!");
        appendOutput("Type Python commands or click Help for examples.\n");
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(110, 30));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    // Executes command from input field
    private void executeCommand() {
        String command = commandField.getText().trim();

        if (command.isEmpty()) {
            return;
        }

        appendOutput(">>> " + command);

        boolean success = commandParser.parseCommand(command);

        if (!success) {
            appendOutput("SyntaxError: Invalid command syntax");
            appendOutput("Type 'help' or click Help button for examples.\n");
        }

        commandField.setText("");
        updateQueueLabel();
    }

    // Shows help information
    private void showHelp() {
        commandParser.showHelp();
        appendOutput("\n--- HELP DISPLAYED IN CONSOLE ---\n");
        appendOutput("\n # MOVEMENT\n" +
                "move up / down / left / right\n" +
                "walk 10 right\n" +
                "\n" +
                "# PYTHON LOOPS\n" +
                "for i in range(5): move up\n" +
                "\n" +
                "# PYTHON CONDITIONS\n" +
                "if x < 200: move right\n" +
                "\n" +
                "# DEBUGGING\n" +
                "print \"checkpoint 1\"\n" +
                "\n" +
                "# CONTROL\n" +
                "clear\n" +
                "wait\n");
    }

    // Clears command queue
    private void clearQueue() {
        commandParser.parseCommand("clear");
        updateQueueLabel();
        appendOutput("🗑️ Command queue cleared.\n");
    }

    // Appends text to output area
    private void appendOutput(String text) {
        outputArea.append(text + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());

        // Limit output to last 100 lines
        String[] lines = outputArea.getText().split("\n");
        if (lines.length > 100) {
            StringBuilder sb = new StringBuilder();
            for (int i = lines.length - 100; i < lines.length; i++) {
                sb.append(lines[i]).append("\n");
            }
            outputArea.setText(sb.toString());
        }
    }

    // Updates queue label with current size
    public void updateQueueLabel() {
        int queueSize = commandParser.getQueueSize();
        queueLabel.setText("Queue: " + queueSize + " command" + (queueSize != 1 ? "s" : ""));

        // Change color based on queue size
        if (queueSize > 0) {
            queueLabel.setForeground(new Color(100, 255, 100));
        } else {
            queueLabel.setForeground(Color.LIGHT_GRAY);
        }
    }

    // Focuses the command input field
    public void focusCommandField() {
        commandField.requestFocus();
    }
}
