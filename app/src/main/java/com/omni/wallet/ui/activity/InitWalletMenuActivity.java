package com.omni.wallet.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

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
import com.omni.wallet.entity.event.CloseUselessActivityEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.ui.activity.createwallet.CreateWalletStepThreeActivity;
import com.omni.wallet.utils.DriveServiceHelper;
import com.omni.wallet.view.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;

import butterknife.OnClick;

public class InitWalletMenuActivity extends AppBaseActivity {
    String TAG = InitWalletMenuActivity.class.getSimpleName();
    private static final int REQUEST_CODE_SIGN_IN = 3;
    private DriveServiceHelper mDriveServiceHelper;
    LoadingDialog mLoadingDialog;

    @Override
    protected int getContentView() {
        return R.layout.activity_init_wallet_menu;
    }

    @Override
    protected void initView() {
        mLoadingDialog = new LoadingDialog(mContext);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @OnClick({R.id.btn_create})
    public void clickCreate() {
        User.getInstance().setBackUp(mContext, false);
        switchActivity(CreateWalletStepThreeActivity.class);
    }

    @OnClick({R.id.btn_recover})
    public void clickRecover() {
        // Authenticate the user. For most apps, this should be done when the user performs an
        // action that requires Drive access rather than in onCreate.
        requestSignIn();
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
     * Handles the {@code result} of a completed sign-in activity initiated from {@link
     * #requestSignIn()}.
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
                    mLoadingDialog.dismiss();
                    if (fileList.getFiles().size() == 0) {
                        ToastUtils.showToast(mContext, "No backup files found.");
                    } else {
                        User.getInstance().setBackUp(mContext, true);
                        LogUtils.e("=============================", User.getInstance().isBackUp(mContext) + "");
                        switchActivity(CreateWalletStepThreeActivity.class);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseUselessActivityEvent(CloseUselessActivityEvent event) {
        finish();
    }
}
