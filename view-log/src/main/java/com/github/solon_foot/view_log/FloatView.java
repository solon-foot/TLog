package com.github.solon_foot.view_log;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.solon_foot.TLog;
import com.github.solon_foot.TLog.PrintProxy;
import java.util.Calendar;

public class FloatView extends FrameLayout implements PrintProxy {

    ImageView btnOk;
    View secondView;
    ListView listView;
    ArrayAdapter<Model> adapter;
    private static final int[] LOG_COLORS = {Color.WHITE,Color.WHITE,
        0xFFbbbbbb,0xFF0070bb,0xFF48bb31,0xFFbbbb23,0xFFff5370,0xFF8f0005
    };

    WindowManager windowManager;
    WindowManager.LayoutParams params;

    Point screenSize = new Point();
    final int viewWidth;
    public FloatView(Context context) {
        super(context);
        viewWidth = (int) (getResources().getDisplayMetrics().density * 50);
        init(context, viewWidth);
//        setLayoutParams(new LayoutParams(width, width));
        btnOk = new ImageView(context);
//        btnOk.setBackgroundResource(R.drawable.icon_btn);
        btnOk.setBackgroundResource(R.drawable.view_log_bg_log);
        btnOk.setImageResource(R.drawable.view_log_icon_log);
        btnOk.setPadding(viewWidth/5,viewWidth/5,viewWidth/5,viewWidth/5);
        btnOk.setOnClickListener(v -> {
            btnOk.setVisibility(GONE);
            secondView.setVisibility(VISIBLE);
            params.width = LayoutParams.MATCH_PARENT;
            params.height = LayoutParams.MATCH_PARENT;
            windowManager.updateViewLayout(this, params);
        });
        LayoutParams params = new LayoutParams(viewWidth,viewWidth);
        params.gravity = Gravity.CENTER;
        addView(btnOk, params);

        initSecondView(context);
    }
    private void copy(String data){
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("debug log",data);
        clipboardManager.setPrimaryClip(clipData);
    }
    private void initSecondView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        secondView = inflater.inflate(R.layout.view_log_layout_log, this, false);
        listView = secondView.findViewById(R.id.list_view);
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                copy(adapter.getItem(position).msg);
                Toast.makeText(getContext(),"Copied",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        adapter = new ArrayAdapter<Model>(context, 0){
            int padding = (int) (getResources().getDisplayMetrics().density*4);
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView =null;
                if (convertView==null){
                    textView = new TextView(parent.getContext());
                    textView.setPadding(padding,padding,padding,0);
                    textView.setTextSize(12);
                }else {
                    textView = (TextView) convertView;
                }
                Model item = getItem(position);
                textView.setText(item.toString());
                textView.setTextColor(LOG_COLORS[item.priority]);
                return textView;
            }

        };
        listView.setAdapter(adapter);
        secondView.findViewById(R.id.close).setOnClickListener(v->{
            params.width = LayoutParams.WRAP_CONTENT;
            params.height = LayoutParams.WRAP_CONTENT;
            windowManager.updateViewLayout(this, params);
            btnOk.setVisibility(VISIBLE);
            secondView.setVisibility(GONE);
        });
        secondView.findViewById(R.id.clear).setOnClickListener(v->{
            adapter.clear();
        });
        secondView.findViewById(R.id.copy).setOnClickListener(v->{
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < adapter.getCount(); i++) {
                Model item = adapter.getItem(i);
                sb.append(item.toString).append('\n');
            }
            copy(sb.toString());
        });
        addView(secondView,0);
        secondView.setVisibility(GONE);
    }

    private void init(Context context, int width) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return;
        }
        params = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //在android7.1以上系统需要使用TYPE_PHONE类型 配合运行时权限
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //在android7.1以上系统需要使用TYPE_PHONE类型 配合运行时权限
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        windowManager.getDefaultDisplay().getSize(screenSize);

        params.width = LayoutParams.WRAP_CONTENT;
        params.height = LayoutParams.WRAP_CONTENT;
        params.y = screenSize.y-width;
        params.x = screenSize.x-width;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
//            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = 0;
        windowManager.addView(this, params);
    }


    float lastX, lastY;
    int tempX, tempY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_MOVE) {
            return super.onTouchEvent(event);
        }
        float x = event.getRawX();
        float y = event.getRawY();
        params.x = (int) (tempX + x - lastX);
        params.y = (int) (tempY + y - lastY);
        if (params.x < 0) {
            params.x = 0;
        }
        if (params.y < 0) {
            params.y = 0;
        }
        if (params.x + params.width > screenSize.x) {
            params.x = screenSize.x - params.width;
        }
        if (params.y + params.height > screenSize.y) {
            params.y = screenSize.y - params.height;
        }
        windowManager.updateViewLayout(this, params);
        return super.onTouchEvent(event);
    }

    private static final int SNAP_VELOCITY = 600;   // 最小滑动速度
    private VelocityTracker mVelocityTracker;   // 速度追踪器
    private boolean mIsSwipe;   // 是否滑动子View


    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        windowManager.getDefaultDisplay().getSize(screenSize);
        params = (WindowManager.LayoutParams) getLayoutParams();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (btnOk.getVisibility()!=VISIBLE) return super.onInterceptTouchEvent(e);
//        if (params.alpha != 1) {
//            params.alpha = 1;
//            windowManager.updateViewLayout(this, params);
//        }
//        mHandler.removeMessages(-1);
//        mHandler.sendEmptyMessageDelayed(-1, 5_000);
        float x = e.getRawX();
        float y = e.getRawY();
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:   // 因为没有拦截，所以不会被调用到
                mIsSwipe = false;

                lastX = x;
                lastY = y;
                tempX = params.x;
                tempY = params.y;
                break;
            case MotionEvent.ACTION_MOVE:

                if (mIsSwipe) {
                    break;
                }
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = mVelocityTracker.getXVelocity();
                float yVelocity = mVelocityTracker.getYVelocity();
                if (Math.abs(xVelocity) > SNAP_VELOCITY || Math.abs(yVelocity) > SNAP_VELOCITY || (e.getEventTime() - e.getDownTime()
                    > 500)) {
                    mIsSwipe = true;
                    return true;
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsSwipe = false;
                mVelocityTracker.clear();
                break;
        }
        return mIsSwipe || super.onInterceptTouchEvent(e);
    }

    public void destory() {
        windowManager.removeView(this);
    }

    @Override
    public void println(int priority, String msg) {
        appendString(new Model(priority,msg));
    }
    private void appendString(Model msg) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            post(() -> appendString(msg));
            return;
        }
       msg.process();
        adapter.add(msg);
    }

    final static class Model {
        final int priority;
        final String msg;
        Thread thread;
        public Model(int priority, String msg) {
            this.priority = priority;
            this.msg = msg;
            this.thread = Thread.currentThread();
        }
        private String toString;
        private static int fixLen = 30;
        private void process() {
            Calendar instance = Calendar.getInstance();
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            process(sb, instance.get(Calendar.HOUR_OF_DAY), 2);
            sb.append(':');
            process(sb, instance.get(Calendar.MINUTE), 2);
            sb.append(':');
            process(sb, instance.get(Calendar.SECOND), 2);
            sb.append(':');
            process(sb, instance.get(Calendar.MILLISECOND), 3);
            sb.append(']');
            sb.append(' ');
            sb.append(thread.getId()).append("/").append(thread.getName());
            if (sb.length()<fixLen){
                for (int i = sb.length(); i < fixLen; i++) {
                    sb.append(' ');
                }
            } else {
                sb.delete(fixLen,sb.length());
            }
            sb.append(' ');
            sb.append(logLevel[priority]).append(':');
            sb.append(msg);
            toString = sb.toString();
        }
        static final char[] logLevel={' ',' ','V','D','I','W','E','A'};
        private void process(StringBuilder sb, int t, int len) {
            int tt = 1;
            while (len > 1) {
                tt *= 10;
                len--;
            }
            while (t < tt) {
                sb.append('0');
                tt /= 10;
            }
            sb.append(t);
        }

        @Override
        public String toString() {
            return toString;
        }
    }
}
