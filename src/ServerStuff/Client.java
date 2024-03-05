package ServerStuff;

import java.io.BufferedInputStream;
import java.io.IOException;

import java.net.Socket;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Client {

    private String Username;
    private String Password;
    private boolean host;
    private Socket socket;

    private BufferedInputStream is;
    private ClientInfo myInfo;
    private ObjectMapper mapper = new ObjectMapper();

    public Client(Socket s) {
        socket = s;
        System.out.println("Connected");
        try {
            is = new BufferedInputStream(socket.getInputStream());
            byte[] buffer = new byte[1080];
            is.read(buffer);

            myInfo = mapper.readValue(new String(buffer, "UTF-8"), ClientInfo.class);
            Username = myInfo.getUsername();
            host = myInfo.getIsHost();
            Password = myInfo.getPassword();

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
