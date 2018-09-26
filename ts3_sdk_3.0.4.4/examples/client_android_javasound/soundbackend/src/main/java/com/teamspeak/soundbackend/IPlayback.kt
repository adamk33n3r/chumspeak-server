package com.teamspeak.soundbackend

interface IPlayback {
    fun ts3client_acquireCustomPlaybackData(
            deviceID: String,
            samples: Int)
            : Int
}