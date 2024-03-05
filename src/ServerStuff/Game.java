package ServerStuff;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import java.util.ArrayList;

public class Game {

    private String password = "";
    private ArrayList<Client> clients = new ArrayList<>();
    private ArrayList<BufferedInputStream> inStreams = new ArrayList<>();
    private ArrayList<BufferedOutputStream> outStreams = new ArrayList<>();

    public void check(){
        new Thread(()->{
            while(true)
            if(clients.size()==0){
                ServerMain.removeGame(password);
                System.out.println("No one in game with password: "+password+"\n Now removed");
                break;

            } else
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        });
    }

    public Game(Client hostClient) {
        password = hostClient.getPassword();
        clients.add(hostClient);
        try {
            inStreams.add(new BufferedInputStream(hostClient.getSocket().getInputStream()));
            outStreams.add(new BufferedOutputStream(hostClient.getSocket().getOutputStream()));
            receive(clients.size() - 1);

        } catch (IOException e) {
            System.out.println("adding error");
            e.printStackTrace();
        }

    }

    public void send(int index, byte[] buffer) {
        new Thread(() -> {
            for (int i = 0; i < clients.size(); i++) {
                try {
                    if(!clients.get(i).getSocket().isClosed())
                    if (i != index) {
                        outStreams.get(i).write(buffer);
                        outStreams.get(i).flush();

                    }

                } catch (Exception e) {
                    System.out.println("send");
                    try {
                        clients.get(i).getSocket().close();
                    } catch (IOException e1) {
                        System.out.println("ERROR closeing");

                        e1.printStackTrace();
                    }
                }
            }

        }).start();
    }

    public void receive(int index) {
        new Thread(() -> {
            while (true) {
                if ((clients.size() - 1) < index) {
                    break;
                }
                if(clients.get(index).getSocket().isClosed())
                break;
                try {
                    if ((clients.size() - 1) < index) {
                        break;
                    }
                    byte[] buffer = new byte[1080];

                    inStreams.get(index).read(buffer);
                    boolean isEmpty = true;

                    for (byte b : buffer) {
                        if (b != 0) {
                            isEmpty = false;
                            break;
                        }
                    }
                    if (isEmpty)
                        if (buffer.length == 0) {
                            isEmpty = false;
                        }
                    if (!isEmpty) {
                        send(index, buffer);

                    }
                    Thread.sleep(1);
                } catch (IOException | InterruptedException e) {
                    System.out.println("recieving ERROR");
                    e.printStackTrace();
                }
            }
            clients.remove(index);
            outStreams.remove(index);
            inStreams.remove(index);

            
        }).start();

    }

    public String getPassword() {
        return password;
    }

    public void addClient(Client newClient) {
        System.out.println("new client");

        clients.add(newClient);
        try {
            inStreams.add(new BufferedInputStream(newClient.getSocket().getInputStream()));
            outStreams.add(new BufferedOutputStream(newClient.getSocket().getOutputStream()));
            receive(clients.size() - 1);
        } catch (IOException e) {
            System.out.println("adding ERROR");

            e.printStackTrace();
        }
    }
}
