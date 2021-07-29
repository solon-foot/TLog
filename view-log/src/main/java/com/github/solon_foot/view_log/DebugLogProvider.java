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
    private final static String NOTIFY_ACTION = "com.github.solon_foot.view_log.notify";
    @Override
    public boolean onCreate() {
//        final FloatUtilManager floatUtilManager = new FloatUtilManager(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getContext())) {
//      Toast.makeText(context, "SoGameDebug功能需要打开悬浮窗权限才能使用", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//            intent.setData(Uri.parse("package:" + context.getPackageName()));
//            context.startActivity(intent);
        }
//        TLog.register(floatUtilManager);
        TLog.register(new FloatView(getContext()));
//        final Context context = getContext();
//        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        int icon = context.getApplicationInfo().icon;
//        Notification.Builder n = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            String channelId = "channelId";
//            NotificationChannel mChannel = new NotificationChannel(channelId, "name", NotificationManager.IMPORTANCE_LOW);
//            mChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
////            Toast.makeText(this, mChannel.toString(), Toast.LENGTH_SHORT).show();
//            manager.createNotificationChannel(mChannel);
//            n = new Notification.Builder(context,channelId);
//        } else {
//            n = new Notification.Builder(context);
//            n.setPriority(Notification.PRIORITY_MAX);
//        }
//        Intent intent = new Intent(NOTIFY_ACTION);
//        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
//        n.setContentTitle("Debug Logger")
//            .setContentText("点击显示或隐藏log")
//            .setSmallIcon(icon)
//            .setContentIntent(pi)
//            .setDefaults(Notification.DEFAULT_ALL)
//            .setWhen(System.currentTimeMillis())
//            .setAutoCancel(false);
//        Notification notification = n.build();
//        notification.flags |= Notification.FLAG_NO_CLEAR;
//        manager.notify(22, notification);
//        context.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                floatUtilManager.toggleLog();
//            }
//        },new IntentFilter(NOTIFY_ACTION));
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
