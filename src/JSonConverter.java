import java.io.*;
import java.net.Socket;

public class JSonConverter {

    public static void main(String[] args) {
        String serverAddress = "localhost"; // Change this to your server's address
        int serverPort = 12345; // Change this to your server's port number

        try {
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to the server");

            // Set up input and output streams
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            PrintWriter writer = new PrintWriter(output, true);

            // Communication loop
            while (true) {
                // Send a message to the server
                String message = "Hello from the client";
                writer.println(message);

                // Receive and print the server's response
                String serverResponse = reader.readLine();
                System.out.println("Server response: " + serverResponse);

                // Sleep for a while to simulate ongoing communication
                Thread.sleep(2000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
