package ServerStuff;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ExClient{

    private final String IPAdress = "192.168.3.74";
    private static final int port = 999;

    Socket socket;
    BufferedInputStream is;
    BufferedOutputStream os;
    ClientInfo myInfo = new ClientInfo();
    ObjectMapper mapper = new ObjectMapper();
    public ExClient(){
        try {

            socket = new Socket(IPAdress, port);

            is = new BufferedInputStream(socket.getInputStream());
            os = new BufferedOutputStream(socket.getOutputStream());
          //  new Thread(() -> {
                try {
                    os.write((mapper.writeValueAsString(myInfo)).getBytes());
                    os.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            //}).start();
            System.out.println("Connected");
                receive();
            while (true) {
                Scanner s = new Scanner(System.in);
                String message = s.nextLine();

                // OutputStream newStream = socket.getOutputStream();
                os.write(message.getBytes());
                os.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    public void receive() {
        new Thread(() -> {
            while (true) {
                try {

                    byte[] buffer = new byte[1080];

                    is.read(buffer);
                    boolean isEmpty = true;

                    for (byte b : buffer) {
                        if (b != 0) {
                            isEmpty = false;
                            break;
                        }
                    }
                    if(isEmpty)
                    if (buffer.length == 0) {
                        isEmpty = false;
                    }
                    if(!isEmpty){
                    System.out.println("rec and sent");
                    System.out.println(new String(buffer, "UTF-8"));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    public static void main(String[] args) {
        new ExClient();
    }

}