/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

/**
 *
 * @author tuenguyen
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */



import Config.TcpConfig;
import model.IPAddress;
//import Model.Member;
import model.ObjectWrapper;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;

/**
 *
 * @author tuenguyen
 */
public class TcpCenter {
    
    private ServerSocket myServer;
    private ServerListening myListening;
    private ArrayList<ServerProcessing> myProcess;
    
    private ArrayList<User> users;
    private String text="";
    private IPAddress myAddress = new IPAddress(TcpConfig.getHost(), TcpConfig.getPort());  // default server host and port
    
  
    
    public ServerSocket getMyServer() {
        return myServer;
    }
    
    public void setMyServer(ServerSocket myServer) {
        this.myServer = myServer;
    }

    public ArrayList<ServerProcessing> getMyProcess() {
        return myProcess;
    }

    public void setMyProcess(ArrayList<ServerProcessing> myProcess) {
        this.myProcess = myProcess;
    }

   

    
     
    public TcpCenter(){
        myProcess = new ArrayList<>();
        users = new ArrayList<>();
        text = "";
        openServer();
       
    }
     
    public TcpCenter( int serverPort) {
        myProcess = new ArrayList<>();
        myAddress.setPort(serverPort);
        openServer();
    }
    
    
    private void openServer(){
        try {
            myServer = new ServerSocket(myAddress.getPort());
            myListening = new ServerListening();
            myListening.start();
            myAddress.setHost(InetAddress.getLocalHost().getHostAddress());
            
            String serverInfor = "Server: " + myAddress.getHost() + "\nPort: " + myAddress.getPort();
            System.out.println(serverInfor);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public void stopServer() {
        try {
            for (ServerProcessing sp:myProcess)
                sp.stop();
            myListening.stop();
            myServer.close();
            System.out.println("stop server TCP");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
     
    public void publicClientNumber() {
        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.SERVER_INFORM_CLIENT_NUMBER, myProcess.size());
        for (ServerProcessing sp : myProcess) {
            sp.sendData(data);
        }
    }
    public void callClient(ObjectWrapper obj) {
        System.out.println(myProcess.size());
        for (ServerProcessing sp : myProcess) {
            sp.sendData(obj);
        }
    }


    /**
     * The class to listen the connections from client, avoiding the blocking of accept connection
     *
     */
    class ServerListening extends Thread{
         
        public ServerListening() {
            super();
        }
         
        public void run() {
            
            try {
                while(true) {
                    
                    Socket clientSocket = myServer.accept();
//                    clientSocket.getInetAddress().getAddress();
                    ServerProcessing sp = new ServerProcessing(clientSocket);
                    sp.start();
                    myProcess.add(sp);
                    System.out.println("more connection");
                    // send to MULTICAST TEXT someone connected
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    } 
    /**
     * The class to treat the requirement from client
     *
     */
    class ServerProcessing extends Thread{
        private Socket mySocket;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;
        private User user;
        public ServerProcessing(Socket s) throws IOException {
            super();
            mySocket = s;
            oos= new ObjectOutputStream(mySocket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(mySocket.getInputStream());
            
        }
         
        public void sendData(Object obj) {
            try {
                oos.writeObject(obj);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        public void run() { 
            try {
                
                while(true) {
                    
                    Object o = ois.readObject();
                    if(o instanceof ObjectWrapper){
                        ObjectWrapper data = (ObjectWrapper)o;
                        // xu li object o day
                        if(data.getPerformative() == ObjectWrapper.PING_HEALTH){
                            // continue
                            System.out.println("Client ping server!!!!");
                        }   
                        if(data.getPerformative() == ObjectWrapper.LOGIN){
                            // co user vao phong
                            User user = (User) data.getData();
                            user.setIpAddress(mySocket.getInetAddress().getAddress().toString());
                            this.user=user;
                            System.out.println("Client login server!!!! : " + user.getUsername());
                            // gui mess sang cac tcp khac
                            users.add(user);
                            ObjectWrapper informToAnother = new ObjectWrapper();
                            informToAnother.setPerformative(ObjectWrapper.CLIENT_CONNECT);
                            informToAnother.setData(users);
                            System.out.println("number user = " + ((ArrayList<User>)informToAnother.getData()).size());
                            callClient(informToAnother);
                            // get texts
                            ObjectWrapper allText = new ObjectWrapper();
                            allText.setData(text);
                            allText.setPerformative(ObjectWrapper.INFORM_HISTORY_CHAT);
                            sendData(allText);
                        }
                        
                        
                    }
                    
                }
            } catch (EOFException | SocketException e) {             
               
                   
                myProcess.remove(this);
                
                ObjectWrapper informToAnother = new ObjectWrapper();
                informToAnother.setPerformative(ObjectWrapper.CLIENT_DISCONNECT);
                users.remove(user);
                informToAnother.setData(users);
                System.out.println("number user = " + ((ArrayList<User>)informToAnother.getData()).size());
                callClient(informToAnother);
                this.stop();
                
            } catch (IOException ex) {
                Logger.getLogger(TcpCenter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(TcpCenter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
