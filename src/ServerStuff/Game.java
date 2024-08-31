package ServerStuff;

import java.io.IOException;

import java.util.ArrayList;

public class Game {

    private String password = "";
    private ArrayList<Client> clients = new ArrayList<>();
    private String ID;
    // ID Format: xx.xxx.xx.xxxx


    public Game(Client hostClient) {
        password = hostClient.getPassword();
        clients.add(hostClient);
        ID = generateRandomID();
        try {
            receive(clients.size() - 1);
        } catch (Exception e) {
            System.out.println("adding error");
            e.printStackTrace();
        }

    }

    public String generateRandomID() {
        String s = "";
        for (int i = 0; i < 11; i++) {
            if (i == 2 || i == 5 || i == 7) {
                s = s + ".";
            }
            s = s + ((int) (Math.random() * 10) + "");
        }
        System.out.println("ID generated: " + s);
        return s;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public void send(int index, byte[] buffer) {
        new Thread(() -> {
            for (int i = 0; i < clients.size(); i++) {
                try {
                    if (!clients.get(i).getSocket().isClosed())
                        if (i != index) {
                            
                            clients.get(i).write(buffer);
                            // clients.get(index).writeToLog(buffer, clients.get(i).getUsername());
                            System.out.println("sent packet");
                        }

                } catch (Exception e) {
                    System.out.println("Send: " + e);
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
                
                if ((clients.size()) < index) {
                    break;
                }
                // if (clients.get(index).getSocket().isClosed())
                //     break;

                try {
                    if ((clients.size() - 1) < index) {
                        break;
                    }
                    if (clients.get(index).getSocket().isClosed())
                        break;
                    if (!clients.get(index).inputStreamIsEmpty()) {
                        byte[] buffer = new byte[4096];

                        if(!clients.get(index).read(buffer)){
                        System.out.println("recieved packet from: " + clients.get(index).getUsername());
                        
                        try {
                            String s = new String(buffer, "UTF-8");
                            if (s.contains(("--=quit"))) {
                                ServerMain.removeGame(password);

                            }
                            System.out.println("Packet: " + s);
                        } catch (Exception e) {
                            System.out.println("cannot convert to string but no problem");
                        }
                        if (buffer.length > 1) {

                            send(index, buffer);
                        }
                    }
                    }

                    Thread.sleep(1);
                } catch (IOException | InterruptedException e) {
                    System.out.println("recieving ERROR");
                    e.printStackTrace();
                    break;
                }
            }
            // if(clients.size()<index)
            // clients.remove(index);

        }).start();

    }

    public String getPassword() {
        return password;
    }

    public String getID() {
        return ID;
    }

    public void addClient(Client newClient) {
        System.out.println("adding client to" + ID);

        try {
            clients.add(newClient);

            receive(clients.size() - 1);

        } catch (Exception e) {
            System.out.println("adding ERROR");

            e.printStackTrace();
        }
    }

    public void remove(Client c){
        clients.remove(c);
    }
    public void getNewHost(){
        clients.get(0).setHost(true);
    }
}
