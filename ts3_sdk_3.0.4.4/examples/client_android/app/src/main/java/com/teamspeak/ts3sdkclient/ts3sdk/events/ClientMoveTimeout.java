package com.teamspeak.ts3sdkclient.ts3sdk.events;

import com.teamspeak.ts3sdkclient.eventsystem.Callbacks;
import com.teamspeak.ts3sdkclient.eventsystem.IEvent;
import com.teamspeak.ts3sdkclient.ts3sdk.states.Visibility;

/**
 * * TeamSpeak 3 sdk client sample
 *
 * Copyright (c) 2007-2017 TeamSpeak-Systems
 *
 * @author Alexej
 * Creation date: 08.02.17
 *
 * Called when a client drops his connection.
 *
 * Parameters:
 *   serverConnectionHandlerID - Server connection handler ID
 *   clientID                  - ID of the moved client
 *   oldChannelID              - ID of the channel the leaving client was previously member of
 *   newChannelID              - 0, as client is leaving
 *   visibility                - Always LEAVE_VISIBILITY
 *   timeoutMessage            - Optional message giving the reason for the timeout
 */
public class ClientMoveTimeout implements IEvent {

    private long serverConnectionHandlerID;

    private int clientID;
    private long oldChannelID;
    private long newChannelID;
    @Visibility
    private int visibility;
    private String timeoutMessage;

    public ClientMoveTimeout() {

    }

    public ClientMoveTimeout(long serverConnectionHandlerID, int clientID, long oldChannelID, long newChannelID, int visibility, String timeoutMessage) {
        super();
        this.serverConnectionHandlerID = serverConnectionHandlerID;
        this.clientID = clientID;
        this.oldChannelID = oldChannelID;
        this.newChannelID = newChannelID;
        if (visibility == 0)
            this.visibility = Visibility.ENTER_VISIBILITY;
        if (visibility == 1)
            this.visibility = Visibility.RETAIN_VISIBILITY;
        if (visibility == 2)
            this.visibility = Visibility.LEAVE_VISIBILITY;
        this.timeoutMessage = timeoutMessage;
        Callbacks.fireEvent(this);
    }

    public int getClientID() {
        return clientID;
    }

    public long getNewChannelID() {
        return newChannelID;
    }

    public long getOldChannelID() {
        return oldChannelID;
    }

    public long getServerConnectionHandlerID() {
        return serverConnectionHandlerID;
    }

    public String getTimeoutMessage() {
        return timeoutMessage;
    }

    public int getVisibility() {
        return visibility;
    }

    @Override
    public String toString() {
        return "ClientMoveTimeout [serverConnectionHandlerID=" + serverConnectionHandlerID + ", clientID=" + clientID + ", oldChannelID=" + oldChannelID + ", newChannelID=" + newChannelID + ", visibility=" + visibility + ", timeoutMessage=" + timeoutMessage + "]";
    }

}
