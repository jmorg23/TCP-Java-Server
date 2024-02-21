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

    public Game(Client hostClient) {
        password = hostClient.getPassword();
        clients.add(hostClient);
        try {
            inStreams.add(new BufferedInputStream(hostClient.getSocket().getInputStream()));
            outStreams.add(new BufferedOutputStream(hostClient.getSocket().getOutputStream()));
            receive(clients.size() - 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        //checkGameDone();

    }


  //  private int timeOff = 0;
/*
    public void checkGameDone() {

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    if (timeOff >= 20) {
                        ServerMain.usedPasswords.remove(getPassword());

                        timeOut();
                        System.out.println("Removed Game");
                        ServerMain.removeGame(password);
                        break;
                    }

                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
*/
    public void timeOut() throws IOException {
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).getSocket().close();
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
                    e.printStackTrace();
                    System.out.println("send");
                    try {
                        clients.get(i).getSocket().close();
                        outStreams.get(i).close();
                        inStreams.get(i).close();

                    } catch (IOException e1) {
                        System.out.println("closeing");

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
                    System.out.println("recieve");
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
            System.out.println("adding");

            e.printStackTrace();
        }
    }
}
