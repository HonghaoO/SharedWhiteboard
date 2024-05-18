package server;

import whiteboard.DrawingAction;
import whiteboard.DrawingActionHistory;
import whiteboard.ManagerBoard;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateWhiteBoard {
    private ServerSocket serverSocket;
    private String username;
    private DrawingActionHistory actionHistory = new DrawingActionHistory();
    private List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private ExecutorService pool = Executors.newCachedThreadPool();
    private List<String> usernames = new CopyOnWriteArrayList<>();

    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private ManagerBoard managerBoard;
    
    private boolean isManagerConnected = false;
    private int waitCount = 1;

    public CreateWhiteBoard(String serverAddress, int port, String username) {
        this.username = username;
        
        
        startServer(serverAddress, port);
        startManager(serverAddress, port);
    }

    private void startServer(String serverAddress, int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on address: " + serverAddress + " at port: " + port);
            new Thread(this::acceptClients).start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void startManager(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            output.writeObject(username); // Send the username to the server
            System.out.println("Connected to the server at " + serverAddress + " on port " + port);

            SwingUtilities.invokeLater(() -> {
                managerBoard = new ManagerBoard(username, serverAddress, port); // Pass username to managerBoard
                managerBoard.setVisible(true);
            });

            new Thread(this::listenForServerMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptClients() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                
                // If the manager is not connected yet, this connection is the manager
                if (!isManagerConnected) {
                    isManagerConnected = true; // Mark manager as connected
                    
                    handleNewClientConnection(clientSocket); // Handle manager connection without asking permission
                    System.out.println("Manager connect run.");
                } else if(waitCount == 1) {
                	System.out.println("Manager 2 connect run.");
                    handleNewClientConnection(clientSocket);
                    waitCount = 2;
                    
                } else if (waitCount == 2){
                    // Show permission dialog to the manager for client connections
                	System.out.println("Client connect run.");
                    SwingUtilities.invokeLater(() -> {
                        int response = JOptionPane.showConfirmDialog(
                            managerBoard,
                            "A new client is trying to connect. Do you want to allow it?",
                            "Client Connection Request",
                            JOptionPane.YES_NO_OPTION
                        );
                        
                        if (response == JOptionPane.YES_OPTION) {
                            handleNewClientConnection(clientSocket);
                        } else {
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("Client connection denied.");
                        }
                    });
                    waitCount += 1;
                } else {
                	System.out.println("Last one");
                    handleNewClientConnection(clientSocket);
                    waitCount = 2;
                }
                    
                
                System.out.println("Count = " + waitCount);
//                System.out.println("manager = " + isManagerConnected);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void handleNewClientConnection(Socket clientSocket) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush(); // Ensure the stream is flushed before creating ObjectInputStream
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ClientHandler clientHandler = new ClientHandler(clientSocket, in, out);
            clients.add(clientHandler);
            System.out.println("Client added: " + clientHandler.username);
            pool.execute(clientHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private String username;

        public ClientHandler(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
            this.socket = socket;
            this.in = in;
            this.out = out;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void run() {
            try {
                System.out.println("Handler started for client: " + username);
                Object usernameCheck = in.readObject();
                if (!(usernameCheck instanceof DrawingAction)) {
                    username = (String) usernameCheck;
                    
                    if (usernames.contains(username)) {
                        out.writeObject(new DrawingAction(DrawingAction.ActionType.DISCONNECT, "Username not unique"));
                        out.flush();
                        System.out.println("Client connection denied due to non-unique username: " + username);
                    } 
                    usernames.add(username);
                    broadcastUsernames();
                    setUsername((String) usernameCheck);
                }

                sendHistory();

                Object obj;
                while ((obj = in.readObject()) != null) {
                    if (obj instanceof DrawingAction) {
                        DrawingAction action = (DrawingAction) obj;
                        if (action.getActionType() == DrawingAction.ActionType.KICK) {
                            String usernameToKick = action.getText();
                            disconnectUser(usernameToKick);
                        } else {
                            actionHistory.addAction(action);
                            broadcastAction(action, this);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error handling client: " + this.username + " or the client have disconnected");
                usernames.remove(this.username);
                broadcastUsernames();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error closing socket: " + e.getMessage());
                }
                clients.remove(this);
            }
        }
        private void sendHistory() {
            try {
                out.writeObject(actionHistory.getHistory());
                out.flush();
                System.out.println("Sent history to client: " + username);
            } catch (IOException e) {
                System.out.println("Error sending history: " + e.getMessage());
            }
        }

        private void broadcastAction(DrawingAction action, ClientHandler sender) {
            for (ClientHandler client : clients) {
                try {
                    if (client.out != null) {
                        client.out.writeObject(action);
                        client.out.flush();
                    }
                } catch (IOException e) {
                    System.out.println("Error broadcasting action to " + client.username + ": " + e.getMessage());
                    clients.remove(client);
                    try {
                        client.socket.close();
                    } catch (IOException ex) {
                        System.out.println("Error closing socket for " + client.username + ": " + ex.getMessage());
                    }
                }
            }
        }

        private void broadcastUsernames() {
            DrawingAction usernameListAction = new DrawingAction(DrawingAction.ActionType.USER_LIST, String.join(",", usernames));
            for (ClientHandler client : clients) {
                try {
                    client.out.writeObject(usernameListAction);
                    client.out.flush();
                } catch (IOException e) {
                    System.out.println("Error broadcasting usernames to " + client.username + ": " + e.getMessage());
                    clients.remove(client);
                    try {
                        client.socket.close();
                    } catch (IOException ex) {
                        System.out.println("Error closing socket for " + client.username + ": " + ex.getMessage());
                    }
                }
            }
        }

        private void disconnectUser(String usernameToKick) {
            for (ClientHandler client : clients) {
                if (client.username != null) {
                    System.out.println("diconnection username: " + client.username);
                    if (client.username.equals(usernameToKick)) {
                        try {
                            System.out.println("user to kick: " + usernameToKick);
                            client.socket.close();
                        } catch (IOException e) {
                            System.out.println("Error closing socket for kicked user: " + usernameToKick);
                        }
                        clients.remove(client);
                        usernames.remove(usernameToKick);
                        broadcastUsernames();
                        System.out.println("User kicked: " + usernameToKick);
                        break;
                    }
                }
            }
        }
    }

    private void listenForServerMessages() {
        try {
            Object test = input.readObject();
            if (test instanceof DrawingAction) {
                DrawingAction action = (DrawingAction) test;
                if (action.getActionType() == DrawingAction.ActionType.USER_LIST) {
//                    System.out.println("the text =: " + action.getText());
                    updateUsernameList(action.getText());
                }
            }
            List<DrawingAction> history = (List<DrawingAction>) input.readObject();
            SwingUtilities.invokeLater(() -> {
                for (DrawingAction action : history) {
                    managerBoard.handleDrawingAction(action);
                }
            });


            while (true) {
                Object obj = input.readObject();
                if (obj instanceof DrawingAction) {
                    DrawingAction action = (DrawingAction) obj;

                    if (action.getActionType() == DrawingAction.ActionType.USER_LIST) {
                        System.out.println("the text =: " + action.getText());
                        updateUsernameList(action.getText());
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            managerBoard.handleDrawingAction(action);
                        });
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error receiving action: " + e.getMessage());
            closeConnection();
        }
    }

    private void updateUsernameList(String usernames) {
        SwingUtilities.invokeLater(() -> {
            managerBoard.updateUserList(usernames);
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
        String username = args.length > 2 ? args[2] : "manager";

        new CreateWhiteBoard(serverAddress, port, username);
    }
}
