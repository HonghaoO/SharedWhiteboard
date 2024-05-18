package whiteboard;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultCaret;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientBoard extends JFrame {
    private static final long serialVersionUID = 1L;
    BufferedImage image = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_BGR);
    Graphics gr = image.getGraphics();
    Graphics2D g = (Graphics2D) gr;
    DrawCanvas canvas = new DrawCanvas();

    Shapes shape = new Shapes();
    Color foreColor = Color.BLACK;
    Color backColor = Color.WHITE;

    private int x1 = 0;
    private int y1 = 0;
    private int x2 = 0;
    private int y2 = 0;
    
    private BufferedImage tempImage = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_ARGB);
    private Graphics2D tempG = tempImage.createGraphics();
    
    

    private boolean eraser = false;
    private boolean brush = true;
    private boolean lineFlag = false;
    private boolean rectFlag = false;
    private boolean trangleFlag = false;
    private boolean oval = false;
    private boolean circle = false;
    private boolean fillovil = false;
    private boolean fillrect = false;
    private boolean cliRect = false;
    private boolean textFlag = false;

    protected JToolBar toolBar;


    private JButton brushButton;
    private JButton eraserButton;
    private JButton clearButton;
    private JButton brushButton1;
    private JButton brushButton2;
    private JButton brushButton3;
    private JButton lineButton;
    private JButton trangleButton;
    private JButton rectButton;
    private JButton circleButton;
    private JButton ovilButton;
    private JButton fillRect;
    private JButton fillOvil;

    private JButton foreButton;
    private JButton backButton;
    private JButton textButton; // New text button


    private JMenuItem exitItem;
    private JMenuBar menuBar;


    private JPanel downPanel;

    private Socket socket;
    private ObjectOutputStream out;
    protected JList<String> userList;

    // Chat components
    private JTextArea chatArea;
    private JTextField chatInput;
    
    public String username;
    
    private int currentStrokeSize;

    public ClientBoard(String username, String serverAddress, int port) {
    	this.username = username;
        setTitle(username + "'s Shared Whiteboard");

        setBounds(0, 0, 1280, 768); 
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            socket = new Socket(serverAddress, port); // Connect to the server
            out = new ObjectOutputStream(socket.getOutputStream());
//            System.out.println("Server Connected");
        } catch (IOException e) {
            e.printStackTrace();
        }

        init();
        addListener();
    }

    private void init() {
        g.setColor(backColor);
        g.fillRect(0, 0, 1024, 768);
        g.setColor(foreColor);
        canvas.setImage(image);
        getContentPane().add(canvas, BorderLayout.CENTER);

        toolBar = new JToolBar();
        toolBar.setLayout(new GridLayout(2, 0)); // Set layout to grid with 2 rows
        toolBar.setPreferredSize(new Dimension(toolBar.getWidth(), 60)); // Increase height of toolbar
        getContentPane().add(toolBar, BorderLayout.NORTH);
        downPanel = new JPanel();
        getContentPane().add(downPanel, BorderLayout.SOUTH);
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        exitItem = new JMenuItem("Close");

        
        eraserButton = new JButton("Erase");
        eraserButton.setToolTipText("Erase");
        toolBar.add(eraserButton);
        clearButton = new JButton("Clear");
        clearButton.setToolTipText("Clear");
        toolBar.add(clearButton);
        brushButton = new JButton("Brush");
        brushButton.setToolTipText("Brush");
        toolBar.add(brushButton);
        toolBar.addSeparator();
        toolBar.add(new JLabel("      Brush Size:"));
        
        brushButton1 = new JButton("Small");
        brushButton1.setToolTipText("Small");
        toolBar.add(brushButton1);
        brushButton2 = new JButton("Medium");
        brushButton2.setToolTipText("Medium");
        toolBar.add(brushButton2);
        brushButton3 = new JButton("Large");
        brushButton3.setToolTipText("Large");
        toolBar.add(brushButton3);
        toolBar.add(new JLabel("            Shapes:"));
        lineButton = new JButton("Line");
        lineButton.setToolTipText("Line");
        toolBar.add(lineButton);
        rectButton = new JButton("Rectangle");
        rectButton.setToolTipText("Rectangle");
        toolBar.add(rectButton);
        fillRect = new JButton("Rect. Filled");
        fillRect.setToolTipText("Rect. Filled");
        toolBar.add(fillRect);
        trangleButton = new JButton("Triangle");
        toolBar.add(trangleButton);
        circleButton = new JButton("Circle");
        circleButton.setToolTipText("Circle");
        toolBar.add(circleButton);
        ovilButton = new JButton("Oval");
        ovilButton.setToolTipText("Oval");
        toolBar.add(ovilButton);
        fillOvil = new JButton("Filled Oval");
        fillOvil.setToolTipText("Filled Oval");
        toolBar.add(fillOvil);
        toolBar.add(new JLabel("            Colours:"));
        foreButton = new JButton("Colour");
        foreButton.setToolTipText("Colour");
        toolBar.add(foreButton);
        backButton = new JButton("Back Colour");
        backButton.setToolTipText("Back Colour");
        toolBar.add(backButton);
        toolBar.addSeparator();

        textButton = new JButton("Text");
        textButton.setToolTipText("Text");
        toolBar.add(textButton);
        toolBar.addSeparator();



        // Chat components
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setPreferredSize(new Dimension(250, 0));
        JLabel chatTitle = new JLabel("Chat Room", JLabel.CENTER);
        chatPanel.add(chatTitle, BorderLayout.NORTH);
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        DefaultCaret caret = (DefaultCaret) chatArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatInput = new JTextField();

        chatPanel.add(chatScrollPane, BorderLayout.CENTER);
        chatPanel.add(chatInput, BorderLayout.SOUTH);
        getContentPane().add(chatPanel, BorderLayout.EAST);
        
        // User list component
        JPanel userListPanel = new JPanel(new BorderLayout());
        JLabel userListTitle = new JLabel("Online Users", JLabel.CENTER); // Add user list title
        userListPanel.add(userListTitle, BorderLayout.NORTH); // Add title to the top of the user list panel

        userList = new JList<>();
        JScrollPane userListScrollPane = new JScrollPane(userList);
        userListScrollPane.setPreferredSize(new Dimension(150, 0)); // Set preferred width for user list panel
        userListPanel.add(userListScrollPane, BorderLayout.CENTER);
        
        getContentPane().add(userListPanel, BorderLayout.WEST);


        canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    private void addListener() {
    	System.out.println(this.username);
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(final MouseEvent e) {
                if (brush) {
                    if (eraser) {
                        g.setColor(backColor);
                        g.fillRect(e.getX(), e.getY(), 20, 20);
                        DrawingAction action = new DrawingAction(DrawingAction.ActionType.ERASE, e.getX(), e.getY(), 20, 20, foreColor.getRGB(), currentStrokeSize);
                        sendDrawingAction(action);
                    } else {
                        x2 = e.getX();
                        y2 = e.getY();
                        g.setColor(foreColor);
                        g.drawLine(x1, y1, x2, y2);
    					x1 = e.getX();
    					y1 = e.getY();
                        DrawingAction action = new DrawingAction(DrawingAction.ActionType.BRUSH, x1, y1, x2, y2, foreColor.getRGB(), currentStrokeSize);
                        sendDrawingAction(action);

                    }
                    canvas.repaint();
                } else if (lineFlag || rectFlag || circle || oval || trangleFlag || fillrect || fillovil) {
                    // Temporary drawing on tempImage
                    tempG.drawImage(image, 0, 0, null); // Copy the current image to tempImage
                    tempG.setColor(foreColor);
                    tempG.setStroke(new BasicStroke(currentStrokeSize));
                    
                    if (lineFlag) {
                        shape.lineShape(tempG, x1, y1, e.getX(), e.getY());
                    } else if (rectFlag || fillrect) {
                        if (fillrect) {
                            tempG.fillRect(x1, y1, e.getX() - x1, e.getY() - y1);
                        } else {
                            shape.rectShape(tempG, x1, y1, e.getX() - x1, e.getY() - y1);
                        }
                    } else if (circle) {
                        int diameter = Math.min(Math.abs(e.getX() - x1), Math.abs(e.getY() - y1));
                        shape.circleShape(tempG, x1, y1, diameter);
                    } else if (oval || fillovil) {
                        if (fillovil) {
                            tempG.fillOval(x1, y1, e.getX() - x1, e.getY() - y1);
                        } else {
                            shape.ovalShape(tempG, x1, y1, e.getX() - x1, e.getY() - y1);
                        }
                    } else if (trangleFlag) {
                        int x[] = new int[3];
                        int y[] = new int[3];
                        x[0] = x1;
                        y[0] = y1;
                        x[1] = e.getX();
                        x[2] = x1 - e.getX() / 2;
                        y[1] = e.getY();
                        y[2] = e.getY();
                        shape.trangleShape(tempG, x, y, 3);
                    }

                    canvas.setTempImage(tempImage); // Set the temporary image for preview
                    canvas.repaint();
                }
            }
        });
        
        // Chat function
        chatInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String message = chatInput.getText();
                    if (!message.trim().isEmpty()) {
                    	
                    	DrawingAction action = new DrawingAction(DrawingAction.ActionType.MESSAGE, username + ": " + message);
//                    	appendChatMessage(message); 
                    	sendDrawingAction(action);
                        chatInput.setText("");
                        System.out.println("message = " + message);
                    }
                }
            }
        });

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                x1 = e.getX();
                y1 = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                x2 = e.getX();
                y2 = e.getY();

                if (textFlag) {
                    String inputText = JOptionPane.showInputDialog("Enter text");
                    if (inputText != null && !inputText.trim().isEmpty()) {
                        g.setColor(foreColor);
                        g.setFont(new Font("Serif", Font.PLAIN, 20));
                        g.drawString(inputText, x1, y1);
                        DrawingAction action = new DrawingAction(DrawingAction.ActionType.TEXT, x1, y1, inputText, foreColor.getRGB());
                        sendDrawingAction(action);
                    }
                } else if (lineFlag) {
                    shape.lineShape(g, x1, y1, x2, y2);
                    canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    DrawingAction action = new DrawingAction(DrawingAction.ActionType.STRAIGHT_LINE, x1, y1, x2, y2, foreColor.getRGB(), currentStrokeSize);
                    sendDrawingAction(action);
                } else if (rectFlag) {
                    shape.rectShape(g, x1, y1, x2 - x1, y2 - y1);
                    canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    DrawingAction action = new DrawingAction(DrawingAction.ActionType.RECT_EMPTY, x1, y1, x2, y2, foreColor.getRGB(), currentStrokeSize);
                    sendDrawingAction(action);
                } else if (fillrect) {
                    g.fillRect(x1, y1, x2 - x1, y2 - y1);
                    canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    DrawingAction action = new DrawingAction(DrawingAction.ActionType.RECT_FILL, x1, y1, x2, y2, foreColor.getRGB(), currentStrokeSize);
                    sendDrawingAction(action);
                } else if (trangleFlag) {
                    int x[] = new int[3];
                    int y[] = new int[3];
                    x[0] = x1;
                    y[0] = y1;
                    x2 = e.getX();
                    y2 = e.getY();
                    x[1] = x2;
                    x[2] = x1 - x2 / 2;
                    y[1] = y2;
                    y[2] = y2;
                    shape.trangleShape(g, x, y, 3);
                    canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    DrawingAction action = new DrawingAction(DrawingAction.ActionType.TRIANGLE, x1, y1, x2, y2, foreColor.getRGB(), currentStrokeSize);
                    sendDrawingAction(action);
                } else if (circle) {
                	int diameter = Math.min(Math.abs(x2 - x1), Math.abs(y2 - y1));
                    shape.circleShape(g, x1, y1, diameter);
                    canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    DrawingAction action = new DrawingAction(DrawingAction.ActionType.CIRCLE, x1, y1, x2, y2, foreColor.getRGB(), currentStrokeSize);
                    sendDrawingAction(action);
                } else if (oval) {
                    shape.ovalShape(g, x1, y1, x2 - x1, y2 - y1);
                    canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    DrawingAction action = new DrawingAction(DrawingAction.ActionType.OVAL, x1, y1, x2, y2, foreColor.getRGB(), currentStrokeSize);
                    sendDrawingAction(action);
                } else if (fillovil) {
                    g.fillOval(x1, y1, x2 - x1, y2 - y1);
                    canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    DrawingAction action = new DrawingAction(DrawingAction.ActionType.OVAL_FILL, x1, y1, x2, y2, foreColor.getRGB(), currentStrokeSize);
                    sendDrawingAction(action);
                } else if (cliRect) {
                    g.setColor(backColor);
                    g.fillRect(x1, y1, x2 - x1, y2 - y1);
                    g.setColor(foreColor);
                    canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    DrawingAction action = new DrawingAction(DrawingAction.ActionType.CLI_RECT, x1, y1, x2, y2, foreColor.getRGB(), currentStrokeSize);
                    sendDrawingAction(action);
                }
                
                // Clear temporary image after drawing
                tempG.clearRect(0, 0, tempImage.getWidth(), tempImage.getHeight());
                canvas.setTempImage(null);
                x1 = 0;
                y1 = 0;
                canvas.repaint();
            }
        });



        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = JOptionPane.showConfirmDialog(ClientBoard.this, "Do you want to exit?", "Notice", JOptionPane.YES_NO_OPTION);
                if (i == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });


        brushButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                brush = true;
                eraser = false;
                rectFlag = false;
                lineFlag = false;
                trangleFlag = false;
            	circle = false;
                oval = false;
                cliRect = false;
                textFlag = false;
                canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }
        });

        eraserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eraser = true;
                brush = true;
                rectFlag = false;
                lineFlag = false;
                trangleFlag = false;
            	circle = false;
                oval = false;
                cliRect = false;
                textFlag = false;
                Toolkit kit = Toolkit.getDefaultToolkit();
                Image img = kit.createImage("lib/cursor.png");
                Cursor c = kit.createCustomCursor(img, new Point(0, 0), "clear");
                canvas.setCursor(c);
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                g.setColor(backColor);
                g.fillRect(0, 0, 1024, 768);
                g.setColor(foreColor);
                DrawingAction action = new DrawingAction(DrawingAction.ActionType.CLEAR, backColor.getRGB(), foreColor.getRGB());
                sendDrawingAction(action);

                canvas.repaint();
            }
        });

        brushButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BasicStroke s = new BasicStroke(1);
                g.setStroke(s);
                currentStrokeSize = 1; // Update current stroke size
            }
        });

        brushButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BasicStroke s = new BasicStroke(5);
                g.setStroke(s);
                currentStrokeSize = 5; // Update current stroke size
            }
        });

        brushButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BasicStroke s = new BasicStroke(10);
                g.setStroke(s);
                currentStrokeSize = 10; // Update current stroke size
            }
        });


        lineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lineFlag = true;
                rectFlag = false;
                trangleFlag = false;
            	circle = false;
                oval = false;
                fillovil = false;
                fillrect = false;
                brush = false;
                cliRect = false;
                textFlag = false;
            }
        });

        rectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rectFlag = true;
                lineFlag = false;
                trangleFlag = false;
            	circle = false;
                oval = false;
                fillovil = false;
                fillrect = false;
                brush = false;
                cliRect = false;
                textFlag = false;
            }
        });

        trangleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trangleFlag = true;
                rectFlag = false;
                lineFlag = false;
            	circle = false;
                oval = false;
                fillovil = false;
                fillrect = false;
                brush = false;
                cliRect = false;
                textFlag = false;
            }
        });
        
        circleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	circle = true;
                oval = true;
                trangleFlag = false;
                rectFlag = false;
                lineFlag = false;
                fillovil = false;
                fillrect = false;
                brush = false;
                cliRect = false;
                textFlag = false;
            }
        });

        ovilButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	circle = false;
                oval = true;
                trangleFlag = false;
                rectFlag = false;
                lineFlag = false;
                fillovil = false;
                fillrect = false;
                brush = false;
                cliRect = false;
                textFlag = false;
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color bgColor = JColorChooser.showDialog(ClientBoard.this, "Background Colour", Color.WHITE);
                if (!(bgColor == null)) {
                    backColor = bgColor;
                }
                g.setColor(backColor);
                g.fillRect(0, 0, 1024, 768);
                g.setColor(foreColor);
                canvas.repaint();
            }
        });

        foreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color frColor = JColorChooser.showDialog(ClientBoard.this, "Colour", Color.BLACK);
                if (!(frColor == null)) {
                    foreColor = frColor;
                }
                g.setColor(foreColor);
                canvas.repaint();
            }
        });



        fillOvil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	circle = false;
                fillovil = true;
                oval = false;
                trangleFlag = false;
                rectFlag = false;
                lineFlag = false;
                fillrect = false;
                cliRect = false;
                brush = false;
                textFlag = false;
            }
        });

        fillRect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	circle = false;
                fillrect = true;
                oval = false;
                trangleFlag = false;
                rectFlag = false;
                lineFlag = false;
                fillovil = false;
                cliRect = false;
                brush = false;
                textFlag = false;
            }
        });





        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int i = JOptionPane.showConfirmDialog(ClientBoard.this, "Do you want to exit?", "Notice", JOptionPane.YES_NO_OPTION);
                if (i == JOptionPane.YES_OPTION) {
                    System.exit(0);
                } else {
                }
            }
        });

        // Text button listener
        textButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFlag = true;
                brush = false;
                eraser = false;
                rectFlag = false;
                lineFlag = false;
                trangleFlag = false;
            	circle = false;
                oval = false;
                fillovil = false;
                fillrect = false;
                cliRect = false;
                canvas.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            }
        });
    }


    public void appendChatMessage(String message) {
        chatArea.append(message + "\n");
    }
    
    public void updateUserList(String usernames) {
        userList.setListData(usernames.split(","));
    }



    protected void sendDrawingAction(DrawingAction action) {
        try {
            out.writeObject(action);
            out.flush();
        } catch (IOException e) {
            System.out.println("Failed to send action: " + e.getMessage());
        }
    }

    public void handleDrawingAction(DrawingAction action) {
        x1 = action.getX1();
        y1 = action.getY1();
        x2 = action.getX2();
        y2 = action.getY2();
        BasicStroke originalStroke = (BasicStroke) g.getStroke();
        
//        System.out.println("Board received action back :" + action.getActionType() + action.getText());
        
        switch (action.getActionType()) {
	        case BRUSH:
	            g.setColor(new Color(action.getColor()));
	            g.setStroke(new BasicStroke(action.getStrokeSize()));
	            g.drawLine(action.getX1(), action.getY1(), action.getX2(), action.getY2());
	            g.setStroke(originalStroke);
	            break;
	        case ERASE:
	            g.setColor(backColor);
	            g.setStroke(new BasicStroke(action.getStrokeSize()));
	            g.fillRect(action.getX1(), action.getY1(), action.getX2(), action.getY2());
	            g.setStroke(originalStroke);
	            break;
            case STRAIGHT_LINE:
                g.setColor(new Color(action.getColor()));
                g.setStroke(new BasicStroke(action.getStrokeSize()));
                shape.lineShape(g, action.getX1(), action.getY1(), action.getX2(), action.getY2());
                g.setStroke(originalStroke);
                break;
            case RECT_EMPTY:
                g.setColor(new Color(action.getColor()));
                g.setStroke(new BasicStroke(action.getStrokeSize()));
                shape.rectShape(g, action.getX1(), action.getY1(), action.getX2() - action.getX1(), action.getY2() - action.getY1());
                g.setStroke(originalStroke);
                break;
            case RECT_FILL:
                g.setColor(new Color(action.getColor()));
                g.setStroke(new BasicStroke(action.getStrokeSize()));
                g.fillRect(action.getX1(), action.getY1(), action.getX2() - action.getX1(), action.getY2() - action.getY1());
                g.setStroke(originalStroke);
                break;
            case TRIANGLE:
                int x[] = new int[3];
                int y[] = new int[3];
                x1 = action.getX1();
                y1 = action.getY1();
                x[0] = x1;
                y[0] = y1;
                x2 = action.getX2();
                y2 = action.getY2();
                x[1] = x2;
                x[2] = x1 - x2 / 2;
                y[1] = y2;
                y[2] = y2;
                g.setColor(new Color(action.getColor()));
                g.setStroke(new BasicStroke(action.getStrokeSize()));
                shape.trangleShape(g, x, y, 3);
                g.setStroke(originalStroke);
                break;
            case CIRCLE:
                g.setColor(new Color(action.getColor()));
                g.setStroke(new BasicStroke(action.getStrokeSize()));
            	int diameter = Math.min(Math.abs(x2 - x1), Math.abs(y2 - y1));
                shape.circleShape(g, x1, y1, diameter);
                g.setStroke(originalStroke);
                break;
            case OVAL:
                g.setColor(new Color(action.getColor()));
                g.setStroke(new BasicStroke(action.getStrokeSize()));
                shape.ovalShape(g, x1, y1, x2 - x1, y2 - y1);
                g.setStroke(originalStroke);
                break;
            case OVAL_FILL:
                g.setColor(new Color(action.getColor()));
                g.setStroke(new BasicStroke(action.getStrokeSize()));
                g.fillOval(x1, y1, x2 - x1, y2 - y1);
                g.setStroke(originalStroke);
                break;
            case CLI_RECT:
                g.setColor(new Color(action.getColor()));
                g.setStroke(new BasicStroke(action.getStrokeSize()));
                g.fillRect(x1, y1, x2 - x1, y2 - y1);
                g.setStroke(originalStroke);
                break;
            case TEXT:
                g.setColor(new Color(action.getColor()));
                g.setFont(new Font("Serif", Font.PLAIN, 20));
                g.drawString(action.getText(), action.getX1(), action.getY1());
                g.setStroke(originalStroke);
                break;
            case MESSAGE:
//            	System.out.println("Message received back:" + action.getText());
            	appendChatMessage(action.getText()); 
            	break;
            case CLEAR:
                g.setColor(new Color(action.getBackColor()));
                g.fillRect(0, 0, 1024, 768);
                g.setColor(new Color(action.getForeColor()));
                break;
        }
        canvas.repaint();
    }
}
