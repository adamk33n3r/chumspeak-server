package com.teamspeak.ts3sdkclient.ts3sdk.events;

import com.teamspeak.ts3sdkclient.ts3sdk.states.ConnectStatus;
import com.teamspeak.ts3sdkclient.eventsystem.IEvent;
import com.teamspeak.ts3sdkclient.eventsystem.Callbacks;
import com.teamspeak.ts3sdkclient.ts3sdk.states.PublicError;


/** TeamSpeak 3 sdk client sample
 *
 * Copyright (c) 2007-2017 TeamSpeak-Systems
 *
 * @author Alexej
 * Creation date: 08.02.17
 *
 *
 * Callback for connection status change.
 * Connection status switches through the states STATUS_DISCONNECTED, STATUS_CONNECTING, STATUS_CONNECTED and STATUS_CONNECTION_ESTABLISHED.
 *
 * Parameters:
 *   serverConnectionHandlerID - Server connection handler ID
 *   newStatus                 - New connection status, see the enum ConnectStatus in clientlib_publicdefinitions.h
 *   errorNumber               - Error code. Should be zero when connecting or actively disconnection.
 *                               Contains error state when losing connection.
 */
public class ConnectStatusChange implements IEvent {

    private long serverConnectionHandlerID;
    private int newStatus;
    private int errorNumber;

    public ConnectStatusChange() {
    }

    public ConnectStatusChange(long serverConnectionHandlerID, int newStatus, int errorNumber) {
        super();
        this.serverConnectionHandlerID = serverConnectionHandlerID;
        this.newStatus = newStatus;
        this.errorNumber = errorNumber;
        Callbacks.fireEvent(this);
    }

    public int getErrorNumber() {
        return errorNumber;
    }

    public int getNewStatus() {
        return newStatus;
    }

    public long getServerConnectionHandlerID() {
        return serverConnectionHandlerID;
    }

    @Override
    public String toString() {
        String status = "";

        switch (newStatus){
            case ConnectStatus.STATUS_DISCONNECTED:
                status = "STATUS_DISCONNECTED";
                break;
            case ConnectStatus.STATUS_CONNECTING:
                status = "STATUS_CONNECTING";
                break;
            case ConnectStatus.STATUS_CONNECTED:
                status = "STATUS_CONNECTED";
                break;
            case ConnectStatus.STATUS_CONNECTION_ESTABLISHING:
                status = "STATUS_CONNECTION_ESTABLISHING";
                break;
            case ConnectStatus.STATUS_CONNECTION_ESTABLISHED:
                status = "STATUS_CONNECTION_ESTABLISHED";
                break;
        }


        return "ConnectStatusChange [serverConnectionHandlerID= " + serverConnectionHandlerID
                + ", newStatus=" + status + ", errorNumber=" + PublicError.getPublicErrorString(errorNumber) + "]";
    }

}
