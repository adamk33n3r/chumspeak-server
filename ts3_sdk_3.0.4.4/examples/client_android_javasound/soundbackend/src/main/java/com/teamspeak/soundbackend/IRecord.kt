package com.teamspeak.soundbackend

interface IRecord {
    fun ts3client_processCustomCaptureData(
            deviceID: String,
            samples: Int)
            : Int
}