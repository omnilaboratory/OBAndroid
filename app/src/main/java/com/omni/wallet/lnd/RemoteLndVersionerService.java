package com.omni.wallet.lnd;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.reactivex.rxjava3.core.Single;
import verrpc.VersionerGrpc;

public class RemoteLndVersionerService implements LndVersionerService {

    private final VersionerGrpc.VersionerStub asyncStub;

    public RemoteLndVersionerService(Channel channel, CallCredentials callCredentials) {
        asyncStub = VersionerGrpc.newStub(channel).withCallCredentials(callCredentials);
    }

    @Override
    public Single<verrpc.Verrpc.Version> getVersion(verrpc.Verrpc.VersionRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.getVersion(request, new RemoteLndSingleObserver<>(emitter)));
    }

}