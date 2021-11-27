/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;

/**
 *
 * @author tuenguyen
 */
public class DAO {
    private String url;
    private String username;
    private String password;
    public static  Connection con;
    public void connectDao(){
        String dbUrl = this.getUrl();
        String dbClass = "com.mysql.jdbc.Driver";
        try {
            Class.forName(dbClass);
            con = DriverManager.getConnection (dbUrl, this.getUsername(),this.getPassword());
            System.out.println("Đã kết nối tới Mysql: " + getUrl());
        }catch(Exception e) {
            System.out.println("loi o day");
            e.printStackTrace();
            
        }
    }
    public DAO(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static Connection getCon() {
        return con;
    }
    public void outRoom(User u){
        try {
            
            String sql="DELETE FROM tblUser WHERE username=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, u.getUsername());
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void joinRoom(User u){
        try {
            String sql = "INSERT INTO tblUser(`username`,`name`,`ipAddress`,`activeVideo`,`activeAudio`,`inRoom`) VALUES(?,?,?,?,?,?)";
            
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getUsername());
            ps.setString(3, u.getIpAddress());
            if(u.isActiveAudio())
                ps.setString(5, "true");
            else
                ps.setString(5, "false");
            if(u.isActiveVideo())
                ps.setString(4, "true");
            else
                ps.setString(4, "false");
            ps.setString(6, "true");
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void addMess(User u, String mess){
        try {
            String sql = "INSERT INTO tblChat(`user`,`mess`) VALUES(?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, u.getUsername());
            ps.setString(2, mess);
            ps.executeUpdate();
            System.out.println("add rtext");
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public String getMess(){
        try {
            String sql = "SELECT * from tblChat";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            String text="";
            while(rs.next()){
                String username = rs.getString("user");
                String mess = rs.getString("mess");
                text = text + username+ " : " + mess + "\n";
            }
            return text;
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    public void updateVideoActive(User u){
        try {
            String sql ="UPDATE tblUser\n" +
                    "SET activeVideo = ?\n" +
                    "WHERE name=?";
            PreparedStatement ps = con.prepareStatement(sql);
            
            if(u.isActiveVideo())
                ps.setString(1, "true");
            else
                ps.setString(1,"false");
            ps.setString(2, u.getName());
            System.out.println(ps.toString());
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public ArrayList<User> getAllUser(){
        try {
            String sql = "SELECT * from tblUser WHERE `inRoom`=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "true");
            ResultSet rs = ps.executeQuery();
            ArrayList<User> users = new ArrayList<User>();
            while(rs.next()){
                String username = rs.getString("name");
                String ipA = "a";
                String activeVideo=rs.getString("activeVideo");
                String activeAudio = rs.getString("activeAudio");
                boolean aV = Boolean.parseBoolean(activeVideo);
                boolean aU = Boolean.parseBoolean(activeAudio);
                User i = new User(ipA, username, username);
                i.setActiveVideo(aV);
                i.setActiveAudio(aU);
                users.add(i);
                
            }
            return users;
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
