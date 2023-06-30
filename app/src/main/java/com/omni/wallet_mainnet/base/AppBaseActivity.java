package com.omni.wallet_mainnet.base;


import android.accounts.Account;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

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
import com.omni.wallet_mainnet.baselibrary.utils.LogUtils;
import com.omni.wallet_mainnet.baselibrary.utils.StringUtils;
import com.omni.wallet_mainnet.baselibrary.utils.ToastUtils;
import com.omni.wallet_mainnet.common.ConstantInOB;
import com.omni.wallet_mainnet.common.ConstantWithNetwork;
import com.omni.wallet_mainnet.framelibrary.base.FrameBaseActivity;
import com.omni.wallet_mainnet.framelibrary.entity.User;
import com.omni.wallet_mainnet.utils.DriveServiceHelper;
import com.omni.wallet_mainnet.utils.MoveCacheFileToFileObd;
import com.omni.wallet_mainnet.view.dialog.UnlockDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * App的Activity父类
 * Created by fa on 2018/8/2.
 */

public abstract class AppBaseActivity extends FrameBaseActivity {
    private static final String TAG = AppBaseActivity.class.getSimpleName();
    private static long STOP_TIME = 0;
    private static boolean stopApp = false;
    UnlockDialog mUnlockDialog;

    private static final int REQUEST_CODE_SIGN_IN = 30;
    private DriveServiceHelper mDriveServiceHelper;

    public String getRunningActivityName() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return runningActivity;
    }

    public static long getStopTime() {
        return STOP_TIME;
    }

    public static void setStopTime(long stopTime) {
        STOP_TIME = stopTime;
    }

    public static boolean isStopApp() {
        return stopApp;
    }

    public static void setStopApp(boolean stopApp) {
        AppBaseActivity.stopApp = stopApp;
    }

    public boolean isRunningForeground() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(this.getApplicationInfo().processName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean isRunningSelf = isRunningForeground();
        if (!isRunningSelf) {
            long stopTime = System.currentTimeMillis();
            setStopTime(stopTime);
            setStopApp(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStopApp()) {
            long startTime = System.currentTimeMillis();
            long stopTime = getStopTime();
            long stopMills = startTime - stopTime;
            setStopApp(false);
            if (stopMills >= ConstantInOB.MINUTE_MILLIS * 5) {
                String runningActivityName = getRunningActivityName();
                String[] runningActivityNameArr = runningActivityName.split("\\.");
                String name = runningActivityNameArr[5];
                Log.e(TAG + "onResume: ", name);
                switch (name) {
                    case "UnlockActivity":
                    case "backup":
                    case "recoverwallet":
                    case "SplashActivity":
                    case "ForgetPwdActivity":
                    case "ForgetPwdNextActivity":
                    case "createwallet":
                    case "InitWalletMenuActivity":
                        break;
                    default:
                        mUnlockDialog = new UnlockDialog(mContext);
                        mUnlockDialog.show();
                        break;
                }
            }
        }
    }

    public void autoBackupFiles() {
        File walletPath = new File(mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadDirectory() + "wallet.db");
        File channelPath = new File(mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadChannelDirectory() + "channel.db");
        String storagePath = Environment.getExternalStorageDirectory() + "/OBMainnetBackupFiles";
        File toWalletPath = new File(Environment.getExternalStorageDirectory() + "/OBMainnetBackupFiles/wallet.db");
        File toChannelPath = new File(Environment.getExternalStorageDirectory() + "/OBMainnetBackupFiles/channel.db");
        if (walletPath.exists() && channelPath.exists()) {
            // 本地备份(Local backup)
            MoveCacheFileToFileObd.createDirs(storagePath);
            MoveCacheFileToFileObd.copyFile(walletPath, toWalletPath);
            MoveCacheFileToFileObd.copyFile(channelPath, toChannelPath);
            MoveCacheFileToFileObd.createFile(storagePath + "/address.txt", User.getInstance().getWalletAddress(mContext));
            // Authenticate the user. For most apps, this should be done when the user performs an
            // action that requires Drive access rather than in onCreate.
            if (StringUtils.isEmpty(User.getInstance().getGoogleAccountName(mContext))) {
                requestSignIn();
            } else {
                GoogleAccountCredential credential =
                        GoogleAccountCredential.usingOAuth2(
                                this, Collections.singleton(DriveScopes.DRIVE_FILE));
                credential.setSelectedAccount(new Account(User.getInstance().getGoogleAccountName(mContext), User.getInstance().getGoogleAccountType(mContext)));
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
            }
        } else {
            ToastUtils.showToast(mContext, "The backup file does not exist");
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
                    User.getInstance().setGoogleAccountName(mContext, googleAccount.getAccount().name);
                    User.getInstance().setGoogleAccountType(mContext, googleAccount.getAccount().type);
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
                .addOnFailureListener(exception -> LogUtils.e(TAG, "Unable to sign in.", exception));
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
                        createAddressFile();
                    } else {
                        List<com.google.api.services.drive.model.File> list = new ArrayList<>();
                        for (int i = 0; i < fileList.getFiles().size(); i++) {
                            if (fileList.getFiles().get(i).getName().contains("_mainnet")) {
                                list.add(fileList.getFiles().get(i));
                            }
                        }
                        if (list.size() == 0) {
                            createAddressFile();
                        } else {
                            saveAddressFile(list.get(1).getId(), list.get(0).getId(), list.get(2).getId());
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    LogUtils.e(TAG, "Unable to query files.", e);
                }
            });
        }
    }

    /**
     * Creates a new file via the Drive REST API.
     */
    private void createAddressFile() {
        if (mDriveServiceHelper != null) {
            LogUtils.e(TAG, "Creating a address file.");
            mDriveServiceHelper.createFile(User.getInstance().getWalletAddress(mContext) + "_mainnet")
                    .addOnSuccessListener(fileId -> createWalletFile())
                    .addOnFailureListener(exception -> {
                        LogUtils.e(TAG, "Couldn't create address file.", exception);
                    });
        }
    }

    private void createWalletFile() {
        if (mDriveServiceHelper != null) {
            LogUtils.e(TAG, "Creating wallet file.");
            String filePath = mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadDirectory() + "wallet.db";
            LogUtils.e(TAG, filePath);
            mDriveServiceHelper.createFile(filePath, "wallet_mainnet.db").addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String s) {
                    createChannelFile();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    LogUtils.e(TAG, "Couldn't create wallet file.", e);
                }
            });
        }
    }

    private void createChannelFile() {
        if (mDriveServiceHelper != null) {
            LogUtils.e(TAG, "Creating channel file.");
            String filePath = mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadChannelDirectory() + "channel.db";
            LogUtils.e(TAG, filePath);
            mDriveServiceHelper.createFile(filePath, "channel_mainnet.db").addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String s) {
                    LogUtils.e(TAG, "Channel fileId" + s);
                    User.getInstance().setAutoBackUp(mContext, true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    LogUtils.e(TAG, "Couldn't create channel file.", e);
                }
            });
        }
    }

    private void saveAddressFile(String walletFileId, String channelFileId, String addressFileId) {
        if (mDriveServiceHelper != null) {
            LogUtils.e(TAG, "Save address file " + addressFileId);

            mDriveServiceHelper.saveAddressFile(addressFileId, User.getInstance().getWalletAddress(mContext) + "_mainnet")
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            saveWalletFile(walletFileId, channelFileId);
                        }
                    })
                    .addOnFailureListener(exception ->
                            LogUtils.e(TAG, "Couldn't Save address file.", exception));
        }
    }

    private void saveWalletFile(String walletFileId, String channelFileId) {
        if (mDriveServiceHelper != null) {
            LogUtils.e(TAG, "Save wallet file " + walletFileId);

            String filePath = mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadDirectory() + "wallet.db";
            mDriveServiceHelper.saveDbFile(walletFileId, filePath, "wallet_mainnet.db")
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            saveChannelFile(channelFileId);
                        }
                    })
                    .addOnFailureListener(exception ->
                            LogUtils.e(TAG, "Couldn't Save wallet file.", exception));
        }
    }

    private void saveChannelFile(String channelFileId) {
        if (mDriveServiceHelper != null) {
            LogUtils.e(TAG, "Save channel file " + channelFileId);

            String filePath = mContext.getExternalFilesDir(null) + "/obd" + ConstantWithNetwork.getInstance(ConstantInOB.networkType).getDownloadChannelDirectory() + "channel.db";
            mDriveServiceHelper.saveDbFile(channelFileId, filePath, "channel_mainnet.db")
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            LogUtils.e(TAG, "Channel fileId" + s);
                            User.getInstance().setAutoBackUp(mContext, true);
                        }
                    })
                    .addOnFailureListener(exception ->
                            LogUtils.e(TAG, "Couldn't Save channel file.", exception));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setStopApp(false);
        if (mUnlockDialog != null) {
            mUnlockDialog.release();
        }
    }
}
