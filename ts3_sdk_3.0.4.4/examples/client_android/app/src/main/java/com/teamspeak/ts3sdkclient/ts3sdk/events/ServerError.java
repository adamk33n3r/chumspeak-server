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
 * This event is called when a the sever sends an error message to the client.
 *
 * Parameters:
 *   serverConnectionHandlerID - The connection handler ID of the server who sent the error event
 *   errorMessage              - String containing a verbose error message, encoded in UTF-8 format
 *   error                     - Error code as defined in public_errors.h
 *   returnCode                - String containing the return code if it has been set by the Client Lib function call which caused this error event. See return code documentation in the sdk description document
 *   extraMessage              - Can contain additional information about the occured error. If no additional information is available, this parameter is an empty string
 */
public class ServerError implements IEvent {

    private long serverConnectionHandlerID;

    private String errorMessage;
    private int error;
    private String returnCode;
    private String extraMessage;

    public ServerError() {
    }

    public ServerError(long serverConnectionHandlerID, String errorMessage, int error, String returnCode, String extraMessage) {
        super();
        this.serverConnectionHandlerID = serverConnectionHandlerID;
        this.errorMessage = errorMessage;
        this.error = error;
        this.returnCode = returnCode;
        this.extraMessage = extraMessage;
        Callbacks.fireEvent(this);
    }

    public int getError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getExtraMessage() {
        return extraMessage;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public long getServerConnectionHandlerID() {
        return serverConnectionHandlerID;
    }

    @Override
    public String toString() {
        return "ServerError [serverConnectionHandlerID=" + serverConnectionHandlerID + ", errorMessage=" + errorMessage + ", error=" + error + ", returnCode=" + returnCode + ", extraMessage=" + extraMessage + "]";
    }
}
