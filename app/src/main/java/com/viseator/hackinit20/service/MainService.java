package com.viseator.hackinit20.service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.jaredrummler.android.processes.ProcessManager;
import com.viseator.hackinit20.R;
import com.viseator.hackinit20.data.ProcessInfo;
import com.viseator.hackinit20.data.UDPDataPackage;
import com.viseator.hackinit20.network.ComUtil;
import com.viseator.hackinit20.util.ConvertData;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by viseator on 7/8/17.
 *
 * @author Wu Di
 *         Email: viseator@gmail.com
 */

public class MainService extends Service implements View.OnTouchListener {
    private static final String TAG = "@vir MainService";
    private ComUtil mComUtil;
    private WindowManager mWindowManager;
    private HashMap<String, ProcessInfo> processinfors;
    private WindowManager.LayoutParams mLayoutParams;
    public String ipAddress;
    private int initX = 0;
    private int initY = 0;
    private int lastX = 0;
    private int lastY = 0;
    private boolean isDragging = false;
    View mContentView;
    @BindView(R.id.test)
    ImageView mImageView;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ComUtil.BROADCAST_PORT:
                    UDPDataPackage result = (UDPDataPackage) (ConvertData.byteToObject((byte[]) msg.obj));
                    ipAddress = result.getIpAddress();
                    Log.d(TAG, ipAddress);
                    mComUtil.broadCast(ConvertData.objectToByte(new UDPDataPackage(getApplicationContext())));
                    break;
            }
            return true;
        }
    });

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showBubble();

        initNetwork();
        processinfors = new HashMap<>();
        PackageManager manager = getPackageManager();

        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos;
        if (Build.VERSION.SDK_INT < 22) {
            runningAppProcessInfos = ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getRunningAppProcesses();
        } else {
            runningAppProcessInfos = ProcessManager.getRunningAppProcessInfo(this);
        }
        if (runningAppProcessInfos != null) {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos) {
                int pid = Process.myPid();

                if (pid != runningAppProcessInfo.pid) {
                    ProcessInfo processInfo = new ProcessInfo();
                    processInfo.setPid(pid);
                    String name = null;
                    try {
                        ApplicationInfo info = manager.getApplicationInfo(runningAppProcessInfo.pkgList != null &&
                                runningAppProcessInfo.pkgList.length > 0 ? runningAppProcessInfo.pkgList[0] : runningAppProcessInfo.processName, 0);
                        name = (String) manager.getApplicationLabel(info);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (name == null) {
                        name = runningAppProcessInfo.processName;
                    }
                    processInfo.setName(name);
                    processInfo.setBegin_time(System.currentTimeMillis());
                    processInfo.setRunning_time(0);
                    processinfors.put(runningAppProcessInfo.processName, processInfo);

                    Log.d(TAG, "进程名: " + processInfo.getName());
                    Log.d(TAG, "进程pid: " + processInfo.getPid() + " ");
                    Log.d(TAG, "进程包名: " + runningAppProcessInfo.processName);
                }
            }
        }
        getUsage();
    }

    private void showBubble() {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mContentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout
                .bubble_layout, null, false);
        ButterKnife.bind(this, mContentView);
        mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mLayoutParams.gravity = Gravity.TOP | Gravity.START;

        mContentView.setOnTouchListener(this);

        mWindowManager.addView(mContentView, mLayoutParams);
        mImageView.setImageDrawable(getDrawable(R.drawable.star));
        Log.d(TAG, String.valueOf("add View"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mContentView != null) {
            mWindowManager.removeView(mContentView);
        }
    }

    private void getUsage() {
        Calendar callendar = Calendar.getInstance();
        long end = callendar.getTimeInMillis();
        callendar.add(Calendar.MINUTE, -1);
        long start = callendar.getTimeInMillis();
        UsageStatsManager manage = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        List<UsageStats> stats = manage.queryUsageStats(UsageStatsManager.INTERVAL_BEST, start, end);
        for (UsageStats stat : stats) {
            String name = stat.getPackageName();
            ProcessInfo info = processinfors.get(name);
            info.setRunning_time(info.getRunning_time() + stat.getLastTimeUsed());
            Log.e(TAG, "运行时间：" + info.getRunning_time());
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDragging = true;
                initX = rawX;
                initY = rawY;
                lastX = rawX;
                lastY = rawY;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isDragging) {
                    break;
                }
                mLayoutParams.x = (mLayoutParams.x + rawX - lastX);
                mLayoutParams.y = (mLayoutParams.y + rawY - lastY);
                mWindowManager.updateViewLayout(mContentView, mLayoutParams);
                lastX = rawX;
                lastY = rawY;
                break;
        }
        return true;
    }

    private void initNetwork() {
        mComUtil = new ComUtil(mHandler);
        mComUtil.startReceiveMsg();
    }
}
