/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Config;

import javax.sound.sampled.AudioFormat;

/**
 *
 * @author tuenguyen
 */
public class UDPconfig {

    public static String getNetworkInterface() {
        return networkInterface;
    }

    public static String getGroupText() {
        return groupText;
    }

    public static String getHostText() {
        return hostText;
    }

    public static String getPortText() {
        return portText;
    }

    public static String getGroupVideo() {
        return groupVideo;
    }

    public static String getHostVideo() {
        return hostVideo;
    }

    public static String getPortVideo() {
        return portVideo;
    }

    public static String getGroupAudio() {
        return groupAudio;
    }

    public static String getHostAudio() {
        return hostAudio;
    }

    public static String getPortAudio() {
        return portAudio;
    }

    public static int getMax_through() {
        return max_through;
    }
    
    private static final String networkInterface="wlp5s0";
    
    private static final String groupText="224.2.2.3";
    private static final String hostText="localhost";
    private static final String portText="4444";
    private static final String portTextRc="4455";
    private static final String groupVideo="224.3.4.4";
    private static final String hostVideo="localhost";
    private static final String portVideo="6666";
    private static final String portVideoRe="6677";

    public static String getPortTextRc() {
        return portTextRc;
    }

    public static String getPortVideoRe() {
        return portVideoRe;
    }

    public static String getPortAudioRe() {
        return portAudioRe;
    }
    
    private static final String groupAudio="224.3.6.6";
    private static final String hostAudio="localhost";
    private static final String portAudio="7777";
    private static final String portAudioRe="7788";
    
    
    private static final int max_through = 512; // 512 byte

    public static AudioFormat defaultFormat = new AudioFormat(16000f, 8, 1, true, true); //11.025khz, 8bit, mono, signed, big endian (changes nothing in 8 bit) ~8kb/s
    public static int defaultDataLenght = 256; //send 1200 samples/packet by default

    public static AudioFormat getDefaultFormat() {
        return defaultFormat;
    }

    public static void setDefaultFormat(AudioFormat defaultFormat) {
        UDPconfig.defaultFormat = defaultFormat;
    }

    public static int getDefaultDataLenght() {
        return defaultDataLenght;
    }

    public static void setDefaultDataLenght(int defaultDataLenght) {
        UDPconfig.defaultDataLenght = defaultDataLenght;
    }
}
