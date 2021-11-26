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
