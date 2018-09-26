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
 * Callback for just created channels.
 *
 * Parameters:
 *   serverConnectionHandlerID - Server connection handler ID
 *   channelID                 - ID of the announced channel
 *   channelParentID           - ID of the parent channel
 *   invokerID                 - ID of the client who created the channel
 *   invokerName               - Name of the client who created the channel
 *   invokerUniqueIdentifier   - Unique ID of the client who requested the creation.
 */
public class NewChannelCreated implements IEvent {

    private long serverConnectionHandlerID;
    private long channelID;
    private long channelParentID;
    private int invokerID;
    private String invokerName;
    private String invokerUniqueIdentifier;

    public NewChannelCreated() {
    }

    public NewChannelCreated(long serverConnectionHandlerID, long channelID, long channelParentID, int invokerID, String invokerName, String invokerUniqueIdentifier) {
        super();
        this.serverConnectionHandlerID = serverConnectionHandlerID;
        this.channelID = channelID;
        this.channelParentID = channelParentID;
        this.invokerID = invokerID;
        this.invokerName = invokerName;
        this.invokerUniqueIdentifier = invokerUniqueIdentifier;
        Callbacks.fireEvent(this);
    }

    public long getChannelID() {
        return channelID;
    }

    public long getChannelParentID() {
        return channelParentID;
    }

    public int getInvokerID() {
        return invokerID;
    }

    public String getInvokerName() {
        return invokerName;
    }

    public String getInvokerUniqueIdentifier() {
        return invokerUniqueIdentifier;
    }

    public long getServerConnectionHandlerID() {
        return serverConnectionHandlerID;
    }

    @Override
    public String toString() {
        return "NewChannelCreated [serverConnectionHandlerID=" + serverConnectionHandlerID + ", channelID=" + channelID + ", channelParentID=" + channelParentID + ", invokerID=" + invokerID + ", invokerName=" + invokerName + ", invokerUniqueIdentifier=" + invokerUniqueIdentifier + "]";
    }

}
