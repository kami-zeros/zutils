package com.zxx.zutils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 请求权限工具
 */

public class PermissionUtils {
    private static final String TAG = "Tag-PermissionUtils-";

	 //此处数字是对应的String[] requestPermissions
    public static final int CODE_RECORD_AUDIO = 0;              //录制音频
    public static final int CODE_GET_ACCOUNTS = 1;              //通讯录
    public static final int CODE_READ_PHONE_STATE = 2;
    public static final int CODE_CALL_PHONE = 3;                //电话
    public static final int CODE_CAMERA = 4;                    //拍摄照片与录制视屏
    public static final int CODE_ACCESS_FINE_LOCATION = 5;
    public static final int CODE_ACCESS_COARSE_LOCATION = 6;    //位置信息
    public static final int CODE_READ_EXTERNAL_STORAGE = 7;     //设备文件、内容、照片
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 8;
    public static final int CODE_MULTI_PERMISSION = 100;

    private static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    private static final String PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
    private static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    private static final String PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE;
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String PERMISSION_READ_LOGS = Manifest.permission.READ_LOGS;
    private static final String PERMISSION_SET_DEBUG_APP = Manifest.permission.SET_DEBUG_APP;
    private static final String PERMISSION_SYSTEM_ALERT_WINDOW = Manifest.permission.SYSTEM_ALERT_WINDOW;
    private static final String PERMISSION_WRITE_APN_SETTINGS = Manifest.permission.WRITE_APN_SETTINGS;

    private static final String[] requestPermissions = {
            PERMISSION_RECORD_AUDIO,
            PERMISSION_GET_ACCOUNTS,
            PERMISSION_READ_PHONE_STATE,
            PERMISSION_CALL_PHONE,
            PERMISSION_CAMERA,
            PERMISSION_ACCESS_FINE_LOCATION,
            PERMISSION_ACCESS_COARSE_LOCATION,
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE
    };

    //相机与读写文件请求
    public static final String[] requestCameraRead = {
            PERMISSION_CAMERA,
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE
    };

    //分享权限
    public static final String[] requestShare = {
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE,
//            PERMISSION_ACCESS_FINE_LOCATION,
//            PERMISSION_CALL_PHONE,
//            PERMISSION_READ_LOGS,
            PERMISSION_READ_PHONE_STATE,
//            PERMISSION_SET_DEBUG_APP,
//            PERMISSION_SYSTEM_ALERT_WINDOW,
//            PERMISSION_GET_ACCOUNTS,
//            PERMISSION_WRITE_APN_SETTINGS
    };

    //地图文件请求
    public static final String[] requestMap = {
            PERMISSION_ACCESS_COARSE_LOCATION,
            PERMISSION_ACCESS_FINE_LOCATION,
            PERMISSION_READ_PHONE_STATE
    };



    /**
     * 1. 请求单个权限
     * 单个权限请求码必须为上述定义的请求码
     * @param requestCode     上述定义的请求码 int 类型（不能自定义）
     * @param permissionGrant 接口
     */
    public static void requestPermission(Activity activity, int requestCode, PermissionGrant permissionGrant) {
        if (activity == null) {
            return;
        }
        Log.e(TAG, "requestPermission requestCode:" + requestCode);
        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            return;
        }

        String requestPermission = requestPermissions[requestCode];

        /**
         * 如果是6.0以下的手机，ActivityCompat.checkSelfPermission()会始终等于PERMISSION_GRANTED，
         但是，如果用户关闭了你申请的权限，ActivityCompat.checkSelfPermission(),会导致程序崩溃(java.lang.RuntimeException: Unknown exception code: 1 msg null)，
         你可以使用try{}catch(){},处理异常，也可以判断系统版本，低于23就不申请权限，直接做你想做的。permissionGrant.onPermissionGranted(requestCode);
         //        if (Build.VERSION.SDK_INT < 23) {
         //            permissionGrant.onPermissionGranted(requestCode);
         //            return;
         //        }
         */
        int checkSelfPermission;
        try {
            checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
        } catch (RuntimeException e) {
            ToastUtil.ToastMsg(activity, "请打开权限");
            Log.e(TAG, "requestPermission：RuntimeException:" + e.getMessage());
            return;
        }
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                //打开权限提示
//                shouldShowRationale(activity, requestCode, requestPermission);
                ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
            }
        } else {
//            ToastUtil.ToastMsg(activity, requestPermissions[requestCode]);
            //Build.VERSION.SDK_INT < 23的处理
            permissionGrant.onPermissionGranted(requestCode);
        }
    }


    /**
     * 2. 一次申请多个请求
     *  可以自定义多个权限数组,如上述：“相机与读写文件请求”
     * @param requestCode 可以自定义请求码
     * @param permissionGrant 接口  API=23室6.0
     */
    public static void requestMultiPermissions(final Activity activity, String[] requestMultiPermissions, int requestCode, PermissionGrant permissionGrant) {
        if (Build.VERSION.SDK_INT >= 23) {
            final List<String> permissionsList = getNoGrantedPermission(activity, requestMultiPermissions, false);
            final List<String> shouldRationalePermissionsList = getNoGrantedPermission(activity,requestMultiPermissions, true);

            if (permissionsList == null || shouldRationalePermissionsList == null) {
                return;
            }

            if (permissionsList.size() > 0) {
                ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), requestCode);
                Log.e(TAG, "showMessageOKCancel requestPermissions");

            } else if (shouldRationalePermissionsList.size() > 0) {
                ActivityCompat.requestPermissions(activity, shouldRationalePermissionsList.toArray(new String[shouldRationalePermissionsList.size()]), requestCode);
                Log.d(TAG, "showMessageOKCancel requestPermissions");

            } else {
                permissionGrant.onPermissionGranted(requestCode);
            }
        } else {
            permissionGrant.onPermissionGranted(requestCode);
        }
    }


    //提示信息（弹窗很丑）
    private static void shouldShowRationale(final Activity activity, final int requestCode, final String requestPermission) {
//        String[] permissionsHint = activity.getResources().getStringArray(R.array.permissions);
//        String permissionsHint = activity.getResources().getString(R.string.permission_request);
        String permissionsHint = "没有此权限，无法开启这个功能，请开启权限。";
        showMessageOKCancel(activity, permissionsHint, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
            }
        });
    }


    //弹窗（弹窗很丑）
    private static void showMessageOKCancel(Activity context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", null)
                .create()
                .show();
    }


    //@param isShouldRationale true: return no granted and shouldShowRequestPermissionRationale permissions
    //                          false:return no granted and !shouldShowRequestPermissionRationale
    public static ArrayList<String> getNoGrantedPermission(Activity activity, String[] requestMultiPermissions, boolean isShouldRationale) {

        ArrayList<String> permissions = new ArrayList<>();

        for (int i = 0; i < requestMultiPermissions.length; i++) {
            String requestPermission = requestMultiPermissions[i];

            int checkSelfPermission = -1;
            try {
                checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
//                Log.e(TAG, "check：" + i + "::" + checkSelfPermission);
//                Log.e(TAG, "GRANTED：" + i + "::" + PackageManager.PERMISSION_GRANTED);
            } catch (RuntimeException e) {
                Log.e(TAG, "getNoGrantedPermission：RuntimeException:" + e.getMessage());
                return null;
            }

            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                    if (isShouldRationale) {
                        permissions.add(requestPermission);
                    }
                } else {
                    if (!isShouldRationale) {
                        permissions.add(requestPermission);
                    }
                }
            }
        }
        return permissions;
    }


    /*****-------------*****/
    //打开手机设置权限窗口
    private static void openSettingActivity(final Activity activity, String message) {
        showMessageOKCancel(activity, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        });
    }

    public interface PermissionGrant {
        void onPermissionGranted(int requestCode);
    }
}

