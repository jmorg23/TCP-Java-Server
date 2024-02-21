
import java.io.*;
import java.net.*;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ServerSend {

private static ObjectMapper mapper = new ObjectMapper();

    public static void sendData(String jsons, Socket socketDest) throws IOException {

        try (ObjectOutputStream os = new ObjectOutputStream(socketDest.getOutputStream())) {

                os.writeUTF(jsons);

            
        }
        
    }
        public static void sendData(INPUTS jsons, Socket socketDest) throws IOException {

        try (ObjectOutputStream os = new ObjectOutputStream(socketDest.getOutputStream())) {
                os.writeUTF(mapper.writeValueAsString(jsons));

            
        }
    }

}
