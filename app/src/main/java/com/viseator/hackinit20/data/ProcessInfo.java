package com.viseator.hackinit20.data;

/**
 * Created by victor on 7/8/17.
 * email: chengyiwang@hustunique.com
 * blog: www.victorwang.science                                            #
 */

public class ProcessInfo {
    private String name;
    private long begin_time;
    private long running_time;
    private int pid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(long begin_time) {
        this.begin_time = begin_time;
    }

    public long getRunning_time() {
        return running_time;
    }

    public void setRunning_time(long running_time) {
        this.running_time = running_time;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
}
