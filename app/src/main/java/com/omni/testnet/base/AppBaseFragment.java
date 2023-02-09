package com.omni.testnet.base;


import com.omni.testnet.framelibrary.base.FrameBaseFragment;

/**
 * Fragment的基类
 * Created by fa on 2018/8/9.
 */

public abstract class AppBaseFragment extends FrameBaseFragment {
    private static final String TAG = AppBaseFragment.class.getSimpleName();

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

}
