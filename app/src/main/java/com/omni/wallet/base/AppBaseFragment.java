package com.omni.wallet.base;


import com.omni.wallet.framelibrary.base.FrameBaseFragment;
import com.omni.wallet.thirdsupport.umeng.UMUtils;

/**
 * Fragment的基类
 * Created by fa on 2018/8/9.
 */

public abstract class AppBaseFragment extends FrameBaseFragment {
    private static final String TAG = AppBaseFragment.class.getSimpleName();

    public void onResume() {
        super.onResume();
        UMUtils.onPageStart(this.getClass().getName());
    }

    public void onPause() {
        super.onPause();
        UMUtils.onPageEnd(this.getClass().getName());
    }

}
