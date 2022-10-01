package com.omni.wallet.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class ChooseBackupTypeActivity extends AppBaseActivity {
    @BindView(R.id.iv_local)
    public ImageView mLocalIv;
    @BindView(R.id.tv_local)
    public TextView mLocalTv;
    @BindView(R.id.iv_google_drive)
    public ImageView mGoogleDriveIv;
    @BindView(R.id.tv_google_drive)
    public TextView mGoogleDriveTv;
    @BindView(R.id.iv_drop_box)
    public ImageView mDropBoxIv;
    @BindView(R.id.tv_drop_box)
    public TextView mDropBoxTv;

    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_white);
    }

    @Override
    protected int titleId() {
        return R.string.choose_backup_type;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_choose_backup_type;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    /**
     * 点击Local
     */
    @OnClick(R.id.layout_local)
    public void clickLocal() {
        mLocalIv.setImageResource(R.mipmap.icon_local);
        mLocalTv.setTextColor(Color.parseColor("#ff2d33c2"));
        mGoogleDriveIv.setImageResource(R.mipmap.icon_google_gray);
        mGoogleDriveTv.setTextColor(Color.parseColor("#ff7f7f7f"));
        mDropBoxIv.setImageResource(R.mipmap.icon_drop_box_gray);
        mDropBoxTv.setTextColor(Color.parseColor("#ff7f7f7f"));
    }

    /**
     * 点击Google Drive
     */
    @OnClick(R.id.layout_google_drive)
    public void clickGoogleDrive() {
        mLocalIv.setImageResource(R.mipmap.icon_local_gray);
        mLocalTv.setTextColor(Color.parseColor("#ff7f7f7f"));
        mGoogleDriveIv.setImageResource(R.mipmap.icon_google);
        mGoogleDriveTv.setTextColor(Color.parseColor("#ff2d33c2"));
        mDropBoxIv.setImageResource(R.mipmap.icon_drop_box_gray);
        mDropBoxTv.setTextColor(Color.parseColor("#ff7f7f7f"));
    }

    /**
     * 点击Drop Box
     */
    @OnClick(R.id.layout_drop_box)
    public void clickDropBox() {
        mLocalIv.setImageResource(R.mipmap.icon_local_gray);
        mLocalTv.setTextColor(Color.parseColor("#ff7f7f7f"));
        mGoogleDriveIv.setImageResource(R.mipmap.icon_google_gray);
        mGoogleDriveTv.setTextColor(Color.parseColor("#ff7f7f7f"));
        mDropBoxIv.setImageResource(R.mipmap.icon_drop_box);
        mDropBoxTv.setTextColor(Color.parseColor("#ff2d33c2"));
    }

    /**
     * 点击Next
     */
    @OnClick(R.id.btn_next)
    public void clickNext() {
        switchActivity(GetBlockDataActivity.class);
    }
}
