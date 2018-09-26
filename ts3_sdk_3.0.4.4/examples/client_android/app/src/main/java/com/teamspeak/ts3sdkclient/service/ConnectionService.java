package com.teamspeak.ts3sdkclient.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.teamspeak.ts3sdkclient.R;
import com.teamspeak.ts3sdkclient.TS3Application;
import com.teamspeak.ts3sdkclient.connection.ConnectionHandler;
import com.teamspeak.ts3sdkclient.ts3sdk.Native;
import com.teamspeak.ts3sdkclient.ts3sdk.events.ClientMove;
import com.teamspeak.ts3sdkclient.ts3sdk.events.ClientMoveSubscription;
import com.teamspeak.ts3sdkclient.ts3sdk.events.ClientMoveTimeout;
import com.teamspeak.ts3sdkclient.ts3sdk.events.DelChannel;
import com.teamspeak.ts3sdkclient.ts3sdk.events.NewChannel;
import com.teamspeak.ts3sdkclient.ts3sdk.events.NewChannelCreated;
import com.teamspeak.ts3sdkclient.ts3sdk.events.ServerError;
import com.teamspeak.ts3sdkclient.ts3sdk.events.TalkStatusChange;
import com.teamspeak.ts3sdkclient.ts3sdk.states.ConnectStatus;
import com.teamspeak.ts3sdkclient.eventsystem.IEvent;
import com.teamspeak.ts3sdkclient.eventsystem.IEventListener;
import com.teamspeak.ts3sdkclient.eventsystem.Callbacks;
import com.teamspeak.ts3sdkclient.helper.Info;
import com.teamspeak.ts3sdkclient.ts3sdk.events.ConnectStatusChange;

import static com.teamspeak.ts3sdkclient.ts3sdk.states.ChannelProperties.CHANNEL_NAME;
import static com.teamspeak.ts3sdkclient.ts3sdk.states.ClientProperties.CLIENT_NICKNAME;
import static com.teamspeak.ts3sdkclient.ts3sdk.states.PublicError.ERROR_connection_lost;
import static com.teamspeak.ts3sdkclient.ts3sdk.states.TalkStatus.STATUS_TALKING;

/**
 * TeamSpeak 3 sdk client sample
 *
 * Copyright (c) 2007-2017 TeamSpeak-Systems
 *
 * @author Anna
 * Creation date: 08.02.17
 *
 * Service class to ensure the server connection can be maintained if the app is in the background
 * The Service will run in the foreground while a server connection is active
 *
 * mConnectionHandler       - ConnectionHandler variable with the current connection Informations
 * mNotificationManager     - NotificationManager to display connection status notifications
 */
public class ConnectionService extends Service implements IEventListener {

    public static final String TAG = ConnectionService.class.getSimpleName();
    public static final int SERVICE_FOREGROUND_NOTIFICATION_ID = 8648;

    private Native nativeInterface;
    private ConnectionHandler mConnectionHandler;
    private Info mInfo;

    private NotificationManager mNotificationManager;

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public ConnectionService getService() {
            return ConnectionService.this;
        }
    }

    @Override
    public void onCreate() {
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        TS3Application app = (TS3Application)getApplication();
        mConnectionHandler = new ConnectionHandler(app);
        mInfo = Info.getInstance();
        nativeInterface = app.getNativeInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        Callbacks.getInstance().registerCallbacks(this);
        mInfo.addMessage(this.getClass().getSimpleName() + ": onStartCommand", Color.parseColor("#228B22"));
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mInfo.addMessage(this.getClass().getSimpleName() + ": onBind", Color.parseColor("#FF8C00"));
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        mInfo.addMessage(this.getClass().getSimpleName() + ": onRebind", Color.parseColor("#FF8C00"));
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mInfo.addMessage(this.getClass().getSimpleName() + ": onUnbind", Color.parseColor("#FF8C00"));
        return true;
    }

    @Override
    public void onDestroy() {
        Callbacks.getInstance().unregisterCallbacks(this);
        mInfo.addMessage(this.getClass().getSimpleName() + ": onDestroy", Color.parseColor("#DC143C"));
    }


    @Override
    public void onTS3Event(IEvent event) {
        Log.d(TAG, event.toString());
        mInfo.addMessage(event.toString(),Color.parseColor("#4169E1"));

        if (event instanceof ConnectStatusChange) {
            ConnectStatusChange statusChangeEvent = (ConnectStatusChange)event;
            @ConnectStatus
            final int connectStatus = statusChangeEvent.getNewStatus();
            mConnectionHandler.setCurrentState(connectStatus);

            switch (connectStatus) {
                case ConnectStatus.STATUS_DISCONNECTED:
                    mConnectionHandler.closeAudioDevices();

                    stopForeground(true);
                    stopSelf();
                    break;
                case ConnectStatus.STATUS_CONNECTING:
                    startForeground(SERVICE_FOREGROUND_NOTIFICATION_ID,
                            createNotification(getString(R.string.button_server_connecting)));
                    break;
                case ConnectStatus.STATUS_CONNECTED:
                    mNotificationManager.notify(SERVICE_FOREGROUND_NOTIFICATION_ID,
                            createNotification(getString(R.string.server_connected)));

                    mConnectionHandler.openAudioDevices();
                    mConnectionHandler.configurePreProcessor();
                    break;
                case ConnectStatus.STATUS_CONNECTION_ESTABLISHING:
                    Log.d(TAG,"Connect status: STATUS_CONNECTION_ESTABLISHING");
                    break;
                case ConnectStatus.STATUS_CONNECTION_ESTABLISHED:
                    Log.d(TAG,"Connect status: STATUS_CONNECTION_ESTABLISHED");
                    break;

                default:
                    break;
            }
            return;
        }
        
        if (event instanceof NewChannel) {
            NewChannel newChannelEvent = (NewChannel) event;

            /* Query channel name from channel ID */
            String channelName = nativeInterface.ts3client_getChannelVariableAsString(
                    newChannelEvent.getServerConnectionHandlerID(),newChannelEvent.getChannelID(),CHANNEL_NAME);
            Log.d(TAG, "New channel: " + channelName);
            return;
        }

        if (event instanceof NewChannelCreated) {
            NewChannelCreated newChannelCreatedEvent = (NewChannelCreated) event;

            /* Query channel name from channel ID */
            String channelName = nativeInterface.ts3client_getChannelVariableAsString(
                    newChannelCreatedEvent.getServerConnectionHandlerID(),newChannelCreatedEvent.getChannelID(),CHANNEL_NAME);
            Log.d(TAG, "New channel created: " + channelName);
            return;
        }

        if (event instanceof DelChannel) {
            DelChannel delChannelEvent = (DelChannel) event;
            Log.d(TAG,"Channel ID "+delChannelEvent.getChannelID()+" deleted by "
                    +delChannelEvent.getInvokerName()+" ("+delChannelEvent.getInvokerID()+")");
            return;
        }

        if (event instanceof ClientMove) {
            ClientMove clientMoveEvent = (ClientMove) event;
            Log.d(TAG,"ClientID "+clientMoveEvent.getClientID()+" moves from channel "+clientMoveEvent.getOldChannelID()+
                    " to "+clientMoveEvent.getNewChannelID()+" with message "+clientMoveEvent.getMoveMessage());
            return;
        }

        if (event instanceof ClientMoveSubscription) {
            ClientMoveSubscription clientMoveSubscriptionEvent = (ClientMoveSubscription) event;

            /* Query channel name from channel ID */
            String clientName = nativeInterface.ts3client_getClientVariableAsString(
                    clientMoveSubscriptionEvent.getServerConnectionHandlerID(), clientMoveSubscriptionEvent.getClientID(), CLIENT_NICKNAME);

            Log.d(TAG, "ClientMoveSubscription Event, ClientName: " + clientName);
            return;
        }

        if (event instanceof ClientMoveTimeout) {
            ClientMoveTimeout clientMoveTimeoutEvent = (ClientMoveTimeout) event;
            Log.d(TAG, "ClientID "+clientMoveTimeoutEvent.getClientID()+" timeouts with message "
                    +clientMoveTimeoutEvent.getTimeoutMessage());
            return;
        }

        if (event instanceof TalkStatusChange) {
            TalkStatusChange talkStatusChangeEvent = (TalkStatusChange) event;

            /* Query client nickname from ID */
            String clientName = nativeInterface.ts3client_getClientVariableAsString(
                    talkStatusChangeEvent.getServerConnectionHandlerID(), talkStatusChangeEvent.getClientID(), CLIENT_NICKNAME);
            if(talkStatusChangeEvent.getStatus() == STATUS_TALKING) {
                Log.d(TAG, "Client \""+clientName+"\" starts talking");
            } else {
                Log.d(TAG, "Client \""+clientName+"\" stops talking");
            }
            return;
        }

        if (event instanceof ServerError) {
            ServerError serverErrorEvent = (ServerError) event;
            Log.d(TAG, "Error for server "+serverErrorEvent.getServerConnectionHandlerID()+": "
                    +serverErrorEvent.getErrorMessage()+ " " + serverErrorEvent.getExtraMessage());
        }
    }

    /**
     * Create a foreground notification to inform the user about current
     * connection state
     * @param text - the text you want to display
     * @return the notification
     */
    private Notification createNotification(String text){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .setTicker(text)
                .setOngoing(true);
        return builder.build();
    }

    public ConnectionHandler getConnectionHandler() {
        return mConnectionHandler;
    }
}
