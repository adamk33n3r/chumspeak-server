package com.teamspeak.ts3sdkclient.connection;

/**
 * TeamSpeak 3 sdk client sample
 *
 * Copyright (c) 2007-2017 TeamSpeak-Systems
 *
 * @author Anna
 * Creation date: 10.02.17
 *
 * Required params to establish a server connection
 */
public class ConnectionParams {
    private String mServerAddress = "";
    private int mPort;
    private String mNickname = "";
    private String mIdentity = "";

    public ConnectionParams(String serverAddress, int port, String nickname, String identity) {
        mServerAddress = serverAddress;
        mPort = port;
        mNickname = nickname;
        mIdentity = identity;
    }

    public String getServerAddress() {
        return mServerAddress;
    }

    public int getPort() {
        return mPort;
    }

    public String getNickname() {
        return mNickname;
    }

    public String getIdentity() {
        return mIdentity;
    }
}
