# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

#Create a Variable for the location of the ts3client library and header files
set(distribution_DIR ${CMAKE_SOURCE_DIR}/../../../../../../)

#Add the library with the IMPORTED attribute for linking
add_library(lib_ts3client SHARED IMPORTED)
set_target_properties(lib_ts3client PROPERTIES IMPORTED_LOCATION
    ${distribution_DIR}/bin/android/${ANDROID_ABI}/libts3client.so)

# build application's shared lib
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -Wall -Werror")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.


add_library( # Sets the name of the library.
             ts3client-wrapper-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             # Associated headers in the same location as their source
             # file are automatically included.

             sdkclient/src/ts3client_wrapper.cpp)


# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies the reference to the header include folder

target_include_directories(ts3client-wrapper-lib PRIVATE ${distribution_DIR}/include/)


# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       ts3client-wrapper-lib
                       lib_ts3client

                       # Links the target library to the log library
                       # included in the NDK.
                       android
                       ${log-lib})


# Android Studio 2.2.0 with CMake support does not pack stl shared libraries,
# so app needs to pack the right shared lib into APK. This sample uses solution
# from https://github.com/jomof/ndk-stl to find the right stl shared lib to use
# and copy it to the right place for Android Studio to pack
# Usage: download ndk-stl-config.cmake into app's directory hosting CMakeLists.txt
#        and just use it with the following line
include(ndk-stl-config.cmake)