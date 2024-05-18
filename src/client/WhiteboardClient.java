package client;

import whiteboard.DrawingAction;
import whiteboard.ClientBoard;

import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.*; 

public class WhiteboardClient {
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private ClientBoard clientBoard;

    public WhiteboardClient(String serverAddress, int port, String username1) {
        String username = JOptionPane.showInputDialog("Enter your username:");
        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username cannot be empty");
            System.exit(0);
        }

        try {
            socket = new Socket(serverAddress, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            output.writeObject(username); // Send the username to the server
            System.out.println("Connected to the server at " + serverAddress + " on port " + port);

            
            
            javax.swing.SwingUtilities.invokeLater(() -> {
                clientBoard = new ClientBoard(username, serverAddress, port); // Pass username to ClientBoard
                clientBoard.setVisible(true);
            });

            new Thread(this::listenForServerMessages).start();
        } catch (IOException e) {
        	SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Connection Failed", "Disconnected", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0); // Close the application
            });

        }
    }

//    public void sendAction(DrawingAction action) {
//        try {
//        	System.out.println("Action Sent by client: ");
//
//            output.writeObject(action);
//            output.flush();
//        } catch (IOException e) {
//            System.out.println("Error sending action to server: " + e.getMessage());
//        }
//    }



    private void listenForServerMessages() {
        try {
        	Object test = input.readObject();
        	if (test instanceof DrawingAction) {
                DrawingAction action = (DrawingAction) test;
                System.out.println("the action =: " + action.getActionType());
                if (action.getActionType() == DrawingAction.ActionType.USER_LIST) {
                	//System.out.println("the text =: " + action.getText());

                    updateUsernameList(action.getText());
                } else if (action.getActionType() == DrawingAction.ActionType.DISCONNECT) {
                	//System.out.println("the text =: " + action.getText());
                	SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, "Duplicated Username", "Disconnected", JOptionPane.INFORMATION_MESSAGE);
                        closeConnection();
                        System.exit(0);
                    });


                    
                }
            }
            List<DrawingAction> history = (List<DrawingAction>) input.readObject();
            javax.swing.SwingUtilities.invokeLater(() -> {
                for (DrawingAction action : history) {
                    clientBoard.handleDrawingAction(action);
                }
            });
            
//            System.out.println("------------------------");

            output.flush();
            while (true) {
                Object obj = input.readObject();
                if (obj instanceof DrawingAction) {
                    DrawingAction action = (DrawingAction) obj;
                    
                    if (action.getActionType() == DrawingAction.ActionType.USER_LIST) {
//                    	System.out.println("the text =: " + action.getText());

                        updateUsernameList(action.getText());
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            clientBoard.handleDrawingAction(action);
                        });
                    }
                }
            }
        } catch (Exception e) {
        	SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "You have been disconnected or kicked from the Server", "Disconnected", JOptionPane.INFORMATION_MESSAGE);
                closeConnection();
                System.exit(0); // Close the application
            });

        }
    }
    
    private void updateUsernameList(String usernames) {
        SwingUtilities.invokeLater(() -> {
            clientBoard.updateUserList(usernames);
        });
    }


    private void closeConnection() {
        try {
        	
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
            

        } catch (IOException e) {
            System.out.println("Error when closing the connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String serverAddress = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5000;
        String username = args.length > 2 ? args[2] : null;
        new WhiteboardClient(serverAddress, port, username);
    }
}
