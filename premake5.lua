workspace "ChumSpeak"
  configurations { "Debug", "Release" }

project "ChumSpeakServer"
  kind "ConsoleApp"
  language "C++"
  targetdir "bin/%{cfg.buildcfg}"

  files { "src/**.cpp" }

  defines { "ChumSpeakServer_VERSION_MAJOR=1", "ChumSpeakServer_VERSION_MINOR=0" }

  includedirs { "include", "ts3_sdk_3.0.4.4/include", "build" }
  --links { "ts3server" }

  configuration "linux"
    links { "./ts3_sdk_3.0.4.4/bin/linux/amd64/ts3server" }
    --linkoptions '-Wl,-rpath=./ts3_sdk_3.0.4.4/bin/linux/amd64'
    runpathdirs { './ts3_sdk_3.0.4.4/bin/linux/amd64' }
    --libdirs { "./ts3_sdk_3.0.4.4/bin/linux/amd64" }
  configuration "windows"
    links { "./ts3_sdk_3.0.4.4/bin/windows/win64/ts3server" }
    --libdirs { "./ts3_sdk_3.0.4.4/bin/windows/win64" }



  filter "configurations.Debug"
    defines { "DEBUG" }
    symbols "On"

  filter "configurations.Release"
    defines { "NDEBUG" }
    optimize "On"
