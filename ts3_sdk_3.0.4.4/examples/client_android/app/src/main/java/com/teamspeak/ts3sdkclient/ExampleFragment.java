package com.teamspeak.ts3sdkclient;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.os.Handler;
import android.text.TextUtils;

import com.teamspeak.ts3sdkclient.connection.ConnectionHandler;
import com.teamspeak.ts3sdkclient.connection.ConnectionParams;
import com.teamspeak.ts3sdkclient.eventsystem.Callbacks;
import com.teamspeak.ts3sdkclient.eventsystem.IEvent;
import com.teamspeak.ts3sdkclient.eventsystem.IEventListener;
import com.teamspeak.ts3sdkclient.helper.Info;
import com.teamspeak.ts3sdkclient.helper.InfoMessage;
import com.teamspeak.ts3sdkclient.helper.MyInfoItemRecyclerViewAdapter;
import com.teamspeak.ts3sdkclient.service.ConnectionService;
import com.teamspeak.ts3sdkclient.service.ServiceUtils;
import com.teamspeak.ts3sdkclient.ts3sdk.Native;
import com.teamspeak.ts3sdkclient.ts3sdk.events.ConnectStatusChange;
import com.teamspeak.ts3sdkclient.ts3sdk.states.ConnectStatus;
import com.teamspeak.ts3sdkclient.ts3sdk.states.PublicError;

import java.util.List;

/**
 * TeamSpeak 3 sdk client sample
 *
 * Copyright (c) 2007-2017 TeamSpeak-Systems
 *
 * @author Anna
 * Creation date: 08.02.17
 *
 * Example Fragment to demonstrate the usage of the ts3client library methods and ts3 events
 */
public class ExampleFragment extends Fragment implements IEventListener, Info.OnInfoListener {

    public static final String TAG = ExampleFragment.class.getSimpleName();

    private Button mConnectButton;
    private EditText mInputServer;
    private EditText mInputPort;
    private EditText mInputNickname;
    private TextInputLayout mLayoutServer;
    private TextInputLayout mLayoutNickname;
    private MyInfoItemRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private boolean mServiceBound = false;
    private boolean mStartConnection = false;
    private ConnectionParams mParams;

    private ConnectionService mConnectionService;
    private ConnectionHandler mConnectionHandler;
    private List<InfoMessage> mMessages = Info.getInstance().getMessages();

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            mConnectionHandler.printConnectionInfo();
            timerHandler.postDelayed(this, 5000);
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mConnectionService = ((ConnectionService.LocalBinder) iBinder).getService();
            mConnectionHandler = mConnectionService.getConnectionHandler();
            /**
             * know that we bound to the the service, we can call the TS3ConnectionHandler to
             * connect to server
             */
            if (mStartConnection) {
                mStartConnection = false;
                int error = mConnectionHandler.startConnection(mParams);

               if (error != PublicError.ERROR_ok){
                   Info.getInstance().addMessage(PublicError.getPublicErrorString(error), Color.parseColor("#DC143C"));
                   ServiceUtils.unBindService(getActivity(),this);
                   ServiceUtils.stopService(getActivity());
               }
            }

            setConnectButtonText(mConnectionHandler.getCurrentState());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            setStatesForUnbound();
        }
    };

    public static final ExampleFragment newInstance(String identity, boolean isEmulator)
    {
        ExampleFragment fragment = new ExampleFragment();
        final Bundle args = new Bundle(1);
        args.putString("ident", identity);
        args.putBoolean("emulator", isEmulator);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_example, container, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //====== init UI
        Button clearButton = (Button) view.findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessages.clear();
                mAdapter.notifyDataSetChanged();
            }
        });

        mConnectButton = (Button) view.findViewById(R.id.button_connect);
        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mConnectionHandler != null
                        && mConnectionHandler.getCurrentState() != ConnectStatus.STATUS_DISCONNECTED) {
                    onClickDisconnect();
                } else onClickConnect();
            }
        });

        mInputServer = (EditText) view.findViewById(R.id.input_server);
        mInputPort = (EditText) view.findViewById(R.id.input_port);
        mInputNickname = (EditText) view.findViewById(R.id.input_nickname);
        mLayoutServer = (TextInputLayout) view.findViewById(R.id.input_layout_server);
        mLayoutNickname = (TextInputLayout) view.findViewById(R.id.input_layout_nickname);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mAdapter = new MyInfoItemRecyclerViewAdapter(mMessages, getContext());
        mRecyclerView.setAdapter(mAdapter);

        boolean isEmu = getArguments().getBoolean("emulator");
        if (isEmu) {
            mInputServer.setHint("10.0.2.2");
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //bind TS3ConnectionService if the service is running
        if (ServiceUtils.isServiceRunning(getActivity())) {
            mServiceBound = ServiceUtils.bindService(getActivity(), mServiceConnection);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //register callbacks to receive TS3Events
        Callbacks.getInstance().registerCallbacks(this);
        //set listener to log messages
        Info.getInstance().setOnInfoListener(this);
        //update ui
        setConnectButtonText(mConnectionHandler != null ? mConnectionHandler.getCurrentState()
                : ConnectStatus.STATUS_DISCONNECTED);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        //unregister callbacks
        Callbacks.getInstance().unregisterCallbacks(this);
        Info.getInstance().setOnInfoListener(null);
        super.onPause();
    }

    @Override
    public void onStop() {
        //unbind TS3ConnectionService if the service was bound before
        if (mServiceBound) {
            ServiceUtils.unBindService(getActivity(), mServiceConnection);
            setStatesForUnbound();
        }
        super.onStop();
    }

    static CharSequence getTextOrHint(EditText t) {
        return(TextUtils.isEmpty(t.getText()) ? t.getHint() : t.getText());
    }

    /**
     * start the {@link ConnectionService} and bind to service
     */
    public void onClickConnect() {
        if (validateInset()) {
            String identity = getArguments().getString("ident");
            int port = mInputPort.getText().length() > 0 ? Integer.parseInt(mInputPort.getText().toString()) : 0;
            mParams = new ConnectionParams(getTextOrHint(mInputServer).toString(),
                    port, mInputNickname.getText().toString(),
                    identity);
            mStartConnection = true;
            ServiceUtils.startService(getActivity());
            mServiceBound = ServiceUtils.bindService(getActivity(), mServiceConnection);
        }
    }

    /**
     * tell the {@link ConnectionHandler} to disconnect from server
     */
    public void onClickDisconnect() {
        mConnectionService.getConnectionHandler().disconnect();
    }

    /**
     * check for empty insets
     *
     * @return false if serverip or nickname is empty
     */
    private boolean validateInset() {
        boolean serverSet = mInputServer.getText().length() != 0 || mInputServer.getHint().length() != 0;
        boolean nicknameSet = mInputNickname.getText().length() != 0;
        setErrorForTextInputLayout(mLayoutServer, !serverSet, getString(R.string.input_required));
        setErrorForTextInputLayout(mLayoutNickname, !nicknameSet, getString(R.string.input_required));

        return serverSet && nicknameSet;
    }

    /**
     * updates the buttonstate
     *
     * @param connectionStatus - the current state of the connection
     */
    private void setConnectButtonText(@ConnectStatus int connectionStatus) {
        switch (connectionStatus) {
            case ConnectStatus.STATUS_CONNECTING:
                mConnectButton.setEnabled(false);
                mConnectButton.setText(R.string.button_server_connecting);
                break;
            case ConnectStatus.STATUS_CONNECTED:
                mConnectButton.setEnabled(true);
                mConnectButton.setText(R.string.button_server_disconnect);
                break;
            case ConnectStatus.STATUS_CONNECTION_ESTABLISHING:
                mConnectButton.setEnabled(true);
                mConnectButton.setText(R.string.button_server_disconnect);
                break;
            case ConnectStatus.STATUS_CONNECTION_ESTABLISHED:
                mConnectButton.setEnabled(true);
                mConnectButton.setText(R.string.button_server_disconnect);
                break;
            case ConnectStatus.STATUS_DISCONNECTED:
                mConnectButton.setEnabled(true);
                mConnectButton.setText(R.string.button_server_connect);
                break;
        }
    }

    private void setStatesForUnbound() {
        mServiceBound = false;
        mConnectionService = null;
        mConnectionHandler = null;
    }

    private void setErrorForTextInputLayout(TextInputLayout layout, boolean showError, String messages) {
        layout.setErrorEnabled(showError);
        layout.setError(showError ? messages : null);
    }

    /**
     * With the {@link IEventListener} we can receive callbacks to update the UI
     *
     * @param event - the incoming event
     */
    @Override
    public void onTS3Event(IEvent event) {
        if (event instanceof ConnectStatusChange) {
            ConnectStatusChange statusChangeEvent = (ConnectStatusChange) event;
            @ConnectStatus
            final int connectStatus = statusChangeEvent.getNewStatus();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setConnectButtonText(connectStatus);
                    if (connectStatus == ConnectStatus.STATUS_CONNECTION_ESTABLISHED) {
                        timerHandler.postDelayed(timerRunnable, 5000);
                    } else if (connectStatus == ConnectStatus.STATUS_DISCONNECTED) {
                        timerHandler.removeCallbacks(timerRunnable);
                    }
                }
            });

            if (connectStatus == ConnectStatus.STATUS_DISCONNECTED) {
                ServiceUtils.unBindService(getActivity(), mServiceConnection);
                mServiceBound = false;
            }
        }
    }

    @Override
    public void onUpdateGUI() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                mRecyclerView.smoothScrollToPosition(0);
            }
        });
    }
}
