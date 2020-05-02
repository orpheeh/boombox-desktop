import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Config {

    final String[] words = {
            "boil", "bombsite", "bombing", "bonn", "flay", "fleck", "flee", "flank", "lay", "lattice", "later",
            "move", "mpg", "mover", "muddy", "mug", "serve", "sesame", "set", "spy", "hearten", "heartburn", "career"
    };

    final Random random = new Random();

    private int port;
    private String username;

    public void load() throws IOException {
        //loadFromFile();
        loadFromMemory();
    }

    public void loadFromMemory(){
        int index1 = random.nextInt(words.length);
        int index2 = random.nextInt(words.length);
        int index3 = random.nextInt(words.length);

        username = words[index1] + words[index2] + words[index3];
        port = 3000 + random.nextInt(999);
    }

    private void loadFromFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("config.txt"));
        String line =  br.readLine();
        port = Integer.parseInt(line);
        username = br.readLine();
    }

    public int getPort(){
        return port;
    }

    public String getUsername(){
        return username;
    }
}
