package com.omni.wallet.obd;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.reactivex.rxjava3.core.Single;
import wtclientrpc.WatchtowerClientGrpc;

public class RemoteObdWatchtowerClientService implements ObdWatchtowerClientService {

    private final WatchtowerClientGrpc.WatchtowerClientStub asyncStub;

    public RemoteObdWatchtowerClientService(Channel channel, CallCredentials callCredentials) {
        asyncStub = WatchtowerClientGrpc.newStub(channel).withCallCredentials(callCredentials);
    }

    @Override
    public Single<wtclientrpc.Wtclient.AddTowerResponse> addTower(wtclientrpc.Wtclient.AddTowerRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.addTower(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<wtclientrpc.Wtclient.RemoveTowerResponse> removeTower(wtclientrpc.Wtclient.RemoveTowerRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.removeTower(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<wtclientrpc.Wtclient.ListTowersResponse> listTowers(wtclientrpc.Wtclient.ListTowersRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.listTowers(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<wtclientrpc.Wtclient.Tower> getTowerInfo(wtclientrpc.Wtclient.GetTowerInfoRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.getTowerInfo(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<wtclientrpc.Wtclient.StatsResponse> stats(wtclientrpc.Wtclient.StatsRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.stats(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<wtclientrpc.Wtclient.PolicyResponse> policy(wtclientrpc.Wtclient.PolicyRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.policy(request, new RemoteObdSingleObserver<>(emitter)));
    }

}