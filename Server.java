import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.net.ServerSocket;

public class Server extends Thread {

    private final int servePort;
    private ArrayList<ServerWorker> workerList = new ArrayList<>();
    
    public Server(int servePort){
        this.servePort = servePort;
    }
    public List<ServerWorker> getWorkList(){
        return workerList;
    }
    @Override
    public void run(){
        try{
            ServerSocket ServerSocket = new ServerSocket(servePort);
            while(true){
                System.out.println("Waiting for connection...");
                Socket clientSocket = ServerSocket.accept();
                System.out.println("Connection accepted " + clientSocket);
                ServerWorker worker = new ServerWorker(this,clientSocket);
                workerList.add(worker);
                worker.start();
                        
            }

        }catch(IOException e){
            System.out.println("Error: " + e);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void removeWorker(ServerWorker serverWorker) {
        // Remove Exception
        workerList.remove(serverWorker);
    }
}
