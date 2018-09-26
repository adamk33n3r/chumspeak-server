package com.teamspeak.soundbackend

class CustomSoundbackend(
        private val iCustomSoundbackend: ICustomSoundbackend,
        iPlayback: IPlayback,
        iRecord: IRecord) {

    companion object {
        private const val deviceID = "Java"
        private const val displayName = "Java"
    }

    var playback = Playback(iPlayback, deviceID)
    var record: Record

    init {
        // TODO find out settings better, as in Opensl backend
        record = Record(iRecord, deviceID)
        iCustomSoundbackend.ts3client_registerCustomDevice(
                deviceID = deviceID,
                deviceDisplayName = displayName,
                capFrequency = record.sampleRate,
                capChannels = record.channelCount,
                capByteBuffer = record.buffer,
                playFrequency = playback.sampleRate,
                playChannels = playback.channelCount,
                playByteBuffer = playback.byteBuffer)
    }

    fun unregister() {
        iCustomSoundbackend.ts3client_unregisterCustomDevice(deviceID)
    }
}