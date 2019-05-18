package com.zxx.zutils;

import android.content.Context;

/**
 * @author zqq on 2019/5/18.
 */
public class ZUtils {

    private static Context appContext ;

    public static void init(Context context) {
        if (null != context) {
            appContext = context;
        }
    }

}
