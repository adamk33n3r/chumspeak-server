#ifndef CONFIG_H
#define CONFIG_H

#include <string>
#include <memory>
#include "json.hpp"

class Config {
    public:
    Config(const std::string &configPath);
    bool save() const;
    std::string dump() const;
    std::string value(const char* key, const char* defaultValue) const;
    nlohmann::json& operator[](const char* key);
    const nlohmann::json& operator[](const char* key) const;
    private:
    nlohmann::json json;
};

#endif
