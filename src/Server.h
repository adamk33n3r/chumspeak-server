#ifndef SERVER_H
#define SERVER_H

#include <memory>
#include <teamspeak/public_definitions.h>

#include "Config.h"
#include "Channel.h"

class Server {
    public:
    Server(std::shared_ptr<Config> config);
    uint64 start();
    void stop() const;
    std::vector<Channel> getChannels() const;
    private:
    std::shared_ptr<Config> config;
    uint64 serverId;
};

#endif
