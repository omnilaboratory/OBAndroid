package com.omni.wallet.obd;

import autopilotrpc.AutopilotGrpc;
import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.reactivex.rxjava3.core.Single;

public class RemoteObdAutopilotService implements ObdAutopilotService {

    private final AutopilotGrpc.AutopilotStub asyncStub;

    public RemoteObdAutopilotService(Channel channel, CallCredentials callCredentials) {
        asyncStub = AutopilotGrpc.newStub(channel).withCallCredentials(callCredentials);
    }

    @Override
    public Single<autopilotrpc.AutopilotOuterClass.StatusResponse> status(autopilotrpc.AutopilotOuterClass.StatusRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.status(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<autopilotrpc.AutopilotOuterClass.ModifyStatusResponse> modifyStatus(autopilotrpc.AutopilotOuterClass.ModifyStatusRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.modifyStatus(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<autopilotrpc.AutopilotOuterClass.QueryScoresResponse> queryScores(autopilotrpc.AutopilotOuterClass.QueryScoresRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.queryScores(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<autopilotrpc.AutopilotOuterClass.SetScoresResponse> setScores(autopilotrpc.AutopilotOuterClass.SetScoresRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.setScores(request, new RemoteObdSingleObserver<>(emitter)));
    }

}