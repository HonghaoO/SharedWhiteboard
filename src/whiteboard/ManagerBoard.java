package whiteboard;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class ManagerBoard extends ClientBoard {

	private JMenuItem saveAsItem;
    private JMenuItem openItem;
    private JButton newButton;
    private JButton saveButton;
    private JButton saveAsButton;
    private JButton openButton;
    private JButton exitButton;
    
    private DrawingActionHistory actionHistory = new DrawingActionHistory();
    
    private String defaultFilePath;




    public ManagerBoard(String username, String serverAddress, int port) {
        super(username, serverAddress, port);
        setTitle("Manager Board");

        // Add any manager-specific components or functionality here
        initManagerComponents();
    }
    
    @Override
    public void handleDrawingAction(DrawingAction action) {
        super.handleDrawingAction(action);
        actionHistory.addAction(action);
    }

    private void initManagerComponents() {
        // Example: Adding a new button for manager-specific functionality
        JButton managerButton = new JButton("Kick User");
        managerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Define manager-specific action here
                showKickUserDialog();
            }
        });
        
        toolBar.add(new JLabel("     Manager:"));
        
        newButton = new JButton("New");
        newButton.setToolTipText("New");
        toolBar.add(newButton);
        
        toolBar.add(managerButton);

        
     // Add saveAs and open buttons
        saveButton = new JButton("Save");
        saveButton.setToolTipText("Save");
        toolBar.add(saveButton);
        
        saveAsButton = new JButton("Save as");
        saveAsButton.setToolTipText("Save as");
        toolBar.add(saveAsButton);
        
        openButton = new JButton("Open");
        openButton.setToolTipText("Open");
        toolBar.add(openButton);
        
        exitButton = new JButton("Close");
        exitButton.setToolTipText("Close");
        toolBar.add(exitButton);
        
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, 1024, 768);
                g.setColor(foreColor);
                DrawingAction action = new DrawingAction(DrawingAction.ActionType.CLEAR, backColor.getRGB(), foreColor.getRGB());
                sendDrawingAction(action);

                canvas.repaint();
            }
        });


        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });

        
        saveAsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAsFile();
            }
        });

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });


        // Add saveAs and open menu items
        saveAsItem = new JMenuItem("Save as");
        openItem = new JMenuItem("Open");
//        fileMenu.add(openItem);
//        fileMenu.add(saveAsItem);

        saveAsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAsFile();
            }
        });

        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = JOptionPane.showConfirmDialog(ManagerBoard.this, "Do you want to close and exit the current whiteboard?", "Notice", JOptionPane.YES_NO_OPTION);
                if (i == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        // Initialize any other manager-specific components
    }

    
    
    
    // Initialize any other manager-specific components
//        exitButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                int i = JOptionPane.showConfirmDialog(ManagerBoard.this, "Are you sure you want to exit? This will disconnect all users.", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
//                if (i == JOptionPane.YES_OPTION) {
//                    kickAllClients();
//                    System.exit(0);
//                }
//            }
//        });

    private void saveFile() {
        if (defaultFilePath == null) {
            defaultFilePath = System.getProperty("user.dir") + File.separator + "default_save";
        }

        try {
            File pngFile = new File(defaultFilePath + ".png");
            ImageIO.write(image, "png", pngFile);

            File txtFile = new File(defaultFilePath + ".txt");
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(txtFile))) {
                oos.writeObject(actionHistory.getHistory());
            }

            JOptionPane.showMessageDialog(ManagerBoard.this, "File saved to default path", "Notice", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    private void saveAsFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG files", "png");
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Text files", "txt");
        chooser.addChoosableFileFilter(pngFilter);
        chooser.addChoosableFileFilter(txtFilter);
        chooser.setFileFilter(pngFilter);

        int returnVal = chooser.showSaveDialog(ManagerBoard.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String filePath = file.getPath();

            try {
                if (chooser.getFileFilter() == pngFilter) {
                    if (!filePath.toLowerCase().endsWith(".png")) {
                        file = new File(filePath + ".png");
                    }
                    ImageIO.write(image, "png", file);
                    JOptionPane.showMessageDialog(ManagerBoard.this, "File saved", "Notice", JOptionPane.INFORMATION_MESSAGE);
                } else if (chooser.getFileFilter() == txtFilter) {
                    if (!filePath.toLowerCase().endsWith(".txt")) {
                        file = new File(filePath + ".txt");
                    }
                    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                        oos.writeObject(actionHistory.getHistory());
                        JOptionPane.showMessageDialog(ManagerBoard.this, "File saved", "Notice", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG files", "png");
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Text files", "txt");
        chooser.addChoosableFileFilter(pngFilter);
        chooser.addChoosableFileFilter(txtFilter);
        chooser.setFileFilter(pngFilter);

        int returnVal = chooser.showOpenDialog(ManagerBoard.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String filePath = file.getPath();

            try {
                if (chooser.getFileFilter() == pngFilter) {
                    image = ImageIO.read(file);
                    canvas.setImage(image);
                    canvas.repaint();
                    JOptionPane.showMessageDialog(ManagerBoard.this, "File opened", "Notice", JOptionPane.INFORMATION_MESSAGE);
                } else if (chooser.getFileFilter() == txtFilter) {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                        List<DrawingAction> history = (List<DrawingAction>) ois.readObject();
                        for (DrawingAction action : history) {
                            handleDrawingAction(action);
                        }
                        JOptionPane.showMessageDialog(ManagerBoard.this, "File opened", "Notice", JOptionPane.INFORMATION_MESSAGE);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showKickUserDialog() {
        // Display a dialog with a text box for the manager to input the username to kick
        String usernameToKick = JOptionPane.showInputDialog(this, "Who would you like to kick?", "Kick User", JOptionPane.PLAIN_MESSAGE);

        if (usernameToKick != null) {
        	if (isUserInList(usernameToKick)) {

	            // Send a DrawingAction to kick the specified user
	        	System.out.println("kick performed, user to kick = " + usernameToKick);
	            DrawingAction action = new DrawingAction(DrawingAction.ActionType.KICK, usernameToKick);
	            sendDrawingAction(action);
        	} else {
                JOptionPane.showMessageDialog(this, "User not found: " + usernameToKick, "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }
    
    private void kickAllClients() {
        // Get the list of users from the JList
        String[] userArray = new String[userList.getModel().getSize()];
        for (int i = 0; i < userArray.length; i++) {
            userArray[i] = userList.getModel().getElementAt(i);
        }
        // Send a KICK action for each user
        for (String username : userArray) {
            if (!username.equals(this.username)) { // Avoid kicking the manager itself
                DrawingAction action = new DrawingAction(DrawingAction.ActionType.KICK, username);
                sendDrawingAction(action);
            }
        }
    }
    
    private boolean isUserInList(String usernameToKick) {
        // Get the list of users from the JList
        String[] userArray = new String[userList.getModel().getSize()];
        for (int i = 0; i < userArray.length; i++) {
            userArray[i] = userList.getModel().getElementAt(i);
        }
        // Check if the username exists in the user list
        return Arrays.asList(userArray).contains(usernameToKick);
    }

    
}
