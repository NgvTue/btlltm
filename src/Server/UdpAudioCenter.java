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
package udpAudio;


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
public class UdpAudioCenter extends Thread{
    private DatagramSocket socketRec; // nhan request tu client
    private MulticastSocket socket ; // ban request len multicast group
    private static int portMultiAudio =Integer.parseInt(UDPconfig.getPortAudio());
    private static String groupAudio = UDPconfig.getGroupAudio();
    private static String interfaceN = UDPconfig.getNetworkInterface();
    
    
    
    public void run(){

        try {
            socket = new MulticastSocket(portMultiAudio);
            socketRec = new DatagramSocket(Integer.parseInt(UDPconfig.getPortAudioRe()));
            InetAddress groupAd = InetAddress.getByName(groupAudio);
            NetworkInterface ni = NetworkInterface.getByName(interfaceN);
            InetSocketAddress group = new InetSocketAddress(groupAd, portMultiAudio);
            socket.joinGroup(group, ni);
            byte[] buf = new byte[UDPconfig.getDefaultDataLenght()];
            DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
            
            while(true){
                socketRec.receive(receivePacket);
                byte[] bufs = receivePacket.getData().clone();
                ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(bufs));
                Messenger messageRec = (Messenger) iStream.readObject();
                byte[] bufAudio = (byte[]) messageRec.getMess();
                iStream.close();
                // xu li am thanh k co tieng noi
                long tot = 0;
                for (int i = 0; i < buf.length; i++) {
                    bufAudio[i] *= 1.0;
                    tot += Math.abs(bufAudio[i]); // tong cac gia tri |x| bien do cua am thanh
                }
                tot *= 2.5;
                tot /= bufAudio.length;
                
                if(tot !=0)
                    sendToAllClients(bufs);
                
            }
        } catch (IOException ex) {
            Logger.getLogger(UdpAudioCenter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UdpAudioCenter.class.getName()).log(Level.SEVERE, null, ex);
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

