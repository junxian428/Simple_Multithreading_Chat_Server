import java.net.Socket;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ServerWorker extends Thread{

    private final Socket clientSocket;
    private final Server server;
    private String login = "null";
    private OutputStream outputStream;

    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;

    }

    @Override
    public void run(){
        try{
            handleClient(clientSocket);
        }catch(IOException e){
            e.printStackTrace();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) throws IOException, InterruptedException {
        InputStream input = clientSocket.getInputStream();
        this.outputStream= clientSocket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        while((line = reader.readLine()) != null){
            String[] tokens = StringUtils.split(line);
            if(tokens != null && tokens.length > 0){
                String cmd = tokens[0];
                if("logoff".equalsIgnoreCase(cmd) || "quit".equalsIgnoreCase(cmd)){
                    handleLogOff();
                    break;
                } else if("login".equalsIgnoreCase(cmd)){
                    handleLogin(outputStream, tokens);
                } else if ("msg".equalsIgnoreCase(cmd)){
                    String[] tokenMsg = StringUtils.split(line, null, 3);
                    handleMessage(tokenMsg);
                }else{
                    String msg = "unknown " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        
            String msg = "You typed: " + line + "\n";
            outputStream.write(msg.getBytes());
                
        }
        //outputStream.write("Hello World\n".getBytes());
        clientSocket.close();

    }

    // Message Command
    //format: "msg" "login" "body"
    private void handleMessage(String[] tokens) throws IOException {
        String sendTo = tokens[1];
        String body = tokens[2];

        List<ServerWorker> workers = server.getWorkList();
        for(ServerWorker worker : workers){
            if(sendTo.equalsIgnoreCase(worker.getLogin())){
                String outMsg = "msg " + login + " " + body + "\n";
                worker.send(outMsg);
            }
        }


    }
    // LogOff Command
    private void handleLogOff() throws IOException {
        server.removeWorker(this);
        List<ServerWorker> workers = server.getWorkList();
        String onlineMsg = "Offline " + login + "\n";
        for(ServerWorker worker : workers){
            if(!login.equals(worker.getLogin())){
                worker.send(onlineMsg);
            }
        }
        clientSocket.close();
        
    }

    public String getLogin(){
        return login;
    }

    private void handleLogin(OutputStream outputstream, String[] tokens) throws IOException {
        if(tokens.length == 3){
            String login = tokens[1];
            String password = tokens[2];
            if(login.equals("guest") && password.equals("guest") ||(login.equals("D210044A") && password.equals("D210044A"))){
                String msg = "login ok\n";
                outputstream.write(msg.getBytes());
                this.login = login;
                System.out.println("User logged in successfully...");
                List<ServerWorker> workers = server.getWorkList();
                // Send Current User All Other Online Login 
                for(ServerWorker worker : workers){
                        if(worker.getLogin() != null){
                            if(!login.equals(worker.getLogin())){
                            String onlineMsg2 = "Online " + worker.getLogin() + "\n";
                            send(onlineMsg2);
                            }
                        }              
                 
                }
                // Send other online users current user's status
                String onlineMsg = "Online " + login + "\n";
                for(ServerWorker worker : workers){
                    if(!login.equals(worker.getLogin())){
                        worker.send(onlineMsg);
                    }
                }
            } else{
                String msg = "login failed\n";
                outputstream.write(msg.getBytes());
            }
        }
    }

    private void send(String onlineMsg) throws IOException {
        if(login != null){
            outputStream.write(onlineMsg.getBytes());
        }
    }
    
}
