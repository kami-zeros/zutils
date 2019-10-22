package com.zxx.zutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 1.commit是原子提交到数据库，所以从提交数据到存在Disk中都是同步过程，中间不可打断。
 * 有一个boolean的返回值，永远都是最后一个调用commit方法的editor变更了最后的数据值。
 * 2.apply方法的原子操作是原子提交的内存中，而非数据库，所以在提交到内存中时不可打断，
 * 之后再异步提交数据到数据库中，因此也不会有相应的返回值。
 * 3.commit提交是同步过程，效率会比apply异步提交的速度慢，但是apply没有返回值，永远无法知道存储是否失败。
 * 4.在不关心提交结果是否成功的情况下，优先考虑apply方法。
 * 对偏好设置文件进行操作的工具类 on 2017/9/20.
 */

public class SPUtil {

    //private static final String APP_VERSION = "app_version";        //app版本号
    private static final String SP_FILE = "share_data";      //偏好设置保存文件名
    public static final String USER_INFO = "userInfo";        //保存用户所有信息json
    public static final String EMPTY = "";

    private SharedPreferences sp;
    private static Editor editor;

    //此处构造方法可以动态设置 --偏好设置保存文件名name
    public SPUtil(Context context, String name) {
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public SPUtil(Context context) {
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sp.edit();//commit或者apply
    }

    //private Context context = BaseApplication.getContext();

    /**
     * 1.存储数据到sharedpreference
     */
    public static void put(Context context, String key, Object object) {
        try {
            SharedPreferences sp = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();

            if (object instanceof String)
                editor.putString(key, (String) object);
            else if (object instanceof Integer) {
                editor.putInt(key, (Integer) object);
            } else if (object instanceof Boolean) {
                editor.putBoolean(key, (Boolean) object);
            } else if (object instanceof Float) {
                editor.putFloat(key, (Float) object);
            } else if (object instanceof Long) {
                editor.putLong(key, (Long) object);
            } else {
                editor.putString(key, object.toString());
            }
            editor.apply();
            //SharedPreferencesCompat.apply(editor);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 2.获取SharedPreferences中存储的数据
     *
     * @param object 在读取存储时的默认值（可以写null）
     */
    public static Object get(Context context, String key, Object object) {
        try {
            SharedPreferences sp = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);

            if (object instanceof String)
                return sp.getString(key, (String) object);
            else if (object instanceof Integer) {
                return sp.getInt(key, (Integer) object);
            } else if (object instanceof Boolean) {
                return sp.getBoolean(key, (Boolean) object);
            } else if (object instanceof Float) {
                return sp.getFloat(key, (Float) object);
            } else if (object instanceof Long) {
                return sp.getLong(key, (Long) object);
            } else {
                return sp.getString(key, object.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * 3.清除sharedpreperence所有数据
     */
    public static void clear(Context context) {
        try {
            SharedPreferences sp = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.apply();
            // SharedPreferencesCompat.apply(editor);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 4. 移除某个key值已经对应的值
     */
    public static void remove(Context context, String key) {
        try {
            SharedPreferences sp = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
            Editor editor = sp.edit();
            editor.remove(key);
            editor.apply();
            //SharedPreferencesCompat.apply(editor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 5. 查询某个key是否已经存在
     */
    public static boolean contains(Context context, String key) {
        try {
            SharedPreferences sp = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
            return sp.contains(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 6. 返回所有的键值对
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /****************--------自定义存储-------********************/

    /**
     * 1.自定义存储数据到name.xml里
     *
     * @param name   存储的名字
     * @param key    key值
     * @param object value值
     */
    public static void putSP(Context context, String name, String key, Object object) {
        try {
            SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();

            if (object instanceof Integer) {
                editor.putInt(key, (Integer) object);
            } else if (object instanceof Boolean) {
                editor.putBoolean(key, (Boolean) object);
            } else if (object instanceof Float) {
                editor.putFloat(key, (Float) object);
            } else if (object instanceof Long) {
                editor.putLong(key, (Long) object);
            } else {
                editor.putString(key, (String) object);
            }
            editor.apply();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 2.获取SharedPreferences中存储的数据
     *
     * @param name   存储的名字（要与putSP对应）
     * @param key    key值
     * @param object 在读取存储时的默认值（可以写null）
     */
    public static Object getSP(Context context, String name, String key, Object object) {
        try {
            SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);

            if (object instanceof Integer) {
                return sp.getInt(key, (Integer) object);
            } else if (object instanceof Boolean) {
                return sp.getBoolean(key, (Boolean) object);
            } else if (object instanceof Float) {
                return sp.getFloat(key, (Float) object);
            } else if (object instanceof Long) {
                return sp.getLong(key, (Long) object);
            } else {
                return sp.getString(key, (String) object);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * 3.清除sharedpreperence对应数据
     *
     * @param name
     */
    public static void clearSP(Context context, String name) {
        try {
            SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.apply();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 4. 移除某个key值已经对应的值
     */
    public static void removeSP(Context context, String name, String key) {
        try {
            SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
            Editor editor = sp.edit();
            editor.remove(key);
            editor.apply();
            //SharedPreferencesCompat.apply(editor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 5. 查询某个key是否已经存在
     */
    public static boolean containsSP(Context context, String name, String key) {
        try {
            SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
            return sp.contains(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 6. 返回所有的键值对
     */
    public static Map<String, ?> getAll(Context context, String name) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 0. 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat {

        private static final Method sApplyMethod = findApplyMethod();

        //反射查找apply的方法
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }
            return null;
        }

        //如果找到则使用apply执行，否则使用commit
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }

}
