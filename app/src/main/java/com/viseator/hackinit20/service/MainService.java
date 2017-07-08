package com.viseator.hackinit20.service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jaredrummler.android.processes.ProcessManager;
import com.viseator.hackinit20.data.ProcessInfo;

import android.app.usage.UsageStats;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by viseator on 7/8/17.
 *
 * @author Wu Di
 *         Email: viseator@gmail.com
 */

public class MainService extends Service {
    private HashMap<String, ProcessInfo> processinfors;
    private static final String TAG = "MainService";
    @Override
    public void onCreate() {

        super.onCreate();
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
                    Log.d(TAG, "进程名: "+processInfo.getName());
                    Log.d(TAG, "进程pid: "+processInfo.getPid()+" ");
                    Log.d(TAG, "进程包名: "+ runningAppProcessInfo.processName);
                }
            }
        }
        getUsage();
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



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
