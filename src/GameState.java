import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class GameState implements State {
    private  String[] player = new String[2];
    private int[] scores = new int[2];
    private  String id;
    private String myUsername;

    private  JPanel panel;
    private  UDPServer server;

    private int currentPlayerIndex = 0;
    private StringBuffer sequence = new StringBuffer();
    private String lastSequence = "";

    private char keyPressed = ' ';

    private int playIndex = -1;

    public GameState(String player1, String player2, String id, String myUsername, JPanel panel, UDPServer server){
        this.player[0] = player1;
        this.player[1] = player2;
        this.id = id;
        this.myUsername = myUsername;
        this.panel = panel;
        this.server = server;

        synchronized (server) {
            server.addListener((message) -> {
                String[] messageParts = message.split(":");
                if (messageParts[0].contains("PLAY")) {

                    currentPlayerIndex = 1 - currentPlayerIndex;
                    int myScore = Integer.parseInt(messageParts[1].trim());
                    int otherScore = Integer.parseInt(messageParts[2].trim());
                    if(messageParts.length > 3) {
                        lastSequence = messageParts[3];
                        Timer timer = new Timer();
                        playIndex = 0;
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if(playIndex < lastSequence.length()) {
                                    keyPressed = lastSequence.charAt(playIndex);
                                }
                                System.out.println(keyPressed);
                                panel.repaint();
                                playIndex++;
                                if(playIndex > lastSequence.length()){
                                    playIndex = -1;
                                    keyPressed = ' ';
                                    panel.repaint();
                                    timer.cancel();
                                }
                            }
                        }, 1000L, 1000L);
                    } else {
                        lastSequence = "";
                    }
                    sequence.delete(0, sequence.length());
                    setMyScore(myScore);
                    setOtherScore(otherScore);
                    panel.repaint();
                }
            });
        }
    }

    public void setMyScore(int s){
        if(player[currentPlayerIndex].equals(myUsername)){
            scores[currentPlayerIndex] = s;
        } else {
            scores[1-currentPlayerIndex] = s;
        }
    }

    public void setOtherScore(int s){
        if(player[currentPlayerIndex].equals(myUsername)){
            scores[1-currentPlayerIndex] = s;
        } else {
            scores[currentPlayerIndex] = s;
        }
    }

    public int getMyScore(){
        if(player[currentPlayerIndex].equals(myUsername)){
            return scores[currentPlayerIndex];
        } else {
            return scores[1-currentPlayerIndex];
        }
    }

    public int getOtherScore(){
        if(player[currentPlayerIndex].equals(myUsername)){
            return scores[1-currentPlayerIndex];
        } else {
            return scores[currentPlayerIndex];
        }
    }

    @Override
    public void render(Graphics2D g2d) {
        g2d.setFont(new Font("Verdana", Font.BOLD, 16));
        g2d.setColor(new Color(255, 255, 255));
        g2d.drawString(myUsername + ": " + getMyScore(), 20, 50);

        g2d.setColor(new Color(255, 255, 255));
        g2d.drawString("Adversaire" + ": " + getOtherScore(), 20, 75);

        if(player[currentPlayerIndex].trim().equals(myUsername) && playIndex < 0){
            g2d.setFont(new Font("Verdana", Font.PLAIN, 32));
            g2d.setColor(new Color(246, 255, 154));
            g2d.drawString("A toi", 100, 200);
        }

        int square = 80;
        int positionx = (int)(panel.getSize().getWidth() - square*3)/2;
        int positiony = (int)(panel.getSize().getHeight() - square*2)/2;

        FontMetrics fm = g2d.getFontMetrics();

        g2d.setColor(Color.CYAN);
        if(keyPressed == 'O'){
            g2d.setColor(new Color(241, 255, 60));
        }
        g2d.fillRect(positionx + square, positiony, square, square);
        g2d.setColor(Color.white);
        g2d.drawString("O", positionx + square + (square - fm.stringWidth("O"))/2, positiony + (square - fm.getHeight())/2 + fm.getAscent());

        g2d.setColor(Color.GREEN);
        if(keyPressed == 'K'){
            g2d.setColor(new Color(241, 255, 60));
        }
        g2d.fillRect(positionx, positiony + square, square, square);
        g2d.setColor(Color.white);
        g2d.drawString("K", positionx + (square - fm.stringWidth("K"))/2, positiony + square + (square - fm.getHeight() )/2 + fm.getAscent());

        g2d.setColor(Color.BLUE);
        if(keyPressed == 'L'){
            g2d.setColor(new Color(241, 255, 60));
        }
        g2d.fillRect(positionx + square, positiony + square, square, square);
        g2d.setColor(Color.white);
        g2d.drawString("L", positionx + square + (square - fm.stringWidth("L"))/2, positiony + square + (square - fm.getHeight() )/2 + fm.getAscent());

        g2d.setColor(Color.MAGENTA);
        if(keyPressed == 'M'){
            g2d.setColor(new Color(241, 255, 60));
        }
        g2d.fillRect(positionx + square * 2, positiony + square, square, square);
        g2d.setColor(Color.white);
        g2d.drawString("M", positionx + square*2 + (square - fm.stringWidth("M"))/2, positiony + square + (square - fm.getHeight() )/2 + fm.getAscent());
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if(player[currentPlayerIndex].equals(myUsername) && playIndex < 0) {
            keyPressed = ("" + keyEvent.getKeyChar()).toUpperCase().charAt(0);
        }
        panel.repaint();
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        if(player[currentPlayerIndex].equals(myUsername) && playIndex < 0) {
            sequence.append(keyPressed);
            keyPressed = ' ';
            panel.repaint();
            if (sequence.toString().length() == lastSequence.length() + 1) {
                panel.repaint();
                try {
                    server.sendMessage("PLAY:" + sequence.toString() + ":" + id, BoomBox.IP, BoomBox.PORT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
