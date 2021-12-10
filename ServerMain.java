import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javax.sql.rowset.serial.SerialArray;

public class ServerMain {
    public static void main(String[] args) {
        int port = 8096;
        Server server = new Server(port);
        server.start();
        
    }

   
    

}   
