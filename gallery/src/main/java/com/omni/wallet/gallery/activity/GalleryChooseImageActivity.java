package com.omni.wallet.gallery.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.utils.ThreadPoolUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.utils.image.ImageUtils;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.divider.DividerItemDecoration;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.framelibrary.common.PageRouteConfig;
import com.omni.wallet.framelibrary.view.dialog.WaitingDialog;
import com.omni.wallet.framelibrary.view.navigationBar.DefaultNavigationBar;
import com.omni.wallet.gallery.R;
import com.omni.wallet.gallery.R2;
import com.omni.wallet.gallery.base.GalleryBaseActivity;
import com.omni.wallet.gallery.common.Constants;
import com.omni.wallet.gallery.entity.GalleryDataTransmit;
import com.omni.wallet.gallery.entity.GalleryEntity;
import com.omni.wallet.gallery.entity.GalleryFilesEntity;
import com.omni.wallet.gallery.entity.event.BrowseImageEvent;
import com.omni.wallet.gallery.entity.event.GalleryEvent;
import com.omni.wallet.gallery.entity.event.SelectImageEvent;
import com.omni.wallet.gallery.util.GalleryConfig;
import com.omni.wallet.gallery.util.GalleryUtils;
import com.omni.wallet.gallery.view.ImagesDirListDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 图库照片选择的Activity
 */
@Route(path = PageRouteConfig.PAGE_GALLERY)
public class GalleryChooseImageActivity extends GalleryBaseActivity {
    private static final String TAG = GalleryChooseImageActivity.class.getSimpleName();

    private DefaultNavigationBar mTitle;// 标题栏
    @BindView(R2.id.rv_choose_image_show_pic)
    public RecyclerView mRecyclerView;// 展示图片的RecyclerView
    @BindView(R2.id.layout_bottom)
    public RelativeLayout mBottomLayout;// 底部的布局
    @BindView(R2.id.tv_image_count)
    public TextView mImageCountTv;// 图片张数
    @BindView(R2.id.tv_choose_all_images)
    public TextView mAllImageTv;// 所有图片

    private Bundle mBundle;
    private Gson gson;
    // 页面类型
    private int mPageType;
    // 是否裁剪照片
    private boolean mCutImage = false;
    // RecyclerView的数据适配器
    private MyRecyclerViewAdapter mAdapter;
    // 保存所有的图片的实体集合
    private List<GalleryEntity> mAllImagesList;
    // 选中的图片信息的实体
    private List<GalleryEntity> mSelectImagesList = new ArrayList<>();
    // 最多选择的图片数量（不限制数量为integer的最大值）
    private int mMaxSelectImageSize = Integer.MAX_VALUE;
    // 获取图库信息的工具类
    private GalleryUtils mGalleryUtils;
    // 展示图片分类信息的Dialog
    private ImagesDirListDialog mImagesDirListDialog;
    // 当前选中的图片分类条目的名字
    private String mCurrentSelectedFileName;

    // 选择图片页面标题
    private String mTitleStr;
    // 加载的对话框
    private WaitingDialog mWaitingDialog;


    @Override
    protected void getBundleData(Bundle bundle) {
        super.getBundleData(bundle);
        this.mBundle = bundle;
        // 页面标题
        mTitleStr = bundle.getString(GalleryConfig.KEY_TITLE);
        if (StringUtils.isEmpty(mTitleStr)) {
            mTitleStr = getResources().getString(R.string.gallery_text_gallery_title);
        }
        // 页面展示方式
        mPageType = bundle.getInt(GalleryConfig.KEY_PAGE_TYPE);
        // 图片最大选择数量
        mMaxSelectImageSize = bundle.getInt(GalleryConfig.KEY_MAX_SELECT_IMAGE_SIZE);
        if (mMaxSelectImageSize == 0) {
            mMaxSelectImageSize = Integer.MAX_VALUE;
        }
        // 是否裁剪照片
        mCutImage = bundle.getBoolean(GalleryConfig.KEY_CUT_IMAGE);
    }

    @Override
    protected int getContentView() {
        return R.layout.gallery_activity_choose_image;
    }

    @Override
    protected void initHeader() {
        mTitle = new DefaultNavigationBar.Builder(mContext)
                .setTitle(mTitleStr)
                .setRightClickListener(new MyRightTextClickListener())
                .build();
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        initRecyclerView();
    }

    @Override
    protected void initData() {
        gson = new GsonBuilder().serializeNulls().create();
        getImagesList();
    }


    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        mAllImagesList = new ArrayList<>();
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(1, mContext,
                DividerItemDecoration.BOTH_SET, R.drawable.gallery_shape_divider_choose_images));
        mAdapter = new MyRecyclerViewAdapter(this, mAllImagesList, R.layout.gallery_view_item_choose_image_show);
        mRecyclerView.setAdapter(mAdapter);
    }


    /**
     * 右上角文字点击事件
     */
    private class MyRightTextClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // 选择图片并关闭界面
            selectImageAndFinish();
        }
    }


    /**
     * 选择图片并关闭页面
     */
    private void selectImageAndFinish() {
        // 将获取的图片地址集合添加到EventBus实体中发送出去
        SelectImageEvent event = new SelectImageEvent();
        event.setSelectImageList(mSelectImagesList);
        EventBus.getDefault().post(event);
        // 关闭页面
        finish();
    }

    /**
     * RecyclerView的适配器
     */
    private class MyRecyclerViewAdapter extends CommonRecyclerAdapter<GalleryEntity> {

        MyRecyclerViewAdapter(Context context, List<GalleryEntity> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, GalleryEntity item) {
            // 设置条目的点击事件
            holder.setOnItemClickListener(new MyItemClickListener(position, item));
            // 设置右上角图标的状态
            holder.getView(R.id.iv_item_select).setSelected(item.isSelected());
            // 设置右上角图标的点击事件
            holder.getView(R.id.iv_item_select).setOnClickListener(new MyRightCheckClickListener(position, item));
            // 设置图片
            holder.setImageByUrl(R.id.iv_choose_image_show, new ViewHolder.HolderImageLoader(item.getFilePath()) {
                @Override
                public void displayImage(Context context, ImageView imageView, String imagePath) {
                    ImageUtils.showImageCenterCrop(GalleryChooseImageActivity.this, imagePath, imageView, R.drawable.gallery_bg_choose_image_show_image);
                }
            });
            // 设置是否选中
            ImageView itemShowImageIv = holder.getView(R.id.iv_choose_image_show);
            if (item.isSelected()) {
                holder.setImageResource(R.id.iv_item_select, R.drawable.gallery_icon_pic_checked);
                itemShowImageIv.setColorFilter(Color.parseColor("#77000000"));
            } else {
                holder.setImageResource(R.id.iv_item_select, R.drawable.gallery_icon_pic_un_checked);
                itemShowImageIv.setColorFilter(Color.parseColor("#00000000"));
            }
        }
    }

    /**
     * 点击事件
     */
    private class MyRightCheckClickListener implements View.OnClickListener {
        private int mPosition;
        private GalleryEntity mGalleryEntity;

        MyRightCheckClickListener(int mPosition, GalleryEntity galleryEntity) {
            this.mPosition = mPosition;
            this.mGalleryEntity = galleryEntity;
        }

        @Override
        public void onClick(View v) {
            // 获取当前状态
            boolean isSelected = v.isSelected();
            // 获取当前选择的图片数量
            int selectedSize = mSelectImagesList.size();
            // 已经选择了一张图片的时候弹Toast提示
            if (mMaxSelectImageSize > 0 && selectedSize >= mMaxSelectImageSize && !isSelected) {
                ToastUtils.showToast(mContext, "可选图片数量已达上限");
                return;
            }
            // 选中状态的图片添加到集合中
            if (!isSelected) {
                mSelectImagesList.add(mGalleryEntity);
            } else {// 未选中状态的图片从集合中移除
                mSelectImagesList.remove(mGalleryEntity);
            }
            // 更新实体中的选中状态
            mGalleryEntity.setSelected(!mGalleryEntity.isSelected());
            // 通知更新
            mAdapter.notifyItemChanged(mPosition);
            // 更新右上角选中数量
            setRightSelectText();
        }
    }


    /**
     * 条目点击事件
     */
    private class MyItemClickListener implements View.OnClickListener {
        private int mPosition;
        private GalleryEntity mGalleryEntity;

        MyItemClickListener(int position, GalleryEntity galleryEntity) {
            this.mPosition = position;
            this.mGalleryEntity = galleryEntity;
        }

        @Override
        public void onClick(View v) {
            // 图片集合对象直接存储到单例中
            String listJson = gson.toJson(mAllImagesList);
            // 图片集合Json
            GalleryDataTransmit.getInstance().setGalleryListJson(listJson);
            // 当前图片在集合中的位置
            mBundle.putInt(GalleryConfig.KEY_CLICK_POSITION, mPosition);
            // 最大选择数量
            mBundle.putInt(GalleryConfig.KEY_MAX_SELECT_IMAGE_SIZE, mMaxSelectImageSize);
            // 跳转图片浏览
            switchActivity(GalleryBrowseImageActivity.class, mBundle);
        }
    }


    /**
     * 获取所有图片信息集合
     */
    private void getImagesList() {
        mGalleryUtils = new GalleryUtils(mContext);
        // 正在加载的Dialog
        initShowWaitingDialog();
        // 数据获取
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                // 获取手机所有图片的信息
                // TODO 需要加一个加载对话框，用来缓冲信息获取时的等待
                List<GalleryEntity> tempList = mGalleryUtils.listAllImageDirs();
                mAllImagesList.clear();
                mAllImagesList.addAll(tempList);
                GalleryEvent event = new GalleryEvent();
                event.setType(GalleryEvent.TYPE_ALL_IMAGE_LIST);
                EventBus.getDefault().post(event);
            }
        });
    }

    // 初始化正在加载的Dialog
    private void initShowWaitingDialog() {
        if (mWaitingDialog == null) {
            mWaitingDialog = new WaitingDialog(mContext);
        }
        mWaitingDialog.showWaitingDialog(getResources().getString(R.string.text_dialog_waiting));
    }

    /**
     * 点击所有图片
     */
    @OnClick(R2.id.tv_choose_all_images)
    public void clickAllImages(View view) {
        // 创建Dialog
        if (mImagesDirListDialog == null) {
            mImagesDirListDialog = new ImagesDirListDialog(mContext);
            // 当前获取到所有图片了就把第一张当做条目中所有图片的首图片展示
            if (mAllImagesList != null && mAllImagesList.size() > 0) {
                String firstImagePath = mAllImagesList.get(0).getFilePath();
                mImagesDirListDialog.setFirstShowPicPath(firstImagePath);
            }
        }
        // 展示
        mImagesDirListDialog.show();
        // 开启线程获取图片目录信息的集合
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                GalleryEvent event = new GalleryEvent();
                event.setType(GalleryEvent.TYPE_ALL_IMAGE_DIR_LIST);
                event.setFilesList(mGalleryUtils.getImagesFileList(null));
                EventBus.getDefault().post(event);
            }
        });

    }


    /**
     * 接收通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GalleryEvent event) {
        int type = event.getType();
        switch (type) {
            case GalleryEvent.TYPE_ALL_IMAGE_LIST:// 初始化的时候获取图片信息成功
                mAdapter.notifyDataSetChanged();
                mImageCountTv.setText(mAllImagesList.size() + "张");
                if (mWaitingDialog != null) {
                    mWaitingDialog.dismiss();
                }
                break;
            case GalleryEvent.TYPE_ALL_IMAGE_DIR_LIST:// 点击全部图片的时候获取图片目录数据成功
                if (mImagesDirListDialog != null) {
                    List<GalleryFilesEntity> galleryFilesEntities = event.getFilesList();
                    mImagesDirListDialog.refreshListData(galleryFilesEntities);
                }
                break;
            case GalleryEvent.TYPE_CLICK_IMAGE_DIR:
                List<GalleryEntity> selectList = event.getChooseList();
                mCurrentSelectedFileName = event.getChooseItemFileName();
                // 因为选择全部的时候实体是null，所以加判断
                if (selectList == null) {
                    selectList = mGalleryUtils.listAllImageDirs();
                    mCurrentSelectedFileName = "所有图片";
                }
                // 设置路径名
                mAllImageTv.setText(mCurrentSelectedFileName);
                // 数据
                mAllImagesList.clear();
                mAllImagesList.addAll(selectList);
                mAdapter.notifyDataSetChanged();
                mImageCountTv.setText(selectList.size() + "张");
                break;
        }

    }


    /**
     * 接收图片大图浏览的时候传递过来的通知，用于更新选中状态
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BrowseImageEvent event) {
        int pageType = event.getPageType();
        if (pageType == Constants.TYPE_SELECT_IMAGE) {
            List<GalleryEntity> imageList = event.getImageList();
            // 更新选中的数据集合
            initSelectImageList(imageList);
            // 直接用回传的数据覆盖本页面数据
            mAllImagesList.clear();
            mAllImagesList.addAll(imageList);
            // 通知更新
            mAdapter.notifyDataSetChanged();
            // 更新右上角选中数量
            setRightSelectText();
        }
    }

    /**
     * 初始化已经选择的图片集合
     */
    private void initSelectImageList(List<GalleryEntity> imageList) {
        mSelectImagesList.clear();
        for (GalleryEntity galleryEntity : imageList) {
            if (galleryEntity.isSelected()) {
                mSelectImagesList.add(galleryEntity);
            }
        }
    }

    /**
     * 设置右上角选中的图片数量
     */
    private void setRightSelectText() {
        int selectSize = mSelectImagesList.size();
        // 设置右上角标题显示
        String rightText = getResources().getString(R.string.gallery_text_gallery_right_text) + "(" + selectSize + "/" + mMaxSelectImageSize + ")";
        if (selectSize == 0) {
            rightText = "";
        }
        mTitle.setRightText(rightText);
    }


    /**
     * 接到更新详情页的通知之后就关闭这个页面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SelectImageEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mAllImagesList != null) {
            mAllImagesList.clear();
            mAllImagesList = null;
        }
        super.onDestroy();
    }
}
