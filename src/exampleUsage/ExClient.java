package exampleUsage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ExClient {

    private final String IPAdress = "192.168.1.101";
    private static final int port = 25565;
    private Socket socket;
    private BufferedInputStream is;
    private BufferedOutputStream os;
    private ClientInfo myInfo = new ClientInfo();
    private ObjectMapper mapper = new ObjectMapper();
    private int timerCon = 0, timeout = 5, minTime = 2;
    private double time = 0;
    private boolean quit = false;

    public ExClient() {
        try {
            Scanner scan = new Scanner(System.in);
            System.out.print("host true/false: ");
            myInfo.setHost(Boolean.parseBoolean(scan.nextLine()));
            System.out.print("Enter Username: ");
            myInfo.setUsername(scan.nextLine());
            System.out.print("Enter password: ");
            myInfo.setPassword(scan.nextLine());

            System.out.println("trying to connect to: " + IPAdress + " on port: " + port);
            socket = new Socket(IPAdress, port);

            is = new BufferedInputStream(socket.getInputStream());
            os = new BufferedOutputStream(socket.getOutputStream());
            // new Thread(() -> {
            try {
                os.write((mapper.writeValueAsString(myInfo)).getBytes());
                os.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
            // }).start();
            System.out.println("Connected");
            receive();
            startTimer();
            while (true) {
                Scanner s = new Scanner(System.in);
                String message = s.nextLine();

                // OutputStream newStream = socket.getOutputStream();
                os.write(message.getBytes());
                os.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void end() throws IOException {
        System.out.println("Ending client, disconnected from server");
        quit = true;

        socket.close();
        System.exit(0);

    }

    public void writePing() throws IOException {
        os.write(new String("PING").getBytes());
        os.flush();
    }

    private void resetTimer(){
        time = 0;
        timerCon = 0;
    }

    private void startTimer() {
        new Thread(() -> {
            while (true) {
                if (quit) {
                    return;
                }else if (timeout <= time) {
                    try {
                        end();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                try {

                    Thread.sleep(100);
                    time += 0.1;
                    if (time >= minTime && timerCon == 0) {
                       // System.out.println("sent ping");
                        writePing();
                        timerCon = 1;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void receive() {
        new Thread(() -> {
            while (true) {
                try {

                    byte[] buffer = new byte[1080];

                    is.read(buffer);
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
                        if (new String(buffer, "UTF-8").replace("\0", "").equals("PING")) {
                            os.write(new String("PONG").getBytes());
                            os.flush();
                            resetTimer();

                            Thread.sleep(5);
                        } else if (new String(buffer, "UTF-8").replace("\0", "").equals("PONG")) {
                           // System.out.println("rec pong");

                            resetTimer();
                            // System.out.println("wrote pong");
                        }else{
                            System.out.println(new String(buffer, "UTF-8"));
                            resetTimer();

                        }

                    }

                } catch (SocketException e){
                    try {
                        end();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static void main(String[] args) {
        new ExClient();
    }

}