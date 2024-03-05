package ServerStuff;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ServerMain {

    private static final int port = 25565;
    private static ServerSocket serverSocket;
    private static ArrayList<Client> clients = new ArrayList<>();
    private static ArrayList<Game> games = new ArrayList<>();
    public static ArrayList<String> usedPasswords = new ArrayList<>();
    private static boolean foundGame = false;
    public static void removeGame(String password){
        for(int i = 0; i<games.size(); i++){
            if(games.get(i).getPassword().equals(password)){
                games.remove(i);
            }
        }
    }
    public ServerMain(){
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server Started");
            while(true){
                clients.add(new Client(serverSocket.accept()));
                foundGame = false;

                System.out.println(clients.get(clients.size()-1).getUsername()+" Has Been Accepted!");
                System.out.println("Host: "+clients.get(clients.size()-1).isHost());
                System.out.println("Password: "+clients.get(clients.size()-1).getPassword());

                if(clients.get(clients.size()-1).isHost()&&(!usedPasswords.contains(clients.get(clients.size()-1).getPassword()))){
                    games.add(new Game(clients.get(clients.size()-1)));
                    clients.remove(clients.get(clients.size()-1));
                    foundGame = true;
                    games.get(games.size()-1).check();

                }else if(!clients.get(clients.size()-1).isHost()){
                    for(int i = 0; i<games.size(); i++){
                        if(games.get(i).getPassword().equals(clients.get(clients.size()-1).getPassword())){
                            games.get(i).addClient(clients.get(clients.size()-1));
                            clients.remove(clients.get(clients.size()-1));       
                            foundGame = true;
                            
                        }
                    }

                }
                if(foundGame == false){
                    clients.remove(clients.get(clients.size()-1));          
                    System.out.println("incorrect password");

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public static void main(String[] args) {
        new ServerMain();
    }
}
