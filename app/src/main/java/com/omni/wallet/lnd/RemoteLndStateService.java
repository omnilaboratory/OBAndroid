package com.omni.wallet.lnd;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import lnrpc.StateGrpc;

public class RemoteLndStateService implements LndStateService {

    private final StateGrpc.StateStub asyncStub;

    public RemoteLndStateService(Channel channel, CallCredentials callCredentials) {
        asyncStub = StateGrpc.newStub(channel).withCallCredentials(callCredentials);
    }

    @Override
    public Observable<lnrpc.Stateservice.SubscribeStateResponse> subscribeState(lnrpc.Stateservice.SubscribeStateRequest request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.subscribeState(request, new RemoteLndStreamObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.Stateservice.GetStateResponse> getState(lnrpc.Stateservice.GetStateRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.getState(request, new RemoteLndSingleObserver<>(emitter)));
    }

}