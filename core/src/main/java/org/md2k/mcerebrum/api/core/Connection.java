package org.md2k.mcerebrum.api.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import org.md2k.mcerebrum.api.core.datakitapi.IDataKitRemoteService;
import org.md2k.mcerebrum.api.core.datakitapi.ipc.IDataKitRemoteCallback;
import org.md2k.mcerebrum.api.core.datakitapi.ipc._Session;
import org.md2k.mcerebrum.api.core.datakitapi.ipc.authenticate.ConnectionCallback;
import org.md2k.mcerebrum.api.core.datakitapi.ipc.authenticate._AuthenticateIn;
import org.md2k.mcerebrum.api.core.status.MCStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
abstract class Connection {
    private static final String SERVER_ID = "org.md2k.mcerebrum.library.datakit.server";
    private Integer sessionId = null;

    private IDataKitRemoteService iDataKitRemoteService;
    private ArrayList<ConnectionCallback> connectionCallbacks;
    private boolean connected;
    private int authenticated;
    private Intent serverIntent;

    Connection() {
        connectionCallbacks = new ArrayList<>();
        iDataKitRemoteService = null;
        connected = false;
        authenticated = MCStatus.AUTHENTICATION_REQUIRED;

    }

    void connect(final ConnectionCallback connectionCallback) {
        if (!connectionCallbacks.contains(connectionCallback))
            connectionCallbacks.add(connectionCallback);
        if (!connected)
            connectToServer();
        else {
            if (authenticated == MCStatus.SUCCESS)
                connectionCallback.onSuccess();
            else connectionCallback.onError(authenticated);
        }
    }

    void disconnect(ConnectionCallback connectionCallback) {
        connectionCallbacks.remove(connectionCallback);
        if (connectionCallbacks.size() == 0) {
            disconnectFromServer();
        }
    }
    boolean isConnected(){
        return connected;
    }

    void disconnectAll() {
        connectionCallbacks.clear();

        disconnectFromServer();
    }

    private void disconnectFromServer() {
        if (!connected) return;
        connected = false;
        authenticated = MCStatus.AUTHENTICATION_REQUIRED;
        if (serverIntent != null) {
            assert MCerebrumAPI.getContext() != null;
            MCerebrumAPI.getContext().stopService(serverIntent);
            MCerebrumAPI.getContext().unbindService(mServiceConnection);
        }
    }

    private void sendConnectionError(int status) {
        for (int i = 0; i < connectionCallbacks.size(); i++)
            connectionCallbacks.get(i).onError(status);
        connectionCallbacks.clear();
    }

    private void sendConnectionSuccess() {
        for (int i = 0; i < connectionCallbacks.size(); i++)
            connectionCallbacks.get(i).onSuccess();
    }


    private void connectToServer() {
        serverIntent = findServer();
        if (serverIntent == null) {
            sendConnectionError(MCStatus.MCEREBRUM_APP_NOT_INSTALLED);
            return;
        }
        try {
            assert MCerebrumAPI.getContext() != null;
            boolean res = MCerebrumAPI.getContext().bindService(serverIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            if (!res) {
                sendConnectionError(MCStatus.MCEREBRUM_BIND_ERROR);
            }

        } catch (SecurityException e) {
            sendConnectionError(MCStatus.MCEREBRUM_BIND_ERROR);
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("abc", "onServiceConnected()");
            connected = true;
            iDataKitRemoteService = IDataKitRemoteService.Stub.asInterface(service);
            authenticate();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("abc", "onServiceDisconnected()");
            iDataKitRemoteService = null;
            connected = false;
            if (authenticated == MCStatus.SUCCESS)
                connectToServer();
        }
    };

    private void authenticate() {
        try {
            assert MCerebrumAPI.getContext() != null;
            _Session in = _AuthenticateIn.create(createSessionId(), MCerebrumAPI.getContext().getPackageName());
            executeAsync(in, new IDataKitRemoteCallback.Stub() {
                @Override
                public void onReceived(_Session _session) {
                    int authenticated = _session.getStatus();
                    if (authenticated == MCStatus.SUCCESS) {
                        sendConnectionSuccess();
                    } else {
                        sendConnectionError(authenticated);
                        disconnectAll();
                    }
                }
            });
        } catch (RemoteException e) {
            sendConnectionError(MCStatus.MCEREBRUM_BIND_ERROR);
        }

    }

    _Session execute(_Session _session) throws RemoteException {
        return iDataKitRemoteService.execute(_session);
    }

    void executeAsync(_Session _session, IDataKitRemoteCallback iDataKitRemoteCallback) throws RemoteException {
        iDataKitRemoteService.executeAsync(_session, iDataKitRemoteCallback);
    }

    private Intent findServer() {
        assert MCerebrumAPI.getContext() != null;
        PackageManager packageManager = MCerebrumAPI.getContext().getPackageManager();
        Intent intent = new Intent(SERVER_ID);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentServices(intent, 0);
        if (resolveInfo.size() == 0) return null;
        if (resolveInfo.size() == 1) {
            intent = new Intent();
            intent.setComponent(new ComponentName(resolveInfo.get(0).serviceInfo.packageName, resolveInfo.get(0).serviceInfo.name));
            return intent;
        }
        for (int i = 0; i < resolveInfo.size(); i++) {
            if (resolveInfo.get(i).serviceInfo.packageName.equals(MCerebrumAPI.getContext().getPackageName())) {
                intent = new Intent();
                intent.setComponent(new ComponentName(resolveInfo.get(i).serviceInfo.packageName, resolveInfo.get(i).serviceInfo.name));
                return intent;
            }
        }
        //TODO: select
        intent = new Intent();
        intent.setComponent(new ComponentName(resolveInfo.get(0).serviceInfo.packageName, resolveInfo.get(0).serviceInfo.name));
        return intent;
    }


    synchronized int createSessionId() {
        if (sessionId == null) {
            sessionId = new Random().nextInt();
        } else sessionId++;
        return sessionId;
    }

}
