import java.awt.*;
import java.awt.event.KeyListener;

public interface State extends KeyListener {
    void render(Graphics2D g2d);
}
