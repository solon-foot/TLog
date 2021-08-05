package com.github.solon_foot.log;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.github.solon_foot.TLog;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SampleActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Map map = new HashMap();
        map.put("a",(byte)0xf2);
        map.put("b",new int[]{1,2,3,4,5,6});
        List list = Arrays.asList(map,map);
        TLog.e(list);
//        TLog.init("APP_NAME", Log.INFO);


    }
    public void testLog(View v){
        TLog.e("on button click");
        TLog.i();
        TLog.v();
        TLog.w();
        TLog.d();

    }
}
