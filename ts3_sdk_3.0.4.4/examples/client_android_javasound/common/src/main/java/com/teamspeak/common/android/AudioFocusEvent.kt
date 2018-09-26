package com.teamspeak.common.android

import android.media.AudioManager
import android.support.annotation.IntDef
import com.teamspeak.common.IEvent

data class AudioFocusEvent(val audioFocusVal : Int) : IEvent {
    @AudioFocus
    val audioFocusChange = audioFocusVal

    companion object {
        @Target(AnnotationTarget.TYPE, AnnotationTarget.PROPERTY)
        @IntDef(AUDIOFOCUS_GAIN, AUDIOFOCUS_LOSS, AUDIOFOCUS_LOSS_TRANSIENT, AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK)
        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        annotation class AudioFocus

        const val AUDIOFOCUS_GAIN = AudioManager.AUDIOFOCUS_GAIN
        const val AUDIOFOCUS_LOSS = AudioManager.AUDIOFOCUS_LOSS
        const val AUDIOFOCUS_LOSS_TRANSIENT = AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
        const val AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK = AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK
    }
}