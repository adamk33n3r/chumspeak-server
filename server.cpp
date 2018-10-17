#include <iostream>
#include <fstream>
#include <cstring>
#include <string>
#include <iomanip>
#include <teamspeak/serverlib.h>
#include <teamspeak/public_errors.h>
#include <json.hpp>

#define LOG(x) std::cout << x << std::endl

using nlohmann::json;

//globals
json config;

bool check_error(unsigned int error);
unsigned int createServer(json &config);
bool saveConfig(const json &config);
bool loadConfig(json &config);
void onChannelCreated(uint64 serverId, anyID invokerClientId, uint64 channelId) {
    unsigned int error = 0;
    char* name;
    std::string name_str;
    //TODO add channel to config and save if invokerClientID != 0
	printf("Channel %llu created by %u on virtual server %llu\n", (unsigned long long)channelId, invokerClientId, (unsigned long long)serverId);
    error = ts3server_getChannelVariableAsString(config["serverId"], channelId, CHANNEL_NAME, &name);
    if(check_error(error)) {
        sprintf(name, "channel_%llu", (unsigned long long)channelId);
        LOG("Failed to get channel variable as string");
    }
    name_str = name;
    if(invokerClientId != 0) {
        config["channels"].push_back({{"id", channelId}, {"name", name_str}});
    }
    LOG(name_str);
}

void onChannelDeleted(uint64 serverId, anyID invokerClientId, uint64 channelId) {
    //TODO remove channel to config and save if invokerClientId != 0
	printf("Channel %llu deleted by %u on virtual server %llu\n", (unsigned long long)channelId, invokerClientId, (unsigned long long)serverId);
}


int main() {
    unsigned int error = 0;
    char c = '\0';
    bool abort = false;
    if(!loadConfig(config)) {
        LOG("Failed to get config");
    }

    ServerLibFunctions functionPointers;
    memset(&functionPointers, 0, sizeof(ServerLibFunctions));
    functionPointers.onChannelCreated = onChannelCreated;
    error = ts3server_initServerLib(&functionPointers, LogType_FILE | LogType_CONSOLE | LogType_USERLOGGING, NULL);
    if(check_error(error)) return error;
    
    error = ts3server_enableFileManager("uploads", NULL, 30033, BANDWIDTH_LIMIT_UNLIMITED, BANDWIDTH_LIMIT_UNLIMITED);
    if(check_error(error)) return error;

    createServer(config);

    while(!abort) {
        c = getchar();
        switch(c) {
        case 'q':
            abort = true;
            break;
        case 'c':
            uint64* ids;
            error = ts3server_getChannelList(config["serverId"], &ids);
            if(check_error(error)) return error;
            for(int i = 0; ids[i]; ++i) {
                std::cout << ids[i] << ", ";
            }
            std::cout << std::endl;
            ts3server_freeMemory(ids);
            break;
        case 'v':
            char* result;
            error = ts3server_getServerLibVersion(&result);
            if(check_error(error)) return error;
            std::cout << result << std::endl;
            ts3server_freeMemory(result);
            break;
        default:
            //defualt
            std::cout << "invalid command " << c << std::endl;
        }
    }

    error = ts3server_stopVirtualServer(config["serverId"]);
    if(check_error(error)) return error;

    error = ts3server_destroyServerLib();
    if(check_error(error)) return error;
    return 0;
}

bool check_error(unsigned int error) {
    if(error != ERROR_ok) {
        char* errorMsg;
        if(ts3server_getGlobalErrorMessage(error, &errorMsg) == ERROR_ok) {
            std::cout << "Error: '" << errorMsg << "'" << std::endl;
            ts3server_freeMemory(errorMsg);
        }
        return true;
    }
    return false;
}

unsigned int createServer(json &config) {
    unsigned int error = 0;
    unsigned int maxClients = 32;
    unsigned int port = 9987;
    const char* serverIp = "0.0.0.0";
    unsigned int channelCount = 0;
    uint64 serverId = 1;
    std::string keyPairStr = config["keyPair"].get<std::string>();
    const char* keyPairConst = keyPairStr.c_str();
    char keyPairBuf[BUFSIZ];
    strcpy(keyPairBuf, keyPairConst);
    char* keyPair = keyPairBuf;
    if(config["channels"].is_array()) {
        channelCount = config["channels"].size();
    }
    struct TS3VirtualServerCreationParams* vscp;
    struct TS3ChannelCreationParams*       ccp;
    struct TS3Variables*                   vars;
    LOG("channelCount " << channelCount);

    error = ts3server_makeVirtualServerCreationParams(&vscp);
    if(check_error(error)) return error;

    error = ts3server_setVirtualServerCreationParams(vscp, port, serverIp, keyPair, maxClients, channelCount, serverId);
    if(check_error(error)) {
        ts3server_freeMemory(vscp);
        LOG("Failed to set virtual server creation params");
        return error;
    }

    for(int index = 0; index < channelCount; ++index) {
        int parentId = 0;
        int id = config["channels"][index]["id"];
        const char* name = config["channels"][index]["name"].get<std::string>().c_str();
        struct TS3Variables* channelVars;
        LOG("index " << index);
        LOG("name " << name);
        LOG("id " << id);

        error = ts3server_getVirtualServerCreationParamsChannelCreationParams(vscp, index, &ccp);
        if(check_error(error)) {
            ts3server_freeMemory(vscp);
            LOG("Failed to get virtual server creation params channel creation params");
            return error;
        }

        error = ts3server_setChannelCreationParams(ccp, parentId, id);
        if(check_error(error)) {
            ts3server_freeMemory(vscp);
            LOG("Failed to set channel creation params");
            return error;
        }

        error = ts3server_getChannelCreationParamsVariables(ccp, &channelVars);
        if(check_error(error)) {
            ts3server_freeMemory(vscp);
            LOG("Failed to get channel creation params variables");
            return error;
        }

        error = ts3server_setVariableAsString(channelVars, CHANNEL_NAME, name);
        if(check_error(error)) {
            ts3server_freeMemory(vscp);
            LOG("Failed to set channel name");
            return error;
        }
        
        if(index == 0) {
            error = ts3server_setVariableAsInt(channelVars, CHANNEL_FLAG_DEFAULT, 1);
            if(check_error(error)) {
                ts3server_freeMemory(vscp);
                LOG("Failed to set channel flag default");
                return error;
            }
        }

        error = ts3server_setVariableAsInt(channelVars, CHANNEL_FLAG_PERMANENT, 1);
        if(check_error(error)) {
            ts3server_freeMemory(vscp);
            LOG("Failed to set channel flag permanent");
            return error;
        }
    }

    error = ts3server_createVirtualServer2(vscp, VIRTUALSERVER_CREATE_FLAG_NONE, &serverId);
    if(check_error(error)) {
        ts3server_freeMemory(vscp);
        LOG("Failed to create virtual server 2");
        return error;
    }

    error = ts3server_getVirtualServerKeyPair(serverId, &keyPair);
    if(check_error(error)) {
        ts3server_freeMemory(vscp);
        LOG("Failed to get virtual server key pair");
        return error;
    }

    config["serverId"] = serverId;
    config["keyPair"] = keyPair;
    saveConfig(config);

    ts3server_freeMemory(vscp);
    return 0;
}

bool saveConfig(const json &config) {
    std::ofstream config_file("config.json");
    if(config_file.good()) {
        config_file << std::setw(2) << config;
        LOG("Saving config");
    } else {
        LOG("Failed to save config");
    }
    config_file.close();
    return config_file.good();
}

bool loadConfig(json &config) {
    std::ifstream config_file("config.json");
    if(config_file.good()) {
        config_file >> config;
        LOG("Loading config");
    } else {
        LOG("Failed to load config. Using default");
        config = {
            {"keyPair", ""},
            {"serverId", "1"},
            {"channels", json::array()}
        };
    }
    config_file.close();
    return config_file.good();
}