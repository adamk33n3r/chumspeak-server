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
 * Creation date: 16.02.17
 *
 * Will be called if other clients in current and subscribed channels being announced to the client.
 *
 * Parameters:
 *   serverConnectionHandlerID - Server connection handler ID
 *   clientID                  - ID of the announced client
 *   oldChannelID              - ID of the subscribed channel where the client left visibility
 *   newChannelID              - ID of the subscribed channel where the client entered visibility
 *   visibility                - Visibility of the announced client. See the enum Visibility in clientlib_publicdefinitions.h
 *                               Values: ENTER_VISIBILITY, RETAIN_VISIBILITY, LEAVE_VISIBILITY
 */
public class ClientMoveSubscription implements IEvent {

    private long serverConnectionHandlerID;

    private int clientID;
    private long oldChannelID;
    private long newChannelID;
    @Visibility
    private int visibility;

    public ClientMoveSubscription() {
    }

    public ClientMoveSubscription(long serverConnectionHandlerID, int clientID, long oldChannelID, long newChannelID, int visibility) {
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

    public int getVisibility() {
        return visibility;
    }

    @Override
    public String toString() {
        return "ClientMoveSubscription [serverConnectionHandlerID=" + serverConnectionHandlerID + ", clientID=" + clientID + ", oldChannelID=" + oldChannelID + ", newChannelID=" + newChannelID + ", visibility=" + visibility + "]";
    }

}
