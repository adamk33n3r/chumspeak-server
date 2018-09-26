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
 * Callback when a channel was deleted.
 *
 * Parameters:
 *   serverConnectionHandlerID - Server connection handler ID
 *   channelID                 - ID of the deleted channel
 *   invokerID                 - ID of the client who deleted the channel
 *   invokerName               - Name of the client who deleted the channel
 *   invokerUniqueIdentifier   - The unique ID of the client who requested the deletion.
 */
public class DelChannel implements IEvent {

    private long serverConnectionHandlerID;

    private long channelID;
    private int invokerID;
    private String invokerName;
    private String invokerUniqueIdentifier;

    public DelChannel() {
    }

    public DelChannel(long serverConnectionHandlerID, long channelID, int invokerID, String invokerName, String invokerUniqueIdentifier) {
        super();
        this.serverConnectionHandlerID = serverConnectionHandlerID;
        this.channelID = channelID;
        this.invokerID = invokerID;
        this.invokerName = invokerName;
        this.invokerUniqueIdentifier = invokerUniqueIdentifier;
        Callbacks.fireEvent(this);
    }

    public long getChannelID() {
        return channelID;
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
        return "DelChannel [serverConnectionHandlerID=" + serverConnectionHandlerID + ", channelID=" + channelID + ", invokerID=" + invokerID + ", invokerName=" + invokerName + ", invokerUniqueIdentifier=" + invokerUniqueIdentifier + "]";
    }

}
