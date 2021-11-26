/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client;

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
import view.Room;

/**
 *
 * @author tuenguyen
 */
public class UdpAudio extends Thread{
    private MulticastSocket socket ; // ban request len multicast group
    private static int portMultiAudio =Integer.parseInt(UDPconfig.getPortAudio());
    private static String groupAudio = UDPconfig.getGroupAudio();
    private static String interfaceN = UDPconfig.getNetworkInterface();
    private Room room;

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
    
    public void run(){

        try {
            socket = new MulticastSocket(portMultiAudio);
            InetAddress groupAd = InetAddress.getByName(groupAudio);
            NetworkInterface ni = NetworkInterface.getByName(interfaceN);
            InetSocketAddress group = new InetSocketAddress(groupAd, portMultiAudio);
            socket.joinGroup(group, ni);
            byte[] buf = new byte[UDPconfig.getDefaultDataLenght() + 1000];
            DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
            
            while(true){
                socket.receive(receivePacket);
                byte[] bufs = receivePacket.getData().clone();
                ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(bufs));
                Messenger messageRec = (Messenger) iStream.readObject();
                this.room.receiveAudio(messageRec);

            }
        } catch (IOException ex) {
            Logger.getLogger(UdpAudio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UdpAudio.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    public void sendToAllClients(byte [] buf) throws UnknownHostException, SocketException, IOException{
        System.out.println("audio sending");
        InetAddress groupAd = InetAddress.getByName(groupAudio);
        NetworkInterface ni = NetworkInterface.getByName(interfaceN);
        InetSocketAddress groupx = new InetSocketAddress(groupAd, portMultiAudio);
        
        
        DatagramPacket packet 
        = new DatagramPacket(buf, buf.length, groupAd, portMultiAudio);
        socket.send(packet);
        
        
    }
}
