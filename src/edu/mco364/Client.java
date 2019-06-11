package edu.mco364;

// Fig. 27.7: Client.java
// Client portion of a stream-socket connection between client and server.

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends JFrame {
    PongPanel panel;
    private JTextField enterField; // enters information from user
    private JPanel displayArea; // display information to user
    private static ObjectOutputStream output; // output stream to server
    private ObjectInputStream input; // input stream from server
    private String message = ""; // message from server
    private String chatServer; // host server for this application
    private Socket client; // socket to communicate with server
    private Color currentColor;

    // initialize chatServer and set up GUI
    public Client(String host) {
        super("Client");

        chatServer = host; // set server to which this client connects
        panel = new PongPanel();
        panel.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notch = e.getWheelRotation() * 10;
                if (panel.getClientPaddle().y <= 325 && panel.getClientPaddle().y > 0) {
                    panel.getClientPaddle().y += notch;
                    sendData(panel.getClientPaddle().y);
                    repaint();
                } else if (panel.getClientPaddle().y >= 325) {
                    panel.getClientPaddle().y = 325;
                    sendData(panel.getClientPaddle().y);
                    repaint();
                } else if (panel.getClientPaddle().y <= 0) {
                    panel.getClientPaddle().y = 5;
                    sendData(panel.getClientPaddle().y);
                    repaint();
                }
            }
        });

        panel.getTimer().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(panel.getBall().x < 300 ){
                    Client.sendData(panel.getBall());
                }
            }

        });

        setTitle("pong");
        setSize(700, 500);
        setVisible(true);
        setLayout(null);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        add(panel);
        repaint();
        revalidate();
    } // end Client constructor

    // connect to server and process messages from server
    public void runClient() {
        try // connect to server, get streams, process connection
        {
            connectToServer(); // create a Socket to make connection
            getStreams(); // get the input and output streams
            processConnection(); // process connection
        } // end try
        catch (EOFException eofException) {
            displayMessage("\nClient terminated connection");
        } // end catch
        catch (IOException ioException) {
            ioException.printStackTrace();
        } // end catch
        finally {
            closeConnection(); // close connection
        } // end finally
    } // end method runClient

    // connect to server
    private void connectToServer() throws IOException {
        displayMessage("Attempting connection\n");

        // create Socket to make connection to server
        client = new Socket(InetAddress.getByName(chatServer), 12345);

        // display connection information
        displayMessage("Connected to: " +
                client.getInetAddress().getHostName());
    } // end method connectToServer

    // get streams to send and receive data
    private void getStreams() throws IOException {
        // set up output stream for objects
        output = new ObjectOutputStream(client.getOutputStream());
        output.flush(); // flush output buffer to send header information

        // set up input stream for objects
        input = new ObjectInputStream(client.getInputStream());

        displayMessage("\nGot I/O streams\n");
    } // end method getStreams

    // process connection with server
    private void processConnection() throws IOException {
        // enable enterField so client user can send messages
        setTextFieldEditable(true);

        do // process messages sent from server
        {
            try // read message and display it
            {
                Object o = input.readObject();
                if (o instanceof String) {
                    String p = (String) o;
                    panel.clickStartButton();
                    repaint();

                }
                if (o instanceof Integer) {
                    int p = (Integer) o; // read new message
                    panel.setServerPaddle(p);
                    repaint();
                } else if (o instanceof Point) {
                    Point p = (Point) o;
                    panel.setBall(p);
                    repaint();
                }


            } // end try
            catch (ClassNotFoundException classNotFoundException) {
                displayMessage("\nUnknown object type received");
            } // end catch

        } while (!message.equals("SERVER>>> TERMINATE"));
    } // end method processConnection

    // close streams and socket
    private void closeConnection() {
        displayMessage("\nClosing connection");
        setTextFieldEditable(false); // disable enterField

        try {
            output.close(); // close output stream
            input.close(); // close input stream
            client.close(); // close socket
        } // end try
        catch (IOException ioException) {
            ioException.printStackTrace();
        } // end catch
    } // end method closeConnection

    // send message to server
    private static void sendData(Object o) {
        try // send object to server
        {
            output.writeObject(o);
            output.flush(); // flush data to output

        } // end try
        catch (IOException ioException) {

        } // end catch
    } // end method sendData

    // manipulates displayArea in the event-dispatch thread
    private void displayMessage(final String messageToDisplay) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() // updates displayArea
                    {

                    } // end method run
                }  // end anonymous inner class
        ); // end call to SwingUtilities.invokeLater
    } // end method displayMessage

    // manipulates enterField in the event-dispatch thread
    private void setTextFieldEditable(final boolean editable) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() // sets enterField's editability
                    {

                    } // end method run
                } // end anonymous inner class
        ); // end call to SwingUtilities.invokeLater
    } // end method setTextFieldEditable
} // end class Client