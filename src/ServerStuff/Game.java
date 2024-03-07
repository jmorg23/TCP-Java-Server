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
            while(true){
            if(clients.size()==0){
                System.out.println("No one in game with password: "+password+"\n Now removed");

                ServerMain.removeGame(password);
                break;

            } else
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            System.out.println(clients.size());

            }
        }).start();
    }

    public Game(Client hostClient) {
        password = hostClient.getPassword();
        clients.add(hostClient);
        try {
            inStreams.add(new BufferedInputStream(hostClient.getSocket().getInputStream()));
            outStreams.add(new BufferedOutputStream(hostClient.getSocket().getOutputStream()));
            receive(clients.size()-1);

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
    int nothingFor = 0;
    public void receive(int index) {
        new Thread(() -> {
            while (true) {
                try{
                //System.out.println(clients.get(index).getSocket().isClosed());
                
                if ((clients.size()) < index) {
                    break;
                }
                if(clients.get(index).getSocket().isClosed())
                break;
                
                    if ((clients.size() - 1) < index) {
                        break;
                    }
                    byte[] buffer = new byte[1000000];

                    if(inStreams.get(index).available()>0){
                        inStreams.get(index).read(buffer);
                        nothingFor = 0;
                    }else{
                        Thread.sleep(100);
                        nothingFor+=100;

                    }
                    if(nothingFor>60000)
                    break;
                    send(index, buffer);


                } catch (Exception e) {
                    System.out.println("recieving ERROR");
                    e.printStackTrace();
                    break;
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
            receive(clients.size()-1);
        } catch (IOException e) {
            System.out.println("adding ERROR");

            e.printStackTrace();
        }
    }
}
