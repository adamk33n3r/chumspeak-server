#include <iostream>
#include <fstream>
#include <cstring>
#include <string>
#include <memory>
#include <teamspeak/serverlib.h>
#include <teamspeak/public_errors.h>
#include <json.hpp>

#include "Config.h"
#include "Listeners.h"
#include "Server.h"

#define LOG(x) std::cout << x << std::endl

bool check_error(unsigned int error);

int main() {
    std::cout << "v" << ChumSpeakServer_VERSION_MAJOR << "." << ChumSpeakServer_VERSION_MINOR << std::endl;

    std::shared_ptr<Config> configPtr = std::make_shared<Config>("config.json");
    Config& config = *configPtr;
    std::cout << config.dump() << std::endl;

    unsigned int error = 0;
    char c = '\0';
    bool abort = false;

    ServerLibFunctions functionPointers = Listeners::getFunctionPointers(configPtr);

    int logTypes = LogType_NONE;
    if (config["logTypes"]["file"].get<bool>()) {
        logTypes |= LogType_FILE;
    }
    if (config["logTypes"]["console"].get<bool>()) {
        logTypes |= LogType_CONSOLE;
    }
    LOG("LogType: " << logTypes);
    error = ts3server_initServerLib(&functionPointers, logTypes, NULL);
    if(check_error(error)) return error;
    
    error = ts3server_enableFileManager("uploads", NULL, 30033, BANDWIDTH_LIMIT_UNLIMITED, BANDWIDTH_LIMIT_UNLIMITED);
    if(check_error(error)) return error;

    Server server(configPtr);
    auto serverId = server.start();
    LOG("Created virtual server with id " << serverId);

    bool unknownInput = false;
    while(!abort) {
        if (!unknownInput) {
            printf("\nEnter command (h for help)> ");
        }
        unknownInput = false;
        c = getchar();
        switch(c) {
            case 'q':
                abort = true;
                break;
            case 'c':
                {
                    std::vector<Channel> channels = server.getChannels(true);
                    for (Channel channel : channels) {
                        LOG(channel.id << " - " << channel.name << " - " << channel.description);
                        for (auto it = channel.clients.begin(); it != channel.clients.end(); ++it) {
                            LOG("\t" << (*it).id << " - " << (*it).nickname);
                        }
                    }
                }
                break;
            case 'v':
                char* result;
                error = ts3server_getServerLibVersion(&result);
                if(check_error(error)) return error;
                std::cout << result << std::endl;
                ts3server_freeMemory(result);
                break;
            case 'h':
                LOG("Check the source code for the available commands.");
                break;
            default:
                unknownInput = true;
        }
    }

    server.stop();

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
