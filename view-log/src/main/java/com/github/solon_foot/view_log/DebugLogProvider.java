package com.github.solon_foot.view_log;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import com.github.solon_foot.TLog;

public class DebugLogProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getContext())) {
//            Intent intent = new Intent();
//            intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getContext().getPackageName()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
        try {
            TLog.register(new FloatView(getContext()));
        }catch (Exception e){}

        return true;
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
