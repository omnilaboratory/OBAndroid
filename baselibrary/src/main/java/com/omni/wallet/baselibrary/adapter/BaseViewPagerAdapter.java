package com.omni.wallet.baselibrary.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;

/**
 * 带有指示器的Viewpager的基类
 */

public abstract class BaseViewPagerAdapter<T extends Fragment> extends FragmentPagerAdapter {
    private static final String TAG = BaseViewPagerAdapter.class.getSimpleName();

    private FragmentManager mFragmentManager;
    // 指示器的数组
    private String[] mIndicators;
    private List<String> mIndicatorsList;
    private List<Fragment> mFragmentList;
    //保存每个Fragment的Tag，刷新页面的依据
    protected SparseArray<String> mTempArray = new SparseArray<>();

    // 当前显示的Fragment
    private T mCurrentFragment;


    public BaseViewPagerAdapter(FragmentManager fm, String[] indicators) {
        super(fm);
        this.mFragmentManager = fm;
        this.mIndicators = indicators;
    }

    public BaseViewPagerAdapter(FragmentManager fm, List<String> indicators) {
        super(fm);
        this.mFragmentManager = fm;
        this.mIndicatorsList = indicators;
    }


    @Override
    public Fragment getItem(int position) {
        //新建一个Fragment来展示ViewPager item的内容，并传递参数
        return convert(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mIndicators != null) {
            return mIndicators[position % mIndicators.length];
        }
        if (mIndicatorsList != null) {
            return mIndicatorsList.get(position % mIndicatorsList.size());
        }
        return "";
    }

    @Override
    public int getCount() {
        if (mIndicators != null) {
            return mIndicators.length;
        }
        if (mIndicatorsList != null) {
            return mIndicatorsList.size();
        }
        return 0;
    }

    // fragment不可见的时候是否销毁
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (destroyItem()) {
            super.destroyItem(container, position, object);
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentFragment = (T) object;
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //得到缓存的fragment
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        String tag = fragment.getTag();
        //保存每个Fragment的Tag
        mTempArray.put(position, tag);
        return fragment;
    }

    /**
     * 拿到指定位置的Fragment
     */
    public Fragment getFragmentByPosition(int position) {
        return mFragmentManager.findFragmentByTag(mTempArray.get(position));
    }

    /**
     * 获取所有的Fragment
     */
    public List<Fragment> getFragments() {
        return mFragmentManager.getFragments();
    }

    /**
     * 刷新指定位置的Fragment
     */
    public void notifyFragmentByPosition(int position) {
        mTempArray.removeAt(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        Fragment fragment = (Fragment) object;
        //如果Item对应的Tag存在，则不进行刷新
        if (mTempArray.indexOfValue(fragment.getTag()) > -1) {
            return super.getItemPosition(object);
        }
        return POSITION_NONE;
    }


    /**
     * 获取当前的Fragment
     */
    public T getCurrentFragment() {
        return mCurrentFragment;
    }


    /**
     * 创建Fragment或者View
     */
    protected abstract T convert(int position);

    /**
     * 条目是否销毁（默认销毁）
     */
    protected boolean destroyItem() {
        return true;
    }
}
