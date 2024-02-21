import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ServerReceive {
    private static String json;
    public static String getInfo(Socket sender) throws IOException{
        try(ObjectInputStream is = new ObjectInputStream(sender.getInputStream())){
            json = new String(is.readUTF());

        }
        return json;
    }
}
