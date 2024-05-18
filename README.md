# Shared Whiteboard Application

## Overview

The Shared Whiteboard Application is a Java-based multi-user drawing tool that allows multiple users to collaborate on a shared canvas in real time. It supports a variety of drawing tools and features, including freehand drawing, shapes, text input, and a chat functionality. The application uses TCP/IP sockets for reliable communication and synchronization between clients and the server.

## Features

### Basic Features
- **Multi-user System**: Multiple users can draw on a shared interactive canvas.
- **Shapes**: Supports drawing lines, circles, ovals, rectangles, and triangles.
- **Free Draw and Erase**: Users can freehand draw and erase with different brush sizes.
- **Text Input**: Allows users to type text anywhere on the whiteboard.
- **Color Selection**: Provides a palette of at least 16 colors for drawing.
- **Real-time Preview**: See the approximate shape while dragging the mouse before finalizing it upon release.

### Advanced Features
1. **Chat Window**: Allows users to communicate via text.
2. **File Menu**: Includes options for creating a new whiteboard, opening, saving, saving as, and closing (managed by the whiteboard manager).
3. **User Management**: The manager can kick out a specific user.

### Usage Guidelines
- **Unique Usernames**: Each user must provide a unique username when joining the whiteboard.
- **Synchronized View**: All users see the same whiteboard image and have drawing privileges.
- **User List**: The client interface shows the usernames of other users currently editing the same whiteboard.
- **Connection Management**: Clients can connect and disconnect at any time, with new clients receiving the current state of the whiteboard upon joining.
- **Manager Privileges**: Only the manager can create, open, save, and close the whiteboard.

### Proposed Startup/Operational Model
- **Manager**: The first user to create the whiteboard becomes the manager.
  ```bash
  java CreateWhiteBoard <serverIPAddress> <serverPort> <username>
  ```
- **Clients**: Other users can join by specifying the server’s IP address and port number.
  ```bash
  java JoinWhiteBoard <serverIPAddress> <serverPort> <username>
  ```
  or run jar files such as:
- **Manager**: The first user to create the whiteboard becomes the manager.
  ```bash
  java -jar CreateWhiteBoard.jar <serverIPAddress> <serverPort> <username>
  ```
- **Clients**: Other users can join by specifying the server’s IP address and port number.
  ```bash
  java -jar JoinWhiteBoard.jar <serverIPAddress> <serverPort> <username>
  ```
- **Approval for New Users**: The manager must approve new users before they can join the whiteboard.
- **Peer List**: An online peer list is maintained and displayed.
- **Session Management**: The manager can kick out any user. When the manager quits, the application terminates and all users are notified.

## System Architecture

The system consists of three main components:
1. **Server**: Manages connections and synchronizes actions across clients.
2. **Manager**: The first user to connect, who has additional privileges.
3. **Client**: Users who join the whiteboard session.

The server is multi-threaded, using a thread-per-connection approach. Each client and the manager connect to the server, which broadcasts drawing actions to all connected users for synchronization.

## Communication Protocols

- **TCP/IP Sockets**: Ensures reliable data transfer and maintains the correct order of messages.
- **Message Format**: Uses the `DrawingAction` class to encapsulate drawing actions and chat messages for synchronization.

## Innovations and Improvements

- **Background Colors**: Allows users to set background colors for better visual distinction.
- **Stroke Size**: Different brush sizes for drawing and erasing.
- **Additional Shapes**: Supports exact circles and various other shapes.
- **Real-time Drawing Feedback**: Displays a preview of shapes while dragging the mouse.

## Installation and Setup

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/shared-whiteboard.git
   cd shared-whiteboard
   ```

2. **Compile the Code**:
   ```bash
   javac -d bin src/**/*.java
   ```

3. **Run the Server (Manager)**:
   ```bash
   java -cp bin server.CreateWhiteBoard <serverIPAddress> <serverPort> <username>
   ```

4. **Run the Client**:
   ```bash
   java -cp bin client.WhiteboardClient <serverIPAddress> <serverPort> <username>
   ```

## Usage

1. **Start the server (manager)**:
   ```bash
   java -cp bin server.CreateWhiteBoard 127.0.0.1 5000 manager
   ```

2. **Join as a client**:
   ```bash
   java -cp bin client.WhiteboardClient 127.0.0.1 5000 username
   ```

3. **Drawing and Chatting**:
   - Use the toolbar to select drawing tools, colors, and shapes.
   - Type messages in the chat input area to communicate with other users.

## Contributing

1. **Fork the Repository**: Click the "Fork" button at the top right of this page.
2. **Clone Your Fork**:
   ```bash
   git clone https://github.com/yourusername/shared-whiteboard.git
   ```
3. **Create a Branch**:
   ```bash
   git checkout -b feature/YourFeature
   ```
4. **Commit Your Changes**:
   ```bash
   git commit -m "Add your feature"
   ```
5. **Push to Your Fork**:
   ```bash
   git push origin feature/YourFeature
   ```
6. **Create a Pull Request**: Navigate to your fork on GitHub and click the "New pull request" button.


## Acknowledgements

- Inspired by various online collaborative whiteboard tools.
- Utilizes Java2D for drawing capabilities.

---

Feel free to contact me at [honghaoou@gmail.com] if you have any questions or suggestions. Happy drawing!

---

**Note**: Replace placeholders like `yourusername` and `your-email@example.com` with your actual GitHub username and email address before publishing the README file.
