package com.omni.wallet_mainnet.baselibrary.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.omni.wallet_mainnet.baselibrary.R;


/**
 * 整个Dialog的控制器也就相当于装配者
 */

class AlertController {
    private static final String TAG = AlertController.class.getSimpleName();
    private AlertDialog mDialog;
    private Window mWindow;
    private AlertViewHelper mViewHelper;

    public AlertController(AlertDialog mDialog, Window mWindow) {
        this.mDialog = mDialog;
        this.mWindow = mWindow;
    }

    /**
     * 获取创建的dialog对象
     */
    public AlertDialog getDialog() {
        return mDialog;
    }

    /**
     * 获取创建的Dialog的Window对象
     */
    public Window getWindow() {
        return mWindow;
    }


    public void setViewHelper(AlertViewHelper viewHelper) {
        this.mViewHelper = viewHelper;
    }

    public <T extends View> T getViewById(int viewId) {
        return mViewHelper.getViewById(viewId);
    }


    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        mViewHelper.setOnClickListener(viewId, listener);
    }

    public void setText(int viewId, CharSequence text) {
        mViewHelper.setText(viewId, text);
    }


    /**
     * 静态内部类，主要用来存放装配Dialog需要的一些参数
     */
    static class AlertParams {
        public final Context mContext;
        // Dialog的主题Id
        public int mThemeResId;
        // Dialog默认是否点击阴影可以取消
        public boolean mOutCancelable = true;
        // 点击返回键是否可以取消
        public boolean mCancelable = true;
        // Dialog的取消监听
        public DialogInterface.OnCancelListener mOnCancelListener;
        // Dialog的消失监听
        public DialogInterface.OnDismissListener mOnDismissListener;
        // Dialog的键盘监听
        public DialogInterface.OnKeyListener mOnKeyListener;
        // Dialog的布局View
        public View mView = null;
        // Dialog的布局文件ID
        public int mViewLayoutResId = R.layout.view_base_dialog;
        /**
         * SparseArray比HashMap要高效，而且目前好像用HashMap会出现兼容问题
         * 这种类型的集合要求Key必须是int类型才行
         */
        // 存放设置的文本内容的集合
        public SparseArray<CharSequence> mTextArrays = new SparseArray<>();
        // 存放点击事件的集合
        // 这里使用软引用，因为有时候我们传递点击事件对象的时候会把相应的Activity引用传递进来，不注意会
        // 造成内存泄漏，所以这里使用软引用，当内存不足时会被释放
//        public SparseArray<WeakReference<View.OnClickListener>> mListenerArrays = new SparseArray<>();
        public SparseArray<View.OnClickListener> mListenerArrays = new SparseArray<>();
        // dialog的动画
        public int mAnimation = 0;
        // Dialog的默认显示位置
        public int mGravity = Gravity.CENTER;
        // Dialog的宽度
        public int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        // Dialog的高度
        public int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;


        public AlertParams(Context context, int themeResId) {
            this.mContext = context;
            this.mThemeResId = themeResId;
        }

        /**
         * 绑定和设置参数的方法，在这个方法中，将保存的参数集通过AlertController绑定到AlertDialog中
         * 而在AlertController的构造方法中传入了需要创建的Dialog对象，所以可以实现参数和Dialog的绑定
         * 具体的数据绑定需要使用到AlertViewHelper
         *
         * @param mAlert alert
         */
        public void apply(AlertController mAlert) {
            // 将相应的参数绑定到Dialog中
            //1.利用AlertViewHelper设置布局
            AlertViewHelper viewHelper = null;
            if (mViewLayoutResId != 0) {
                viewHelper = new AlertViewHelper(mContext, mViewLayoutResId);
            }
            if (mView != null) {
                viewHelper = new AlertViewHelper();
                viewHelper.setContentView(mView);
            }
            // 判断是否设置了布局，如果没有就抛出异常
            if (viewHelper == null) {
                throw new IllegalArgumentException("布局文件不能为空，" +
                        "请调用AlertDialog.Builder的setContentView()方法设置");
            }
            // 将初始化后的AlertViewHelper赋值到全局变量，以便于用dialog对象调用相应方法的时候
            // 可以使用这个AlertViewHelper调用对应操作页面元素的方法
            mAlert.setViewHelper(viewHelper);
            // 设置了布局文件，就需要将布局文件设置到Dialog中
            mAlert.getDialog().setContentView(viewHelper.getContentView());
            //2.设置文本
            int textArraySize = mTextArrays.size();
            for (int i = 0; i < textArraySize; i++) {
                viewHelper.setText(mTextArrays.keyAt(i), mTextArrays.valueAt(i));
            }
            //3.设置点击事件
            int listenerArraySize = mListenerArrays.size();
            for (int i = 0; i < listenerArraySize; i++) {
                viewHelper.setOnClickListener(mListenerArrays.keyAt(i), mListenerArrays.valueAt(i));
            }
            // 4.设置特效，配置一些万能参数动画什么的（使用传递过来的Window对象）
            Window window = mAlert.getWindow();
            // 设置位置
            window.setGravity(mGravity);
            //设置动画
            if (mAnimation != 0) {
                window.setWindowAnimations(mAnimation);
            }
            // 设置宽高
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = mWidth;
            params.height = mHeight;
            window.setAttributes(params);
        }
    }
}
