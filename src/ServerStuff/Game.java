package ServerStuff;


import java.io.IOException;

import java.util.ArrayList;

public class Game {

    private String password = "";
    private ArrayList<Client> clients = new ArrayList<>();
    //private ArrayList<BufferedInputStream> inStreams = new ArrayList<>();
    //private ArrayList<BufferedOutputStream> outStreams = new ArrayList<>();

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
            receive(clients.size() - 1);
        } catch (Exception e) {
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
                        clients.get(i).getOs().write(buffer);
                        clients.get(i).getOs().flush();
                        System.out.println("sent packet");

                    }

                } catch (Exception e) {
                    System.out.println("Send: "+e);
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
                if ((clients.size()) < index) {
                    break;
                }
                if(clients.get(index).getSocket().isClosed())
                    break;
                try {
                    if ((clients.size() - 1) < index) {
                        break;
                    }
                    if(clients.get(index).getSocket().isClosed())
                    break;
                    byte[] buffer = new byte[4096];

                    clients.get(index).getIs().read(buffer);
                    System.out.println("recieved packet from: "+clients.get(index).getUsername());
                    //System.out.println(new String(buffer, "UTF-8"));

                    if(buffer.length>1){

                    send(index, buffer);
                    }

                    Thread.sleep(1);
                } catch (IOException | InterruptedException e) {
                    System.out.println("recieving ERROR");
                    e.printStackTrace();
                    break;
                }
            }
            clients.remove(index);


            
        }).start();

    }

    public String getPassword() {
        return password;
    }

    public void addClient(Client newClient) {
        System.out.println("new client");

        clients.add(newClient);
        try {

            receive(clients.size() - 1);
        } catch (Exception e) {
            System.out.println("adding ERROR");

            e.printStackTrace();
        }
    }
}
