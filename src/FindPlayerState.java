import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FindPlayerState implements State {

    private List<String> playersName = new ArrayList<>();
    private UDPServer server;
    private JPanel panel;
    private int selectedIndex = 0;
    private String myUsername;

    public FindPlayerState(UDPServer server, String username, JPanel panel){
        this.panel = panel;
        this.server = server;
        myUsername = username;
        server.addListener((message) -> {
            String[] messageParts = message.split(":");
            if(messageParts[0].contains("FIND_PLAYER")){
                for(int i = messageParts.length-1; i > 0; i--){
                    playersName.add(messageParts[i]);
                }
                panel.repaint();
            }
        });
        getAllPlayer(username);
    }

    public void getAllPlayer(String username){
        try {
            server.sendMessage("FIND_PLAYER:" +username, BoomBox.IP, BoomBox.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(Graphics2D g2d) {
        g2d.setColor(Color.white);
        Font bittermilk = new Font("Verdana", Font.BOLD, 24);
        Font neon =  new Font("Verdana", Font.BOLD, 24);

        g2d.setFont(bittermilk.deriveFont(32f));
        g2d.setColor(Color.white);
        g2d.drawString("Bienvenue", ((int)panel.getSize().getWidth() - g2d.getFontMetrics().stringWidth("Bienvenue")) / 2, 10 + g2d.getFontMetrics().getAscent());
        g2d.setFont(bittermilk.deriveFont(24f));
        g2d.drawString(myUsername, ((int)panel.getSize().getWidth() - g2d.getFontMetrics().stringWidth(myUsername)) / 2, 30 + g2d.getFontMetrics().getHeight() + g2d.getFontMetrics().getAscent());
        g2d.drawString("****", ((int)panel.getSize().getWidth() - g2d.getFontMetrics().stringWidth("***")) / 2, 30 + g2d.getFontMetrics().getHeight()*2 + g2d.getFontMetrics().getAscent());

        int square = 300;
        int x = (int)(panel.getSize().getWidth() - square) / 2;
        int y = (int)(panel.getSize().getHeight() - square) / 2;

        g2d.setFont(bittermilk.deriveFont(24f));
        g2d.setColor(Color.black);
        g2d.drawString("Choisi un adversaire", x + (square - g2d.getFontMetrics().stringWidth("Choisir un adversaire")) / 2, y + g2d.getFontMetrics().getAscent());
        g2d.setFont(new Font("Verdana", Font.BOLD, 16));
        for(int i = 0; i < playersName.size(); i++){
            if(i >= 11){
                break;
            }
            if(i == selectedIndex){
                g2d.setColor(new Color(249, 255, 139));
            } else {
                g2d.setColor(Color.black);
            }
            String str = playersName.get(i);
//          g2d.drawString(str, ((int)panel.getSize().getWidth() - g2d.getFontMetrics().stringWidth(str))/2, y + 30 + 30 * (i+1) + g2d.getFontMetrics().getAscent());
            g2d.drawString(str, 50, y + 30 + 40 * (i+1) + g2d.getFontMetrics().getAscent());
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER){
            //Start new game
            try {
                server.sendMessage("NEW_GAME:" + myUsername + ":" + playersName.get(selectedIndex),
                        BoomBox.IP, BoomBox.PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(keyEvent.getKeyCode() == KeyEvent.VK_UP){
            //Move top
            selectedIndex--;
            if(selectedIndex < 0){
                selectedIndex = 0;
            }
        } else if(keyEvent.getKeyCode() == KeyEvent.VK_DOWN){
            //Move bottom
            selectedIndex++;
            if(selectedIndex >= playersName.size()){
                selectedIndex = playersName.size()-1;
            }
        }
        panel.repaint();
    }
}
