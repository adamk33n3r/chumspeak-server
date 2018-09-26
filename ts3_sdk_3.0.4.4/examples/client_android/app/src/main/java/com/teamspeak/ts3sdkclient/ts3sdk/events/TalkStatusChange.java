package com.teamspeak.ts3sdkclient.ts3sdk.events;

import com.teamspeak.ts3sdkclient.eventsystem.Callbacks;
import com.teamspeak.ts3sdkclient.eventsystem.IEvent;
import com.teamspeak.ts3sdkclient.ts3sdk.states.TalkStatus;

/**
 * TeamSpeak 3 sdk client sample
 *
 * Copyright (c) 2007-2017 TeamSpeak-Systems
 *
 * @author Alexej
 * Creation date: 08.02.17
 *
 * This event is called when a client starts or stops talking.
 *
 * Parameters:
 *   serverConnectionHandlerID - Server connection handler ID
 *   status                    - 1 if client starts talking, 0 if client stops talking
 *   isReceivedWhisper         - 1 if this event was caused by whispering, 0 if caused by normal talking
 *   clientID                  - ID of the client who announced the talk status change
 */
public class TalkStatusChange implements IEvent {

    private long serverConnectionHandlerID;

    private int status;
    private int isReceivedWhisper;
    private int clientID;

    public TalkStatusChange() {
    }

    public TalkStatusChange(long serverConnectionHandlerID, int status, int isReceivedWhisper, int clientID) {
        super();
        this.serverConnectionHandlerID = serverConnectionHandlerID;
        this.status = status;
        this.isReceivedWhisper = isReceivedWhisper;
        this.clientID = clientID;
        Callbacks.fireEvent(this);
    }

    public int getClientID() {
        return clientID;
    }

    public int getIsReceivedWhisper() {
        return isReceivedWhisper;
    }

    public long getServerConnectionHandlerID() {
        return serverConnectionHandlerID;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        String status = "";

        switch (this.status){
            case TalkStatus.STATUS_NOT_TALKING:
                status = "STATUS_NOT_TALKING";
                break;
            case TalkStatus.STATUS_TALKING:
                status = "STATUS_TALKING";
                break;
            case TalkStatus.STATUS_TALKING_WHILE_DISABLED:
                status = "STATUS_TALKING_WHILE_DISABLED";
                break;
        }
        return "TalkStatusChange [serverConnectionHandlerID=" + serverConnectionHandlerID + ", status=" + status + ", isReceivedWhisper=" + isReceivedWhisper + ", clientID=" + clientID + "]";
    }

}
