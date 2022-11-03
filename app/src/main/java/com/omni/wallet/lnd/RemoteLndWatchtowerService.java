package com.omni.wallet.lnd;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.reactivex.rxjava3.core.Single;
import watchtowerrpc.WatchtowerGrpc;

public class RemoteLndWatchtowerService implements LndWatchtowerService {

    private final WatchtowerGrpc.WatchtowerStub asyncStub;

    public RemoteLndWatchtowerService(Channel channel, CallCredentials callCredentials) {
        asyncStub = WatchtowerGrpc.newStub(channel).withCallCredentials(callCredentials);
    }

    @Override
    public Single<watchtowerrpc.WatchtowerOuterClass.GetInfoResponse> getInfo(watchtowerrpc.WatchtowerOuterClass.GetInfoRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.getInfo(request, new RemoteLndSingleObserver<>(emitter)));
    }

}