package CodeQuest.Main;

import java.util.*;
import java.util.regex.*;

// Parses and executes Python-like commands for player control
public class CommandParser {
    public GamePanel gamePanel; // Reference to game panel
    public CommandAdapter adapter; // Adapter that executes commands
    private Map<String, Integer> variables; // Stores loop variables
    
    // Constructor initializes parser
    public CommandParser(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.adapter = new CommandAdapter(gamePanel);
        this.variables = new HashMap<>();
    }

    // Parses and executes command string
    public boolean parseCommand(String command) {
        try {
            command = command.trim();
            if (command.isEmpty()) return true;

            // Handle clear/stop command
            if (command.equals("clear") || command.equals("stop")) {
                adapter.clearQueue();
                return true;
            }

            // Handle help command
            if (command.equals("help")) {
                showHelp();
                return true;
            }

            // Handle for loop
            if (command.startsWith("for ")) {
                return parseForLoop(command);
            }

            // Handle if statement
            if (command.startsWith("if ")) {
                return parseIfStatement(command);
            }

            // Handle multiple commands separated by ; or newline
            if (command.contains("\n") || command.contains(";")) {
                String[] lines = command.split("[;\n]");
                boolean allValid = true;

                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        if (!parseCommand(line)) { // Recursively parse each command
                            allValid = false;
                        }
                    }
                }

                if (!allValid) {
                    dealDamageForError(); // Apply damage for error
                }

                return allValid;
            }

            // Execute single command
            boolean success = executeSingleCommand(command);

            if (!success) {
                dealDamageForError(); // Apply damage for error
            }

            return success;

        } catch (Exception e) {
            dealDamageForError();
            return false;
        }
    }
    
    // Applies damage to player when command has error
    private void dealDamageForError() {
        if (gamePanel.healthSystem != null && !gamePanel.healthSystem.isDead()) {
            boolean damageApplied = gamePanel.healthSystem.takeDamage(1);

            if (damageApplied && gamePanel.healthSystem.isDead()) {
                gamePanel.gameState = gamePanel.gameOverState; // Game over if health depleted
            }
        }
    }

    // Wrapper method for parsing
    public void parse(String command) {
        parseCommand(command);
    }

    // Parses Python-style for loop
    private boolean parseForLoop(String line) {
        Pattern pattern = Pattern.compile("for\\s+(\\w+)\\s+in\\s+range\\s*\\(\\s*(\\d+)\\s*\\)\\s*:\\s*(.+)");
        Matcher matcher = pattern.matcher(line);

        if (matcher.matches()) {
            String varName = matcher.group(1); // Loop variable name
            int count = Integer.parseInt(matcher.group(2)); // Loop count
            String body = matcher.group(3).trim(); // Loop body

            // Execute loop body count times
            for (int i = 0; i < count; i++) {
                variables.put(varName, i); // Set loop variable
                executeSingleCommand(body);
            }
            return true;
        }

        return false;
    }

    // Parses Python-style if statement
    private boolean parseIfStatement(String line) {
        Pattern pattern = Pattern.compile("if\\s+(.+?)\\s*:\\s*(.+)");
        Matcher matcher = pattern.matcher(line);

        if (matcher.matches()) {
            String condition = matcher.group(1).trim(); // Condition
            String command = matcher.group(2).trim(); // Command to execute

            if (evaluateCondition(condition)) { // Check condition
                executeSingleCommand(command);
            }
            return true;
        }

        return false;
    }

    // Evaluates conditional expression
    private boolean evaluateCondition(String condition) {
        Pattern pattern = Pattern.compile("(\\w+)\\s*([<>!=]+)\\s*(\\d+)");
        Matcher matcher = pattern.matcher(condition);

        if (matcher.matches()) {
            String varName = matcher.group(1); // Variable name
            String operator = matcher.group(2); // Comparison operator
            int value = Integer.parseInt(matcher.group(3)); // Comparison value

            int varValue = getVariableValue(varName);

            // Evaluate comparison
            switch (operator) {
                case "<": return varValue < value;
                case ">": return varValue > value;
                case "==": return varValue == value;
                case "!=": return varValue != value;
                case "<=": return varValue <= value;
                case ">=": return varValue >= value;
            }
        }
        return false;
    }

    // Gets value of variable (loop var or built-in)
    private int getVariableValue(String varName) {
        if (variables.containsKey(varName)) {
            return variables.get(varName);
        }

        // Built-in variables
        switch (varName) {
            case "x": return gamePanel.player.worldX;
            case "y": return gamePanel.player.worldY;
            case "health": return gamePanel.healthSystem != null ?
                          gamePanel.healthSystem.getCurrentHealth() : 0;
            default: return 0;
        }
    }
    
    // Executes a single command
    private boolean executeSingleCommand(String cmd) {
        cmd = cmd.trim();

        // Print command
        if (cmd.startsWith("print ")) {
            String message = cmd.substring(6).trim();
            message = message.replaceAll("^['\"]|['\"]$", ""); // Remove quotes
            adapter.executePrint(message);
            return true;
        }

        // Wait command
        if (cmd.startsWith("wait")) {
            if (cmd.equals("wait")) {
                adapter.executeWait(500); // Default wait
            } else {
                try {
                    String[] parts = cmd.split("\\s+");
                    if (parts.length > 1) {
                        int ms = Integer.parseInt(parts[1]); // Custom wait time
                        adapter.executeWait(ms);
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        }

        // Turn command
        if (cmd.startsWith("turn ")) {
            String direction = cmd.substring(5).trim();
            adapter.executeTurn(direction);
            return true;
        }

        // Walk command (move multiple steps)
        if (cmd.startsWith("walk ")) {
            String[] parts = cmd.split("\\s+");
            if (parts.length == 3) {
                try {
                    int steps = Integer.parseInt(parts[1]); // Number of steps
                    String direction = parts[2]; // Direction
                    for (int i = 0; i < steps; i++) {
                        executeMovement(direction);
                    }
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return false;
        }

        // Move command
        if (cmd.startsWith("move ")) {
            String direction = cmd.substring(5).trim();
            return executeMovement(direction);
        }

        // Method-style commands
        switch (cmd) {
            case "player.moveup()":
                adapter.executeMoveUp();
                return true;
            case "player.movedown()":
                adapter.executeMoveDown();
                return true;
            case "player.moveleft()":
                adapter.executeMoveLeft();
                return true;
            case "player.moveright()":
                adapter.executeMoveRight();
                return true;
            default:
                return false;
        }
    }

    // Executes movement in specified direction
    private boolean executeMovement(String direction) {
        direction = direction.toLowerCase().replaceAll("['\"]", "");

        switch (direction) {
            case "up":
            case "north":
            case "w":
                adapter.executeMoveUp();
                return true;
            case "down":
            case "south":
            case "s":
                adapter.executeMoveDown();
                return true;
            case "left":
            case "west":
            case "a":
                adapter.executeMoveLeft();
                return true;
            case "right":
            case "east":
            case "d":
                adapter.executeMoveRight();
                return true;
            default:
                return false;
        }
    }

    // Displays help information
    public void showHelp() {
        System.out.println("\n╔═══════════════ PYTHON COMMANDS ═══════════════╗");
        System.out.println("│                                                 │");
        System.out.println("│ MOVEMENT (Python style):                        │");
        System.out.println("│   move up / down / left / right                 │");
        System.out.println("│   walk 5 up                                     │");
        System.out.println("│   turn left                                     │");
        System.out.println("│                                                 │");
        System.out.println("│ MOVEMENT (Method style):                        │");
        System.out.println("│   player.moveup()                               │");
        System.out.println("│   player.movedown()                             │");
        System.out.println("│   player.moveleft()                             │");
        System.out.println("│   player.moveright()                            │");
        System.out.println("│                                                 │");
        System.out.println("│ PYTHON FOR LOOPS:                               │");
        System.out.println("│   for i in range(5): player.moveup()            │");
        System.out.println("│   for i in range(3): move right                 │");
        System.out.println("│                                                 │");
        System.out.println("│ PYTHON IF STATEMENTS:                           │");
        System.out.println("│   if x < 200: player.moveright()                │");
        System.out.println("│   if y > 100: move up                           │");
        System.out.println("│   if health > 2: move right                     │");
        System.out.println("│                                                 │");
        System.out.println("│ UTILITIES:                                      │");
        System.out.println("│   print \"message\"                               │");
        System.out.println("│   wait                                          │");
        System.out.println("│   wait 1000                                     │");
        System.out.println("│                                                 │");
        System.out.println("│ CONTROL:                                        │");
        System.out.println("│   clear / stop                                  │");
        System.out.println("│                                                 │");
        System.out.println("╚═════════════════════════════════════════════════╝\n");
    }
    
    // Returns number of queued commands
    public int getQueueSize() {
        return adapter.getQueueSize();
    }

    // Returns whether commands are currently executing
    public boolean isExecuting() {
        return adapter.isExecuting();
    }

    // Sets delay between command executions
    public void setCommandDelay(int delayMs) {
        adapter.setActionDelay(delayMs);
    }
}
