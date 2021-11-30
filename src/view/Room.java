/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import Client.UdpAudio;
import Client.UdpText;
import Client.UdpVideo;
import Config.TcpConfig;
import Config.UDPconfig;
import com.github.sarxos.webcam.Webcam;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
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
import javax.swing.JLabel;
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
    private UdpText udpText;
    
    private ServerProcessing repTcpServer;
    
    ArrayList<JLabel > viewImages;
    private SourceDataLine speaker;
    
    private String statusShare="NotSetup";
    public void receiveImage(Messenger messageRec) {
        try {
            if(messageRec.getType().equalsIgnoreCase("ShareScreen")){
                System.out.println("hereeee");
                
                byte[] buf = (byte[]) messageRec.getMess();
                System.out.println(buf.length);
                InputStream is  = new ByteArrayInputStream(buf);
                BufferedImage bi = ImageIO.read(is);
                bi = resize(bi,jLabel4.getWidth(), jLabel4.getHeight());
                jLabel4.setIcon(new ImageIcon(bi));
                return;
            }
            User user = messageRec.getUser();
            int index= -1;
            if(users == null)return;
            for(int i=0;i<users.size();i++){
                if(users.get(i).getUsername().equalsIgnoreCase(user.getUsername())){
                    index = i;
                }
            }
            
            if(index!=-1){
                System.out.println("receive video from " + user.getUsername());
            }
            else{
                return;
            }
            if(users.get(index).isActiveVideo()==false){
                return;
            }
            byte[] buf = (byte[]) messageRec.getMess();
            InputStream is  = new ByteArrayInputStream(buf);
            BufferedImage bi = ImageIO.read(is);
            
            
            
            if(index == 0 ){
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

    public void receiveText(Messenger messageRec) {
        User u = messageRec.getUser();
        String mess = (String) messageRec.getMess();
        jTextArea1.append("\n"+u.getUsername() +" : " +  mess);
        
    }
    class ServerProcessing extends Thread{
        private Socket mySocket;
        ObjectOutputStream oos;
        ObjectInputStream ois ;
        public void send(ObjectWrapper data){
            
            try {
                oos.writeObject(data); //
            } catch (IOException ex) {
                Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
                    Object dat = ois.readObject();
                    if(dat instanceof ObjectWrapper){
                        ObjectWrapper recData =(ObjectWrapper)dat;
                        if(recData.getPerformative()==ObjectWrapper.CLIENT_CONNECT ||recData.getPerformative()==ObjectWrapper.CLIENT_DISCONNECT
                                ||recData.getPerformative() == ObjectWrapper.UPDATE_USERS){
                            ArrayList<User> usersx = (ArrayList<User>) recData.getData();
                            System.out.println("Number user in room = " + ((ArrayList<User>) recData.getData()).size() + " -- " + recData.getPerformative());
                            users = (ArrayList<User>) usersx.clone();
                            for(User u : users){
                                System.out.println(u.getUsername() + " " + u.isActiveVideo());
                            }
                            resetRoom();
                        }
                        if(recData.getPerformative() == ObjectWrapper.INFORM_HISTORY_CHAT){
                            String history = (String) recData.getData();
                            jTextArea1.setText(history);
                            System.out.println(history + " his");
                        }
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
        
        jLabel1.setIcon(null);


        jLabel2.setIcon(null);


        jLabel3.setIcon(null);


        jLabel4.setIcon(null);  
        
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
            handleVideo();
            
            udpAudio = new UdpAudio();
            udpAudio.setRoom(this);
            udpAudio.start();
            
            udpText = new UdpText();
            udpText.setRoom(this);
            udpText.start();
            
            AudioFormat af = UDPconfig.getDefaultFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, null);
            speaker = (SourceDataLine) AudioSystem.getLine(info);
            speaker.open(af, UDPconfig.getDefaultDataLenght());
            speaker.start();
            
            startThreadShare();
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
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        share = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setText("send");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

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
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 899, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        share.setText("share");
        share.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shareActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton1))
                            .addComponent(jScrollPane1)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(79, 79, 79)
                        .addComponent(jButton2)
                        .addGap(134, 134, 134)
                        .addComponent(jButton3)
                        .addGap(103, 103, 103)
                        .addComponent(share)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(share))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    public void handleVideo(){
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(new Dimension(320,240));
        webcam.open();
        Thread runnableVideo = new Thread() {
            @Override
            public void run() {

                try {

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    while(true){
                        if(user.isActiveVideo() == false){
                            if(webcam.isOpen())webcam.close();
                            continue;
                        }
                        if(webcam.isOpen() == false)webcam.open();
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
        runnableVideo.start();
    }
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        this.user.setActiveVideo(!this.user.isActiveVideo());
        // inform open cam
        ObjectWrapper data = new ObjectWrapper();
        data.setPerformative(ObjectWrapper.VIDEO_SWITCH);
        data.setData(this.user.isActiveVideo());
        
        repTcpServer.send(data);
        
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
                                long tot = 0;
                                for (int i = 0; i < buf.length; i++) {
                                    buf[i] *= 1.0;
                                    tot += Math.abs(buf[i]); // tong cac gia tri |x| bien do cua am thanh
                                }
                                if(tot==0)continue;
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            // TODO add your handling code here:
            String text = jTextField1.getText();
            Messenger mess = new Messenger();
            mess.setUser(user);
            mess.setMess(text);
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream);
            oo.writeObject(mess);
            oo.close();
            
            byte[] serializedMessage = bStream.toByteArray();
            
            udpText.sendToAllClients(serializedMessage);
            jTextField1.setText("");
            
            ObjectWrapper data = new ObjectWrapper();
            data.setPerformative(ObjectWrapper.SEND_TEXT);
            data.setData(text);
            repTcpServer.send(data);
            
        } catch (IOException ex) {
            Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed
    public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
    private static BufferedImage resize2(BufferedImage bi, int toWidth, int toHeight) {
        Graphics g = null;
        try {
          Image scaledImage = bi.getScaledInstance(toWidth, toHeight, Image.SCALE_SMOOTH);
          BufferedImage ret = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);
          g = ret.getGraphics();
          g.drawImage(scaledImage, 0, 0, null);
          return ret;
        } catch (Exception e) {
          throw new RuntimeException(e);
        } finally {
          if (g != null) {
            g.dispose();
          }
        }
    }
    private void startThreadShare(){
        Thread runnable = new Thread(){
            @Override
            public void run(){
                try {
                    Toolkit tk = Toolkit.getDefaultToolkit();
                    Dimension d = tk.getScreenSize();
                    Rectangle rec = new Rectangle(0, 0,d.width, d.height);
                    Robot ro = new Robot();
                    
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    
                    while(true){
                        if(statusShare.equalsIgnoreCase("NotSetup")){
                            continue;
                        }
                        BufferedImage img = ro.createScreenCapture(rec);
//                        img = resize(img, 320, 240)
                        img = resize2(img,640,480);
                        Thread.sleep(50);
                        ImageIO.write(img, "JPG", baos);
                        byte[] buf = baos.toByteArray();
                        Messenger messageClass = new Messenger();
                        messageClass.setUser(user);
                        messageClass.setMess(buf);
                        messageClass.setType("ShareScreen");
                        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                        ObjectOutput oo = new ObjectOutputStream(bStream); 
                        oo.writeObject(messageClass);
                        oo.close();
                        baos.reset();
                        byte[] serializedMessage = bStream.toByteArray();
                        udpVideo.sendToAllClients(serializedMessage);
//                        jLabel4.setIcon(new ImageIcon(img));
                    }
                    
                } catch (AWTException ex) {
                    Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
                }
            };
        };
        runnable.start();
    }
    private void shareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shareActionPerformed
        if(statusShare.equalsIgnoreCase("NotSetup")){
            statusShare = "Setup";
        }
        else
        {
            statusShare="NotSetup";
            jLabel4.setIcon(null);
        }
        ObjectWrapper data = new ObjectWrapper();
        data.setPerformative(ObjectWrapper.VIDEO_SWITCH);
        data.setData(this.user.isActiveVideo());
        repTcpServer.send(data);
    }//GEN-LAST:event_shareActionPerformed

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel1MouseClicked

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
    private javax.swing.JButton share;
    // End of variables declaration//GEN-END:variables
}
