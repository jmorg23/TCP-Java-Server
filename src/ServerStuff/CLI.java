package ServerStuff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;

public class CLI {

    private JFrame frame;
    private JTextField commandField;
    private JTextArea outputArea;
    private ArrayList<String> commandHistory;
    private int commandIndex;

    public CLI() {

        commandHistory = new ArrayList<>();
        commandIndex = 0;

    }

    public void startGUI() {
        createAndShowGUI();

    }

    private void createAndShowGUI() {
        frame = new JFrame("Game Command Line");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        commandField = new JTextField();
        commandField.addActionListener(new CommandListener());
        commandField.addKeyListener(new CommandKeyListener());
        commandField.setFont(new Font("Monospaced", Font.PLAIN, 18));

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 18));

        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        panel.add(commandField, BorderLayout.SOUTH);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    private class CommandListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = commandField.getText();
            commandField.setText("");
            commandHistory.add(input);
            commandIndex = commandHistory.size();
            processCommand(input);
        }
    }

    private class CommandKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                if (commandIndex > 0) {
                    commandIndex--;
                    commandField.setText(commandHistory.get(commandIndex));
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                if (commandIndex < commandHistory.size() - 1) {
                    commandIndex++;
                    commandField.setText(commandHistory.get(commandIndex));
                } else {
                    commandField.setText("");
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    private void processCommand(String input) {
        String[] parts = input.split(" ");
        String command = parts[0].toLowerCase();

        switch (command) {
            case "/disp":
                display(parts[1]);
                break;
            case "/show":
                display(parts[1]);
                break;

            case "/rem":
                remove(parts[1]);
                break;
            case "/del":
                remove(parts[1]);
                break;
            case "/h":

                printHelp();
                break;
            case "/q":
                frame.dispose();
                break;
            default:
                outputArea.append("Unknown command. Type '/h' for help.\n");
        }
    }



    private void remove(String g) {
        if (g.equals("/all")) {
            System.out.println("Removing all games");
            outputArea.append("Removing all games");
            for (Game c : ServerMain.games) {
                ServerMain.removeGame(c.getPassword());
            }
            System.out.println("Successfully removed");
            outputArea.append("Successfully removed");
        }
        if (ServerMain.usedPasswords.contains(g)) {

            outputArea.append("Attempting to remove game with password: " + g);

            ServerMain.removeGame(g);
            outputArea.append("successfully removed");
        } else {
            outputArea.append("no game with password: " + g);
        }
    }

    private void end(String u, String p) {
        outputArea.append("looking for client with username: " + u + " with password:" + p);
        if (ServerMain.usedPasswords.contains(p)) {
            for (int i = 0; i < ServerMain.games.size(); i++) {

                if (ServerMain.games.get(i).getPassword().equals(p)) {
                    for (Client c : ServerMain.games.get(i).getClients()) {
                        if (c.getUsername().equals(u)) {
                            try {
                                outputArea.append("ending " + u + " from script");
                                c.end();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

    }

    private void display(String g) {
        if (g.equals("/all")) {
            outputArea.append("Showing all games");
            for (Game c : ServerMain.games) {
                outputArea.append("Game: " + c.getID());
            }
        } else if (g.substring(0, 7).equals("clients")) {
            String a = g.substring(8, g.length());
            outputArea.append("looking for clients in: " + a);
            for (Game ga : ServerMain.games) {
                if (ga.getPassword().equals(a)) {
                    for (Client c : ga.getClients()) {
                        outputArea.append("found client with username: " + c.getUsername());
                    }
                }
            }

        }

    }

    private void printHelp() {
        outputArea.append("Available commands:\n");
        outputArea.append(
                "/show or /disp \n          clients <GameCode(password)>\n           </all (for all active games)> Displays specific game info.\n");
        outputArea.append(
                "/rem or /del <GameCode(password)> </all (for all active games)> - removes active game sessions.\n");

        // outputArea.append("/end <GameCode(password)> </all (for all active games)> -
        // removes active game sessions.\n");

        outputArea.append("/rem or rem <object_name> - Add an object\n");
        outputArea.append("/rem or rem <object_name> - Add an object\n");
        outputArea.append("/rem or rem <object_name> - Add an object\n");
        outputArea.append("/rem or rem <object_name> - Add an object\n");
        outputArea.append("/rem or rem <object_name> - Add an object\n");
        outputArea.append("/rem or rem <object_name> - Add an object\n");
        outputArea.append("/rem or rem <object_name> - Add an object\n");
        outputArea.append("/rem or rem <object_name> - Add an object\n");
        outputArea.append("/rem or rem <object_name> - Add an object\n");

        outputArea.append("/h - Print this help message\n");
        outputArea.append("/q - Quit the command line\n");
    }

}
