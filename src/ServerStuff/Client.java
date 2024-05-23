package ServerStuff;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.Socket;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Client {

    private String Username;
    private String Password;
    private boolean host;
    private Socket socket;

    private BufferedInputStream is;
    private BufferedOutputStream os;
    private ClientInfo myInfo;
    private ObjectMapper mapper = new ObjectMapper();

    public Client(Socket s) {
        socket = s;
        System.out.println("Connected");
        try {
            is = new BufferedInputStream(socket.getInputStream());
            os = new BufferedOutputStream(socket.getOutputStream());
            byte[] buffer = new byte[1080];
            is.read(buffer);
            String str = new String(buffer, "UTF-8");
            System.out.println(str);
            str = removeSpecialChars(str);
            myInfo = mapper.readValue(str, ClientInfo.class);
            Username = myInfo.getUsername();
            host = myInfo.getIsHost();
            Password = myInfo.getPassword();
            //Ping myping = new Ping(this);
            //myping.start();

        } catch (IOException e) {
            e.printStackTrace();
            try {
                is.close();
                socket.close();
                System.out.println("HACKER ALERT");

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public BufferedInputStream getIs(){
        return is;
    }
    public BufferedOutputStream getOs(){
        return os;
    }

    public static String removeSpecialChars(String str) {
        // Define a regular expression pattern to match special characters
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\s{}\"\\[\\]:;,]+");
        Matcher matcher = pattern.matcher(str);
        
        // Replace all occurrences of special characters with an empty string
        return matcher.replaceAll("");
    }
    
    public String getUsername() {
        return Username;
    }

    public boolean isHost() {
        return host;
    }

    public String getPassword() {
        return Password;
    }

    public Socket getSocket() {
        return socket;
    }

}
