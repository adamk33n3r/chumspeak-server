#include "Listeners.h"

#include <iostream>
#include <teamspeak/serverlib.h>
#include <teamspeak/public_errors.h>

#include "Utils.h"

std::shared_ptr<Config> Listeners::config;

ServerLibFunctions Listeners::getFunctionPointers(const std::shared_ptr<Config>& config) {
    Listeners::config = config;
    ServerLibFunctions functionPointers;
    memset(&functionPointers, 0, sizeof(ServerLibFunctions));
    functionPointers.onChannelCreated = Listeners::onChannelCreated;
    functionPointers.onChannelDeleted = Listeners::onChannelDeleted;
    functionPointers.onClientConnected = Listeners::onClientConnected;
    functionPointers.onClientDisconnected = Listeners::onClientDisconnected;
    return functionPointers;
}

void Listeners::onChannelCreated(uint64 serverId, anyID invokerClientId, uint64 channelId) {
    printf("Channel %lu created by %u on virtual server %lu\n", channelId, invokerClientId, serverId);
    // std::cout << "onChannelCreated: " << serverId << invokerClientId << std::endl;
    Config& config = *Listeners::config;

    char* name;
    uint error = ts3server_getChannelVariableAsString(serverId, channelId, CHANNEL_NAME, &name);
    Utils::checkError(error, "Failed to get channel name");
    char* description;
    error = ts3server_getChannelVariableAsString(serverId, channelId, CHANNEL_DESCRIPTION, &description);
    Utils::checkError(error, "Failed to get channel description");
    int permanent;
    error = ts3server_getChannelVariableAsInt(serverId, channelId, CHANNEL_FLAG_PERMANENT, &permanent);
    Utils::checkError(error, "Failed to get channel permanent status");

    // Is permanent and was created by client
    if(permanent == 1 && invokerClientId != 0) {
        config["channels"].push_back({{ "id", channelId }, { "name", name }, { "description", description }});
        config.save();
    }

    ts3server_freeMemory(name);
    ts3server_freeMemory(description);
}

void Listeners::onChannelDeleted(uint64 serverId, anyID invokerClientId, uint64 channelId) {
    printf("Channel %lu deleted by %u on virtual server %lu\n", channelId, invokerClientId, serverId);
    Config& config = *Listeners::config;

    // Deleted by client
    if(invokerClientId != 0) {
        auto& channels = config["channels"];
        for (int i = 0; i < channels.size(); ++i) {
            if (channels[i]["id"] == channelId) {
                channels.erase(i);
                config.save();
                break;
            }
        }
    }
}

void Listeners::onClientConnected(uint64 serverId, anyID clientId, uint64 channelId, unsigned int* removeClientError) {
    char* nickname;
    if (ts3server_getClientVariableAsString(serverId, clientId, CLIENT_NICKNAME, &nickname) == ERROR_ok) {
        printf("Client #%u: %s connected to virtual server %lu\n", clientId, nickname, serverId);
        ts3server_freeMemory(nickname);
    } else {
        printf("%s", "Could not get client's nickname\n");
    }
    char* uid;
    if (ts3server_getClientVariableAsString(serverId, clientId, CLIENT_UNIQUE_IDENTIFIER, &uid) == ERROR_ok) {
        printf("UID: %s\n", uid);
        ts3server_freeMemory(uid);
    }
    // printf("%s\n", config->dump().c_str());
}

void Listeners::onClientDisconnected(uint64 serverId, anyID clientId, uint64 channelId) {
    char* nickname;
    if (ts3server_getClientVariableAsString(serverId, clientId, CLIENT_NICKNAME, &nickname) == ERROR_ok) {
        printf("Client #%u: %s disconnected from virtual server %lu\n", clientId, nickname, serverId);
        ts3server_freeMemory(nickname);
    } else {
        printf("%s", "Could not get client's nickname\n");
    }
    char* uid;
    if (ts3server_getClientVariableAsString(serverId, clientId, CLIENT_UNIQUE_IDENTIFIER, &uid) == ERROR_ok) {
        printf("UID: %s\n", uid);
        ts3server_freeMemory(uid);
    }
    // printf("%s\n", config->dump().c_str());
}
