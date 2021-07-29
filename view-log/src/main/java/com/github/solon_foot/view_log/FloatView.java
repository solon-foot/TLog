package com.github.solon_foot.view_log;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.github.solon_foot.TLog;
import com.github.solon_foot.TLog.PrintProxy;
import java.util.Calendar;

public class FloatView extends FrameLayout implements PrintProxy {

    TextView btnOk;
    View secondView;
    ListView listView;
    ArrayAdapter<String> adapter;

    WindowManager windowManager;
    WindowManager.LayoutParams params;

    Point screenSize = new Point();
    final int viewWidth;
    public FloatView(Context context) {
        super(context);
        viewWidth = (int) (getResources().getDisplayMetrics().density * 50);
        init(context, viewWidth);
//        setLayoutParams(new LayoutParams(width, width));
        btnOk = new TextView(context);
//        btnOk.setBackgroundResource(R.drawable.icon_btn);
        btnOk.setBackgroundColor(Color.RED);
        btnOk.setTextColor(Color.BLACK);
        btnOk.setText("OK");
        btnOk.setTextSize(18);
        btnOk.setGravity(Gravity.CENTER);
        btnOk.setOnClickListener(v -> {
            params.width = LayoutParams.MATCH_PARENT;
            params.height = LayoutParams.MATCH_PARENT;
            windowManager.updateViewLayout(this, params);
            btnOk.setVisibility(GONE);
            secondView.setVisibility(VISIBLE);
        });
        LayoutParams params = new LayoutParams(viewWidth,viewWidth);
        params.gravity = Gravity.CENTER;
        addView(btnOk, params);

        initSecondView(context);
    }

    private void initSecondView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        secondView = inflater.inflate(R.layout.layout_log, this, false);
        listView = secondView.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        secondView.findViewById(R.id.close).setOnClickListener(v->{
            params.width = LayoutParams.WRAP_CONTENT;
            params.height = LayoutParams.WRAP_CONTENT;
            windowManager.updateViewLayout(this, params);
            btnOk.setVisibility(VISIBLE);
            secondView.setVisibility(GONE);
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

        params.width = width;
        params.height = width;
//        params.y = screenSize.y-width;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
//            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = 0;
        windowManager.addView(this, params);
    }


    private void setViewAlpha(float alpha) {
        getHandler().post(() -> {
            params.alpha = alpha;
            windowManager.updateViewLayout(this, params);
        });
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

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
        appendString(msg);
    }
    StringBuilder sb = new StringBuilder();
    private void appendString(String msg) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            post(() -> appendString(msg));
            return;
        }
        Calendar instance = Calendar.getInstance();
        sb.setLength(0);
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
        sb.append(msg);
        sb.append('\n');
        adapter.add(sb.toString());
    }
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
}
