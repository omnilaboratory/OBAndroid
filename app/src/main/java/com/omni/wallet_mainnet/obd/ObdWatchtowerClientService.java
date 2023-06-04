package com.omni.wallet_mainnet.obd;

import io.reactivex.rxjava3.core.Single;

public interface ObdWatchtowerClientService {

    Single<wtclientrpc.Wtclient.AddTowerResponse> addTower(wtclientrpc.Wtclient.AddTowerRequest request);

    Single<wtclientrpc.Wtclient.RemoveTowerResponse> removeTower(wtclientrpc.Wtclient.RemoveTowerRequest request);

    Single<wtclientrpc.Wtclient.ListTowersResponse> listTowers(wtclientrpc.Wtclient.ListTowersRequest request);

    Single<wtclientrpc.Wtclient.Tower> getTowerInfo(wtclientrpc.Wtclient.GetTowerInfoRequest request);

    Single<wtclientrpc.Wtclient.StatsResponse> stats(wtclientrpc.Wtclient.StatsRequest request);

    Single<wtclientrpc.Wtclient.PolicyResponse> policy(wtclientrpc.Wtclient.PolicyRequest request);
}