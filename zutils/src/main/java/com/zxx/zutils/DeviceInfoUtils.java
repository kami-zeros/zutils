package com.zxx.zutils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Author: zxx
 * Date: 2016/4/27 17:26
 * 手机设备信息手机工具类
 */
public class DeviceInfoUtils {
    /**
     * 获得IMEI号
     * Android 6.0 后动态申请
     * Android 10.0 彻底禁止第三方应用获取设备IMEI即使申请了READ_PHONE_STATE
     */
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                //没有授权
                String imei = tm.getDeviceId() != null ? tm.getDeviceId() : "";
                if (imei.equals("0")) {
                    imei = "000000000000000";
                }
                int len = 15 - imei.length();
                for (int i = 0; i < len; i++) {
                    imei += "0";
                }
                return imei;
            }
        }
        return "";
    }

    /**
     * 获取deviceId
     * 很多机器是无法获取到的这个值，所以后来改为mac地址
     */
    public static String getIMIEStatus(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return "";
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            //ZLog.e(tm.getDeviceId() + "<--");
            return "" + tm.getDeviceId();
        } else {
            ZLog.e(tm.getDeviceId() + "<--");
            return "" + tm.getDeviceId();
        }
    }

    /**
     * 唯一标识
     * 1.获取IMEI
     * 2.设备序列号
     * 3.MAC地址
     * 4.Android_ID
     * 5.UUId
     */
    public static String getDeviceId(Context context) {
        String deviceId = "";
        try {
            deviceId = getIMIEStatus(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (deviceId == null || "".equals(deviceId)) {
            try {
                //deviceId = getMac(context).replace(":", "");
                deviceId = getMac(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (deviceId == null || "".equals(deviceId)) {
            try {
                deviceId = getAndroidId(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (deviceId == null || "".equals(deviceId)) {
            if (deviceId == null || "".equals(deviceId)) {
                UUID uuid = UUID.randomUUID();
                deviceId = uuid.toString().replace("-", "");
                writeDeviceID(deviceId);
            }
        }
        return deviceId;
    }

    private static void writeDeviceID(String deviceId) {
//        FileOutputStream outputStream=new FileOutputStream(file);
    }

    /**
     * Android Id
     * 刷机、root、恢复出厂设置会改变
     */
    private static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    /**
     * 获取IP地址
     */
    public static String getIP(Context context) {
        int WIFI_IP = getWIFIIP(context);
        String GPRS_IP = getGPRSIP();
        String ip = "0.0.0.0";
        if (WIFI_IP != 0) {
            ip = intToIP(WIFI_IP);
        } else if (!TextUtils.isEmpty(GPRS_IP)) {
            ip = GPRS_IP;
        }
        return ip;
    }

    /**
     * 返回当前版本号名称，例如：V1.0
     */
    public static String getVersionName(Context context) {
        // 默认为1.0
        String ret_val = "V1.0";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            ret_val = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret_val;
    }

    /**
     * 返回当前版本号的值，例如：1
     */
    public static int getVersionCode(Context context) {
        // 默认为1
        int ret_val = 1;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            ret_val = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret_val;
    }

    /**
     * 获取AndroidManifest.xml中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值, 或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context context, String key) {
        if (context == null || TextUtils.isEmpty(key)) {
            return "";
        }
        String resultData = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        if (applicationInfo.metaData.get(key) != null) {
                            resultData = applicationInfo.metaData.get(key).toString();
                        }
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return resultData;
    }

    /**
     * 获得wifi的IP地址
     */
    private static int getWIFIIP(Context context) {
        // 获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getIpAddress();
    }

    /**
     * 获取MAC地址，注意：手机重启，mac地址为null；
     * 00:87:36:43:F8:D9
     * 好像Android 6.0 后通过WifiManager获取到的mac是固定：02:00:00:00:00:00
     */
    public static String getMac(Context context) {
        if (context != null) {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            return info.getMacAddress();
        } else {
            return "";
        }
    }

    /**
     * 解决获取Mac固定值时用的（也只是暂时支持）
     * （没有开启WiFi也可以获取到）
     */
    public static String getWifiMac() {
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            if (enumeration == null) {
                return "";
            }
            while (enumeration.hasMoreElements()) {
                NetworkInterface anInterface = enumeration.nextElement();
                if (anInterface.getName().equals("wlan0")) {
                    return anInterface.getHardwareAddress().toString().replace(":", "");
                }
            }
        } catch (Exception e) {
            ZLog.e(e.getMessage());
        }
        return "";
    }

    /**
     * 整型IP地址转成String的
     */
    private static String intToIP(int IPAddress) {
        return (IPAddress & 0xFF) + "." + ((IPAddress >> 8) & 0xFF) + "." + ((IPAddress >> 16) & 0xFF) + "." + (IPAddress >> 24 & 0xFF);
    }

    /**
     * 获取数据网络的IP地址
     */
    private static String getGPRSIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> addresses = networkInterface.getInetAddresses(); addresses.hasMoreElements(); ) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * https://www.jianshu.com/p/7b919910c892
     * 检查锁屏状态，如果锁屏先点亮屏幕
     */
    private void checkLockAndShowNotification(Context context, String content) {
        //管理锁屏的一个服务
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {//锁屏
            //获取电源管理器对象
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (!pm.isScreenOn()) {
                @SuppressLint("InvalidWakeLockTag")
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
                wl.acquire();  //点亮屏幕
                wl.release();  //任务结束后释放
            }
            //sendNotification(content);
        } else {
            //sendNotification(content);
        }
    }

}
