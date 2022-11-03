package com.omni.wallet.lnd;

import io.reactivex.rxjava3.core.Single;

public interface LndWalletUnlockerService {

    Single<lnrpc.Walletunlocker.GenSeedResponse> genSeed(lnrpc.Walletunlocker.GenSeedRequest request);

    Single<lnrpc.Walletunlocker.InitWalletResponse> initWallet(lnrpc.Walletunlocker.InitWalletRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface unlockWallet is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.Walletunlocker.UnlockWalletResponse> unlockWallet(lnrpc.Walletunlocker.UnlockWalletRequest request);

    Single<lnrpc.Walletunlocker.ChangePasswordResponse> changePassword(lnrpc.Walletunlocker.ChangePasswordRequest request);
}