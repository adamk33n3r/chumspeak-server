package com.teamspeak.common

import android.app.Activity
import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.support.annotation.RequiresApi

object AudioHelpers {

    fun getConnectedDeviceTypes(audioManager : AudioManager, inOrOut: Boolean) : Pair<Boolean, Boolean> {
        var hasHeadDevice = false
        var hasBtDevice = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val flag = if (inOrOut)
                AudioManager.GET_DEVICES_INPUTS else AudioManager.GET_DEVICES_OUTPUTS

            val devices = audioManager.getDevices(flag)

            for (device in devices) {
                if (device.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                        device.type == AudioDeviceInfo.TYPE_WIRED_HEADSET
                || device.type == AudioDeviceInfo.TYPE_USB_HEADSET)
                    hasHeadDevice = true
                else if (device.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                        device.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO)
                    hasBtDevice = true
            }
        } else {
            // other option for headset plug:
            /* val iFilter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        val iStatus = context.registerReceiver(null, iFilter)
        val isConnected = iStatus.getIntExtra("state", 0) == 1 */
            //TODO dunno about those bluetooth
            hasHeadDevice = audioManager.isWiredHeadsetOn
            hasBtDevice = audioManager.isBluetoothA2dpOn || audioManager.isBluetoothScoOn || audioManager.isBluetoothScoAvailableOffCall
        }
        return Pair( hasHeadDevice, hasBtDevice )
    }

    // TODO: test if this applies to opensl es, otherwise use key handling like in TS android app
    fun setVolumeControlStreamVoiceCall(activity: Activity) {
        activity.volumeControlStream = AudioManager.STREAM_VOICE_CALL
    }

    fun setVolumeControlStreamRinging(activity: Activity) {
        activity.volumeControlStream = AudioManager.STREAM_RING
    }

    fun setSpeakerphoneOn(audioManager: AudioManager, on: Boolean) {
        if (audioManager.isSpeakerphoneOn != on)
            audioManager.isSpeakerphoneOn = on
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getAudioAttributes() : AudioAttributes {
        return AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .build()
    }

}