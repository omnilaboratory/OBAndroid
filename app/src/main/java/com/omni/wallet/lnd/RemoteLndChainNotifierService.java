package com.omni.wallet.lnd;

import chainrpc.ChainNotifierGrpc;
import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.reactivex.rxjava3.core.Observable;

public class RemoteLndChainNotifierService implements LndChainNotifierService {

    private final ChainNotifierGrpc.ChainNotifierStub asyncStub;

    public RemoteLndChainNotifierService(Channel channel, CallCredentials callCredentials) {
        asyncStub = ChainNotifierGrpc.newStub(channel).withCallCredentials(callCredentials);
    }

    @Override
    public Observable<chainrpc.Chainnotifier.ConfEvent> registerConfirmationsNtfn(chainrpc.Chainnotifier.ConfRequest request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.registerConfirmationsNtfn(request, new RemoteLndStreamObserver<>(emitter)));
    }

    @Override
    public Observable<chainrpc.Chainnotifier.SpendEvent> registerSpendNtfn(chainrpc.Chainnotifier.SpendRequest request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.registerSpendNtfn(request, new RemoteLndStreamObserver<>(emitter)));
    }

    @Override
    public Observable<chainrpc.Chainnotifier.BlockEpoch> registerBlockEpochNtfn(chainrpc.Chainnotifier.BlockEpoch request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.registerBlockEpochNtfn(request, new RemoteLndStreamObserver<>(emitter)));
    }

}