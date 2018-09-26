package com.teamspeak.ts3sdkclient.ts3sdk.events;

import com.teamspeak.ts3sdkclient.eventsystem.Callbacks;
import com.teamspeak.ts3sdkclient.eventsystem.IEvent;
import com.teamspeak.ts3sdkclient.ts3sdk.states.Visibility;

/**
 * TeamSpeak 3 sdk client sample
 *
 * Copyright (c) 2007-2017 TeamSpeak-Systems
 *
 * @author Alexej
 * Creation date: 08.02.17
 *
 *
 * Called when a client joins, leaves or moves to another channel.
 *
 * Parameters:
 *   serverConnectionHandlerID - Server connection handler ID
 *   clientID                  - ID of the moved client
 *   oldChannelID              - ID of the old channel left by the client
 *   newChannelID              - ID of the new channel joined by the client
 *   visibility                - Visibility of the moved client. See the enum Visibility in clientlib_publicdefinitions.h
 *                               Values: ENTER_VISIBILITY, RETAIN_VISIBILITY, LEAVE_VISIBILITY
 *   moveMessage               - When a client disconnects from the server, this includes the optional message set by the disconnecting client in
 *                               ts3client_stopConnection.
 */
public class ClientMove implements IEvent {
    private long serverConnectionHandlerID;
    private int clientID;
    private long oldChannelID;
    private long newChannelID;
    @Visibility
    private int visibility;
    private String moveMessage;

    public ClientMove() {
    }

    public ClientMove(long serverConnectionHandlerID, int clientID, long oldChannelID, long newChannelID, int visibility, String moveMessage) {
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
        this.moveMessage = moveMessage;
        Callbacks.fireEvent(this);
    }

    public int getClientID() {
        return clientID;
    }

    public String getMoveMessage() {
        return moveMessage;
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

    public int getVisibility() {
        return visibility;
    }

    @Override
    public String toString() {
        return "ClientMove [serverConnectionHandlerID=" + serverConnectionHandlerID + ", clientID=" + clientID + ", oldChannelID=" + oldChannelID + ", newChannelID=" + newChannelID + ", visibility=" + visibility + ", moveMessage=" + moveMessage + "]";
    }

}
