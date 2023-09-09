package org.example;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    private static JFrame frame;
    private static JPanel panel;
    private static JLabel statusLabel;

    private static MongoClient mongoClient;
    private static MongoCollection<Document> mongoCollection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
            connectToMongoDB();
        });
    }

    private static void createAndShowGUI() {
        frame = new JFrame("MongoDB Login/Register");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        statusLabel = new JLabel("Connection to database successful");
        panel.add(statusLabel);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        panel.add(loginButton);
        panel.add(registerButton);

        frame.add(panel);
        frame.setSize(300, 200);
        frame.setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });
    }

    private static void connectToMongoDB() {
        String URI = ""; // Your mongodb compass url
        MongoClientURI mongoClientURI = new MongoClientURI(URI);
        mongoClient = new MongoClient(mongoClientURI);

        MongoDatabase mongoDatabase = mongoClient.getDatabase("test");
        mongoCollection = mongoDatabase.getCollection("test");
    }

    public static void login() {
        String username = JOptionPane.showInputDialog(frame, "Enter your username:");
        if (username != null) {
            String password = JOptionPane.showInputDialog(frame, "Enter your password:");
            if (password != null) {
                Document found = mongoCollection.find(new Document("username", username)).first();
                if (found != null) {
                    String storedPassword = found.getString("password");
                    if (password.equals(storedPassword)) {
                        statusLabel.setText("Login successful! Welcome, " + username + "!");
                    } else {
                        statusLabel.setText("Incorrect password. Please try again.");
                    }
                } else {
                    statusLabel.setText("Username not found. Please try again.");
                }
            }
        }
    }

    public static void register() {
        String username = JOptionPane.showInputDialog(frame, "Enter a new username:");
        if (username != null) {
            Document found = mongoCollection.find(new Document("username", username)).first();
            if (found == null) {
                String password = JOptionPane.showInputDialog(frame, "Enter a password for your account:");
                if (password != null) {
                    Document newUser = new Document("username", username).append("password", password);
                    mongoCollection.insertOne(newUser);
                    statusLabel.setText("Registration successful! You can now log in.");
                }
            } else {
                statusLabel.setText("Username already exists. Please choose another.");
            }
        }
    }
}
