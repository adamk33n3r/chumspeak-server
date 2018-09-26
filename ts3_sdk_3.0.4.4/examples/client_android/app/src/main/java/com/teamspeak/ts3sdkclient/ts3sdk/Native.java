package com.teamspeak.ts3sdkclient.ts3sdk;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.Log;
import android.content.Context;

/**
 * TeamSpeak 3 sdk client sample
 *
 * Copyright (c) 2007-2017 TeamSpeak-Systems
 *
 * @author Alexej
 * Creation date: 08.02.17
 *
 * This class loads the ts3sdk library and the generated ts3client-wrapper-lib.
 * From here you can call the library functions defined in ts3client_wrapper.h
 */

public class Native {

    private static final String TAG = Native.class.getSimpleName();

    private boolean initialized = false;

    public Native(Context applicationContext) {
        final int result = ts3client_startInit(applicationContext);
        if (result == 0) {
            /* Query and print client lib version */
            Log.d(TAG, "SDK Library version: " + ts3client_getClientLibVersion());
        } else {
            Log.e(TAG, "Couldn't initialize client lib.");
        }
        initialized = result == 0;
    }

    public void Dispose() {
        if (initialized) {
            try {
                ts3client_destroyClientLib();
                Log.d(TAG, "ClientLib destroyed");
            } catch (UnsatisfiedLinkError e) {
                // Lib may be gone already on shutdown
            }
            initialized = false;
        }
    }

    /**
     * The native libraries needed to be loaded before they can be used
     */
    public static void loadLibrary() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            System.loadLibrary("libc++_shared.so");  // not autoloaded on API Level 17 (Android 4.2)
        }
        System.loadLibrary("ts3client");
        System.loadLibrary("ts3client-wrapper-lib");
    }

    public boolean isInitialized() { return initialized; }

    private native int ts3client_startInit(Context applicationContext);
    private native int ts3client_destroyClientLib();

    public native int ts3client_getClientID(long serverConnectionHandlerID);

    public native long ts3client_spawnNewServerConnectionHandler();
    public native int ts3client_destroyServerConnectionHandler(long serverConnectionHandlerID);

    public native int ts3client_startConnection(long serverConnectionHandlerID, String identity, String ip, int port, String nickname, String[] channel, String defaultChannelPassword, String serverPassword);
    public native int ts3client_stopConnection(long serverConnectionHandlerID, String message);

    public native String ts3client_createIdentity();
    public native String ts3client_getClientLibVersion();

    public native int ts3client_registerCustomDevice(String deviceID, String deviceDisplayName, int capFrequency, int capChannels, int playFrequency, int playChannels);
    public native int ts3client_unregisterCustomDevice(String deviceID);

    public native int ts3client_openCaptureDevice(long serverConnectionHandlerID, String modeID, String captureDevice);
    public native int ts3client_openPlaybackDevice(long serverConnectionHandlerID, String modeID, String captureDevice);
    public native int ts3client_closeCaptureDevice(long serverConnectionHandlerID);
    public native int ts3client_closePlaybackDevice(long serverConnectionHandlerID);

    public native int ts3client_activateCaptureDevice(long serverConnectionHandlerID);

    public native int ts3client_setPreProcessorConfigValue(long serverConnectionHandlerID, String ident, String value);
    public native String ts3client_getPreProcessorConfigValue(long serverConnectionHandlerID, String ident);

    public native float ts3client_getPlaybackConfigValueAsFloat(long serverConnectionHandlerID, String ident);
    public native int ts3client_setPlaybackConfigValue(long serverConnectionHandlerID, String ident, String value);

    public native String ts3client_getClientVariableAsString(long serverConnectionHandlerID, int clientID, int flag);

    public native String ts3client_getChannelVariableAsString(long serverConnectionHandlerID, long channelID, int flag);

    public enum ConnectionProperties {
        CONNECTION_PING(0),                                             //average latency for a round trip through and back this connection
        CONNECTION_PING_DEVIATION(1),                                  //standard deviation of the above average latency
        CONNECTION_CONNECTED_TIME(2),                                  //how long the connection exists already
        CONNECTION_IDLE_TIME(3),                                       //how long since the last action of this client
        CONNECTION_CLIENT_IP(4),                                       //IP of this client (as seen from the server side)
        CONNECTION_CLIENT_PORT(5),                                     //Port of this client (as seen from the server side)
        CONNECTION_SERVER_IP(6),                                       //IP of the server (seen from the client side) - only available on yourself, not for remote clients, not available server side
        CONNECTION_SERVER_PORT(7),                                     //Port of the server (seen from the client side) - only available on yourself, not for remote clients, not available server side
        CONNECTION_PACKETS_SENT_SPEECH(8),                             //how many Speech packets were sent through this connection
        CONNECTION_PACKETS_SENT_KEEPALIVE(9), CONNECTION_PACKETS_SENT_CONTROL(10), CONNECTION_PACKETS_SENT_TOTAL(11),                              //how many packets were sent totally (this is PACKETS_SENT_SPEECH + PACKETS_SENT_KEEPALIVE + PACKETS_SENT_CONTROL)
        CONNECTION_BYTES_SENT_SPEECH(12), CONNECTION_BYTES_SENT_KEEPALIVE(13), CONNECTION_BYTES_SENT_CONTROL(14), CONNECTION_BYTES_SENT_TOTAL(15), CONNECTION_PACKETS_RECEIVED_SPEECH(16), CONNECTION_PACKETS_RECEIVED_KEEPALIVE(17), CONNECTION_PACKETS_RECEIVED_CONTROL(18), CONNECTION_PACKETS_RECEIVED_TOTAL(19), CONNECTION_BYTES_RECEIVED_SPEECH(20), CONNECTION_BYTES_RECEIVED_KEEPALIVE(21), CONNECTION_BYTES_RECEIVED_CONTROL(22), CONNECTION_BYTES_RECEIVED_TOTAL(23), CONNECTION_PACKETLOSS_SPEECH(24), CONNECTION_PACKETLOSS_KEEPALIVE(25), CONNECTION_PACKETLOSS_CONTROL(26), CONNECTION_PACKETLOSS_TOTAL(27),                                //the probability with which a packet round trip failed because a packet was lost
        CONNECTION_SERVER2CLIENT_PACKETLOSS_SPEECH(28),                 //the probability with which a speech packet failed from the server to the client
        CONNECTION_SERVER2CLIENT_PACKETLOSS_KEEPALIVE(29), CONNECTION_SERVER2CLIENT_PACKETLOSS_CONTROL(30), CONNECTION_SERVER2CLIENT_PACKETLOSS_TOTAL(31), CONNECTION_CLIENT2SERVER_PACKETLOSS_SPEECH(32), CONNECTION_CLIENT2SERVER_PACKETLOSS_KEEPALIVE(33), CONNECTION_CLIENT2SERVER_PACKETLOSS_CONTROL(34), CONNECTION_CLIENT2SERVER_PACKETLOSS_TOTAL(35), CONNECTION_BANDWIDTH_SENT_LAST_SECOND_SPEECH(36),               //howmany bytes of speech packets we sent during the last second
        CONNECTION_BANDWIDTH_SENT_LAST_SECOND_KEEPALIVE(37), CONNECTION_BANDWIDTH_SENT_LAST_SECOND_CONTROL(38), CONNECTION_BANDWIDTH_SENT_LAST_SECOND_TOTAL(39), CONNECTION_BANDWIDTH_SENT_LAST_MINUTE_SPEECH(40),               //howmany bytes/s of speech packets we sent in average during the last minute
        CONNECTION_BANDWIDTH_SENT_LAST_MINUTE_KEEPALIVE(41), CONNECTION_BANDWIDTH_SENT_LAST_MINUTE_CONTROL(42), CONNECTION_BANDWIDTH_SENT_LAST_MINUTE_TOTAL(43), CONNECTION_BANDWIDTH_RECEIVED_LAST_SECOND_SPEECH(44), CONNECTION_BANDWIDTH_RECEIVED_LAST_SECOND_KEEPALIVE(45), CONNECTION_BANDWIDTH_RECEIVED_LAST_SECOND_CONTROL(46), CONNECTION_BANDWIDTH_RECEIVED_LAST_SECOND_TOTAL(47), CONNECTION_BANDWIDTH_RECEIVED_LAST_MINUTE_SPEECH(48), CONNECTION_BANDWIDTH_RECEIVED_LAST_MINUTE_KEEPALIVE(49), CONNECTION_BANDWIDTH_RECEIVED_LAST_MINUTE_CONTROL(50), CONNECTION_BANDWIDTH_RECEIVED_LAST_MINUTE_TOTAL(51), CONNECTION_ENDMARKER(52);

        private int ConnectionProperties;

        ConnectionProperties(int p) {
            ConnectionProperties = p;
        }

        public int getConnectionProperties() {
            return ConnectionProperties;
        }
    }
    public double ts3client_getConnectionVariableAsDouble(long serverConnectionHandlerID, int clientID, ConnectionProperties flag) {
        return ts3client_getConnectionVariableAsDouble(serverConnectionHandlerID, clientID, flag.getConnectionProperties());
    }

    public native double ts3client_getConnectionVariableAsDouble(long serverConnectionHandlerID, int clientID, int flag);
}
