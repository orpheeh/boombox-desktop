import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class BoomBox {

    public static String IP = "127.0.0.1";
    public static int PORT = 43215;

    static void createAndShowGUI(UDPServer server, String username){
        JFrame frame = new JFrame("Boom Box");
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                super.windowClosing(windowEvent);
                server.stopServer();
                System.exit(0);
            }
        });

        frame.add(new GamePanel(server, username, frame));
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args){
        //Charger le port de le fichier de config
        Config config = new Config();
        try {
            config.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int port = config.getPort();
        String username = config.getUsername();

        //Charger les sockets
        UDPServer server = new UDPServer(port);
        server.startServer();

        //Connexion au serveur
        try {
            server.sendMessage("CONNEXION:" + username + ":" + port, "127.0.0.1", 43215);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Lancer le programme
        createAndShowGUI(server, username);
    }
}
