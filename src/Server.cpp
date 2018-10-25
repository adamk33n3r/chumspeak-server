#include "Server.h"

#include <stdexcept>
#include <iostream>
#include <string>
#include <teamspeak/serverlib.h>
#include <teamspeak/public_errors.h>

#include "Utils.h"
#include "Log.h"

Server::Server(std::shared_ptr<Config> config): config(config), serverId(1) {
}

uint64 Server::start() {
    Config &config = *this->config;

    unsigned int error = 0;
    unsigned int maxClients = 32;
    unsigned int channelCount = 0;

    std::string keyPairStr = config.value("keyPair", "");
    if(config["channels"].is_array()) {
        channelCount = config["channels"].size();
    }
    struct TS3VirtualServerCreationParams* vscp;
    struct TS3ChannelCreationParams*       ccp;
    struct TS3Variables*                   vars;
    LOG("channelCount " << channelCount);

    try {

        error = ts3server_makeVirtualServerCreationParams(&vscp);
        Utils::checkError(error, "Failed to make virtual server creation params");

        error = ts3server_setVirtualServerCreationParams(
            vscp,
            9987,
            NULL,
            keyPairStr.c_str(),
            maxClients,
            channelCount,
            serverId
        );
        Utils::checkError(error, "Failed to set virtual server creation params");

        for(int index = 0; index < channelCount; ++index) {
            auto channel = config["channels"][index];
            int id = channel["id"];
            std::string name = channel["name"].get<std::string>();
            std::string description = channel["description"].get<std::string>();
            struct TS3Variables* channelVars;

            error = ts3server_getVirtualServerCreationParamsChannelCreationParams(vscp, index, &ccp);
            Utils::checkError(error, "Failed to get virtual server creation params channel creation params");

            error = ts3server_setChannelCreationParams(ccp, 0, id);
            Utils::checkError(error, "Failed to set channel creation params");

            error = ts3server_getChannelCreationParamsVariables(ccp, &channelVars);
            Utils::checkError(error, "Failed to get channel creation params variables");

            error = ts3server_setVariableAsString(channelVars, CHANNEL_NAME, name.c_str());
            Utils::checkError(error, "Failed to set channel name");

            error = ts3server_setVariableAsString(channelVars, CHANNEL_DESCRIPTION, description.c_str());
            Utils::checkError(error, "Failed to set channel description");
            
            if(index == 0) {
                error = ts3server_setVariableAsInt(channelVars, CHANNEL_FLAG_DEFAULT, 1);
                Utils::checkError(error, "Failed to set channel flag default");
                error = ts3server_setVariableAsInt(channelVars, CHANNEL_CODEC, CODEC_SPEEX_NARROWBAND);
                Utils::checkError(error, "Failed to set channel codec");
            }

            error = ts3server_setVariableAsInt(channelVars, CHANNEL_FLAG_PERMANENT, 1);
            Utils::checkError(error, "Failed to set channel flag permanent");
        }

        error = ts3server_createVirtualServer2(vscp, VIRTUALSERVER_CREATE_FLAG_NONE, &serverId);
        Utils::checkError(error, "Failed to create virtual server 2");

        char* keyPair;
        error = ts3server_getVirtualServerKeyPair(serverId, &keyPair);
        Utils::checkError(error, "Failed to get virtual server key pair");

        config["keyPair"] = keyPair;
        config.save();

        ts3server_freeMemory(vscp);
        return serverId;
    } catch (const std::runtime_error &error) {
        std::cerr << error.what() << std::endl;
        throw;
    }

}

void Server::stop() const {
    Utils::checkError(ts3server_stopVirtualServer(serverId), "Failed to stop server");
}

std::vector<Channel> Server::getChannels() const {
    uint64* ids;
    unsigned int error;
    error = ts3server_getChannelList(serverId, &ids);
    Utils::checkError(error, "Failed to retrieve channel list");

    std::vector<Channel> list;
    for(int i = 0; ids[i]; ++i) {
        uint64 cid = ids[i];
		char* name;
        char* description;
        error = ts3server_getChannelVariableAsString(serverId, cid, CHANNEL_NAME, &name);
        Utils::checkError(error, "Failed to retrieve channel name");
        error = ts3server_getChannelVariableAsString(serverId, cid, CHANNEL_DESCRIPTION, &description);
        Utils::checkError(error, "Failed to retrieve channel description");

        list.push_back({ cid, name, description });
    }
    ts3server_freeMemory(ids);
    return list;
}
