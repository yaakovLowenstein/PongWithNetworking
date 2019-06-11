package edu.mco364;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

class PongPanel extends JPanel {

    int score = 0;
    int highScore;
    String winner;
    Boolean endGame = false;
    Properties pongProps = new Properties();
    ArrayList<Integer> highScoresList = new ArrayList<>(10);
    ArrayList<String> initials = new ArrayList<>(10);
    JButton startButton = new startButton();
    private Point serverPaddle = new Point(585, 200);
    private Point clientPaddle = new Point(5, 10);
    private Point paddleDelta = new Point(5, 5);
    private Point ball = new Point(30, 10);
    private Point ballDelta = new Point(5, 5);
    private Timer timer;
    private int serverScore;
    private int clientScore;
    private Boolean startButtonPushed = false;

    public PongPanel() {
        setBounds(40, 25, 600, 400);
        setSize(600, 400);
        setBackground(Color.black);
        addKeyListener();
        playGame();
    }

    private void endGame() {

        timer.stop();
        if (serverScore == 11 || clientScore == 11) {
            JOptionPane.showMessageDialog(null, "game over");
            setClientScore(0);
            setServerScore(0);
        }

        if (!highScoresList.isEmpty()) {
            if (score > highScoresList.get(0)) {
                highScore = score;
                pongProps.setProperty("High Score", highScore + "");
                try {
                    FileOutputStream out = new FileOutputStream("PongProps.txt");
                    pongProps.store(out, "Pong Properties");
                    out.close();
                } catch (Exception e) {
                }
            }
        }
        setEndGame();


    }

    private void highScoreKeeper(int score, ArrayList<Integer> highScoresList, ArrayList<String> initials) {

        String initialsInput = "";
        if (highScoresList.size() < 10) {
            initialsInput = javax.swing.JOptionPane.showInputDialog("enter initials");
            highScoresList.add(score);
            Collections.sort(highScoresList, Collections.reverseOrder());
        } else if (score > highScoresList.get(9)) {
            initialsInput = javax.swing.JOptionPane.showInputDialog("enter initials");
            highScoresList.set(9, score);

            Collections.sort(highScoresList, Collections.reverseOrder());

        }
        //adding initials to list
        int indexOfScore = highScoresList.indexOf(score);
        initials.add(indexOfScore, initialsInput);

        //serialization
        String fileName = "file";
        try {
            FileOutputStream file = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(highScoresList);
            out.writeObject(initials);
            out.close();
            file.close();

        } catch (IOException ex) {
            System.out.println("Error0");
        }

        String output = "";
        for (int i = 0; i < highScoresList.size(); i++) {
            String temp = " " + highScoresList.get(i);
            output += (i + 1) + ". " + initials.get(i) + temp + "\n";

        }
        JOptionPane.showMessageDialog(null, output);
    }


    private void updateBall() {

        if (ball.y > 365) {
            ballDelta.y = -ballDelta.y;
        }

        if (ball.y < 5) {
            ballDelta.y = -ballDelta.y;

        }

        if (ball.x > serverPaddle.x - 30 && ball.y + 15 > serverPaddle.y && ball.y + 15 < serverPaddle.y + 60) {
            ballDelta.x = -ballDelta.x;
        }
        if (ball.x < clientPaddle.x + 20 && ball.y + 15 > clientPaddle.y && ball.y + 15 < clientPaddle.y + 60) {
            ballDelta.x = -ballDelta.x;

        }
        ball.x += ballDelta.x;
        ball.y += ballDelta.y;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.RED);
        g.fillRect(serverPaddle.x, serverPaddle.y, 10, 60);
        g.fillRect(clientPaddle.x, clientPaddle.y, 10, 60);
        g.setColor(Color.GREEN);
        g.fillOval(ball.x, ball.y, 30, 30);
        g.setColor(Color.white);
        g.drawString("Server Score: " + serverScore, 500, 380);
        g.drawString("Client Score: " + clientScore, 10, 380);
    }


    /*
        private void userPaddleFixedMovement() {
            if (serverPaddle.y == 0) {
                paddleDelta.y = -paddleDelta.y;
            }
            if (serverPaddle.y > 335) {
                paddleDelta.y = -paddleDelta.y;
            }
            serverPaddle.y += paddleDelta.y;


        }
    */
    private void loadHighestScore() {
        try {
            FileInputStream input = new FileInputStream("PongProps.txt");
            pongProps.load(input);
            highScore = Integer.parseInt(pongProps.getProperty("High Score"));
            input.close();
        } catch (Exception ex) {
        }
    }

    private void loadTopTenScores() {

        try {
            FileInputStream file = new FileInputStream("file");
            ObjectInputStream in = new ObjectInputStream(file);
            highScoresList = (ArrayList) in.readObject();
            initials = (ArrayList) in.readObject();
            in.close();
            file.close();

        } catch (IOException ex) {

        } catch (ClassNotFoundException ex) {
        }
        String output = "";
        if (!highScoresList.isEmpty()) {
            for (int i = 0; i < highScoresList.size(); i++) {
                String temp = " " + highScoresList.get(i);
                highScoresList.get(i);
                initials.add(initials.get(i));
                output += (i + 1) + ". " + initials.get(i) + temp + "\n";

            }

            JOptionPane.showMessageDialog(null, output);
        }
    }


    private void addKeyListener() {
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (serverPaddle.y <= 325 && serverPaddle.y > 0) {
                        serverPaddle.y -= 10;
                        repaint();
                    } else {
                        serverPaddle.y += 10;
                        repaint();
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (serverPaddle.y <= 325 && serverPaddle.y > 0) {
                        serverPaddle.y += 10;
                        repaint();
                    } else {
                        serverPaddle.y -= 10;
                        repaint();
                    }
                }

            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    private void playGame() {
        timer = new Timer(25, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBall();
                startButton.setVisible(false);
                //  userPaddleFixedMovement();
                repaint();

                if (ball.x + 20 > serverPaddle.x) {
                    clientScore++;
                    endGame();
                    setWinner(clientPaddle);
                }
                if (ball.x + 20 < clientPaddle.x) {
                    serverScore++;

                    endGame();
                    setWinner(serverPaddle);
                }
            }
        });
    }

    public Point getServerPaddle() {
        return serverPaddle;
    }

    public void setServerPaddle(int serverPaddle) {
        this.serverPaddle.y = serverPaddle;
        repaint();
    }

    public Point getClientPaddle() {
        return clientPaddle;
    }

    public void setClientPaddle(int clientPaddle) {
        this.clientPaddle.y = clientPaddle;
        repaint();
    }

    public Point getBall() {
        return ball;
    }

    public void setBall(Point ball) {
        this.ball = ball;
        repaint();
    }

    public void clickStartButton() {
        startButton.doClick();
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(Point paddle) {
        if (paddle.equals(serverPaddle)) {
            winner = "serverPaddle";
        } else winner = "clientPaddle";
    }

    public void setEndGame() {
        endGame = true;
    }

    public Boolean getEndGame() {
        return endGame;
    }

    public void hs() {

        highScoreKeeper(score, highScoresList, initials);
    }

    public int getServerScore() {
        return serverScore;
    }

    public void setServerScore(int serverScore) {
        this.serverScore = serverScore;
    }

    public int getClientScore() {
        return clientScore;
    }

    public void setClientScore(int clientScore) {
        this.clientScore = clientScore;
    }

    public Boolean getStartButtonPushed() {
        return startButtonPushed;
    }

    public void setStartButtonPushed(Boolean startButtonPushed) {
        this.startButtonPushed = startButtonPushed;
    }
public Timer getTimer (){
        return timer;
}
    private class startButton extends JButton {

        public startButton() {
            setText("start");
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    score = 0;
                    ball = new Point(30, 10);
                    timer.start();
                    setVisible(false);


                }

            });
        }
    }
}







