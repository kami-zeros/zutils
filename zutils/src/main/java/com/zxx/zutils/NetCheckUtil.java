package com.zxx.zutils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;

/**
 * 网络检测工具类 on 2017/11/29.
 */

public class NetCheckUtil {

    /**
     * 1. 判断网络连接
     */
    public static boolean isConnectNet(@NonNull Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 2. 判断WIFI是否连接
     */
    public static boolean isWifiConnected(@NonNull Context context) {
        return isConnected(context, ConnectivityManager.TYPE_WIFI);
    }


    /**
     * 3. 判断移动网络是否连接
     */
    public static boolean isMobileConnected(@NonNull Context context) {
        return isConnected(context, ConnectivityManager.TYPE_MOBILE);
    }


    private static boolean isConnected(@NonNull Context context, int type) {
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //检测API是否小于21，因为API21之后getNetworkInfo(int networkType)方法被弃用
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                NetworkInfo networkInfo = manager.getNetworkInfo(type);
                return networkInfo != null && networkInfo.isConnected();

            } else {
                return isConnected(manager, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static boolean isConnected(@NonNull ConnectivityManager manager, int type) {
        Network[] networks = manager.getAllNetworks();
        NetworkInfo networkInfo;

        for (Network network : networks) {
            networkInfo = manager.getNetworkInfo(network);
            if (networkInfo != null && networkInfo.getType() == type && networkInfo.isConnected()) {
                return true;
            }
        }
        return false;
    }
	
	
	  /**
     * 4. 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        try {
            Intent intent = new Intent("/");
            ComponentName cm = new ComponentName("com.android.settings",
                    "com.android.settings.WirelessSettings");
            intent.setComponent(cm);
            intent.setAction("android.intent.action.VIEW");
            activity.startActivityForResult(intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
