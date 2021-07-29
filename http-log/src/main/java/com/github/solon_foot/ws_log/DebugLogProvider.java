package com.github.solon_foot.ws_log;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import com.github.solon_foot.TLog;
import java.io.IOException;

public class DebugLogProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        TLog.register(WsServer.it());
        int portNumber = Integer.valueOf(getContext().getString(R.string.LOG_PORT_NUMBER));
        try {
            new HttpServer(getContext(),portNumber).start();
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
}
