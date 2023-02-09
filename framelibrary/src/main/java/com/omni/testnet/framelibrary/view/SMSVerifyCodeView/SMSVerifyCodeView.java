package com.omni.testnet.framelibrary.view.SMSVerifyCodeView;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

import com.omni.testnet.baselibrary.utils.LogUtils;
import com.omni.testnet.baselibrary.utils.StringUtils;
import com.omni.testnet.baselibrary.utils.ToastUtils;
import com.omni.testnet.framelibrary.R;
import com.omni.testnet.framelibrary.view.dialog.VerifyImageDialog;


/**
 * 短信验证码的控件
 */

public class SMSVerifyCodeView extends AppCompatTextView {
    private static final String TAG = SMSVerifyCodeView.class.getSimpleName();

    // 是否展示
    private boolean mAttached;
    // 发送验证码后等待的时间（秒）
    private int mDelayTime = 60;
    // Button 上面的文本
    private String mShowText = "发送验证码";
    // 点击之后等待接口返回时的文字
    private String mWaitingText = "请稍候...";
    // Button 等待的时候显示的文本
    private String mDelayText = "重新发送(" + FORMAT_TAG + ")";
    // 重新获取的时候显示的文字
    private String mReGetText = "重新发送";
    // 现在是否在倒计时
    private boolean mIsDelayed = false;
    // 当前显示时间数字
    private int mCurrentDelayTime = 0;
    // 接口获取验证码的帮助类
    private BaseSMSVerifyCodeHelper mHelper;
    // 短信验证码发送成功提示语
    private String mSMSTips;
    // 是否弹短信发送成功的提示语
    private boolean mIsShowTips = true;

    // 显示图形验证码的弹窗
    private VerifyImageDialog mVerifyImageDialog;
    // 获取短信验证码的手机号
    private String mPhoneNum;

    private boolean mAutoSendFirst = false;// 初始化时候是否自动发送
    private boolean mAutoCountdownFirst = false;// 初始化时候是否自动倒计时
    private boolean mNeedCountdown = false;// 请求接口完成后是否需要倒计时
    private String mFirstSendPhone;// 初始化时候自动发送的手机号

    private static final String FORMAT_TAG = "%d";

    public SMSVerifyCodeView(Context context) {
        this(context, null);
    }

    public SMSVerifyCodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SMSVerifyCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        setGravity(Gravity.CENTER);
        String text = getText().toString().trim();
        if (!StringUtils.isEmpty(text)) {
            mShowText = text;
        }
        mHelper = new SMSVerifyCodeHelper(getContext());
        mHelper.setCallback(new MySMSVerifyCodeCallback());
    }

    /**
     * 设置帮助类
     */
    public void setHelper(BaseSMSVerifyCodeHelper helper) {
        this.mHelper = helper;
        this.mHelper.setCallback(new MySMSVerifyCodeCallback());
    }

    /**
     * 发送短信验证码接口的回调
     */
    private class MySMSVerifyCodeCallback implements BaseSMSVerifyCodeHelper.SMSVerifyCodeCallback {

        @Override
        public void onRequestSMSVerifyCode() {
            // 显示请稍候文字，等待接口结果返回
            LogUtils.e(TAG, "================>");
            showWaitText();
        }

        @Override
        public void onSMSVerifyCodeSuccess() {
            // 短信发送成功的提示语
            showTips();
            // 获取短信验证码开始倒计时
            if (mNeedCountdown) {// 有的情况倒计时需要在控件添加到窗口的时候自己处理，所以这里做判断
                startCountDown();
            }
            if (mCallback != null) {
                mCallback.onSuccess();
            }
        }

        @Override
        public void onSMSVerifyCodeFail(String errorCode) {
            // 控件置为重新获取
            reset();
            if (mCallback != null) {
                mCallback.onFail(errorCode);
            }
        }
    }

    /**
     * 显示提示语
     */
    private void showTips() {
        if (mIsShowTips) {
            if (StringUtils.isEmpty(mSMSTips)) {
                mSMSTips = getContext().getResources().getString(R.string.text_tips_sms_send_success);
            }
            ToastUtils.showToast(getContext(), mSMSTips);
        }
    }

    /**
     * 显示正在获取请稍候
     */
    public void showWaitText() {
        // 更改UI显示的样式
        setEnabled(false);
        // 设置文字为请稍候
        setText(mWaitingText);
    }

    /**
     * 显示重新获取
     */
    public void reset() {
        // 更改UI显示的样式
        setEnabled(true);
        // 设置文字为重新获取
        setText(mReGetText);
    }

    /**
     * 倒计时
     */
    private Runnable mTimer = new Runnable() {

        @Override
        public void run() {
            mCurrentDelayTime -= 1;
            if (mCurrentDelayTime <= 0) {
                mIsDelayed = false;
                setEnabled(true);
                setClickable(true);
                setText(mReGetText);
            } else {
                getHandler().postDelayed(mTimer, 1000);
                setDelayTimeText(mCurrentDelayTime);
            }
        }
    };

    /**
     * 设置倒计时的时候显示的文字
     */
    private void setDelayTimeText(int time) {
        setText(String.format(mDelayText, time));
    }


    /**
     * 开始倒计时
     */
    private void startCountDown() {
        if (!mIsDelayed) {// 如果没有在等待
            setEnabled(false);
            setClickable(false);
            mIsDelayed = true;
            mCurrentDelayTime = mDelayTime;
            if (getHandler() != null) {
                getHandler().post(mTimer);
            }
        }
    }

    /**
     * 接口获取短信验证码
     */
    public void getVerifyCode(String phone) {
        this.mPhoneNum = phone;
        this.mNeedCountdown = true;
        if (mVerifyImageDialog == null) {
            mVerifyImageDialog = new VerifyImageDialog(getContext());
            mVerifyImageDialog.setCallback(new MyVerifyImageDialogCallback());
        }
        // 显示图形验证码弹窗
        mVerifyImageDialog.show(String.valueOf(System.currentTimeMillis()));
    }

    /**
     * 接口获取短信验证码
     */
    public void getVerifyCodeNoImage(String phone) {
        this.mNeedCountdown = true;
        if (mHelper != null) {
            mHelper.getSmsVerifyCode(phone, "", "");
        }
    }

    /**
     * 接口获取短信验证码
     */
    public void getVerifyCodeNoImageNotCountdown(String phone) {
        this.mNeedCountdown = false;
        if (mHelper != null) {
            mHelper.getSmsVerifyCode(phone, "", "");
        }
    }

    /**
     * 接口获取短信验证码
     */
    public void getVerifyCode(String phone, String imageCode) {
        this.mNeedCountdown = true;
        if (mHelper != null) {
            mHelper.getSmsVerifyCode(phone, imageCode, "");
        }
    }

    /**
     * 接口获取短信验证码
     */
    public void getVerifyCode(String phone, String imageCode, String randomStr) {
        this.mNeedCountdown = true;
        if (mHelper != null) {
            mHelper.getSmsVerifyCode(phone, imageCode, randomStr);
        }
    }

    /**
     * 图形验证码弹窗回调
     */
    private class MyVerifyImageDialogCallback implements VerifyImageDialog.VerifyImageDialogCallback {

        @Override
        public void onClickConfirm(String verifyCode, String random) {
            if (StringUtils.isEmpty(verifyCode)) {
                LogUtils.e(TAG, "没有输入图形验证码");
                return;
            }
            if (mHelper != null) {
                mHelper.getSmsVerifyCode(mPhoneNum, verifyCode, random);
            }
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mAttached) {
            mAttached = true;
            setText(mShowText);
            if (mAutoSendFirst) {// 初始化自动发送
                getVerifyCodeNoImage(mFirstSendPhone);
            } else if (mAutoCountdownFirst) {// 初始化自动倒计时
                // 短信发送成功的提示语
                showTips();
                // 获取短信验证码开始倒计时
                startCountDown();
                // 倒计时完毕之后重置
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            mAttached = false;
            mIsDelayed = false;
            mAutoSendFirst = false;
            mAutoCountdownFirst = false;
            getHandler().removeCallbacks(mTimer);
        }
    }

    public void setSMSTips(String smsTips) {
        this.mSMSTips = smsTips;
    }

    public void setSMSTips(int smsTipsId) {
        this.mSMSTips = getContext().getResources().getString(smsTipsId);
    }

    public void setIsShowTips(boolean isShowTips) {
        this.mIsShowTips = isShowTips;
    }

    public int getDelayTime() {
        return mDelayTime;
    }

    public void setDelayTime(int mDelayTime) {
        this.mDelayTime = mDelayTime;
    }

    public String getShowText() {
        return mShowText;
    }

    public void setShowText(String showText) {
        this.mShowText = showText;
    }

    public String getReGetText() {
        return mReGetText;
    }

    public void setReGetText(String mReGetText) {
        this.mReGetText = mReGetText;
    }

    public String getDelayText() {
        return mDelayText;
    }

    public void setDelayText(String start, String end) {
        this.mDelayText = start + FORMAT_TAG + end;
    }

    public void setAutoSendFirstPhone(String phone) {
        this.mAutoSendFirst = true;
        this.mFirstSendPhone = phone;
    }

    public void setAutoCountdownFirst() {
        this.mAutoCountdownFirst = true;
    }

    private SMSVerifyCodeViewCallback mCallback;

    public void setCallback(SMSVerifyCodeViewCallback callback) {
        this.mCallback = callback;
    }

    public interface SMSVerifyCodeViewCallback {
        void onSuccess();

        void onFail(String errorCode);
    }
}
