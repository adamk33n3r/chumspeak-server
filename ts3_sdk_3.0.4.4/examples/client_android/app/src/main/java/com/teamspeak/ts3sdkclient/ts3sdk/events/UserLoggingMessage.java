package com.teamspeak.ts3sdkclient.ts3sdk.events;


import com.teamspeak.ts3sdkclient.eventsystem.IEvent;
import com.teamspeak.ts3sdkclient.eventsystem.Callbacks;

/**
 * TeamSpeak 3 sdk client sample
 *
 * Copyright (c) 2007-2017 TeamSpeak-Systems
 *
 * @author Alexej
 * Creation date: 08.02.17
 *
 * Called if user-defined logging was enabled when initialzing the Client Lib. Allows user to customize logging and handling of critical errors:
 *
 * Parameters:
 *   logMessage                - Actual log message text
 *   logLevel                  - Severity of log message, defined by the enum LogLevel. Note that only log messages of a level higher than the one config- ured with ts3client_setLogVerbosity will appear
 *   logChannel                - Optional custom text to categorize the message channel
 *   logID                     - Server connection handler ID identifying the current server connection when using multiple connections
 *   logTime                   - String with date and time when the log message occured
 *   completeLogString         - Provides a verbose log message including all previous parameters for convinience
 */
public class UserLoggingMessage implements IEvent {

    private String logMessage;

    private int logLevel;
    private String logChannel;
    private long logID;
    private String logTime;
    private String completeLogString;

    public UserLoggingMessage() {
    }

    public UserLoggingMessage(String logMessage, int logLevel, String logChannel, long logID, String logTime, String completeLogString) {
        super();
        this.logMessage = logMessage;
        this.logLevel = logLevel;
        this.logChannel = logChannel;
        this.logID = logID;
        this.logTime = logTime;
        this.completeLogString = completeLogString;
        Callbacks.fireEvent(this);
   }

    public String getCompleteLogString() {
        return completeLogString;
    }

    public String getLogChannel() {
        return logChannel;
    }

    public long getLogID() {
        return logID;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public String getLogTime() {
        return logTime;
    }

    @Override
    public String toString() {
        return "UserLoggingMessage [logMessage=" + logMessage + ", logLevel=" + logLevel + ", logChannel=" + logChannel + ", logID=" + logID + ", logTime=" + logTime + ", completeLogString=" + completeLogString + "]";
    }

}
