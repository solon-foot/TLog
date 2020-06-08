package com.github.solon_foot;

import android.util.Log;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TLog {

    private static final int LOG_MAX_LEN = 1024 * 4 - 100;
    private static String LOG_TAG = "=>";
    private static int DebugLevel = Log.VERBOSE;

    public interface ObjectToString {

        boolean process(StringBuilder sb, Object o);
    }

    static List<ObjectToString> mList = new ArrayList<>();

    static {
        final char[] digits = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'
        };
        mList.add(new ObjectToString() {
            @Override
            public boolean process(StringBuilder sb, Object o) {
                if (!(o instanceof Byte)) {
                    return false;
                }
                int t = (Byte) o & 0xFF;
                sb.append(digits[t >> 4 & 0xF]);
                sb.append(digits[t & 0xF]);
                return true;
            }
        });


    }

    public static void init(String TAG, int debugLevel) {
        DebugLevel = debugLevel;
        LOG_TAG = TAG;
    }

    public static void regist(ObjectToString objectToString) {
        mList.add(0, objectToString);
    }

    public static void v(Object... args) {
        println(Log.VERBOSE, args);
    }

    public static void d(Object... args) {
        println(Log.DEBUG, args);
    }

    public static void i(Object... args) {
        println(Log.INFO, args);
    }

    public static void w(Object... args) {
        println(Log.WARN, args);
    }

    public static void e(Object... args) {
        println(Log.ERROR, args);
    }

    private static String stackTrace(int t) {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        StringBuilder sb = new StringBuilder("打印调用堆栈\n");
        for (int i = 1; i < trace.length && t > 0; i++) {
            if (trace[i].getClassName().equals(TLog.class.getName())) {
                continue;
            }
            t--;
            sb.append("=> ");
            sb.append(toSteString(trace[i]));
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void trace(int t) {
        Log.e(LOG_TAG, stackTrace(t));
    }


    private static String toSteString(StackTraceElement trace) {
        StringBuilder result = new StringBuilder();
        result.append(trace.getClassName()).append(".");
        result.append(trace.getMethodName());
        if (trace.isNativeMethod()) {
            result.append("(Native Method)");
        } else if (trace.getFileName() != null) {
            if (trace.getLineNumber() >= 0) {
                result.append("(").append(trace.getFileName()).append(":")
                    .append(trace.getLineNumber()).append(")");
            } else {
                result.append("(").append(trace.getFileName()).append(")");
            }
        } else {
            if (trace.getLineNumber() >= 0) {
                // The line number is actually the dex pc.
                result.append("(Unknown Source:").append(trace.getLineNumber()).append(")");
            } else {
                result.append("(Unknown Source)");
            }
        }
        return result.toString();
    }

    private static String getLineInfo() {
        StackTraceElement[] traces = new Throwable().fillInStackTrace().getStackTrace();
        if (traces.length > 3) {
            StackTraceElement trace = traces[3];
            StringBuilder result = new StringBuilder();
            if (trace.getLineNumber() >= 0) {
                result.append("(").append(trace.getFileName()).append(":")
                    .append(trace.getLineNumber()).append(")");
            } else {
                result.append("(").append(trace.getFileName()).append(")");
            }
            result.append("#").append(trace.getMethodName());
            return result.toString();

//            return toSteString(trace);
        }
        return "(Unknown Source)";
    }

    private static int println(int priority, Object... msgs) {
        if (DebugLevel > priority) {
            return 0;
        }
        if (msgs == null) {
            return Log.println(priority, LOG_TAG, "null");
        }
        if (msgs.length == 0) {
            return Log.println(priority, LOG_TAG, stackTrace(5));
        }
//        String tags = LOG_TAG + getLineNumber();
        StringBuilder sb = new StringBuilder(getLineInfo());
        int len = 40 - sb.length();//保持输出数据对齐
        for (int i = 0; i < len; i++) {
            sb.append(' ');
        }
        sb.append("=> ");
        for (Object msg : msgs) {
            objectToStr(sb, msg);
            sb.append(" ");
        }

        if (sb.length() > LOG_MAX_LEN) {
            int l = sb.length();
            for (int i = 0; i < l; i += LOG_MAX_LEN) {
                Log.println(priority, LOG_TAG, sb.substring(i, Math.min(LOG_MAX_LEN + i, l)));
            }
            return 0;
        } else {
            return Log.println(priority, LOG_TAG, sb.toString());
        }
    }

    public static String toString(Object o) {
        StringBuilder stringBuilder = new StringBuilder();
        TLog.objectToStr(stringBuilder, o);
        return stringBuilder.toString();
    }

    public static void objectToStr(StringBuilder sb, Object o) {
        if (o == null) {
            sb.append("null");
        }
        for (ObjectToString objectToString : mList) {
            if (objectToString.process(sb, o)) {
                return;
            }
        }
        if ((o instanceof Throwable)) {

            Throwable e = (Throwable) o;
            sb.append(e.toString());
            StackTraceElement[] trace = e.getStackTrace();
            for (int i = 0; i < trace.length; i++) {
                sb.append("\n=> ");
                sb.append(toSteString(trace[i]));
            }
            return;
        }
        if (o.getClass().isArray()) {

            int len = Array.getLength(o);
            if (len == 0) {
                sb.append("[]");
                return;
            }
            len -= 1;
            sb.append('[');
            for (int i = 0; ; i++) {
                objectToStr(sb, Array.get(o, i));
                if (i == len) {
                    sb.append(']');
                    return;
                }
                sb.append(',').append(' ');
            }
        }
        if ((o instanceof Iterable)) {
            objectToStr(sb, ((Iterable) o).iterator());
            return;
        }
        if ((o instanceof Iterator)) {

            Iterator it = (Iterator) o;
            if (!it.hasNext()) {
                sb.append("[]");
                return;
            }

            sb.append('[');
            for (; ; ) {
                Object e = it.next();
                objectToStr(sb, e);
                if (!it.hasNext()) {
                    sb.append(']');
                    return;
                }
                sb.append(',').append(' ');
            }
        }
        if ((o instanceof Map)) {

            Map map = (Map) o;
            Iterator<Entry> i = map.entrySet().iterator();
            if (!i.hasNext()) {
                sb.append("{}");
                return;
            }

            sb.append('{');
            for (; ; ) {
                Entry e = i.next();
                Object key = e.getKey();
                Object value = e.getValue();
                objectToStr(sb, key);
                sb.append('=');
                objectToStr(sb, value);
                if (!i.hasNext()) {
                    sb.append('}');
                    return;
                }
                sb.append(',').append(' ');
            }
        }
        sb.append(o);
    }
}
