#ifndef CHANNEL_H
#define CHANNEL_H

#include "Client.h"

struct Channel {
    uint64 id;
    std::string name;
    std::string description;
    std::vector<Client> clients;
};

#endif
