#include "Utils.h"

#include <iostream>
#include <teamspeak/serverlib.h>
#include <teamspeak/public_errors.h>

#include "Log.h"

bool Utils::checkError(unsigned int error, const char* msg) {
    if(error == ERROR_ok) {
        return false;
    }

    char* errorMsg;
    if(ts3server_getGlobalErrorMessage(error, &errorMsg) == ERROR_ok) {
        std::cerr << "Error: '" << errorMsg << "'" << std::endl;
        ts3server_freeMemory(errorMsg);
    }
    LOG(msg);
    throw std::runtime_error(msg);
}
