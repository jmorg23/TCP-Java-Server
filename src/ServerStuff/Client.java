package ServerStuff;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;

import exampleUsage.ClientInfo;

public class Client {

    private String Username;
    private String Password;
    private boolean host;
    private Socket socket;
    private Log log;

    private BufferedInputStream is;
    private BufferedOutputStream os;
    private ClientInfo myInfo;
    private ObjectMapper mapper = new ObjectMapper();
    private int timeout = 10;
    private double time = 0;
    private double minTime = 4;
    private boolean quit = false;

    public Client(Socket s, Log serverLog) {
        socket = s;
        System.out.println("Connected");
        try {
            is = new BufferedInputStream(socket.getInputStream());
            os = new BufferedOutputStream(socket.getOutputStream());
            byte[] buffer = new byte[1080];
            is.read(buffer);
            String str = new String(buffer, "UTF-8");
            System.out.println(str);
            str = removeSpecialChars(str);
            myInfo = mapper.readValue(str, ClientInfo.class);
            Username = myInfo.getUsername();
            host = myInfo.getIsHost();
            Password = myInfo.getPassword();


        } catch (IOException e) {
            e.printStackTrace();
            try {
                serverLog.write("Client did not give the right format to join at: ["+LocalDateTime.now()+"]");

                is.close();
                socket.close();
                System.out.println("HACKER ALERT");

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void writePing()  {
        try {
            os.write(new String("PING").getBytes());
            os.flush();
        }catch (SocketException e1){
            try {
                System.out.println(Username+" had an error writing Ping: "+e1);

                end();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }catch (IOException e) {
            e.printStackTrace();
        } 
        
    }
    public void writePong()  {
        try {
            os.write(new String("PONG").getBytes());
            os.flush();

        }catch (SocketException e1){
            try {
                System.out.println(Username+" had an error writing Pong: "+e1);

                end();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }catch (IOException e) {
            e.printStackTrace();
        } 
        
    }

    public void write(byte[] buffer) throws IOException {
        log.writeReceived(new String(buffer, "UTF-8").replace("\0", ""));

        os.write(buffer);
        os.flush();
    }

    private void resetTimer() {
        time = 0;
        timerCon = 0;
        timer2Con = 0;

    }


    private int timerCon = 0;
    private int timer2Con = 0;

    private void startTimer() {
        new Thread(() -> {
            while (true) {
                if (timeout <= time) {
                    try {
                        System.out.println("PING timed Out: "+Username);

                        end();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                try {
                    if(quit){
                        return;
                    }
                    Thread.sleep(100);
                    time += 0.1;
                    if((time>=minTime&&timerCon ==0)||((time>=minTime*2)&&timer2Con==0)){
                        writePing();
                        timerCon = 1;
                        if(time>=minTime*2){
                            timer2Con=1;
                        }
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }


    /**
     * 
     * @param buffer
     * @return boolean for is ping
     * @throws IOException
     */
    public boolean read(byte[] buffer) throws IOException {
        is.read(buffer);
        if (new String(buffer, "UTF-8").replace("\0", "").equals("PONG")) {
            resetTimer();

            return true;
        } else if(new String(buffer, "UTF-8").replace("\0", "").equals("PING")) {
            resetTimer();
            writePong();

            return true;
        }else{

            resetTimer();
            if(log!=null)
            log.writeSend(new String(buffer, "UTF-8").replace("\0", ""));
            return false;
        }

    }

    public boolean inputStreamIsEmpty() throws IOException {
        return is.available() < 1;
    }

    public void init() throws IOException {
        log = new Log(ServerMain.getIDFromPassword(Password), Username, host);
        startTimer();
    }

    public static String removeSpecialChars(String str) {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\s{}\"\\[\\]:;,]+");
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("");
    }

    public String getUsername() {
        return Username;
    }

    public boolean isHost() {
        return host;
    }

    public String getPassword() {
        return Password;
    }

    public Socket getSocket() {
        return socket;
    }
    public void setHost(boolean b){
        host = true;
        log.write("Updated To Host at: ["+LocalDateTime.now()+"]");
    }
    public void end() throws IOException {
        quit = true;
        socket.close();
        System.out.println("ending client: "+Username);
        ServerMain.getGameFromPassword(Password).remove(this);
        log.leave();

        if(isHost()){
            ServerMain.getGameFromPassword(Password).getNewHost();
        }

    }

}
