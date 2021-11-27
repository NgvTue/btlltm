/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Config;

/**
 *
 * @author tuenguyen
 */
public class TcpConfig {
    private static String host = "192.168.1.17";
    private static int port = 8888;
    private static String url = "jdbc:mysql://localhost:3306/ltm?autoReconnect=true&useSSL=false";
    private static String username="root";
    private static String password = "1";

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        TcpConfig.url = url;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        TcpConfig.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        TcpConfig.password = password;
    }
    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        TcpConfig.host = host;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        TcpConfig.port = port;
    }
    
}
