#include "Listeners.h"

#include <iostream>
#include <teamspeak/serverlib.h>

std::shared_ptr<Config> Listeners::config;

bool checkError(unsigned int error, const char* msg);

ServerLibFunctions Listeners::getFunctionPointers(const std::shared_ptr<Config>& config) {
    Listeners::config = config;
    ServerLibFunctions functionPointers;
    memset(&functionPointers, 0, sizeof(ServerLibFunctions));
    functionPointers.onChannelCreated = Listeners::onChannelCreated;
    functionPointers.onChannelDeleted = Listeners::onChannelDeleted;
    functionPointers.onClientConnected = Listeners::onClientConnected;
    return functionPointers;
}

void Listeners::onChannelCreated(uint64 serverId, anyID invokerClientId, uint64 channelId) {
    printf("Channel %llu created by %u on virtual server %llu\n", channelId, invokerClientId, serverId);
    // std::cout << "onChannelCreated: " << serverId << invokerClientId << std::endl;
    Config& config = *Listeners::config;

    char* name;
    unsigned int error = ts3server_getChannelVariableAsString(serverId, channelId, CHANNEL_NAME, &name);
    checkError(error, "Failed to get channel name");

    // Created by client
    if(invokerClientId != 0) {
        config["channels"].push_back({{ "id", channelId }, { "name", name }});
        config.save();
    }
}

void Listeners::onChannelDeleted(uint64 serverId, anyID invokerClientId, uint64 channelId) {
    printf("Channel %llu deleted by %u on virtual server %llu\n", (unsigned long long)channelId, invokerClientId, (unsigned long long)serverId);
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
    printf("Client %llu connected to virtual server %llu\n", clientId, serverId);
    printf("%s\n", config->dump().c_str());
}
