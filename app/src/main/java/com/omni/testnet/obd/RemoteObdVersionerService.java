package com.omni.testnet.obd;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.reactivex.rxjava3.core.Single;
import verrpc.VersionerGrpc;

public class RemoteObdVersionerService implements ObdVersionerService {

    private final VersionerGrpc.VersionerStub asyncStub;

    public RemoteObdVersionerService(Channel channel, CallCredentials callCredentials) {
        asyncStub = VersionerGrpc.newStub(channel).withCallCredentials(callCredentials);
    }

    @Override
    public Single<verrpc.Verrpc.Version> getVersion(verrpc.Verrpc.VersionRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.getVersion(request, new RemoteObdSingleObserver<>(emitter)));
    }

}