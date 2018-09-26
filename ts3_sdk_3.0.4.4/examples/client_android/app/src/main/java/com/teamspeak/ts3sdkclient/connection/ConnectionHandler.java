package com.teamspeak.ts3sdkclient.connection;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

import com.teamspeak.ts3sdkclient.TS3Application;
import com.teamspeak.ts3sdkclient.helper.Info;
import com.teamspeak.ts3sdkclient.ts3sdk.Native;
import com.teamspeak.ts3sdkclient.ts3sdk.states.ConnectStatus;

import static com.teamspeak.ts3sdkclient.Constants.PRE_PROCESSOR_VALUE_AGC;
import static com.teamspeak.ts3sdkclient.Constants.PRE_PROCESSOR_VALUE_DENOISE;
import static com.teamspeak.ts3sdkclient.Constants.PRE_PROCESSOR_VALUE_VOICE_ACTIVATION_LEVEL_DB;
import static com.teamspeak.ts3sdkclient.Constants.TS3_DEFAULT_SERVERPORT;
import static com.teamspeak.ts3sdkclient.ts3sdk.states.PublicError.ERROR_ok;

/**
 * TeamSpeak 3 sdk client sample
 *
 * Copyright (c) 2007-2017 TeamSpeak-Systems
 *
 * @author Anna
 * Creation date: 08.02.17
 *
 * Class that holds all data for one server connection
 */
public class ConnectionHandler {

    public static final String TAG = ConnectionHandler.class.getSimpleName();
    
    private long serverConnectionHandlerId;
    private ConnectionParams mParams;
    private Info mInfo;
    private TS3Application application;
    private Native nativeInterface;


    @ConnectStatus
    private int currentState = ConnectStatus.STATUS_DISCONNECTED;
    private boolean captureDeviceOpen;
    private boolean playbackDeviceOpen;

    public ConnectionHandler(TS3Application app) {
        mInfo = Info.getInstance();
        application = app;
        nativeInterface = app.getNativeInstance();

        serverConnectionHandlerId = nativeInterface.ts3client_spawnNewServerConnectionHandler();
    }

    public int startConnection(ConnectionParams params) {
        mParams = params;

        prepareAudio();

        String ip = params.getServerAddress();
        int port = params.getPort() != 0 ? params.getPort() : TS3_DEFAULT_SERVERPORT;
        String nickname = params.getNickname();
        String defaultChannelPassword = "";
        String serverPassword = "secret";
        String[] defaultChannelArray = new String[]{""};

        //Connect to server on the given ip address with the given nickname, no default channel, no default channel password and server password "secret"
        return nativeInterface.ts3client_startConnection(serverConnectionHandlerId, params.getIdentity(), ip, port, nickname,
                defaultChannelArray, defaultChannelPassword, serverPassword);
    }

    /**
     * Stop the actual server connection and destroys the server connection handler in the teamspeak library
     */
    public void disconnect(){
         //Disconnect from server
        nativeInterface.ts3client_stopConnection(serverConnectionHandlerId, "leaving");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Destroy server connection handler
                nativeInterface.ts3client_destroyServerConnectionHandler(serverConnectionHandlerId);
            }
        }, 200);
    }


    public void configurePreProcessor(){
        // Set the speech preprocessor values
        nativeInterface.ts3client_setPreProcessorConfigValue(serverConnectionHandlerId, PRE_PROCESSOR_VALUE_VOICE_ACTIVATION_LEVEL_DB, "-30");
        nativeInterface.ts3client_setPreProcessorConfigValue(serverConnectionHandlerId, PRE_PROCESSOR_VALUE_DENOISE, "true");
        nativeInterface.ts3client_setPreProcessorConfigValue(serverConnectionHandlerId, PRE_PROCESSOR_VALUE_AGC, "true");
    }

    /**
     * Routes the audio to the earpiece speaker and sets the audio mode MODE_IN_COMMUNICATION for voice communication and
     * echo cancellation if supported by the device
     */
    private void prepareAudio() {

        AudioManager audioManager = (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);

        if (audioManager != null) {
            audioManager.setSpeakerphoneOn(false);

            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        }
    }

    /**
     * registers an audio device if no audio device has been registered before and opens the capture and playback device.
     */
    public void openAudioDevices() {

        if (!captureDeviceOpen)
        {
            Log.d(TAG, "Opening capture device for server connection handler " + serverConnectionHandlerId);
            // Open capture device we created earlier

            captureDeviceOpen = nativeInterface.ts3client_openCaptureDevice(serverConnectionHandlerId, "", "")
                    == ERROR_ok;
        }

        if (!playbackDeviceOpen)
        {
            //Open playback device we created earlier
            Log.d(TAG, "Opening playback device for server connection handler " + serverConnectionHandlerId);

            playbackDeviceOpen = nativeInterface.ts3client_openPlaybackDevice(serverConnectionHandlerId, "", "")
                    == ERROR_ok;
        }
    }

    /**
     * closes the capture and playback device and unregisters the audio device if a audio device has been registered before.
     * Destroys the native audio Engine, audio player and audio recorder
     */
    public void closeAudioDevices() {

        if (captureDeviceOpen)
        {
            Log.d(TAG, "Closing capture device for server connection handler "+serverConnectionHandlerId );
            /* Close capture device we created earlier */
            if(nativeInterface.ts3client_closeCaptureDevice(serverConnectionHandlerId) == ERROR_ok){
                captureDeviceOpen = false;
            }
        }

        if (playbackDeviceOpen)
        {
            Log.d(TAG, "Closing playback device for server connection handler "+serverConnectionHandlerId );
            /* Close playback device we created earlier */

            if(nativeInterface.ts3client_closePlaybackDevice(serverConnectionHandlerId) == ERROR_ok){
                playbackDeviceOpen = false;
            }
        }
    }

    public long getServerConnectionHandlerId() {
        return serverConnectionHandlerId;
    }

    @ConnectStatus
    public int getCurrentState() {
        return currentState;
    }

    public boolean isCaptureDeviceOpen() {
        return captureDeviceOpen;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public ConnectionParams getParams() {
        return mParams;
    }

    public void printConnectionInfo() {
        int myID = nativeInterface.ts3client_getClientID(serverConnectionHandlerId);
        if (myID == -1)
            return;

        double ping = nativeInterface.ts3client_getConnectionVariableAsDouble(serverConnectionHandlerId, myID, Native.ConnectionProperties.CONNECTION_PING);
        double pingDeviation = nativeInterface.ts3client_getConnectionVariableAsDouble(serverConnectionHandlerId, myID, Native.ConnectionProperties.CONNECTION_PING_DEVIATION);
        double totalServerToClientPacketLoss = nativeInterface.ts3client_getConnectionVariableAsDouble(serverConnectionHandlerId, myID, Native.ConnectionProperties.CONNECTION_SERVER2CLIENT_PACKETLOSS_TOTAL);
        String message = "Connection Info -";
        message += " Ping: " + (new Double(ping).toString());
        message += " +/- " + (new Double(pingDeviation).toString());
        message += " Total Server To Client Packetloss: " + (new Double(totalServerToClientPacketLoss).toString());
        mInfo.addMessage(message, Color.parseColor("#4169E1"));
    }
}
