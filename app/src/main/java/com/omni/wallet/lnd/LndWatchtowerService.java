package com.omni.wallet.lnd;

import io.reactivex.rxjava3.core.Single;

public interface LndWatchtowerService {

    Single<watchtowerrpc.WatchtowerOuterClass.GetInfoResponse> getInfo(watchtowerrpc.WatchtowerOuterClass.GetInfoRequest request);
}