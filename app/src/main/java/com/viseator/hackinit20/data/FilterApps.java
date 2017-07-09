package com.viseator.hackinit20.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 7/9/17.
 * email: chengyiwang@hustunique.com
 * blog: www.victorwang.science                                            #
 */

public class FilterApps {
    private static List<String> filerapps = new ArrayList<>();

    public static void init() {
        filerapps.add("cn.wiz.note"); //为知笔记
        filerapps.add("com.tencent.mm"); //微信
        filerapps.add("fm.jihua.kecheng"); //课程格子
        filerapps.add("com.tencent.mobileqq"); //QQ
        filerapps.add("com.tencent.tmgp.sgame"); //王者荣耀
        filerapps.add("com.netease.onmyoji.vivo"); //阴阳师
    }

    public static boolean isQqOrWechat(String packagername) {
        if (packagername.equals("com.tencent.mobileqq")  || packagername.equals("com.tencent.mm")) {
            return true;
        }
        return false;
    }

    public static boolean isOtherApp(String packagename) {
        if (contains(packagename) && !isQqOrWechat(packagename)) {
            return true;
        }
        return false;
    }
    public static boolean contains(String packagename) {
        return filerapps.contains(packagename);
    }
}
