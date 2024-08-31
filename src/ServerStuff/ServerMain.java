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
    private static ArrayList<Client> clients = new ArrayList<>();
    private static ArrayList<Game> games = new ArrayList<>();
    public static ArrayList<String> usedPasswords = new ArrayList<>();
    private static boolean foundGame = false;
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
            serverSocket = new ServerSocket(port);
            System.out.println("Server Started");
            System.out.println("On port: " + port);

            serverLog = new Log("ServerLog");

        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            try {

                while (true) {

                    clients.add(new Client(serverSocket.accept(), serverLog));
                    foundGame = false;

                    System.out.println(clients.get(clients.size() - 1).getUsername() + " Has Been Accepted!");
                    System.out.println("Host: " + clients.get(clients.size() - 1).isHost());
                    System.out.println("Password: " + clients.get(clients.size() - 1).getPassword());
                    serverLog.write("Client Joined with usr: "+clients.get(clients.size() - 1).getUsername()+" With Password: "+clients.get(clients.size() - 1).getPassword()+" is host: "+clients.get(clients.size()-1).isHost()+" at: ["+LocalDateTime.now()+"]");
                    if (clients.get(clients.size() - 1).isHost()
                            && (!usedPasswords.contains(clients.get(clients.size() - 1).getPassword()))) {
                        games.add(new Game(clients.get(clients.size() - 1)));
                        usedPasswords.add(clients.get(clients.size() - 1).getPassword());
                        clients.get(clients.size() - 1).init();
                        clients.remove(clients.get(clients.size() - 1));
                        foundGame = true;
                        serverLog.write("Client was accepted as a host at: ["+LocalDateTime.now()+"]");
                        serverLog.write("Created game: "+games.get(games.size()-1).getID()+" and password: "+games.get(games.size()-1).getPassword()+" at: ["+LocalDateTime.now()+"]");

                    }
                    // They are a client
                    else if (!clients.get(clients.size() - 1).isHost()) {
                        for (int i = 0; i < games.size(); i++) {

                            if (games.get(i).getPassword().equals(clients.get(clients.size() - 1).getPassword())) {
                                games.get(i).addClient(clients.get(clients.size() - 1));
                                clients.get(clients.size() - 1).init();

                                clients.remove(clients.get(clients.size() - 1));
                                foundGame = true;
                                serverLog.write("Client was accepted as a client at: ["+LocalDateTime.now()+"]");

                            }
                        }

                    }
                    // they are the host but password is used
                    else {
                        BufferedOutputStream os = new BufferedOutputStream(
                                clients.get(clients.size() - 1).getSocket().getOutputStream());
                        os.write(new String("Password Already Taken").getBytes());
                        os.close();
                        clients.get(clients.size() - 1).getSocket().close();
                        foundGame = false;
                        serverLog.write("Client tried to be host with a used password at: ["+LocalDateTime.now()+"]");

                    }
                    if (foundGame == false) {
                        clients.remove(clients.get(clients.size() - 1));
                        System.out.println("client removed");
                        serverLog.write("Client could not get into a game at: ["+LocalDateTime.now()+"]");

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
                        // System.out.println("used passwords length: " + usedPasswords.size());
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
