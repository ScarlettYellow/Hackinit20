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
    private long end_time;
    private String begin_time_text;
    private String end_time_text;

    public String getBegin_time_text() {
        return begin_time_text;
    }

    public void setBegin_time_text(String begin_time_text) {
        this.begin_time_text = begin_time_text;
    }

    public String getEnd_time_text() {
        return end_time_text;
    }

    public void setEnd_time_text(String end_time_text) {
        this.end_time_text = end_time_text;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

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
