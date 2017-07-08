package com.viseator.hackinit20.data;

import java.util.List;

/**
 * Created by viseator on 7/8/17.
 *
 * @author Wu Di
 *         Email: viseator@gmail.com
 */

public class DataBean {

    /**
     * code : 1
     * message : string
     * gesture : 1
     * app : [{"name":"king","time":"11111","isOpen":"false"},{"name":"yin","time":"11111","isOpen":"true"}]
     */

    private int code;
    private String message;
    private int gesture;
    private List<AppBean> app;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getGesture() {
        return gesture;
    }

    public void setGesture(int gesture) {
        this.gesture = gesture;
    }

    public List<AppBean> getApp() {
        return app;
    }

    public void setApp(List<AppBean> app) {
        this.app = app;
    }

    public static class AppBean {
        /**
         * name : king
         * time : 11111
         * isOpen : false
         */

        private String name;
        private long time;
        private boolean isOpen;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public boolean isIsOpen() {
            return isOpen;
        }

        public void setIsOpen(boolean isOpen) {
            this.isOpen = isOpen;
        }
    }
}
