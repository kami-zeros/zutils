package com.zxx.zutils;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Activity管理器
 *
 * @author zqq on 2018/10/31
 */
public class AppManager {

    //内存泄露
    private static Stack<Activity> mActivityStack;

    /***寄存整个应用Activity**/
//    private final Stack<WeakReference<Activity>> activitys = new Stack<WeakReference<Activity>>();

    /*-单例:静态内部类式-*/
    private AppManager() {
    }

    private static class Holder {
        private static AppManager appManager = new AppManager();
    }

    public static AppManager getInstance() {
        return Holder.appManager;
    }

    public static Stack<Activity> getActivityStack() {
        return mActivityStack;
    }

    public static void setActivityStack(Stack<Activity> activityStack) {
        mActivityStack = activityStack;
    }

    /**
     * 将Activity压入Application栈
     */
    public void pushActivity(Activity activity) {
        if (mActivityStack == null) {
            synchronized (AppManager.class) {
                if (mActivityStack == null) {
                    mActivityStack = new Stack<>();
                }
            }
        }
        mActivityStack.push(activity);
//        mActivityStack.add(activity);
    }


    /**
     * 将传入的Activity对象从栈中移除
     */
    public void removeActivity(Activity activity) {
        if (mActivityStack != null && mActivityStack.size() > 0) {
            if (activity != null) {
                activity.finish();
                mActivityStack.remove(activity);
            }
        }
    }

    /**
     * 获取栈顶的activity，先进后出原则
     */
    public Activity getLastActivity() {
        return mActivityStack.lastElement();
    }

    /**
     * 获取当前activity
     */
    public Activity getCurrentActivity() {
        if (mActivityStack.size() > 0) {
            return mActivityStack.get(mActivityStack.size() - 1);
        }
        return null;
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : mActivityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 查找栈中是否存在指定的activity
     */
    public boolean checkActivity(Class<?> cls) {
        for (Activity activity : mActivityStack) {
            if (activity.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            mActivityStack.remove(activity);
            if (!activity.isFinishing()) {
                finish(activity);
            }
        }
    }

    public void finish(Activity activity) {
        if (activity != null) {
            activity.finish();
//            activity.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);//向右滑动出去
//            activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);//向左滑动出去
            activity = null;
        }
    }


    /**
     * finish指定的activity之上所有的activity
     *
     * @param actCls        目标Activity
     * @param isIncludeSelf 当前Activity
     */
    public boolean finishToActivity(Class<? extends Activity> actCls, boolean isIncludeSelf) {
        List<Activity> buf = new ArrayList<Activity>();
        int size = mActivityStack.size();
        Activity activity;
        for (int i = size - 1; i >= 0; i--) {
            activity = mActivityStack.get(i);
            if (activity.getClass().isAssignableFrom(actCls)) {
                for (Activity a : buf) {
                    a.finish();
                }
                return true;
            } else if (i == size - 1 && isIncludeSelf) {
                buf.add(activity);
            } else if (i != size - 1) {
                buf.add(activity);
            }
        }
        return false;
    }

    /**
     * 结束所有activity
     */
    public void removeAll() {
        if (mActivityStack != null) {
            synchronized (mActivityStack) {
                for (Activity act : mActivityStack) {
                    if (act != null && !act.isFinishing()) {
                        act.finish();
                    }
                }
                mActivityStack.clear();
            }
        }
    }

    /**
     * 退出应用程序
     */
    public void appExit() {
        try {
            removeAll();
            //退出JVM(java虚拟机),释放所占内存资源,0表示正常退出(非0的都为异常退出)
            System.exit(0);
            //从操作系统中结束掉当前程序的进程
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
