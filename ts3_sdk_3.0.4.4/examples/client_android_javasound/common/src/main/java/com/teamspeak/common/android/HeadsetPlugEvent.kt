package com.teamspeak.common.android

import com.teamspeak.common.IEvent

data class HeadsetPlugEvent(
        val state: Boolean = false,
        val name: String = "",
        val microphone: Boolean = false
) : IEvent
