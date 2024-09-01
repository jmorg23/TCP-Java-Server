package ServerStuff;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerMain {

    private static final int port = 25565;
    private static ServerSocket serverSocket;
    private static Client curClient;
    private static ArrayList<Game> games = new ArrayList<>();
    public static ArrayList<String> usedPasswords = new ArrayList<>();
    private static boolean allowCommands = true;
    private static Log serverLog;

    public static void removeGame(String password) {
        System.out.println("attempting to get rid of game");
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).getPassword().equals(password)) {
                for (Client c : games.get(i).getClients()) {
                    try {
                        c.end();
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
                System.out.println("game removed with password: " + games.get(i).getPassword());
                games.remove(i);

            }
        }
    }

    public ServerMain() {
        
        try {
            serverSocket = new ServerSocket(port, Integer.MAX_VALUE);
            System.out.println("Server Started");
            System.out.println("On port: " + port);

            serverLog = new Log("ServerLog");

        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            try {

                while (true) {
                    System.out.println("looking for client");
                    curClient = new Client(serverSocket.accept(), serverLog);

                    System.out.println(curClient.getUsername() + " Has Been Accepted!");
                    System.out.println("Host: " + curClient.isHost());
                    System.out.println("Password: " + curClient.getPassword());
                    serverLog.write("Client Joined with usr: "+curClient.getUsername()+" With Password: "+curClient.getPassword()+" is host: "+curClient.isHost()+" at: ["+LocalDateTime.now()+"]");
                    if (curClient.isHost()
                            && (!usedPasswords.contains(curClient.getPassword()))) {
                        games.add(new Game(curClient));
                        usedPasswords.add(curClient.getPassword());
                        curClient.init();
                        serverLog.write("Client was accepted as a host at: ["+LocalDateTime.now()+"]");
                        serverLog.write("Created game: "+games.get(games.size()-1).getID()+" and password: "+games.get(games.size()-1).getPassword()+" at: ["+LocalDateTime.now()+"]");

                    }
                    // They are a client
                    else if (!curClient.isHost()) {
                        for (int i = 0; i < games.size(); i++) {

                            if (games.get(i).getPassword().equals(curClient.getPassword())) {
                                games.get(i).addClient(curClient);
                                curClient.init();

                                serverLog.write("Client was accepted as a client at: ["+LocalDateTime.now()+"]");

                            }
                        }

                    }
                    // they are the host but password is used
                    else {

                        curClient.write(new String("Password Already Taken").getBytes());
                        curClient.end();
                        serverLog.write("Client tried to be host with a used password at: ["+LocalDateTime.now()+"]");

                    }

                    serverLog.write("Client Handling Success at: ["+LocalDateTime.now()+"]\n\n");

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        getCommands();

    }

    public static void getCommands() {

        while (true) {
            if (allowCommands) {
                Scanner scanner = new Scanner(System.in);

                String command = scanner.nextLine();
                if (command.contains("remove")) {
                    String g = command.substring(7, command.length());
                    System.out.println("Attempting to remove game with password: " + g);
                    if (usedPasswords.contains(g)) {
                        removeGame(g);
                        System.out.println("successfully removed");
                    } else {
                        System.out.println("no game with password: " + g);
                    }
                } else if (command.contains("end")) {
                    String u = command.substring(command.indexOf("/u")+3, command.indexOf("/p")-1);
                    String p = command.substring(command.indexOf(u) + u.length()+3, command.length());
                    System.out.println("looking for client with username: " + u + " with password:" + p);
                    if (usedPasswords.contains(p)) {
                        for (int i = 0; i < games.size(); i++) {

                            if (games.get(i).getPassword().equals(p)) {
                                for (Client c : games.get(i).getClients()) {
                                    if (c.getUsername().equals(u)) {
                                        try {
                                            System.out.println("ending "+u+" from script");
                                            c.end();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }

                } else if (command.contains("show")) {
                    String g = command.substring(5, command.length());
                    if (g.equals("/all")) {
                        System.out.println("Showing all games");
                        for (Game c : games) {
                            System.out.println("Game: " + c.getID());
                        }
                    } else if (g.substring(0, 7).equals("clients")) {
                        String a = g.substring(8, g.length());
                        System.out.println("looking for clients in: " + a);
                            for (Game ga : games) {
                                if (ga.getPassword().equals(a)) {
                                    for(Client c:ga.getClients()){
                                    System.out.println("found client with username: " + c.getUsername());
                                    }
                                }
                            }
                        
                    }

                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }

        }

    }

    public static String getIDFromPassword(String p) {
        for (Game g : games) {
            if (g.getPassword().equals(p)) {
                return g.getID();

            }
        }
        return null;
    }

    public static Game getGameFromPassword(String p) {
        for (Game g : games) {
            if (g.getPassword().equals(p)) {
                return g;

            }
        }
        return null;
    }

    public static void main(String[] args) {
        new ServerMain();
    }
}
