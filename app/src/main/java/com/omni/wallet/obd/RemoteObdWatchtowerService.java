package com.omni.wallet.obd;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.reactivex.rxjava3.core.Single;
import watchtowerrpc.WatchtowerGrpc;

public class RemoteObdWatchtowerService implements ObdWatchtowerService {

    private final WatchtowerGrpc.WatchtowerStub asyncStub;

    public RemoteObdWatchtowerService(Channel channel, CallCredentials callCredentials) {
        asyncStub = WatchtowerGrpc.newStub(channel).withCallCredentials(callCredentials);
    }

    @Override
    public Single<watchtowerrpc.WatchtowerOuterClass.GetInfoResponse> getInfo(watchtowerrpc.WatchtowerOuterClass.GetInfoRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.getInfo(request, new RemoteObdSingleObserver<>(emitter)));
    }

}