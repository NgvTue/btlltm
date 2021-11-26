/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import Client.UdpAudio;
import Client.UdpVideo;
import Config.TcpConfig;
import Config.UDPconfig;
import com.github.sarxos.webcam.Webcam;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.ImageIcon;
import model.Messenger;
import model.ObjectWrapper;
import model.User;

/**
 *
 * @author tuenguyen
 */
public class Room extends javax.swing.JFrame {

    /**
     * Creates new form Room
     */
    private ArrayList<User> users;
    private User user;
    private UdpVideo udpVideo; 
    private UdpAudio udpAudio;
    private ServerProcessing repTcpServer;
    
    private SourceDataLine speaker;
    public void receiveImage(Messenger messageRec) {
        try {
            User user = messageRec.getUser();
            int index= -1;
            for(int i=0;i<users.size();i++){
                if(users.get(i).getUsername().equalsIgnoreCase(user.getUsername())){
                    index = i;
                }
            }
            if(index!=-1){
                System.out.println("receive video from " + user.getUsername());
            }
            
            byte[] buf = (byte[]) messageRec.getMess();
            InputStream is  = new ByteArrayInputStream(buf);
            BufferedImage bi = ImageIO.read(is);
            if(index == 0){
                jLabel1.setIcon(new ImageIcon(bi));
            }
            if(index == 1){
                jLabel2.setIcon(new ImageIcon(bi));
            }
            if(index == 2){
                jLabel3.setIcon(new ImageIcon(bi));
            }
            if(index == 3){
                jLabel4.setIcon(new ImageIcon(bi));
            }
        } catch (IOException ex) {
            Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void receiveAudio(Messenger messageRec) {
        if(messageRec.getUser().getUsername().equalsIgnoreCase(user.getUsername())==false){
            byte[] toPlay=(byte[]) messageRec.getMess();
            speaker.write(toPlay,0, toPlay.length);
            System.out.println("run speaker : " + messageRec.getUser().getUsername());
        }
    }
    class ServerProcessing extends Thread{
        private Socket mySocket;
        ObjectOutputStream oos;
        ObjectInputStream ois ;
        public void run() {
            try {
                mySocket =  new Socket(TcpConfig.getHost(),  TcpConfig.getPort());
                oos = new ObjectOutputStream(mySocket.getOutputStream());
                ois = new ObjectInputStream(mySocket.getInputStream());
                
                // send connect to TCP
                ObjectWrapper data = new ObjectWrapper();
                
                data.setPerformative((ObjectWrapper.LOGIN));
                data.setData(user);
                oos.writeObject(data); //
                
                // nhan lai list cac user trong phong
                
                while(true){    
                    ObjectWrapper obj =(ObjectWrapper)ois.readObject();
                    if(obj.getPerformative()==ObjectWrapper.CLIENT_CONNECT ||obj.getPerformative()==ObjectWrapper.CLIENT_DISCONNECT ){
                        ArrayList<User> usersx = (ArrayList<User>) obj.getData();
                        System.out.println("Number user in room = " + usersx.size());
                        users = (ArrayList<User>) usersx.clone();
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public void resetRoom(){
        for(int i=0;i<users.size();i++){
            
        }
    }
    public Room(User user) {
        try {
            initComponents();
            this.user = user;
            repTcpServer = new ServerProcessing();
            repTcpServer.start();
            
            udpVideo = new UdpVideo();
            udpVideo.setRoom(this);
            udpVideo.start();
            
            
            AudioFormat af = UDPconfig.getDefaultFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, null);
            speaker = (SourceDataLine) AudioSystem.getLine(info);
            speaker.open(af, UDPconfig.getDefaultDataLenght());
            speaker.start();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setText("send");

        jButton2.setText("cam");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("mic");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(118, 196, 227));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 12, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jButton2)
                        .addGap(137, 137, 137)
                        .addComponent(jButton3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(new Dimension(320,240));
        webcam.open();
        
        Thread runnable = new Thread() {
            @Override
            public void run() {
                
                try {
                    
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    while(true){
                        
                        BufferedImage image = webcam.getImage();

                        System.out.println(image.getHeight() + "-" + image.getWidth() + "-" + image.toString());
                          
                        
                        ImageIO.write(image, "JPG", baos);
                        byte[] buf = baos.toByteArray();
                        Messenger messageClass = new Messenger();
                        messageClass.setUser(user);
                        messageClass.setMess(buf);
                        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                        ObjectOutput oo = new ObjectOutputStream(bStream); 
                        oo.writeObject(messageClass);
                        oo.close();
                        baos.reset();
                        byte[] serializedMessage = bStream.toByteArray();
                        udpVideo.sendToAllClients(serializedMessage);

                    }
                } catch (SocketException ex) {
                    Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        runnable.start();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        Thread runnable = new Thread(){
            @Override
            public void run(){
                try {
                    AudioFormat af = UDPconfig.getDefaultFormat();
                    DataLine.Info info = new DataLine.Info(TargetDataLine.class, af);
                    TargetDataLine mic = (TargetDataLine) (AudioSystem.getLine(info));
                    mic.open(af);
                    mic.start();

                    while(true){
                        byte[] buf = new byte[UDPconfig.getDefaultDataLenght()];
                        if (mic.available() >= UDPconfig.defaultDataLenght) { //we got enough data to send
                            while (mic.available() >= UDPconfig.defaultDataLenght) { //flush old data from mic to reduce lag, and read most recent data
                                mic.read(buf, 0, buf.length); //read from microphone
                                byte[] bufClone = buf.clone();
                                Messenger mess = new Messenger();
                                mess.setUser(user);
                                mess.setMess(bufClone);
                                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                                ObjectOutput oo = new ObjectOutputStream(bStream); 
                                oo.writeObject(mess);
                                oo.close();
                                
                                byte[] serializedMessage = bStream.toByteArray();
                                udpAudio.sendToAllClients(serializedMessage);
//                                centerAudio.send(buf); // send buf cho server xu li truoc

                            }
                        }


                    }
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        runnable.start();
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
