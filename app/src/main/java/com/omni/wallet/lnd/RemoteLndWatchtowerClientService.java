package com.omni.wallet.lnd;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.reactivex.rxjava3.core.Single;
import wtclientrpc.WatchtowerClientGrpc;

public class RemoteLndWatchtowerClientService implements LndWatchtowerClientService {

    private final WatchtowerClientGrpc.WatchtowerClientStub asyncStub;

    public RemoteLndWatchtowerClientService(Channel channel, CallCredentials callCredentials) {
        asyncStub = WatchtowerClientGrpc.newStub(channel).withCallCredentials(callCredentials);
    }

    @Override
    public Single<wtclientrpc.Wtclient.AddTowerResponse> addTower(wtclientrpc.Wtclient.AddTowerRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.addTower(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<wtclientrpc.Wtclient.RemoveTowerResponse> removeTower(wtclientrpc.Wtclient.RemoveTowerRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.removeTower(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<wtclientrpc.Wtclient.ListTowersResponse> listTowers(wtclientrpc.Wtclient.ListTowersRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.listTowers(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<wtclientrpc.Wtclient.Tower> getTowerInfo(wtclientrpc.Wtclient.GetTowerInfoRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.getTowerInfo(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<wtclientrpc.Wtclient.StatsResponse> stats(wtclientrpc.Wtclient.StatsRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.stats(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<wtclientrpc.Wtclient.PolicyResponse> policy(wtclientrpc.Wtclient.PolicyRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.policy(request, new RemoteLndSingleObserver<>(emitter)));
    }

}