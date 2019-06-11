package edu.mco364;

// Fig. 27.5: Server.java
// Server portion of a client/server stream-socket connection. 
import java.awt.*;
import java.awt.event.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;


enum Planet {
    MERCURY (2.4E17, "Jublop"), VENUS(5,""), EARTH(7,""), MARS(42,"");

    Planet(double mass, String watusiName) {
        this.mass = mass;
        this.watusiName = watusiName;
    }
    private double mass;
    private String watusiName;

    public String toString(){
        return String.format("%s %d", watusiName, mass);
    }
}

public class Server extends JFrame
{
    private JTextField enterField; // inputs message from user
    private JPanel displayArea; // display information to user
    private static ObjectOutputStream output; // output stream to client
    private ObjectInputStream input; // input stream from client
    private ServerSocket server; // server socket
    private Socket connection; // connection to client
    private int counter = 1; // counter of number of connections
    private Color currentColor = Color.BLACK;
    PongPanel panel;
    private JButton startButton;
    // set up GUI
    public Server() {
        super("Server");
        panel = new PongPanel();

        // sendData( panel.getBall());
        panel.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notch = e.getWheelRotation() * 10;
                if (panel.getServerPaddle().y <= 325 && panel.getServerPaddle().y > 0) {
                    panel.getServerPaddle().y += notch;
                    sendData(panel.getServerPaddle().y);
                    repaint();
                } else if (panel.getServerPaddle().y >= 325) {
                    panel.getServerPaddle().y = 325;
                    sendData(panel.getServerPaddle().y);
                    repaint();
                } else if (panel.getServerPaddle().y <= 0) {
                    panel.getServerPaddle().y = 5;
                    sendData(panel.getServerPaddle().y);
                    repaint();
                }


            }
        });


        startButton = new JButton();
        startButton.setText("Start Button");
        panel.getTimer().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(panel.getBall().x > 300 ){
                    Server.sendData(panel.getBall());
            }
        }

        });


        setTitle("pong");
        setSize(700, 500);
        setVisible(true);
        setLayout(null);
        panel.setServerScore(0);
        panel.setClientScore(0);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        panel.add(startButton);
        add(panel);
        startButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.setStartButtonPushed(true);
                panel.clickStartButton();
                startGame();

            }
        });


        repaint();
        revalidate();
    } // end Server constructor

    // set up and run server
    public void runServer()
    {
        try // set up server to receive connections; process connections
        {
            server = new ServerSocket( 12345, 100 ); // create ServerSocket

            while ( true )
            {
                try
                {
                    waitForConnection(); // wait for a connection
                    getStreams(); // get input & output streams
                    processConnection(); // process connection
                } // end try
                catch ( EOFException eofException )
                {
                    displayMessage( "\nServer terminated connection" );
                } // end catch
                finally
                {
                    closeConnection(); //  close connection
                    ++counter;
                } // end finally
            } // end while
        } // end try
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        } // end catch
    } // end method runServer

    // wait for connection to arrive, then display connection info
    private void waitForConnection() throws IOException
    {
        displayMessage( "Waiting for connection\n" );
        connection = server.accept(); // allow server to accept connection
        displayMessage( "Connection " + counter + " received from: " +
                connection.getInetAddress().getHostName() );
    } // end method waitForConnection

    // get streams to send and receive data
    private void getStreams() throws IOException
    {
        // set up output stream for objects
        output = new ObjectOutputStream( connection.getOutputStream() );
        output.flush(); // flush output buffer to send header information

        // set up input stream for objects
        input = new ObjectInputStream( connection.getInputStream() );

        displayMessage( "\nGot I/O streams\n" );
    } // end method getStreams

    // process connection with client
    private void processConnection() throws IOException
    {
        // enable enterField so server user can send messages
        setTextFieldEditable( true );

        do // process messages sent from client
        {
            try // read message and display it
            {
                Object o = input.readObject();
                if (o instanceof Integer) {
                    int p = (Integer) o; // read new message
                    panel.setClientPaddle(p);
                    repaint();
                }
                else if (o instanceof Point) {
                    Point p = (Point) o;
                    panel.setBall(p);
                    repaint();
                }
            } // end try
            catch ( ClassNotFoundException classNotFoundException )
            {
                displayMessage( "\nUnknown object type received" );
            } // end catch

        } while ( true );
    } // end method processConnection

    // close streams and socket
    private void closeConnection()
    {
        displayMessage( "\nTerminating connection\n" );
        setTextFieldEditable( false ); // disable enterField

        try
        {
            output.close(); // close output stream
            input.close(); // close input stream
            connection.close(); // close socket
        } // end try
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        } // end catch
    } // end method closeConnection

    // send message to client
    private static void sendData(Object o)
    {
        try // send object to client
        {
            output.writeObject( o );
            output.flush(); // flush output to client
        } // end try
        catch ( IOException ioException )
        {
        } // end catch
    } // end method sendData

    private void startGame(){
        try{
            output.writeObject( "Start");
            output.flush();
        }
        catch( IOException ioexcept){

        }


    }

    // manipulates displayArea in the event-dispatch thread
    private void displayMessage( final String messageToDisplay )
    {
        SwingUtilities.invokeLater(
                new Runnable()
                {
                    public void run() // updates displayArea
                    {

                    } // end method run
                } // end anonymous inner class
        ); // end call to SwingUtilities.invokeLater
    } // end method displayMessage

    // manipulates enterField in the event-dispatch thread
    private void setTextFieldEditable( final boolean editable )
    {
        SwingUtilities.invokeLater(
                new Runnable()
                {
                    public void run() // sets enterField's editability
                    {
                        enterField.setEditable( editable );
                    } // end method run
                }  // end inner class
        ); // end call to SwingUtilities.invokeLater
    } // end method setTextFieldEditable
} // end class Server