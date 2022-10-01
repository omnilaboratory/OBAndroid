package com.omni.wallet.baselibrary.http.dialog;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.omni.wallet.baselibrary.R;
import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 每个Activity关联一个Dialog
 */

public class LoadingDialog {

    private static final String TAG = LoadingDialog.class.getSimpleName();
    private boolean mCancelable = false;
    private Map<String, LinkedList<Object>> mRequestTagMap = new HashMap<>();// 缓存Activity中所有Request的Tag
    private Map<String, AlertDialog> mDialogMap = new HashMap<>();// 缓存每个Activity对应的唯一 一个Dialog
    private Map<String, ObjectAnimator> mDialogAnimatorMap = new HashMap<>();// 缓存每个Activity对应的Dialog的动画

    private static LoadingDialog mInstance;

    public static LoadingDialog getInstance() {
        if (mInstance == null) {
            synchronized (LoadingDialog.class) {
                if (mInstance == null) {
                    mInstance = new LoadingDialog();
                }
            }
        }
        return mInstance;
    }

    private LoadingDialog() {
    }

    public void setCancelable(boolean cancelable) {
        this.mCancelable = cancelable;
    }

    public void show(Object target) {
        show(target, "");
    }

    public void show(Object target, String text) {
        if (target == null) {
            return;
        }
        LogUtils.d(TAG, target.getClass().getSimpleName() + "请求展示dialog");
        Activity activity = null;
        if (target instanceof Activity) {
            activity = (Activity) target;
        } else if (target instanceof Fragment) {
            activity = ((Fragment) target).getActivity();
        }
        // 显示Dialog
        if (activity != null) {
            String key = activity.getClass().getSimpleName();
            // 缓存请求的tag
            cacheRequestTag(key, target);
            LogUtils.d(TAG, activity.getClass().getSimpleName() + "中网络请求个数：" + mRequestTagMap.get(key).size());
            // 创建并显示Dialog
            makeAndShowDialog(text, activity);

        }
    }

    /**
     * 请求的tag缓存到Map中
     */
    private void cacheRequestTag(String key, Object value) {
        LinkedList<Object> tagList = mRequestTagMap.get(key);
        if (tagList == null) {
            tagList = new LinkedList<>();
        }
        tagList.add(value);
        mRequestTagMap.put(key, tagList);
    }

    /**
     * 创建并显示Dialog
     */
    private void makeAndShowDialog(String text, Activity activity) {
        // 获取Dialog
        AlertDialog dialog = makeWaitingDialog(activity);
        if (dialog == null) {
            LogUtils.d(TAG, "showLoadingDialog 时 Dialog为null");
            return;
        }
        // 文字描述
        TextView waitingTv = dialog.getViewById(R.id.tv_dialog_waiting);// 描述文字
        // 设置描述文字
        if (!StringUtils.isEmpty(text)) {
            waitingTv.setText(text);
        }
        // Dialog正在显示，就return
        if (dialog.isShowing()) {
            LogUtils.d(TAG, "Dialog Is Showing");
            return;
        }
        LogUtils.d(TAG, "Show Dialog");
        // 显示
        dialog.show();
        // 加载的动画
        ObjectAnimator animator = makeRotationAnimator(activity, dialog);
        animator.start();
    }

    /**
     * 创建加载中的Dialog实例
     */
    private AlertDialog makeWaitingDialog(Activity activity) {
        // 获取缓存中的Dialog
        String key = activity.getClass().getSimpleName();
        AlertDialog dialog = mDialogMap.get(key);
        if (dialog == null) {
            dialog = new AlertDialog.Builder(activity, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_dialog_loading)
                    .setCanceledOnTouchOutside(false)
                    .setOnDismissListener(new MyDismissCallback(key))  // 设置消失监听
                    .create();
            // 放到缓存
            mDialogMap.put(key, dialog);
        }
        // 是否能取消
        dialog.setCancelable(mCancelable);
        return dialog;
    }

    /**
     * 创建旋转动画
     */
    private ObjectAnimator makeRotationAnimator(Activity activity, AlertDialog dialog) {
        // 缓存中获取
        String key = activity.getClass().getSimpleName();
        ObjectAnimator animator = mDialogAnimatorMap.get(key);
        if (animator == null) {
            View loadingIv = dialog.getViewById(R.id.iv_dialog_waiting);// 旋转动画的View
            loadingIv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            animator = ObjectAnimator.ofFloat(loadingIv, "rotation", 0, 359f);
            animator.setInterpolator(new LinearInterpolator());
            animator.setDuration(1200);
            animator.setRepeatCount(ObjectAnimator.INFINITE);
            // 放到缓存
            mDialogAnimatorMap.put(key, animator);
        }
        return animator;
    }

    /**
     * 消失的监听
     */
    private class MyDismissCallback implements DialogInterface.OnDismissListener {
        private String mKey;

        MyDismissCallback(String key) {
            this.mKey = key;
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            LogUtils.d(TAG, mKey + "的Dialog的消失回调");
            // 消失的时候，取消Activity对应的tagList中的所有网络请求
            List<Object> tagList = mRequestTagMap.get(mKey);
            if (tagList != null && tagList.size() > 0) {
                List<Object> tempList = new ArrayList<>();
                for (Object tag : tagList) {
                    // 去重
                    if (!tempList.contains(tag)) {
                        tempList.add(tag);
                        LogUtils.d(TAG, "取消" + tag.getClass().getSimpleName() + "的网络请求");
                        HttpUtils.cancelCall(tag.getClass());
                    }
                }
                tempList.clear();
            }
            // 清理缓存
            mRequestTagMap.remove(mKey);
            LogUtils.d(TAG, "移除Map中" + mKey + "的TagList");
        }
    }


    /**
     * Activity网络请求完成的时候调用
     */
    public void dismiss(Activity activity) {
        removeTagFromCache(activity);
    }

    /**
     * Fragment网络请求完成的时候调用
     */
    public void dismiss(Fragment fragment) {
        removeTagFromCache(fragment);
    }


    /**
     * 从缓存中移除对应的请求Tag
     */
    private void removeTagFromCache(Object tag) {
        Activity activity = null;
        if (tag instanceof Activity) {
            activity = (Activity) tag;
        } else if (tag instanceof Fragment) {
            activity = ((Fragment) tag).getActivity();
        }
        if (activity == null) {
            return;
        }
        String key = activity.getClass().getSimpleName();
        LinkedList<Object> tagList = mRequestTagMap.get(key);
        if (tagList != null && tagList.size() > 0) {
            // 移除Target
            tagList.remove(tag);
            LogUtils.d(TAG, "Dismiss时移除了" + tag.getClass().getSimpleName() + "之后缓存集合长度" + (tagList.size()));
            // dismissDialog
            dismissDialog(activity);
        }
        if (tagList == null || tagList.size() == 0) {
            mRequestTagMap.remove(key);
        }
    }

    /**
     * Activity被销毁的时候，取消该Activity下tag集合对应的所有请求
     */
    public void onActivityDestroy(Activity activity) {
        String key = activity.getClass().getSimpleName();
        List<Object> tagList = mRequestTagMap.get(key);
        if (tagList != null && tagList.size() > 0) {
            List<Object> tempList = new ArrayList<>();
            for (Object tag : tagList) {
                // 去重
                if (!tempList.contains(tag)) {
                    tempList.add(tag);
                    LogUtils.d(TAG, "取消" + tag.getClass().getSimpleName() + "的网络请求");
                    HttpUtils.cancelCall(tag.getClass());
                }
            }
            tempList.clear();// 释放
            // 处理Dialog
            dismissDialog(activity);
        }
        // 从缓存中移除该Activity对应的tag集合
        mRequestTagMap.remove(key);
        // 清理Dialog相关缓存
        mDialogMap.remove(key);
        mDialogAnimatorMap.remove(key);
        LogUtils.d(TAG, "移除" + key + " DialogMap 和 DialogAnimatorMap 的缓存");
    }

    /**
     * Fragment被销毁的时候，取消该Fragment对应的父类Activity的缓存集合中的数据和相应请求
     */
    public void onFragmentDestroy(Fragment fragment) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            return;
        }
        String key = activity.getClass().getSimpleName();
        LinkedList<Object> tagList = mRequestTagMap.get(key);
        if (tagList != null && tagList.size() > 0) {
            List<Object> tempList = new ArrayList<>();
            for (Object tag : tagList) {
                if (tag == fragment) {
                    tempList.add(tag);
                    LogUtils.d(TAG, "取消" + tag.getClass().getSimpleName() + "的网络请求");
                    // 取消请求
                    HttpUtils.cancelCall(tag.getClass());
                }
            }
            // 从缓存中移除
            tagList.removeAll(tempList);
            tempList.clear();
            // 操作Dialog
            dismissDialog(activity);
        }
        if (tagList == null || tagList.size() == 0) {
            mRequestTagMap.remove(key);
        }
    }

    /**
     * Dialog
     */
    private void dismissDialog(Activity activity) {
        if (activity == null) {
            return;
        }
        String key = activity.getClass().getSimpleName();
        // 是否需要使Dialog消失
        if (!needDismissDialog(key)) {
            LogUtils.d(TAG, "Dialog无需Dismiss");
            return;
        }
        LogUtils.d(TAG, "Dialog Dismiss");
        // Dialog消失，并从缓存中移除
        AlertDialog dialog = mDialogMap.get(key);
        if (dialog != null) {
            dialog.dismiss();
            mDialogMap.remove(key);
        }
        // Animator取消，并从缓存中移除
        ObjectAnimator animator = mDialogAnimatorMap.get(key);
        if (animator != null) {
            animator.cancel();
            mDialogAnimatorMap.remove(key);
        }
    }

    /**
     * 判断是否需要使Dialog消失
     */
    private boolean needDismissDialog(String key) {
        LinkedList<Object> targetList = mRequestTagMap.get(key);
        // 该Activity对应的tagList为空或者长度为0的时候，Dialog需要Dismiss
        return targetList == null || targetList.size() == 0;
    }
}

