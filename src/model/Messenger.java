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
public class Messenger implements Serializable{
    private User user;
    private Object mess;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Object getMess() {
        return mess;
    }

    public void setMess(Object mess) {
        this.mess = mess;
    }

   
    
    
}
