package com.zxx.zutils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;


/**
 * toast 工具类 on 2017/9/19.
 */

public class ToastUtil {

    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mToast.cancel();
        }
    };


    /**
     * 显示吐司
     * @param text 文本
     * @param duration 时长
     */
    private static void showToast(Context context, String text, int duration) {
        mHandler.removeCallbacks(runnable);
        if (mToast != null) {
            mToast.setText(text);   // 当前token正在显示，直接修改显示的文本
            mToast.setDuration(duration);
        } else {
            /*这样的话，不管传递什么context进来，都只会引用全局唯一的context，不会产生内存泄露*/
            mToast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT);
//            mToast.setGravity(Gravity.CENTER, 0, 0);
        }
        mHandler.postDelayed(runnable, duration);
        mToast.show();
    }

    // 默认duration为1300
    public static void ToastMsg(Context mContext, String text) {
        ToastMsg(mContext, text, 1300);
    }


    public static void ToastMsg(Context context, String str, int duration) {
        showToast(context, str, duration);
    }




}
