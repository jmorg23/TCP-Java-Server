import java.io.*;
import java.net.*;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

// copy into cmd for a connection
//cd Downloads
//nc64 64.57.58.125 25565
public class WeatherServer {

    Thread ClientThread;
    private int socketCon = 0;
    // private int sendIPCon = 0;

    ArrayList<Socket> clients = new ArrayList<>();

    private int connections = 0;
    // private String hostName;
    ServerSocket serverSocket;
    INPUTS inputs = new INPUTS();
    private static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws InterruptedException {

        new WeatherServer();

        // serv.startListening();
    }

    public void startListening() throws InterruptedException {
        newCon = 1;

        new Thread(() -> {
            // if (socketCon == 0) {
            System.out.println("looking for connection");
            try {
                if(connections == 3){
                clients.set(1, serverSocket.accept());
            connections = 1;    
            }else
                clients.add(serverSocket.accept());
                System.out.println("connected!!");
                connections++;
                con = 0;
                newCon = 0;
            } catch (IOException e) {
                e.printStackTrace();
            }

            // }
        }).start();
        while (connections == 0) {
            Thread.sleep(100);
        }

    }

    public void readFromPi() {
        System.out.println(connections);
        System.out.println(inputsNeeded);
        if ((connections == 2) && (inputsNeeded)) {
            System.out.println("called");
            try (ObjectInputStream is = new ObjectInputStream(clients.get(0).getInputStream())) {
                inputs = mapper.readValue(is.readUTF(), INPUTS.class);

                System.out.println("temp from pi = " + inputs.getTemp());
                System.out.println("humid from pi = " + inputs.getHumid());

                is.close();
                // Thread.sleep(6000);
                //inputsNeeded = false;
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("not true");
                // System.exit(0);

            }
        }

    }

    private boolean inputsNeeded = true;
    int picon = 0;
    int newCon = 0;



    public WeatherServer() {

        int port = 25566; // Choose a port number
        try {
            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
//System.out.println("connections:" +connections);
            try {

            
               
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    

                if ((newCon == 0)||(connections == 0)) {
                    System.out.println("Server is listening on port " + port);
                        if (connections == 0) {
                         System.out.println("Clients Cleared");
                            clients.clear();
                        }
                    try {
                        startListening();
                    } catch (InterruptedException e) {
                        System.out.println("start listening is happening too many times");
                        e.printStackTrace();
                    }
                }
                    try {

                        if (connections == 2) {

                            try {

                                if (con == 0) {
                                    System.out.println("connection recieved");
                                    con = 1;
                                    readFromPi();
                                    System.out.println("pi read");
                                    ServerSend.sendData(inputs, clients.get(1));
                                    System.out.println("info sent");
                                    connections = 0;
                                    newCon = 0;
                                    clients.get(1).close();
                                    clients.get(0).close();

                                    clients.clear();
                                }

                            } catch (Exception we) {
                                //clients.get(0).close();
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

                

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        
    }

    int con = 0;

}
