package com.viseator.hackinit20.service;

import android.app.ActivityManager;
import android.app.Service;
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

import com.jaredrummler.android.processes.ProcessManager;
import com.viseator.hackinit20.R;
import com.viseator.hackinit20.data.FilterApps;
import com.viseator.hackinit20.data.ProcessInfo;
import com.viseator.hackinit20.data.UDPDataPackage;
import com.viseator.hackinit20.network.ComUtil;
import com.viseator.hackinit20.network.GetNetworkInfo;
import com.viseator.hackinit20.util.ConvertData;
import com.viseator.hackinit20.utils.DateUtils;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
    private HashMap<String, ProcessInfo> processinfors, oldprocessinfo;
    private WindowManager.LayoutParams mLayoutParams;
    public String ipAddress;
    private boolean ipGot = false;
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
                    String ip = new String((byte[]) msg.obj, ComUtil.CHARSET);
                    if (!ip.equals(GetNetworkInfo.getIp(getApplicationContext())
                    )) {
                        ipAddress = ip;
                        Log.d(TAG, ipAddress);
                        mComUtil.broadCast(new String(GetNetworkInfo.getIp(getApplicationContext
                                ())).getBytes(ComUtil.CHARSET));
                        ipGot = true;
                    }
                    break;
            }
            return true;
        }
    });

    private Handler handler = new Handler();

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
        oldprocessinfo = new HashMap<>();
        handler.postDelayed(getAppRunninginfo, 10000);
    }

    private Runnable getAppRunninginfo = new Runnable() {
        @Override
        public void run() {
            PackageManager manager = getPackageManager();
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos;
            //清空原来数据
            processinfors.clear();
            if (Build.VERSION.SDK_INT < 22) {
                runningAppProcessInfos = ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getRunningAppProcesses();
            } else {
                runningAppProcessInfos = ProcessManager.getRunningAppProcessInfo(MainService.this);
            }
            if (runningAppProcessInfos != null) {
                for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos) {
                    int pid = Process.myPid();
                    if (pid != runningAppProcessInfo.pid && FilterApps.contains(runningAppProcessInfo.processName)) {
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
                        processinfors.put(runningAppProcessInfo.processName, processInfo);
                    }
                }
            }
            Set<String> oldkeys = oldprocessinfo.keySet();
            for (String s : oldkeys) {
                if (processinfors.get(s) == null) {
                    ProcessInfo info = oldprocessinfo.get(s);
                    long endtime = System.currentTimeMillis();
                    info.setEnd_time(endtime);
                    info.setEnd_time_text(DateUtils.formatToNormal(endtime));
                    info.setRunning_time(info.getEnd_time() - info.getBegin_time());
                    Log.e(TAG, "取消进程名: " + info.getName());
                    Log.e(TAG, "取消进程pid: " + info.getPid() + " ");
                    Log.e(TAG, "取消进程包名: " + s);
                    // TODO: 7/9/17 remove data from database status:close
                }
            }
            Set<String> currentkeys = processinfors.keySet();
            for (String s : currentkeys) {
                if (oldprocessinfo.get(s) == null) {
                    ProcessInfo info = processinfors.get(s);
                    long begin_time = System.currentTimeMillis();
                    info.setBegin_time(begin_time);
                    info.setBegin_time_text(DateUtils.formatToNormal(begin_time));
                    Log.e(TAG, "打开进程名: " + info.getName());
                    Log.e(TAG, "打开进程pid: " + info.getPid() + " ");
                    Log.e(TAG, "打开进程包名: " + s);
                    // TODO: 7/9/17 add data from database status: open
                }
            }
            oldprocessinfo = (HashMap<String, ProcessInfo>) processinfors.clone();
            //每隔10秒钟更新一次
            handler.postDelayed(getAppRunninginfo, 10000);
        }
    };

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
