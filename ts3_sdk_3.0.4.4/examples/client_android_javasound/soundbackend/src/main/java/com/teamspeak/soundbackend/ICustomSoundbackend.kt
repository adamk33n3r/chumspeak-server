package com.teamspeak.soundbackend

import java.nio.ByteBuffer

interface ICustomSoundbackend {
    fun ts3client_registerCustomDevice(
            deviceID: String,
            deviceDisplayName: String,
            capFrequency: Int,
            capChannels: Int,
            capByteBuffer: ByteBuffer,
            playFrequency: Int,
            playChannels: Int,
            playByteBuffer: ByteBuffer)
            : Int

    fun ts3client_unregisterCustomDevice(deviceID: String): Int
}