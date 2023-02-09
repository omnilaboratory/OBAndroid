package com.omni.testnet.obd;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import lnrpc.StateGrpc;

public class RemoteObdStateService implements ObdStateService {

    private final StateGrpc.StateStub asyncStub;

    public RemoteObdStateService(Channel channel, CallCredentials callCredentials) {
        asyncStub = StateGrpc.newStub(channel).withCallCredentials(callCredentials);
    }

    @Override
    public Observable<lnrpc.Stateservice.SubscribeStateResponse> subscribeState(lnrpc.Stateservice.SubscribeStateRequest request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.subscribeState(request, new RemoteObdStreamObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.Stateservice.GetStateResponse> getState(lnrpc.Stateservice.GetStateRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.getState(request, new RemoteObdSingleObserver<>(emitter)));
    }

}