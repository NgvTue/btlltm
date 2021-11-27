/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client;

import Config.UDPconfig;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.logging.Level;
import java.util.logging.Logger;
import view.Room;

/**
 *
 * @author tuenguyen
 */
public abstract class UDPForm extends Thread{
    private MulticastSocket socket ; // ban request len multicast group
    private static int port;
    private static String group;
    private static String interfaceN;
    private Room room;

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
    public abstract void  processingRecevieData(); // xu ly du lieu nhan duoc tu cac client khac
    public abstract void send();// gui du lieu toi cac client khac
    public void run(){
        try {
            socket = new MulticastSocket(port);
            
            InetAddress groupAd = InetAddress.getByName(group);
            NetworkInterface ni = NetworkInterface.getByName(interfaceN);
            InetSocketAddress group = new InetSocketAddress(groupAd, port);
            
            socket.joinGroup(group, ni);
            
            byte[] buf = new byte[UDPconfig.getDefaultDataLenght()];
            DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
            
            
            while(true){
                processingRecevieData();
            }
            
            
        } catch (IOException ex) {
            Logger.getLogger(UDPForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
