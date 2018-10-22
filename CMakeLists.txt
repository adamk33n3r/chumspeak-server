cmake_minimum_required (VERSION 2.6)
project (ChumSpeakServer)

set (CMAKE_CXX_STANDARD 17)

include_directories ("${PROJECT_SOURCE_DIR}/ts3_sdk_3.0.4.4/include")
include_directories ("${PROJECT_SOURCE_DIR}/include")

set (ChumSpeakServer_VERSION_MAJOR 1)
set (ChumSpeakServer_VERSION_MINOR 0)

configure_file (
  "${PROJECT_SOURCE_DIR}/ChumSpeakConfig.h.in"
  "${PROJECT_BINARY_DIR}/ChumSpeakConfig.h"
)

# For config
include_directories("${PROJECT_BINARY_DIR}")

# add_executable (chumspeak-server example.c id_io.c)
add_executable (chumspeak-server
  src/main.cpp
  src/Config.cpp
  src/Listeners.cpp
  src/Server.cpp
)
#link_directories ("${PROJECT_SOURCE_DIR}/ts3_sdk_3.0.4.4/bin/linux/amd64")
find_library (TS3SERVER_LIBRARY
  NAMES ts3server
  PATHS "${PROJECT_SOURCE_DIR}/ts3_sdk_3.0.4.4/bin/linux/amd64"
)
#target_link_libraries (chumspeak-server ts3server)
target_link_libraries (chumspeak-server "${TS3SERVER_LIBRARY}")

set (TS3SERVER_LIBRARIES ${TS3SERVER_LIBRARY})
