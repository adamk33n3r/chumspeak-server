#include "Config.h"

#include <iostream>
#include <iomanip>
#include <string>
#include <fstream>

#define LOG(x) std::cout << x << std::endl

Config::Config(const std::string &configPath) {
    std::ifstream configFile(configPath);
    if(configFile.good()) {
        LOG("Loading config");
        configFile >> json;
        json["keyPair"] = json.value("keyPair", "");
        json["channels"] = json.value("channels", nlohmann::json::array());
        json["logTypes"] = json.value("logTypes", nlohmann::json({
            { "file", true }, 
            { "console", false }
        }));
        json["logTypes"]["file"] = json["logTypes"].value("file", true);
        json["logTypes"]["console"] = json["logTypes"].value("console", false);
    } else {
        LOG("Failed to load config. Using default");
        json = {
            { "keyPair", "" },
            { "channels", nlohmann::json::array() },
            { "logTypes", { "file", true }, { "console", false } }
        };
    }
    configFile.close();
}

bool Config::save() const {
    std::ofstream config_file("config.json");
    if(config_file.good()) {
        config_file << std::setw(2) << json;
        LOG("Saving config");
    } else {
        LOG("Failed to save config");
    }
    config_file.close();
    return config_file.good();
}

std::string Config::dump() const {
    return json.dump();
}

std::string Config::value(const char* key, const char* defaultValue) const {
    return this->json.value(key, defaultValue);
}

nlohmann::json& Config::operator[](const char* key) {
    return this->json[key];
}

const nlohmann::json& Config::operator[](const char* key) const {
    return this->json[key];
}
