package com.teamspeak.ts3sdkclient.ts3sdk.events;

import com.teamspeak.ts3sdkclient.eventsystem.Callbacks;
import com.teamspeak.ts3sdkclient.eventsystem.IEvent;

/**
 * TeamSpeak 3 sdk client sample
 *
 * Copyright (c) 2007-2017 TeamSpeak-Systems
 *
 * @author Alexej
 * Creation date: 08.02.17
 *
 * Callback for current channels being announced to the client after connecting to a server.
 *
 * Parameters:
 *   serverConnectionHandlerID - Server connection handler ID
 *   channelID                 - ID of the announced channel
 *   channelParentID           - ID of the parent channel
 */
public class NewChannel implements IEvent {

    private long serverConnectionHandlerID;
    private long channelID;
    private long channelParentID;

    public NewChannel() {
    }

    public NewChannel(long serverConnectionHandlerID, long channelID, long channelParentID) {
        super();
        this.serverConnectionHandlerID = serverConnectionHandlerID;
        this.channelID = channelID;
        this.channelParentID = channelParentID;
        Callbacks.fireEvent(this);
    }

    public long getChannelID() {
        return channelID;
    }

    public long getChannelParentID() {
        return channelParentID;
    }

    public long getServerConnectionHandlerID() {
        return serverConnectionHandlerID;
    }

    @Override
    public String toString() {
        return "NewChannel [serverConnectionHandlerID=" + serverConnectionHandlerID + ", channelID=" + channelID + ", channelParentID=" + channelParentID + "]";
    }

}
