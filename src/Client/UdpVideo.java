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
public class UdpVideo extends Thread{
    
    private MulticastSocket socket ; // ban request len multicast group
    private static int portMultiVideo =Integer.parseInt(UDPconfig.getPortVideo());
    private static String groupVideo = UDPconfig.getGroupAudio();
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
            socket = new MulticastSocket(portMultiVideo);
            
            InetAddress groupAd = InetAddress.getByName(groupVideo);
            NetworkInterface ni = NetworkInterface.getByName(interfaceN);
            InetSocketAddress group = new InetSocketAddress(groupAd, portMultiVideo);
            socket.joinGroup(group, ni);
            byte[] buf = new byte[50000];
            DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
            while(true){
                socket.receive(receivePacket); 
                byte[] bufs = receivePacket.getData().clone();
                ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(bufs));
                Messenger messageRec = (Messenger) iStream.readObject();
                this.room.receiveImage(messageRec);
                
            }
        } catch (IOException ex) {
            Logger.getLogger(UdpVideo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UdpVideo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void sendToAllClients(byte [] buf) throws UnknownHostException, SocketException, IOException{
        System.out.println("video sending");
        InetAddress groupAd = InetAddress.getByName(groupVideo);
        NetworkInterface ni = NetworkInterface.getByName(interfaceN);
        InetSocketAddress groupx = new InetSocketAddress(groupAd, portMultiVideo);
        DatagramPacket packet 
          = new DatagramPacket(buf, buf.length, groupAd, portMultiVideo);
        socket.send(packet);

    }

    public MulticastSocket getSocket() {
        return socket;
    }

    public void setSocket(MulticastSocket socket) {
        this.socket = socket;
    }

    public static int getPortMultiVideo() {
        return portMultiVideo;
    }

    public static void setPortMultiVideo(int portMultiVideo) {
        UdpVideo.portMultiVideo = portMultiVideo;
    }

    public static String getGroupVideo() {
        return groupVideo;
    }

    public static void setGroupVideo(String groupVideo) {
        UdpVideo.groupVideo = groupVideo;
    }

    public static String getInterfaceN() {
        return interfaceN;
    }

    public static void setInterfaceN(String interfaceN) {
        UdpVideo.interfaceN = interfaceN;
    }
    
}
