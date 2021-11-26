/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

import Config.UDPconfig;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Messenger;

/**
 *
 * @author tuenguyen
 */
public class UdpTextCenter extends Thread{
     private DatagramSocket socketRec; // nhan request tu client
    private MulticastSocket socket ; // ban request len multicast group
    private static int portMultiText =Integer.parseInt(UDPconfig.getPortText());
    private static String groupText= UDPconfig.getGroupText();
    private static String interfaceN = UDPconfig.getNetworkInterface();
    public void run(){
        try {
            socket = new MulticastSocket(portMultiText);
            
            socketRec = new DatagramSocket(Integer.parseInt(UDPconfig.getPortTextRc()));
            
            InetAddress groupAd = InetAddress.getByName(groupText);
            NetworkInterface ni = NetworkInterface.getByName(interfaceN);
            InetSocketAddress group = new InetSocketAddress(groupAd, portMultiText);
            socket.joinGroup(group, ni);
            
            
            byte[] buf = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
            
            while(true){
                socketRec.receive(receivePacket);
                
                byte[] bufRec = receivePacket.getData();
                sendToAllClients(bufRec);
                
            }
        } catch (IOException ex) {
            Logger.getLogger(UdpTextCenter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendToAllClients(byte [] buf) throws UnknownHostException, SocketException, IOException{
        System.out.println("text sending");
        InetAddress groupAd = InetAddress.getByName(groupText);
        NetworkInterface ni = NetworkInterface.getByName(interfaceN);
        InetSocketAddress groupx = new InetSocketAddress(groupAd, portMultiText);
        DatagramPacket packet 
          = new DatagramPacket(buf, buf.length, groupAd, portMultiText);
        socket.send(packet);

    }
}
