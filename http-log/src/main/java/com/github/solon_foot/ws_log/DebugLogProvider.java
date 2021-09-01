package com.github.solon_foot.ws_log;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ProviderInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import com.github.solon_foot.TLog;
import java.io.IOException;

public class DebugLogProvider extends ContentProvider {

    int portNumber = 8080;
    boolean showNotification = true;
    private Context context;
    @Override
    public boolean onCreate() {
        TLog.e(1);
        TLog.register(WsServer.it());
        context = getContext();
        int id =getContext().getResources().getIdentifier("LOG_PORT_NUMBER","integer",getContext().getPackageName());
        if (id!= Resources.ID_NULL){
            portNumber = getContext().getResources().getInteger(id);
        }
        try {
            new HttpServer(getContext(),portNumber).start();
            id = getContext().getResources().getIdentifier("SHOW_DEVICE_IP","bool",getContext().getPackageName());
            if (id!= Resources.ID_NULL){
                showNotification = getContext().getResources().getBoolean(id);
            }
            if (showNotification){
                registerEvent();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    @Override
    public Cursor query( Uri uri,  String[] strings,  String s,  String[] strings1,
         String s1) {
        return null;
    }

    
    @Override
    public String getType( Uri uri) {
        return null;
    }

    
    @Override
    public Uri insert( Uri uri,  ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete( Uri uri,  String s,  String[] strings) {
        return 0;
    }

    @Override
    public int update( Uri uri,  ContentValues contentValues,  String s,  String[] strings) {
        return 0;
    }

    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);
    }


    private void showNotification(String ip) {
                final Context context = getContext();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int icon = context.getApplicationInfo().icon;
        Builder n = null;
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            String channelId = "channelId";
            NotificationChannel mChannel = new NotificationChannel(channelId, "name", NotificationManager.IMPORTANCE_LOW);
            mChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
//            Toast.makeText(this, mChannel.toString(), Toast.LENGTH_SHORT).show();
            manager.createNotificationChannel(mChannel);
            n = new Builder(context,channelId);
        } else {
            n = new Builder(context);
            if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                n.setPriority(Notification.PRIORITY_MAX);
            }
        }
        n.setContentTitle("Debug Logger")
            .setSmallIcon(icon)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(false);
        if (ip == null){
            n.setContentText("no ip");
        } else {
            n.setContentText("http://"+ip+":"+portNumber);
        }
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = n.build();
        } else {
            notification = n.getNotification();
        }
        notification.flags |= Notification.FLAG_NO_CLEAR;
        manager.notify(-22, notification);
    }

    private void registerEvent() {

        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            networkChanged23();
        } else {
            networkChanged21();
        }

    }
    private static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }
    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    void networkChanged23(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        NetworkRequest request = builder.build();
        connectivityManager.registerNetworkCallback(request, new NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                onIpChanged();
            }
        });
    }
    void networkChanged21(){
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onIpChanged();
            }
        }, intentFilter);
    }
    private void onIpChanged() {
        String ip = getIp();
        showNotification(ip);
    }
    private String getIp() {
        WifiManager wifiManager  = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null)return null;
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        if (connectionInfo == null)return null;
        int ip = connectionInfo.getIpAddress();
        if (ip == 0)return null;
        return int2ip(ip);
    }
}
