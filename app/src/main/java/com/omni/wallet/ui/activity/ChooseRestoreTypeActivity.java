package com.omni.wallet.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.common.ConstantInOB;
import com.omni.wallet.common.ConstantWithNetwork;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepThreeActivity;
import com.omni.wallet.utils.DriveServiceHelper;
import com.omni.wallet.utils.MoveCacheFileToFileObd;
import com.omni.wallet.view.dialog.LoadingDialog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 汉: 选择恢复文件方式的页面
 * En: ChooseRestoreTypeActivity
 * author: guoyalei
 * date: 2023/5/30
 */
public class ChooseRestoreTypeActivity extends AppBaseActivity {
    private static final String TAG = ChooseRestoreTypeActivity.class.getSimpleName();
    @BindView(R.id.view_top)
    View mTopView;
    @BindView(R.id.layout_local_directory_file)
    LinearLayout mLocalDirectoryFileLayout;
    @BindView(R.id.tv_google_drive)
    TextView mGoogleDriveTv;
    @BindView(R.id.tv_local_directory)
    TextView mLocalDirectoryTv;
    @BindView(R.id.tv_file_name)
    TextView mFileNameTv;
    @BindView(R.id.tv_file_name_another)
    TextView mFileNameAnotherTv;

    private static final int REQUEST_CODE_SIGN_IN = 3;
    private DriveServiceHelper mDriveServiceHelper;
    LoadingDialog mLoadingDialog;
    int tag = 0;

    @Override
    protected View getStatusBarTopView() {
        return mTopView;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_choose_restore_type;
    }

    @Override
    protected void initView() {
        mLoadingDialog = new LoadingDialog(mContext);
    }

    @Override
    protected void initData() {

    }

    /**
     * Click google drive
     * 点击google drive
     */
    @OnClick(R.id.layout_google_drive)
    public void clickGoogleDrive() {
        tag = 1;
        mGoogleDriveTv.setTextColor(Color.parseColor("#000000"));
        mLocalDirectoryTv.setTextColor(Color.parseColor("#40000000"));
        mLocalDirectoryFileLayout.setVisibility(View.GONE);
    }

    /**
     * Click local directory
     * 点击local directory
     */
    @OnClick(R.id.layout_local_directory)
    public void clickLocalDirectory() {
        tag = 2;
        mGoogleDriveTv.setTextColor(Color.parseColor("#40000000"));
        mLocalDirectoryTv.setTextColor(Color.parseColor("#000000"));
        mLocalDirectoryFileLayout.setVisibility(View.VISIBLE);
        mFileNameTv.setText(Environment.getExternalStorageDirectory() + "/OBBackupFiles/");
        mFileNameAnotherTv.setText(Environment.getExternalStorageDirectory() + "/OBBackupFiles/");
    }

    /**
     * Click back
     * 点击返回
     */
    @OnClick(R.id.btn_back)
    public void clickBack() {
        finish();
    }

    /**
     * Click start
     * 点击开始
     */
    @OnClick(R.id.btn_start)
    public void clickStart() {
        if (tag == 0) {
            ToastUtils.showToast(mContext, "Please select from where to recover your wallet");
            return;
        }
        if (tag == 1) {
            // Authenticate the user. For most apps, this should be done when the user performs an
            // action that requires Drive access rather than in onCreate.
            requestSignIn();
        } else if (tag == 2) {
            // 本地恢复(Local restore)
            String storageWalletPath = mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadDirectory();
            String storageChannelPath = mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadChannelDirectory();
            File fromWalletPath = new File(Environment.getExternalStorageDirectory() + "/OBBackupFiles/wallet.db");
            File fromChannelPath = new File(Environment.getExternalStorageDirectory() + "/OBBackupFiles/channel.db");
            File toWalletPath = new File(mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadDirectory() + "wallet.db");
            File toChannelPath = new File(mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadChannelDirectory() + "channel.db");
            if (fromWalletPath.exists() && fromChannelPath.exists()) {
                MoveCacheFileToFileObd.createDirs(storageWalletPath);
                MoveCacheFileToFileObd.createDirs(storageChannelPath);
                MoveCacheFileToFileObd.copyFile(fromWalletPath, toWalletPath);
                MoveCacheFileToFileObd.copyFile(fromChannelPath, toChannelPath);
//                try {
//                    BufferedReader bf = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory() + "/OBBackupFiles/address.txt"));
//                    for (String line = bf.readLine(); line != null; line = bf.readLine()) {
//                        User.getInstance().setWalletAddress(mContext, line);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                switchActivity(CreateWalletStepThreeActivity.class);
            } else {
                ToastUtils.showToast(mContext, "The backup file does not exist");
            }
        }
    }

    /**
     * Starts a sign-in activity using {@link #REQUEST_CODE_SIGN_IN}.
     */
    private void requestSignIn() {
        LogUtils.e(TAG, "Requesting sign-in");

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    mLoadingDialog.show();
                    handleSignInResult(resultData);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, resultData);
    }

    /**
     * Handles the {@code result} of a completed sign-in activity initiated from requestSignIn.
     */
    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {
                    LogUtils.e(TAG, "Signed in as " + googleAccount.getEmail());

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    this, Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive googleDriveService =
                            new Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    new GsonFactory(),
                                    credential)
                                    .setApplicationName("OB Wallet")
                                    .build();

                    // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                    // Its instantiation is required before handling any onClick actions.
                    mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
                    query();
                })
                .addOnFailureListener(exception -> {
                    mLoadingDialog.dismiss();
                    LogUtils.e(TAG, "Unable to sign in.", exception);
                });
    }

    /**
     * Queries the Drive REST API for files visible to this app and lists them in the content view.
     */
    private void query() {
        if (mDriveServiceHelper != null) {
            LogUtils.e(TAG, "Querying for files.");

            mDriveServiceHelper.queryFiles().addOnSuccessListener(new OnSuccessListener<FileList>() {
                @Override
                public void onSuccess(FileList fileList) {
                    if (fileList.getFiles().size() == 0) {
                        mLoadingDialog.dismiss();
                        ToastUtils.showToast(mContext, "No backup files found.");
                    } else {
                        List<com.google.api.services.drive.model.File> list = new ArrayList<>();
                        for (int i = 0; i < fileList.getFiles().size(); i++) {
                            if (!fileList.getFiles().get(i).getName().contains("_mainnet")) {
                                list.add(fileList.getFiles().get(i));
                            }
                        }
                        if (list.size() == 0) {
                            mLoadingDialog.dismiss();
                            ToastUtils.showToast(mContext, "No backup files found.");
                        } else {
                            readAddressFile(list.get(1).getId(), list.get(0).getId(), list.get(2).getId());
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mLoadingDialog.dismiss();
                    LogUtils.e(TAG, "Unable to query files.", e);
                }
            });
        }
    }

    /**
     * Retrieves the title and content of a file identified by {@code fileId} and populates the UI.
     */
    private void readAddressFile(String walletFileId, String channelFileId, String addressFileId) {
        if (mDriveServiceHelper != null) {
            LogUtils.e(TAG, "Reading address file " + addressFileId);

            mDriveServiceHelper.readFile(addressFileId)
                    .addOnSuccessListener(nameAndContent -> {
//                        String address = nameAndContent.first;
//                        User.getInstance().setWalletAddress(mContext, address);
                        readWalletFile(walletFileId, channelFileId);
                    })
                    .addOnFailureListener(exception ->
                            LogUtils.e(TAG, "Couldn't read addreess file.", exception));
        }
    }

    /**
     * Retrieves the title and content of a file identified by {@code fileId} and populates the UI.
     */
    private void readWalletFile(String walletFileId, String channelFileId) {
        if (mDriveServiceHelper != null) {
            LogUtils.e(TAG, "Reading wallet file " + walletFileId);

            mDriveServiceHelper.downloadFile(walletFileId)
                    .addOnSuccessListener(walletFileContent -> {
                        String filePath = mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadDirectory() + "wallet.db";
//                        String filePath = mContext.getExternalFilesDir(null) + "/wallet.db";
                        binaryToFile(walletFileContent.toByteArray(), filePath);
                        readChannelFile(channelFileId);
                    })
                    .addOnFailureListener(exception ->
                            LogUtils.e(TAG, "Couldn't read wallet file.", exception));
        }
    }

    private void readChannelFile(String channelFileId) {
        if (mDriveServiceHelper != null) {
            LogUtils.e(TAG, "Reading channel file " + channelFileId);

            mDriveServiceHelper.downloadFile(channelFileId)
                    .addOnSuccessListener(channelFileContent -> {
                        mLoadingDialog.dismiss();
                        // 新建目标目录
                        // Create new target directory
                        (new File(mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadChannelDirectory())).mkdirs();
                        String filePath = mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadChannelDirectory() + "channel.db";
                        binaryToFile(channelFileContent.toByteArray(), filePath);
                        switchActivity(CreateWalletStepThreeActivity.class);
                    })
                    .addOnFailureListener(exception ->
                            LogUtils.e(TAG, "Couldn't read channel file.", exception));
        }
    }

    // 二进制数据转成文件
    public void binaryToFile(byte[] bytes, String filePath) {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fos = new FileOutputStream(filePath);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bos != null) {
                        bos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}