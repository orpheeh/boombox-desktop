import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private State state;
    private UDPServer server;
    private String username;
    private JFrame frame;

    GamePanel(UDPServer server, String username, JFrame frame){
        this.server = server;
        this.username = username;
        this.frame = frame;
        setPreferredSize(new Dimension(480, 630));
        setMaximumSize(new Dimension(480, 630));
        setMinimumSize(new Dimension(480, 630));
        setBackground(new Color(116, 158, 255));
        state = new FindPlayerState(server, username, this);
        this.frame.addKeyListener(state);

        server.addListener((message) -> {
            String[] messageParts = message.split(":");
            if(messageParts[0].contains("NEW_GAME")){
                this.frame.removeKeyListener(state);
                state = new GameState(messageParts[2].trim(), messageParts[3].trim(),
                        messageParts[1].trim(), username, this, server);
                this.frame.addKeyListener(state);
                this.repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2d = (Graphics2D)graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        state.render(g2d);
    }
}
