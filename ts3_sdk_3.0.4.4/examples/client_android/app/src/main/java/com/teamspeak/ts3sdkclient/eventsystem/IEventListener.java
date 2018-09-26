package com.teamspeak.ts3sdkclient.eventsystem;

/**
 * TeamSpeak 3 sdk client sample
 *
 * Copyright (c) 2007-2017 TeamSpeak-Systems
 *
 * @author Anna
 * Creation date: 08.02.17
 *
 * Eventmethod Interface for ts3clientlibrary events
 * All classes that want to receive ts3client library events have to implement this Interface
 */
public interface IEventListener {
    void onTS3Event(IEvent event);
}
