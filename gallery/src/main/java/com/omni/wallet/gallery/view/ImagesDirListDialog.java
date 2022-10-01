package com.omni.wallet.gallery.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.baselibrary.utils.image.ImageUtils;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.gallery.R;
import com.omni.wallet.gallery.entity.GalleryEntity;
import com.omni.wallet.gallery.entity.GalleryFilesEntity;
import com.omni.wallet.gallery.entity.event.GalleryEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 所有图片弹出的Dialog
 */

public class ImagesDirListDialog {

    private static final String TAG = ImagesDirListDialog.class.getSimpleName();
    private Context mContext;
    // 弹窗
    private AlertDialog mDialog;
    // RecyclerView
    private RecyclerView mRecyclerView;
    // 数据
    private List<GalleryFilesEntity> mGalleryFilesEntities;
    // 适配器
    private MyRecyclerViewAdapter mAdapter;
    // 第一个条目显示的图片
    private String mFirstShowPicPath;
    // 上次选中的条目的索引
    private int mLastSelectedPosition = -1;


    public ImagesDirListDialog(Context context) {
        this.mContext = context;
        mGalleryFilesEntities = new ArrayList<>();
        if (mDialog == null) {
            mDialog = new AlertDialog.Builder(context)
                    .setContentView(R.layout.gallery_layout_dialog_choose_image_file)
                    .fromBottom(true)
                    .fullWidth()
                    .create();
        }
        mRecyclerView = mDialog.getViewById(R.id.rv_show_all_pic);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MyRecyclerViewAdapter(context, mGalleryFilesEntities, R.layout.gallery_view_item_choose_image_file);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 刷新数据源
     */
    public void refreshListData(List<GalleryFilesEntity> galleryFilesEntities) {
        if (mGalleryFilesEntities != null) {
            mGalleryFilesEntities.clear();
            mGalleryFilesEntities.addAll(galleryFilesEntities);
        } else {
            mGalleryFilesEntities = new ArrayList<>();
            mGalleryFilesEntities.addAll(galleryFilesEntities);
        }
        // 追加第一条全部图片的数据,null就行，就是占位用的
        mGalleryFilesEntities.add(0, makeAllImageData(mFirstShowPicPath));
        // 上次选中条目置为选中状态
        if (mLastSelectedPosition != -1) {
            mGalleryFilesEntities.get(mLastSelectedPosition).selected = true;
        }
        // 通知更新
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 创建第一条全部图片的数据实体
     */
    @NonNull
    private GalleryFilesEntity makeAllImageData(String firstShowImage) {
        GalleryFilesEntity galleryFilesEntity = new GalleryFilesEntity();
        List<GalleryEntity> galleryEntities = new ArrayList<>();
        GalleryEntity galleryEntity = new GalleryEntity();
        galleryEntity.setFilePath(firstShowImage);
        galleryEntities.add(galleryEntity);
        galleryFilesEntity.selected = mLastSelectedPosition == -1;
        galleryFilesEntity.entityContent = galleryEntities;
        return galleryFilesEntity;
    }

    /**
     * 设置第一个条目需要显示的图片的url
     */
    public void setFirstShowPicPath(String firstShowPicPath) {
        this.mFirstShowPicPath = firstShowPicPath;
    }

    /**
     * 适配器
     */
    private class MyRecyclerViewAdapter extends CommonRecyclerAdapter<GalleryFilesEntity> {

        MyRecyclerViewAdapter(Context context, List<GalleryFilesEntity> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, int position, GalleryFilesEntity item) {
            holder.setOnItemClickListener(new MyItemClickListener(position, item));
            TextView fileNameTv = holder.getView(R.id.tv_file_item_name);
            TextView fileCountTv = holder.getView(R.id.tv_file_item_count);
            if (position != 0) {
                // 设置文件名
                fileNameTv.setText(item.fileName);
                // 设置图片数量
                fileCountTv.setVisibility(View.VISIBLE);
                fileCountTv.setText(item.entityContent.size() + "张");
            } else {
                // 设置文件名
                fileNameTv.setText("所有图片");
                // 设置图片数量隐藏
                fileCountTv.setVisibility(View.GONE);
            }
            // 设置首个图片显示
            holder.setImageByUrl(R.id.iv_file_item_image, new ViewHolder.HolderImageLoader(item.entityContent.get(0).getFilePath()) {
                @Override
                public void displayImage(Context context, ImageView imageView, String imagePath) {
                    ImageUtils.showImageCenterCrop(context, imagePath, imageView);
                }
            });
            // 设置勾选状态
            if (item.selected) {
                holder.getView(R.id.iv_file_item_select).setVisibility(View.VISIBLE);
            } else {
                holder.getView(R.id.iv_file_item_select).setVisibility(View.GONE);
            }
        }
    }


    /**
     * 条目点击事件
     */
    private class MyItemClickListener implements View.OnClickListener {
        private int mPosition;
        private GalleryFilesEntity mGalleryFilesEntity;

        MyItemClickListener(int mPosition, GalleryFilesEntity mGalleryFilesEntity) {
            this.mPosition = mPosition;
            this.mGalleryFilesEntity = mGalleryFilesEntity;
        }


        @Override
        public void onClick(View v) {
            // 发通知
            GalleryEvent event = new GalleryEvent();
            event.setType(GalleryEvent.TYPE_CLICK_IMAGE_DIR);
            if (mPosition == 0) {
                event.setChooseList(null);
            } else {
                event.setChooseList(mGalleryFilesEntity.entityContent);
            }
            event.setChooseItemFileName(mGalleryFilesEntity.fileName);
            EventBus.getDefault().post(event);
            mDialog.dismiss();
            // 设置上次选中条目
            mLastSelectedPosition = mPosition;
        }
    }


    /**
     * 显示
     */
    public void show() {
        if (mDialog != null) {
            mDialog.show();
            mAdapter.notifyDataSetChanged();
        }
    }
}
