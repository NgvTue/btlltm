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
public class ObjectWrapper implements Serializable{
    
    private static final long serialVersionUID = 20210811011L;
    private int performative;
    private Object data;
    public static final int SERVER_INFORM_CLIENT_NUMBER = -1;
    
    public static final int CLIENT_CONNECT=4;
    public static final int CLIENT_DISCONNECT=5;
    public static final int ADMIN_CONNECT=6;
    
    
    public static final int LOGIN=7;
    
    
    public static final int REGISTER=8;
   
    public static final int INFORM_USER=9;
    public static final int INFORM_ROOM=10;
    
    public static final int INFORM_HISTORY_CHAT=11;
    public static final int SEND_TEXT=12;
            
    public static final int JOIN_ROOM=13;
    public static final int PING_HEALTH = 0;
    public static final int REFRESH_LIST = 1;
    public static final int SEND_FILE = 2;
    public static final int KEEP_SEND_FILE=3;
    public static final int VIDEO_SWITCH = 14;
    public static final int AUDIO_SWITCH=15;
    public static final int UPDATE_USERS=16;

    public static int getSERVER_INFORM_CLIENT_NUMBER() {
        return SERVER_INFORM_CLIENT_NUMBER;
    }

    public static int getCLIENT_CONNECT() {
        return CLIENT_CONNECT;
    }

    public static int getCLIENT_DISCONNECT() {
        return CLIENT_DISCONNECT;
    }

    public static int getADMIN_CONNECT() {
        return ADMIN_CONNECT;
    }

    public static int getLOGIN() {
        return LOGIN;
    }

    public static int getREGISTER() {
        return REGISTER;
    }

    public static int getINFORM_USER() {
        return INFORM_USER;
    }

    public static int getINFORM_ROOM() {
        return INFORM_ROOM;
    }

    public static int getINFORM_HISTORY_CHAT() {
        return INFORM_HISTORY_CHAT;
    }

    public static int getSEND_TEXT() {
        return SEND_TEXT;
    }

    public static int getJOIN_ROOM() {
        return JOIN_ROOM;
    }

    public static int getSEND_FILE() {
        return SEND_FILE;
    }

    public static int getKEEP_SEND_FILE() {
        return KEEP_SEND_FILE;
    }

    public static int getVIDEO_SWITCH() {
        return VIDEO_SWITCH;
    }

    public static int getAUDIO_SWITCH() {
        return AUDIO_SWITCH;
    }

    public static int getUPDATE_USERS() {
        return UPDATE_USERS;
    }
    
    
    public  ObjectWrapper(){
        super();
    }
    
    public ObjectWrapper(int performative, Object data) {
        this.performative = performative;
        this.data = data;
    }
    
    public void setPerformative(int performative) {
        this.performative = performative;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public static int getPING_HEALTH() {
        return PING_HEALTH;
    }

    public static int getREFRESH_LIST() {
        return REFRESH_LIST;
    }

    public int getPerformative() {
        return performative;
    }

    public Object getData() {
        return data;
    }
}
