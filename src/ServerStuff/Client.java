package ServerStuff;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import java.net.Socket;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Client {

    private String Username;
    private String Password;
    private boolean host;
    private Socket socket;

    BufferedInputStream is;
    BufferedOutputStream os;
    ClientInfo myInfo;
    ObjectMapper mapper = new ObjectMapper();


    public Client(Socket s) {
        socket = s;
        System.out.println("connected");
//new Thread(()->{

        try {
            is = new BufferedInputStream(socket.getInputStream());
            byte[] buffer = new byte[1080];
            //is.readAllBytes();
            is.read(buffer);
        
            myInfo = mapper.readValue(new String(buffer, "UTF-8"), ClientInfo.class);
            Username = myInfo.getUsername();
            host = myInfo.getIsHost();
            Password = myInfo.getPassword();
            //System.out.println("username = " + myInfo.getUsername());
            //System.out.println("passowrd = " + myInfo.getPassword());
        } catch (IOException e) {
            e.printStackTrace();
        }
    //}).start();
    }
    public String getUsername(){
        return Username;
    }
    public boolean isHost(){
        return host;
    }
    public String getPassword(){
        return Password;
    }

    public void getData() {

    }
    public Socket getSocket(){
        return socket;
    }


}
