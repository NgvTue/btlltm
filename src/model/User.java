/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;

/**
 *
 * @author tuenguyen
 */
public class User implements Serializable{
    private String ipAddress;
    private String username;
    private String name;
    private boolean activeVideo;
    private boolean activeAudio;
    private boolean activeSharing;
    public User(String ipAddress, String username, String name) {
        this.ipAddress = ipAddress;
        this.username = username;
        this.name = name;
        activeVideo= false;
        activeAudio=false;
    }

    public boolean isActiveVideo() {
        return activeVideo;
    }

    public void setActiveVideo(boolean activeVideo) {
        this.activeVideo = activeVideo;
    }

    public boolean isActiveAudio() {
        return activeAudio;
    }

    public void setActiveAudio(boolean activeAudio) {
        this.activeAudio = activeAudio;
    }

    public User() {
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
