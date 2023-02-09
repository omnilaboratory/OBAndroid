package com.omni.testnet.obd;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.reactivex.rxjava3.core.Single;
import lnrpc.WalletUnlockerGrpc;

public class RemoteObdWalletUnlockerService implements ObdWalletUnlockerService {

    private final WalletUnlockerGrpc.WalletUnlockerStub asyncStub;

    public RemoteObdWalletUnlockerService(Channel channel, CallCredentials callCredentials) {
        asyncStub = WalletUnlockerGrpc.newStub(channel).withCallCredentials(callCredentials);
    }

    @Override
    public Single<lnrpc.Walletunlocker.GenSeedResponse> genSeed(lnrpc.Walletunlocker.GenSeedRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.genSeed(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.Walletunlocker.InitWalletResponse> initWallet(lnrpc.Walletunlocker.InitWalletRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.initWallet(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.Walletunlocker.UnlockWalletResponse> unlockWallet(lnrpc.Walletunlocker.UnlockWalletRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.unlockWallet(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.Walletunlocker.ChangePasswordResponse> changePassword(lnrpc.Walletunlocker.ChangePasswordRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.changePassword(request, new RemoteObdSingleObserver<>(emitter)));
    }

}