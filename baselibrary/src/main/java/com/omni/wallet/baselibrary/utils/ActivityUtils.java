package com.omni.wallet.baselibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序
 */
public class ActivityUtils {
    private static final String TAG = ActivityUtils.class.getSimpleName();
    private static Stack<Activity> activityStack;
    private static ActivityUtils instance;

    private ActivityUtils() {
    }

    public void print() {
        for (Activity activity : activityStack) {
            LogUtils.e(TAG, "activity：" + activity.getClass().getSimpleName());
        }
    }

    /**
     * 单一实例
     */
    public static ActivityUtils getInstance() {
        if (instance == null) {
            synchronized (ActivityUtils.class) {
                if (instance == null) {
                    instance = new ActivityUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 添加Activity
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Class<? extends Activity> clazz) {
        if (clazz == null) {
            return;
        }
        if (activityStack != null) {
            Activity temp = null;
            for (Activity activity : activityStack) {
                if (activity != null && activity.getClass() == clazz) {
                    temp = activity;
                    activity.finish();
                    break;
                }
            }
            if (temp != null) {
                activityStack.remove(temp);
            }
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(String className) {
        if (StringUtils.isEmpty(className)) {
            return;
        }
        if (activityStack != null) {
            Activity temp = null;
            for (Activity activity : activityStack) {
                if (activity != null && activity.getClass().getName().equals(className)) {
                    temp = activity;
                    activity.finish();
                    break;
                }
            }
            if (temp != null) {
                activityStack.remove(temp);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (Activity a : activityStack) {
            if (a != null) {
                a.finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 结束除了指定Activity之外的所有Activity
     */
    public void finishActivityExpect(String className) {
        if (activityStack != null) {
            List<Activity> tempList = new ArrayList<>();
            for (Activity activity : activityStack) {
                if (activity != null && !activity.getClass().getName().equals(className)) {
                    activity.finish();
                    tempList.add(activity);
                }
            }
            if (tempList.size() > 0) {
                activityStack.removeAll(tempList);
            }
        }
    }

//    private boolean containClass(String className, String... classNameArray) {
//        if (classNameArray == null || classNameArray.length == 0) {
//            return false;
//        }
//        int size = classNameArray.length;
//        for (int i = 0; i < size; i++) {
//            return !StringUtils.isEmpty(classNameArray[i]) && classNameArray[i].equals(className);
//        }
//        return false;
//    }


    /**
     * @param className
     * @Title: finishActivityForName
     * @Description: 根据名字结束除了指定Activity
     * @return: void
     * @author: eye_fa
     */
    public void finishActivityForName(String className) {
        if (activityStack != null) {
            for (Activity activity : activityStack) {
                if (activity != null && activity.getClass().getName().equals(className)) {
                    activity.finish();
                }
            }
        }
    }

    /**
     * 切换 Activity
     */
    public static void switchActivity(Context context, Class<?> clas) {
        switchActivity(context, clas, null);
    }

    /**
     * 切换 Activity
     */
    public static void switchActivity(Context context, Class<?> clas, Bundle bundle) {
        Intent intent = new Intent(context, clas);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }


    /**
     * 获取栈顶的Activity的全类名
     */
    public static String getTopActivityName() {
        if (activityStack.size() == 0) {
            return null;
        }
        return activityStack.get(activityStack.size() - 1).getClass().getName();
    }

    /**
     * 获取栈顶的Activity的类名
     */
    public static String getTopActivitySimpleName() {
        if (activityStack.size() == 0) {
            return "";
        }
        return activityStack.get(activityStack.size() - 1).getClass().getSimpleName();
    }
}