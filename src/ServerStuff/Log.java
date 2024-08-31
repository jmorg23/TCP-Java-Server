package ServerStuff;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class Log {
    private String filePath;

    public Log(String id, String username, boolean isHost) throws IOException {

        Files.createDirectories(Paths.get("logs/" + id));
        filePath = "logs/" + id + "/" + username+".log";
        write("Log file created, is host: "+isHost+" at: ["+LocalDateTime.now()+"]");

    }
    public Log(String filename) throws IOException {

        //Files.createDirectories(Paths.get("logs/" + filename));
        filePath = "logs/" + filename+".log";
        write("Log file created at: ["+LocalDateTime.now()+"]");

    }

    public void writeSend(String dataSent) {
        try (FileWriter writer = new FileWriter(filePath, true)) { // 'true' indicates append mode
            // Write content to the file
            writer.write("Sent data: "+dataSent+" at: " + "["+LocalDateTime.now()+"]\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeReceived(String dataRec) {
        try (FileWriter writer = new FileWriter(filePath, true)) { // 'true' indicates append mode
            // Write content to the file
            writer.write("Recieved data: "+dataRec+"  at: ["+ LocalDateTime.now()+"]\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void leave(){
        try (FileWriter writer = new FileWriter(filePath, true)) { // 'true' indicates append mode
        // Write content to the file
        writer.write("Left Server at: ["+ LocalDateTime.now()+"]\n");

    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    public void write(String data) {
        try (FileWriter writer = new FileWriter(filePath, true)) { // 'true' indicates append mode
            // Write content to the file
            writer.write(data+"\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
