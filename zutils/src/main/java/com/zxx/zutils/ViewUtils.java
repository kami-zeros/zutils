package com.zxx.zutils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zxx on 2019/8/13
 */
public class ViewUtils {

    //获取输入框文本去除左右空格
    public static String getTextTrim(TextView view) {
        try {
            return view.getText().toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //获取输入框文本去除左右空格长度
    public static int getTextTrimLength(TextView view) {
        try {
            return view.getText().toString().trim().length();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //true if str is null or zero length
    public static boolean checkEmptyView(TextView view) {
        String value = ViewUtils.getTextTrim(view);
        return TextUtils.isEmpty(value);
    }

    //true if str is null or zero length
    public static boolean checkEmptyView(TextView... views) {
        for (TextView view : views) {
            String value = ViewUtils.getTextTrim(view);
            if (TextUtils.isEmpty(value)) {
                return true;
            }
        }
        return false;
    }

    //(不能用异或^)这里是当所有相同时返回true，有一个不同返回false
    public static boolean checkSameBoolean(List<Boolean> list) {
        if (list == null) return false;
        int m = 0;
        for (int i = 0; i < list.size(); i++) {
            Boolean aBoolean = list.get(i);
            if (aBoolean) {
                m++;
            } else {
                m--;
            }
        }
        return Math.abs(m) == list.size();
    }

    //true=没有相同值，
    public static boolean checkSameValue(Object[] ar) {
        try {
            Set set = new HashSet();
            for (int i = 0; i < ar.length; i++) {
                set.add(ar[i]);
            }
            return set.size() == ar.length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //字符串 to 整型
    public static int strToInteger(String str) {
        int value = 0;
        try {
            if (TextUtils.isEmpty(str)) {
                return 0;
            } else {
                value = Integer.parseInt(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    //字符串 to Float
    public static float strToFloat(String str) {
        float value = 0;
        try {
            if (TextUtils.isEmpty(str)) {
                return 0;
            } else {
                value = Float.parseFloat(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    //保留几位小数
    public static String doubleDecimal(double value, int digits) {
        String str = "";
        try {
            NumberFormat ddf = NumberFormat.getNumberInstance();
            ddf.setMaximumFractionDigits(digits);
            //没有逗号分隔
            ddf.setGroupingUsed(false);
            str = ddf.format(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    //view展开动画
    public static void expand(View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        int height = view.getMeasuredHeight();
        DropAnim.getInstance().animateOpen(view, height);
    }


    //view关闭动画
    public static void collapse(View view) {
        DropAnim.getInstance().animateClose(view);
    }


    private static class DropAnim {

        private static DropAnim dropAnim;

        public static DropAnim getInstance() {
            if (dropAnim == null) {
                dropAnim = new DropAnim();
            }
            return dropAnim;
        }

        public void animateOpen(View view, int mHidderViewMeasureHeight) {
            view.setVisibility(View.VISIBLE);
            ValueAnimator animator = createDropAnimator(view, 0, mHidderViewMeasureHeight);
            animator.start();
        }

        public void animateClose(final View view) {
            int orightHeight = view.getHeight();
            ValueAnimator animator = createDropAnimator(view, orightHeight, 0);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                }
            });
            animator.start();
        }

        private ValueAnimator createDropAnimator(final View view, int start, int end) {
            ValueAnimator animator = ValueAnimator.ofInt(start, end);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.height = value;
                    view.setLayoutParams(params);
                }
            });
            return animator;
        }
    }


    //小数后面只能输入几位
    public static class DecimalDigitsInputFilter implements InputFilter {

        private final int decimalDigits;

        public DecimalDigitsInputFilter(int digits) {
            this.decimalDigits = digits;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            // source:当前输入的字符
            // start:输入字符的开始位置
            // end:输入字符的结束位置
            // dest：当前已显示的内容
            // dstart:当前光标开始位置
            // dent:当前光标结束位置
            //Log.e("", "source=" + source + ",start=" + start + ",end=" + end + ",dest=" + dest.toString() + ",dstart=" + dstart + ",dend=" + dend);
            if (dest.length() == 0 && source.equals(".")) {
                return "0.";
            }
            String dValue = dest.toString();
            String[] splitArray = dValue.split("\\.");
            if (splitArray.length > 1) {
                String dotValue = splitArray[1];
                //输入框小数的位数是 decimalDigits 的情况时小数位不可以输入，整数位可以正常输入
                if (dotValue.length() == decimalDigits && dest.length() - dstart <= decimalDigits) {
                    return "";
                }
            }
            return null;
        }
    }

}
