import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class UDPServer implements Runnable{
    private int port;
    private volatile boolean running;
    private List<ServerListener> listener = new ArrayList<>();
    private Thread serverThread;

    public UDPServer(int port){
        this.port = port;
    }

    public void startServer(){
        serverThread = new Thread(this);
        serverThread.start();
    }

    public void run(){
        try {
            listen();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void addListener(ServerListener sl){
        synchronized (this) {
            listener.add(sl);
        }
    }

    public void stopServer(){
        running = false;
        try {
            sendMessage("finish", "127.0.0.1", port);
            serverThread.join();
        } catch (InterruptedException | IOException ex){
            ex.printStackTrace();
        }
    }

    private void listen() throws IOException {
        running = true;
        DatagramSocket socketServer = new DatagramSocket(port);
        while(running){
            byte[] data = new byte[1000];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            System.out.println("En attente de packet " + port);
            socketServer.receive(packet);
            byte[] receiveData = packet.getData();
            String message = new String(receiveData);
            System.out.println(message);
            synchronized(this) {
                for (int i = 0; i < listener.size(); i++) {
                    ServerListener sl = listener.get(i);
                    sl.exec(message.trim());
                }
            }
        }
    }

    public void sendMessage(String message, String ip, int port) throws IOException {
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, new InetSocketAddress(ip, port));
        DatagramSocket socket = new DatagramSocket();
        socket.send(packet);
    }
}
