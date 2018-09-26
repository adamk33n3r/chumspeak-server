package com.teamspeak.common

import android.os.Build
import android.support.annotation.RequiresApi
import android.telecom.*
import android.telecom.Connection.PROPERTY_SELF_MANAGED
import android.telecom.Connection.CAPABILITY_HOLD
import android.telecom.Connection.CAPABILITY_SUPPORT_HOLD

@RequiresApi(Build.VERSION_CODES.M)
class TelecomConnectionService : ConnectionService() {

    /** Telecom calls this method in response to your app calling placeCall to create a new outgoing call. */
    override fun onCreateOutgoingConnection(connectionManagerPhoneAccount: PhoneAccountHandle?, request: ConnectionRequest?): Connection {
        val ourConnection = MessengerConnection()

        // TODO will likely crash on N_MR1
        @RequiresApi(Build.VERSION_CODES.N_MR1)
        ourConnection.connectionProperties = PROPERTY_SELF_MANAGED

        ourConnection.connectionCapabilities = CAPABILITY_HOLD
        ourConnection.connectionCapabilities = CAPABILITY_SUPPORT_HOLD

        return ourConnection
    }

    /** When your app calls placeCall and the outgoing call cannot be placed, Telecom calls this method.
     * In response to this, your app should inform the user (for example, using an alert box or toast) that the outgoing call cannot be placed. */
    override fun onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount: PhoneAccountHandle?, request: ConnectionRequest?) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)
    }

    /** called in response to addIncomingConnection, if Android allows */
    override fun onCreateIncomingConnection(connectionManagerPhoneAccount: PhoneAccountHandle?, request: ConnectionRequest?): Connection {
        val ourConnection = MessengerConnection()

        // TODO will likely crash on N_MR1
        @RequiresApi(Build.VERSION_CODES.N_MR1)
        ourConnection.connectionProperties = PROPERTY_SELF_MANAGED

        ourConnection.connectionCapabilities = CAPABILITY_HOLD
        ourConnection.connectionCapabilities = CAPABILITY_SUPPORT_HOLD

        return ourConnection
    }

    /** called in response to addIncomingConnection, if Android denies
     "Your app should silently reject the incoming call, optionally posting a notification to inform the user of the missed call." */
    override fun onCreateIncomingConnectionFailed(connectionManagerPhoneAccount: PhoneAccountHandle?, request: ConnectionRequest?) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
    }

    inner class MessengerConnection : Connection() {

        /** Telecom calls this method when you add a new incoming call
         * and your app should show its incoming call UI. */
        override fun onShowIncomingCallUi() {
            super.onShowIncomingCallUi()
        }

        /** Telecom calls this method to inform your app that the current audio route or mode
         * has changed. This is called in response to your app changing the audio mode using
         * setAudioRoute(int). This may also be called if the system changes the audio route
         * (when a Bluetooth headset disconnects itself, for example). */
        override fun onCallAudioStateChanged(state: CallAudioState?) {
            super.onCallAudioStateChanged(state)
        }

        /** Telecom calls this method when it wants to hold a call.
         * In response to this, your app should hold the call and then invoke setOnHold()
         * to inform Telecom that the call is held. Telecom may call this method when an
         * InCallService showing your call (Android Auto, for example) wants to relay a user request
         * to hold the call. */
        override fun onHold() {
            super.onHold()
        }

        /** Telecom calls this method when it wants to unhold a call.
         * Once your app has unheld the call, it should invoke setActive() to inform Telecom that
         * the call is no longer held. Telecom may call this method when a InCallService showing
         * your call (Android Auto, for example) wants to relay a user request to unhold the call.*/
        override fun onUnhold() {
            super.onUnhold()
        }

        /** Telecom calls this method to inform your app that an incoming call should be answered.
         * Once your app has answered the call, it should invoke setActive() to inform Telecom that
         * the call has been answered. Telecom may call this method when your app adds a new
         * incoming call and there is already an ongoing call in another app.
         * As discussed previously, Telecom will display the incoming call UI on behalf of your app
         * in these instances.*/
        override fun onAnswer() {
            super.onAnswer()
        }

        /*override fun onAnswer(videoState: Int) {
            super.onAnswer(videoState)
        }*/

        /** Telecom calls this method when it wants to reject an incoming call.
         * Once your app has rejected the call, it should call
         * setDisconnected(android.telecom.DisconnectCause) and specify REJECTED.
         * Your app should then call destroy() to inform Telecom you are done with the call.
         * Similar to onAnswer(), Telecom will call this method when the user has rejected an
         * incoming call from your app. */
        override fun onReject() {
            // TODO

            this.setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
        }

        /** Telecom calls this method when it wants to disconnect a call. Once the call has ended,
         * your app should call setDisconnected(android.telecom.DisconnectCause) and specify LOCAL
         * to indicate that a user request caused the call to be disconected.
         * Your app should then call destroy() to inform Telecom you are done with the call.
         * Telecom may call this method when the user has disconnected a call through another
         * InCallService such as Android Auto.*/
        override fun onDisconnect() {
            this.destroy()
        }
    }
}