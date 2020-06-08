package com.github.solon_foot.log;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import com.github.solon_foot.TLog;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SampleActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Map map = new HashMap();
        map.put("a",(byte)0xf2);
        map.put("b",new int[]{1,2,3,4,5,6});
        List list = Arrays.asList(map,map);
        TLog.e(list);
        TLog.init("APP_NAME", Log.INFO);
    }
}
