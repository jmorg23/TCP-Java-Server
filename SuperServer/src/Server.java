import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

// copy into cmd for a connection
//cd Downloads
//nc64 64.57.58.125 25565
public class Server {

    Thread ClientThread;
    private int socketCon = 0;
    // private int sendIPCon = 0;

    ArrayList<Socket> clients = new ArrayList<>();
    static HashMap<String, String> games = new HashMap<>();
    static ArrayList<String> que = new ArrayList<>();
    static ArrayList<Socket> SocketQue = new ArrayList<>();

    private int connections = 0;
    ServerSocket serverSocket;

    private static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws InterruptedException {

  
            new Server();
        loop();

    }

    public void startListening() throws InterruptedException {

        new Thread(() -> {
            try {

                clients.set(1, serverSocket.accept());
                clients.add(serverSocket.accept());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
        while (connections == 0) {
            Thread.sleep(100);
        }

    }

    static boolean currentlyAccepting = false;
    static boolean newConnection = false;
    private static final int port = 25565;
    private static boolean scan = true;
    public static void loop(){
      
   
            try(ServerSocket serverSocket = new ServerSocket(port)){



        while (true) {
            try {
                if (!currentlyAccepting) {

                    new Thread(() -> {
                        try {

                            currentlyAccepting = true;
                            SocketQue.add(serverSocket.accept());
                            currentlyAccepting = false;
                            try (ObjectInputStream is = new ObjectInputStream(
                                    SocketQue.get(SocketQue.size() - 1).getInputStream())) {
                                        String usrname = is.readUTF();
                                        que.add(usrname);
                                        games.put(usrname, is.readUTF());
                               System.out.println(usrname);
                                is.close();
                            } catch (Exception e) {
                                SocketQue.get(SocketQue.size() - 1).close();
                            }

                        } catch (IOException e) {

                        }
                    }).start();

                }
                if(scan)
                new Thread(()->{
                    scan = false;
                Scanner s = new Scanner(System.in);
                String aHost = s.nextLine();
                //.substring(2, aHost.length())
                for (int i = 0; i < que.size(); i++){
                    System.out.println("try: "+ i);
                    if (aHost.substring(2, aHost.length()).equals(que.get(i))) {
                        System.out.println("success password = "+ games.get(que.get(i)));
     
                    }
                }
                scan = true;
                }).start();

                Thread.sleep(10);
            } catch (Exception e) {

            }

        }
                    }catch(Exception e){

            }

    }

    public Server() {

    }

}
