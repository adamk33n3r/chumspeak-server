#ifndef LISTENERS_H
#define LISTENERS_H

#include <memory>
#include <teamspeak/serverlib.h>
#include <teamspeak/public_definitions.h>

#include "Config.h"

class Listeners {
    public:
    static ServerLibFunctions getFunctionPointers(const std::shared_ptr<Config>& config);
    static void onChannelCreated(uint64 serverId, anyID invokerClientId, uint64 channelId);
    static void onChannelDeleted(uint64 serverId, anyID invokerClientId, uint64 channelId);
    static void onClientConnected(uint64 serverId, anyID clientId, uint64 channelId, unsigned int* removeClientError);
    static void onClientDisconnected(uint64 serverId, anyID clientId, uint64 channelId);
    static void onClientStartTalkingEvent(uint64 serverID, anyID clientID);

    private:
    static std::shared_ptr<Config> config;
};

#endif
