package com.omni.wallet.gallery.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.omni.wallet.baselibrary.utils.DisplayUtil;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.utils.image.ImageUtils;
import com.omni.wallet.baselibrary.view.HackyViewPager;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.framelibrary.common.PageRouteConfig;
import com.omni.wallet.framelibrary.view.navigationBar.DefaultNavigationBar;
import com.omni.wallet.gallery.R;
import com.omni.wallet.gallery.R2;
import com.omni.wallet.gallery.base.GalleryBaseActivity;
import com.omni.wallet.gallery.common.Constants;
import com.omni.wallet.gallery.entity.GalleryDataTransmit;
import com.omni.wallet.gallery.entity.GalleryEntity;
import com.omni.wallet.gallery.entity.event.BrowseImageEvent;
import com.omni.wallet.gallery.entity.event.SelectImageEvent;
import com.omni.wallet.gallery.util.CutImageUtils;
import com.omni.wallet.gallery.util.GalleryConfig;
import com.omni.wallet.gallery.view.GalleryDeleteImageDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 浏览图片的Activity
 */
@Route(path = PageRouteConfig.PAGE_BROWSE_IMAGE)
public class GalleryBrowseImageActivity extends GalleryBaseActivity {
    private static final String TAG = GalleryBrowseImageActivity.class.getSimpleName();

    // 标题栏
    private DefaultNavigationBar mTitle;
    // 展示图片的ViewPager
    @BindView(R2.id.staff_vp_browse_image)
    public HackyViewPager mBrowseImageViewPager;
    // 底部选择按钮的布局
    @BindView(R2.id.gallery_layout_select_image)
    public RelativeLayout mSelectImageLayout;
    // 底部选择CheckBox
    @BindView(R2.id.gallery_cb_select_image)
    public CheckBox mSelectImageCb;
    // 底部显示选择的图片的水平RecyclerView
    @BindView(R2.id.gallery_recycler_browse_image_bottom)
    public RecyclerView mBottomRecyclerView;


    // 图片删除的Dialog
    private GalleryDeleteImageDialog mDeleteDialog;
    // 图片的链接地址集合
    private List<GalleryEntity> mImageList;
    // 选中的图片的集合
    private List<GalleryEntity> mSelectImageList = new ArrayList<>();
    // adapter
    private MyBrowseImageAdapter mAdapter;
    // 传递过来的选中的图片的索引
    private int mSelectPosition;
    // 当前显示的图片对应的数字
    private int mCurrentSelected;
    // 当前页面的索引
    private int mCurrentPosition;
    // 浏览图片的类型（图库浏览、资料上传详情页浏览、已提交审核详情浏览）
    private int mPageType;
    // 详情页的传递过来的数据集合对应的分类
    private int mDataType;
    // 图库浏览的时候最多能选择的数量
    private int mMaxSelectSize;
    // 关闭页面的时候是否需要发送更新图库选中状态的通知
    private boolean mUpdateSelectPageOnFinish = true;
    // 是否裁剪照片
    private boolean mCutImage = false;
    // 裁剪之后的文件新路径
    private String mNewFilePath;
    // 裁剪图片的宽度
    private int mCutImageWidth;
    // 裁剪图片的高度
    private int mCutImageHeight;
    // 裁剪图片的宽高比X
    private int mAspectX;
    // 裁剪图片的宽高比Y
    private int mAspectY;
    // 请求码
    private int mRequestCode = -0x11110000;
    // 底部水平RecyclerView的适配器
    private BottomRecyclerAdapter mBottomRecyclerAdapter;

    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(this, R.color.color_black);
    }

    @Override
    protected int getContentView() {
        return R.layout.gallery_activity_browse_image;
    }

    @Override
    protected void getBundleData(Bundle bundle) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        // 获取全部图片的集合
        String imageListJson = GalleryDataTransmit.getInstance().getGalleryListJson();
        mImageList = gson.fromJson(imageListJson, new TypeToken<List<GalleryEntity>>() {
        }.getType());
        // 初始化已经选择的图片集合
        initSelectImageList();
        // 索引
        mSelectPosition = bundle.getInt(GalleryConfig.KEY_CLICK_POSITION);
        mCurrentSelected = mSelectPosition + 1;
        // 获取页面类型
        mPageType = bundle.getInt(GalleryConfig.KEY_PAGE_TYPE);
        // 获取详情页的数据类型
        mDataType = bundle.getInt(GalleryConfig.KEY_DATA_TYPE);
        // 最多还能选几张
        mMaxSelectSize = bundle.getInt(GalleryConfig.KEY_MAX_SELECT_IMAGE_SIZE);
        // 是否裁剪照片
        mCutImage = bundle.getBoolean(GalleryConfig.KEY_CUT_IMAGE);
        // 获取裁剪图片的宽度
        mCutImageWidth = bundle.getInt(GalleryConfig.KEY_CUT_IMAGE_WIDTH);
        // 获取裁剪图片的高度
        mCutImageHeight = bundle.getInt(GalleryConfig.KEY_CUT_IMAGE_HEIGHT);
        // 获取的裁剪图片的宽高比X
        mAspectX = bundle.getInt(GalleryConfig.KEY_CUT_IMAGE_ASPECT_X);
        // 获取的裁剪图片的宽高比Y
        mAspectY = bundle.getInt(GalleryConfig.KEY_CUT_IMAGE_ASPECT_Y);
        // 请求码
        mRequestCode = bundle.getInt(GalleryConfig.KEY_BROWSE_IMAGE_REQUEST_CODE);
    }

    /**
     * 初始化已经选择的图片集合
     */
    private void initSelectImageList() {
        for (GalleryEntity galleryEntity : mImageList) {
            if (galleryEntity.isSelected()) {
                mSelectImageList.add(galleryEntity);
            }
        }
    }

    @Override
    protected void initHeader() {
        String title = mCurrentSelected + "/" + getListCount();
        mTitle = new DefaultNavigationBar.Builder(this)
                .setLeftTitle(title)
                .setTitleBgColorRes(R.color.color_white)
                .setRightClickListener(new MyRightClickListener())
                .build();
    }

    @Override
    protected void initView() {
        // 设置标题右边文字
        setTitleRightText();
        // 添加滚动监听
        mBrowseImageViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mCurrentPosition = position % getListCount();
                int currentSelected = position % getListCount() + 1;
                // 设置指示器文字
                setTitleText(currentSelected);
                // 图库的图片浏览的时候初始化右上角的选中状态
                if (mPageType == Constants.TYPE_SELECT_IMAGE) {
                    mSelectImageCb.setChecked(mImageList.get(position).isSelected());
                }
                // 缩略列表更新
                mBottomRecyclerAdapter.notifyDataSetChanged();
                // 滚动到选中的位置
                scrollToCheckedItem();
            }
        });
        // 图库选择图片浏览的时候初始化当前页面的选择框的选中状态
        if (mPageType == Constants.TYPE_SELECT_IMAGE) {
            mSelectImageLayout.setVisibility(View.VISIBLE);
            mSelectImageCb.setChecked(mImageList.get(mSelectPosition).isSelected());
        } else {
            mSelectImageLayout.setVisibility(View.GONE);
        }
        // 设置底部缩略列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBottomRecyclerView.setLayoutManager(linearLayoutManager);
        mBottomRecyclerAdapter = new BottomRecyclerAdapter(mContext, mSelectImageList, R.layout.gallery_view_item_browse_image_bottom);
        mBottomRecyclerView.setAdapter(mBottomRecyclerAdapter);
    }

    /**
     * 设置右上角文字状态
     */
    private void setTitleRightText() {
        if (mPageType == Constants.TYPE_SELECT_IMAGE) {// 图库选择图片
            // 单选模式的时候不操作右上角文字
            int selectImageSize = getSelectImageListSize();
            String rightText;
            if (selectImageSize > 0) {
                rightText = getResources().getString(R.string.gallery_text_browse_image_right_text) + "(" + selectImageSize + "/" + mMaxSelectSize + ")";
            } else {
                rightText = getResources().getString(R.string.gallery_text_browse_image_right_text);
            }
            mTitle.setRightText(rightText);
        } else if (mPageType == Constants.TYPE_EDIT_IMAGE) {// 查看并编辑图片集合
            mTitle.setRightText(getResources().getString(R.string.gallery_text_browse_image_title_delete));
        } else if (mPageType == Constants.TYPE_BROWSE_IMAGE) {// 单纯的浏览图片
            mTitle.setRightVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        mAdapter = new MyBrowseImageAdapter();
        // 设置Adapter
        mBrowseImageViewPager.setAdapter(mAdapter);
        // 初始化第一个选中的图片位置
        mBrowseImageViewPager.setCurrentItem(mSelectPosition);
    }


    /**
     * 获取图片数量
     */
    private int getListCount() {
        return mImageList == null ? 0 : mImageList.size();
    }

    /**
     * 设置Title数量显示
     */
    private void setTitleText(int currentSelected) {
        mTitle.setLeftTitleText(currentSelected + "/" + getListCount());
    }


    /**
     * 右上角点击事件监听
     */
    private class MyRightClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mPageType == Constants.TYPE_SELECT_IMAGE) {
                if (mCutImage) {
                    cutImage();
                } else {
                    selectImageAndFinish();
                }
            } else if (mPageType == Constants.TYPE_EDIT_IMAGE) {// 图片集合操作的时候的浏览（删除）
                if (mDeleteDialog == null) {
                    mDeleteDialog = new GalleryDeleteImageDialog(mContext, new MyDeleteDialogCallBack());
                }
                mDeleteDialog.show();
            }
        }
    }

    /**
     * 裁剪图片
     */
    private void cutImage() {
        if (mSelectImageList.size() > 0) {
            String oldFilePath = mSelectImageList.get(0).getFilePath();
            mNewFilePath = new File(oldFilePath).getParentFile().getAbsolutePath()
                    + File.separator + System.currentTimeMillis() + ".jpg";
            // 调用裁剪
            new CutImageUtils().cutImage(mContext, oldFilePath, mNewFilePath, mAspectX, mAspectY, mCutImageWidth, mCutImageHeight);
        } else {
            selectImageAndFinish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryConfig.REQUEST_CODE_CUT_IMAGE) {
            File file = new File(mNewFilePath);
            LogUtils.e(TAG, "=====picturePath=====>" + mNewFilePath);
            // 如果裁剪之后的图片存在就把路径添加到集合中传递出去
            if (file.exists()) {
                GalleryEntity entity = new GalleryEntity();
                entity.setFilePath(mNewFilePath);
                mSelectImageList.clear();
                mSelectImageList.add(entity);
                selectImageAndFinish();
            } else {
                LogUtils.e(TAG, "========裁剪之后的文件不存在========>");
            }
        }
    }

    /**
     * 选择图片并关闭页面
     */
    private void selectImageAndFinish() {
        // 如果选中的图片集合中无数据，就把当前显示的图片添加到集合中当成选中的图片
        if (mSelectImageList.size() == 0) {
            mSelectImageList.add(mImageList.get(mCurrentPosition));
        }
        // 将获取的图片集合发送出去
        SelectImageEvent event = new SelectImageEvent();
        event.setSelectImageList(mSelectImageList);
        EventBus.getDefault().post(event);
        mUpdateSelectPageOnFinish = false;
        finish();
    }


    /**
     * 点击下方选择图片的CheckBox
     */
    @OnClick(R2.id.gallery_cb_select_image)
    public void clickCheckBox(View view) {
        // 这个状态为点击之后，CheckBox已经改变之后的状态
        boolean isChecked = mSelectImageCb.isChecked();
        int selectImageSize = getSelectImageListSize();
        // 当前没选中，并且最大选择数已经没有了，就弹出Toast，并阻止继续选择
        if (isChecked && mMaxSelectSize - selectImageSize <= 0) {
            ToastUtils.showToast(mContext, "可选图片数量已达上限");
            // 重置选中状态
            mSelectImageCb.setChecked(false);
            return;
        }
        // 添加到集合中
        GalleryEntity checkedEntity = mImageList.get(mCurrentPosition);
        if (isChecked) {
            mSelectImageList.add(checkedEntity);
        } else {
            mSelectImageList.remove(checkedEntity);
        }
        // 集合选中状态变化
        mImageList.get(mCurrentPosition).setSelected(isChecked);
        // 设置右上角文字显示选择的图片数量
        setTitleRightText();
        // 更新缩略集合
        mBottomRecyclerAdapter.notifyDataSetChanged();
        // 滚动到选中的位置
        scrollToCheckedItem();
    }

    /**
     * 滚动到选中的位置
     */
    private void scrollToCheckedItem() {
        int position = getBottomSelectPosition();
        if (position != -1) {
            mBottomRecyclerView.scrollToPosition(position);
        }
    }

    /**
     * 获取当前显示的Image在选中集合中的索引
     */
    private int getBottomSelectPosition() {
        for (GalleryEntity entity : mSelectImageList) {
            if (mImageList.get(mCurrentPosition) == entity) {
                return mSelectImageList.indexOf(entity);
            }
        }
        return -1;
    }


    /**
     * 删除图片的Dialog的回调
     */
    private class MyDeleteDialogCallBack implements GalleryDeleteImageDialog.DeleteDialogCallBack {

        @Override
        public void onClickDelItem() {
            mImageList.remove(mCurrentPosition);
            if (mImageList.size() > 0) {
                mAdapter.notifyDataSetChanged();
                setTitleText(mCurrentPosition + 1);
            } else {
                finish();
            }
        }
    }

    /**
     * BannerViewPager的Adapter
     */
    private class MyBrowseImageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return getListCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(mContext);
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    finish();
                }
            });
            ImageUtils.showImageFitCenter(GalleryBrowseImageActivity.this, mImageList.get(position).getFilePath(), photoView);
            container.addView(photoView);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // 把 Object 强转为 View，然后将 view 从 ViewGroup 中清除
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            // 最简单解决 notifyDataSetChanged() 页面不刷新问题的方法
            return POSITION_NONE;
        }
    }


    /**
     * 获取已经选中的图片集合数量
     */
    private int getSelectImageListSize() {
        int result = 0;
        for (int i = 0; i < mImageList.size(); i++) {
            GalleryEntity galleryEntity = mImageList.get(i);
            if (galleryEntity.isSelected()) {
                result++;
            }
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        // 页面关闭的时候通知更新数据
        notifyDataChanged();
        super.onDestroy();
    }

    /**
     * 通知更新
     */
    private void notifyDataChanged() {
        if (mUpdateSelectPageOnFinish) {
            // 关闭的时候发送刷新数据的通知
            BrowseImageEvent event = new BrowseImageEvent();
            event.setRequestCode(mRequestCode);
            event.setPageType(mPageType);
            event.setDataType(mDataType);
            event.setImageList(mImageList);
            event.setSelectImageList(mSelectImageList);
            EventBus.getDefault().post(event);
        }
    }


    /**
     * 底部RecyclerView的适配器
     */
    private class BottomRecyclerAdapter extends CommonRecyclerAdapter<GalleryEntity> {

        public BottomRecyclerAdapter(Context context, List<GalleryEntity> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, int position, GalleryEntity item) {
            //设置图片展示
            holder.setImageByUrl(R.id.gallery_iv_browse_image_item_bottom, new ViewHolder.HolderImageLoader(item.getFilePath()) {
                @Override
                public void displayImage(Context context, ImageView imageView, String imagePath) {
                    ImageUtils.showImageCenterCrop(context, imagePath, imageView, DisplayUtil.dp2px(mContext, 60), DisplayUtil.dp2px(mContext, 60));
                }
            });
            // 设置边框展示
            View coverView = holder.getView(R.id.gallery_view_browse_image_item_bottom_cover);
            if (mImageList.get(mCurrentPosition) == item) {
                coverView.setSelected(true);
            } else {
                coverView.setSelected(false);
            }
            // 条目点击监听
            holder.setOnItemClickListener(new BottomListItemClickListener(item));

        }
    }

    /**
     * 底部缩略集合的条目点击监听
     */
    private class BottomListItemClickListener implements View.OnClickListener {
        private GalleryEntity mItem;

        public BottomListItemClickListener(GalleryEntity item) {
            this.mItem = item;
        }

        @Override
        public void onClick(View v) {
            int index = mImageList.indexOf(mItem);
            if (index >= 0 && index < mImageList.size()) {
                mCurrentPosition = index;
                mBottomRecyclerAdapter.notifyDataSetChanged();
                mBrowseImageViewPager.setCurrentItem(mCurrentPosition);
            }

        }
    }


}
