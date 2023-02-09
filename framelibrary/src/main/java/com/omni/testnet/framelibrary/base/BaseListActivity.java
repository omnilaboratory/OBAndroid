package com.omni.testnet.framelibrary.base;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.omni.testnet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.testnet.baselibrary.view.recyclerView.adapter.MultiTypeSupport;
import com.omni.testnet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.testnet.baselibrary.view.recyclerView.pullToRefresh.PullToRefreshRecyclerView;
import com.omni.testnet.framelibrary.R;
import com.omni.testnet.framelibrary.entity.BaseListEntity;
import com.omni.testnet.framelibrary.view.pullToRefresh.DefaultLoadView;
import com.omni.testnet.framelibrary.view.pullToRefresh.DefaultRefreshView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.google.gson.internal.$Gson$Types.getRawType;

/**
 * 使用RecyclerView的列表的Activity
 */

public abstract class BaseListActivity<ShowData extends BaseListEntity, Request> extends FrameBaseActivity {
    private static final String TAG = BaseListActivity.class.getSimpleName();
    protected PullToRefreshRecyclerView mRecyclerView;
    protected List<ShowData> mData = new ArrayList<>();
    protected MyAdapter mAdapter;
    protected DefaultLoadView mLoadView;
    protected int mCurrentPage = 1;
    protected int mPageNum = 10;


    @Override
    protected int getContentView() {
        return R.layout.activity_base_list;
    }

    @Override
    protected void initView() {
        initRecyclerView();
    }

    @Override
    protected void initData() {

    }

    protected void initRecyclerView() {
        mRecyclerView = getRecyclerView();
        if (mRecyclerView == null) {
            return;
        }
        mRecyclerView.setLayoutManager(getLayoutManager());
        mAdapter = new MyAdapter(mContext, mData, new MyMultiTypeSupport());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setRefreshEnable(true);
        mRecyclerView.addRefreshViewCreator(getRefreshView());
        mLoadView = getLoadView();
        mRecyclerView.setLoadEnable(true);
        mRecyclerView.setOnPullToRefreshListener(new MyPullToRefreshListener());
        mRecyclerView.autoRefresh();
    }


    /**
     * 数据适配器
     */
    protected class MyAdapter extends CommonRecyclerAdapter<ShowData> {

        public MyAdapter(Context context, List<ShowData> data, MultiTypeSupport<ShowData> multiTypeSupport) {
            super(context, data, multiTypeSupport);
        }

        @Override
        public void convert(ViewHolder holder, int position, ShowData item) {
            convertView(holder, position, item);
        }

        @Override
        public void onNotifyView(ViewHolder holder, int position, ShowData item) {
            notifyItemView(holder, position, item);
        }
    }

    /**
     * 多布局支持
     */
    public class MyMultiTypeSupport implements MultiTypeSupport<ShowData> {

        @Override
        public int getLayoutId(ShowData item, int position) {
            return itemLayoutId(item, position);
        }
    }

    /**
     * 下拉上拉的监听
     */
    public class MyPullToRefreshListener implements PullToRefreshRecyclerView.PullToRefreshListener {

        @Override
        public void onRefresh() {
            mCurrentPage = getStartPage();
            requestData(true);
        }

        @Override
        public void onLoad() {
            requestData(false);
        }
    }

    /**
     * 数据成功后统一处理
     */
    protected void onSuccessData(boolean isRefresh, List<Request> result) {
        if (mBinder == null) {
            return;
        }
        if (isRefresh) {
            mData.clear();
            if (result == null || result.size() == 0) {
                // 添加无数据布局
                setNoDataViewShow();
                // 上拉加载禁用
                mRecyclerView.onRequestNoData();
            } else {
                // 添加条目数据
                makeItemData(result);
                // 开启上拉
                mRecyclerView.onLoadViewEnable(mLoadView);
                // 判断是否已经到底了
                if (mCurrentPage == getStartPage() && result.size() < mPageNum) {
                    // 显示没有更多了
                    mRecyclerView.setLoadNoData(true);
                }
                // 页码自增
                mCurrentPage++;
            }
        } else {
            if (result == null || result.size() == 0) {
                mRecyclerView.onLoadNoData();
            } else {
                // 添加条目数据
                makeItemData(result);
                // 页码自增
                mCurrentPage++;
            }
        }
        // 通知更新
        mAdapter.notifyDataSetChanged();
        if (mRecyclerView != null) {
            if (isRefresh) {
                mRecyclerView.stopRefresh();
            } else {
                mRecyclerView.stopLoad();
            }
        }
    }

    /**
     * 数据失败后统一处理
     */
    protected void onFailData(boolean isRefresh) {
        if (mBinder == null) {
            return;
        }
        if (mData.size() == 0) {
            setNoDataViewShow();
            mAdapter.notifyDataSetChanged();
        }
        if (mRecyclerView != null) {
            if (isRefresh) {
                mRecyclerView.stopRefresh();
            } else {
                mRecyclerView.stopLoad();
            }
        }
    }

    protected void stopRefreshAndLoad(boolean isRefresh) {
        if (mBinder == null) {
            return;
        }
        if (mRecyclerView != null) {
            if (isRefresh) {
                mRecyclerView.stopRefresh();
            } else {
                mRecyclerView.stopLoad();
            }
        }
    }

    /**
     * 无数据的布局显示
     */
    public void setNoDataViewShow() {
        mData.clear();
        ShowData noDataEntity = createShowData();
        if (noDataEntity != null) {
            noDataEntity.setItemType(BaseListEntity.TYPE_ITEM_NO_DATA);
        }
        mData.add(noDataEntity);
    }

    /**
     * 获取泛型类型，并构建实体
     */
    private ShowData createShowData() {
        try {
            // getGenericSuperclass()获得带有泛型的父类
            Type superClass = getClass().getGenericSuperclass();
            // getActualTypeArguments获取参数化类型的数组，泛型可能有多个
            Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
            // 通过type获取class的类型
            Class<?> clazz = getRawType(type);
            // 实例化
            return (ShowData) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(mContext);
    }

    protected DefaultRefreshView getRefreshView() {
        return new DefaultRefreshView();
    }

    protected DefaultLoadView getLoadView() {
        return new DefaultLoadView();
    }

    protected abstract int itemLayoutId(ShowData item, int position);

    protected abstract void convertView(ViewHolder holder, int position, ShowData item);

    protected void notifyItemView(ViewHolder holder, int position, ShowData item) {

    }

    protected int getStartPage() {
        return 1;
    }

    protected abstract void requestData(boolean isRefresh);

    protected abstract void makeItemData(List<Request> result);

    protected PullToRefreshRecyclerView getRecyclerView() {
        return findView(R.id.layout_activity_base_list);
    }

}
