package com.teamspeak.common.internal

import android.bluetooth.BluetoothHeadset
import android.content.IntentFilter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.util.Log
import android.content.Intent
import android.content.BroadcastReceiver
import android.media.AudioManager
import android.os.Parcelable
import android.os.CountDownTimer
import com.teamspeak.common.IAudioBluetooth
import java.util.concurrent.atomic.AtomicBoolean


class AudioBluetooth(
        private val context: Context,
        private val audioManager: AudioManager,
        private val iAudioBluetooth: IAudioBluetooth) {

    companion object {
        private val TAG = AudioBluetooth::class.java.simpleName
    }

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothHeadset: BluetoothHeadset? = null
    private var connectedHeadset: BluetoothDevice? = null

    val isBluetoothAudioConnected: Boolean
        get() = bluetoothHeadset != null && bluetoothHeadset!!.isAudioConnected(connectedHeadset)

    /**
     * Register a headset profile listener
     * @return false    if device does not support bluetooth or current platform does not supports
     * use of SCO for off call or error in getting profile proxy.
     */
    fun start(): Boolean {
        Log.d(TAG, "startBluetooth")

        // Device support bluetooth
        if (bluetoothAdapter != null) {
            if (audioManager.isBluetoothScoAvailableOffCall) {
                // All the detection and audio connection are done in mHeadsetProfileListener
                if (bluetoothAdapter.getProfileProxy(context,
                                headsetProfileListener,
                                BluetoothProfile.HEADSET)) {
                    return true
                }
            }
        }

        return false
    }

    fun stop() {
        Log.d(TAG, "stopBluetooth")

        starter.stopCountdown()

        if (bluetoothHeadset != null) {
            // Need to call stopVoiceRecognition here when the app
            // change orientation or close with headset still turns on.
            bluetoothHeadset!!.stopVoiceRecognition(connectedHeadset)
            context.unregisterReceiver(headsetBroadcastReceiver)
            bluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothHeadset)
            bluetoothHeadset = null
        }
    }

    private val headsetProfileListener = object : BluetoothProfile.ServiceListener {

        override fun onServiceDisconnected(profile: Int) {
            if (profile != BluetoothProfile.HEADSET)
                return

            bluetoothHeadset = null
            Log.i(TAG, "BluetoothHeadsetService disconnected")
        }

        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile != BluetoothProfile.HEADSET)
                return

            Log.d(TAG, "Profile listener onServiceConnected")

            // bluetoothHeadset is just a headset profile, it does not represent a headset device.
            bluetoothHeadset = proxy as BluetoothHeadset

            // If a headset is connected before this application starts,
            // ACTION_CONNECTION_STATE_CHANGED will not be broadcast.
            // So we need to check for an already connected headset.
            val devices = bluetoothHeadset!!.connectedDevices
            if (devices.size > 0) {
                // Only one headset can be connected at a time,
                // so the connected headset is at index 0.
                connectedHeadset = devices[0]
                iAudioBluetooth.onBluetoothHeadsetConnectStatusChange(true)
            }

            // During the active life time of the app, a user may turn on and off the headset.
            // So register for broadcast of connection states.
            context.registerReceiver(headsetBroadcastReceiver,
                    IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED))
            // Calling startVoiceRecognition does not result in immediate audio connection.
            // So register for broadcast of audio connection states. This broadcast will
            // only be sent if startVoiceRecognition returns true.
            context.registerReceiver(headsetBroadcastReceiver,
                    IntentFilter(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED))
        }
    }

    /**
     * Handle headset and Sco audio connection states.
     */
    private val headsetBroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val state: Int
            if (action == BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED) {
                state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE,
                        BluetoothHeadset.STATE_DISCONNECTED)

                if (state == BluetoothHeadset.STATE_CONNECTED) {
                    connectedHeadset = intent.getParcelableExtra<Parcelable>(BluetoothDevice.EXTRA_DEVICE) as BluetoothDevice?

                    // Calling startVoiceRecognition always returns false here,
                    // that why a count down timer is implemented to call
                    // startVoiceRecognition in the onTick.
                    starter.startCountdown()
                    Log.d(TAG, "Start count down")
                } else if (state == BluetoothHeadset.STATE_DISCONNECTED) {
                    // Calling stopVoiceRecognition always returns false here
                    // as it should since the headset is no longer connected.
                    starter.stopCountdown()
                    connectedHeadset = null

                    // override this if you want to do other thing when the device is disconnected.
                    iAudioBluetooth.onBluetoothHeadsetConnectStatusChange(false)

                    Log.d(TAG, "Headset disconnected")
                }
            } else if (action == BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED) {
                state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_AUDIO_DISCONNECTED)
                if (state == BluetoothHeadset.STATE_AUDIO_CONNECTED) {
                    Log.d(TAG, "Headset audio connected")
                    starter.startCountdown()
                } else if (state == BluetoothHeadset.STATE_AUDIO_DISCONNECTED) {
                    // The headset audio is disconnected, but calling
                    // stopVoiceRecognition always returns true here.
                    bluetoothHeadset?.stopVoiceRecognition(connectedHeadset)

                    Log.d(TAG, "Headset audio disconnected")
                }
            }
        }
    }

    private val starter = object : CountDownTimer(2000, 200) {

        var isCountdownOn = AtomicBoolean(false)
        var isFirstTick = AtomicBoolean(false)

        fun startCountdown() {
            if (isCountdownOn.compareAndSet(false, true)) {
                isFirstTick.set(true)
                this.start()
            }
        }

        fun stopCountdown() {
            if (isCountdownOn.compareAndSet(true, false)) {
                this.cancel()
            }
            isFirstTick.set(false)
        }

        override fun onTick(millisUntilFinished: Long) {
            // First tick calls always returns false.
            // The second stick always returns true if the countDownInterval is set to 1000.
            // It is somewhere in between 500 to a 1000.
            if (bluetoothHeadset != null && bluetoothHeadset!!.startVoiceRecognition(connectedHeadset)) {
                Log.d(TAG, "Started voice recognition")
                iAudioBluetooth.onBluetoothHeadsetConnectStatusChange(true)
                this.cancel()
                this.isCountdownOn.set(false)
            } else if (isFirstTick.compareAndSet(true, false)){
                audioManager.isBluetoothScoOn = true
                audioManager.startBluetoothSco()
            }
        }

        override fun onFinish() {
            // Calls to startVoiceRecognition in onStick are not successful.
            // Should implement something to inform user of this failure
            isCountdownOn.set(false)
            Log.w(TAG, "Could not start voice recognition")
        }
    }
}