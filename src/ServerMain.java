
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

// copy into cmd for a connection
//cd Downloads
//nc64 64.57.58.125 25565
public class ServerMain {

    Thread ClientThread;
    Thread conThread;
    private int socketCon = 0;
    //private int sendIPCon = 0;

    ArrayList<Socket> clients = new ArrayList<>();

    private int connections = 0;
   // private String hostName;
    ServerSocket serverSocket;
   // private static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws InterruptedException {

        new ServerMain();

        // serv.startListening();
    }

    public void startListening() throws InterruptedException {

        conThread = new Thread(() -> {
            if (socketCon == 0) {
                System.out.println("looking for connection");
                try {
                    clients.add(serverSocket.accept());
                    System.out.println("connected!!");
                    connections++;
                    con = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        conThread.start();
        while (connections == 0) {
            Thread.sleep(100);
        }

    }

    public void readFromPi() {

        Thread t = new Thread(()->{
                while(true){
                try(ObjectInputStream os = new ObjectInputStream(clients.get(0).getInputStream())){
                    os.readUTF();
                    os.close();
                    Thread.sleep(6000);
                
                }catch(Exception e){
                    System.out.println("exception");
                }

            }

            
        });
        t.start();

    }
    int picon = 0;
    public ServerMain() {

        int port = 25565; // Choose a port number
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                System.out.println("Server is listening on port " + port);
                if (connections == 0) {
                    
                    try {
                        startListening();
                    } catch (InterruptedException e) {
                        System.out.println("start listening is happening too many times");
                        e.printStackTrace();
                    }
                }

                while (connections != 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {

                        if (connections != 0) {

                            if(picon == 0){
                                picon =1;
                               // readFromPi();
                            }
                            try {

                                if (con == 0) {
                                   System.out.println("connection recieved");
                                    startListening();
                                    con = 1;
                                    ServerSend.sendData(new INPUTS(), clients.get(connections-1));

                                }


                            } catch (Exception we) {
                                clients.get(0).close();
                                connections = 0;
                                System.out.println("socket is closed!!!!!!!");

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        connections = 0;
                        System.out.println("asfd");
                        try {
                            serverSocket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    int con = 0;

}
