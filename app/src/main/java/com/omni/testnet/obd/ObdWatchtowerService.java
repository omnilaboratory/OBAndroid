package com.omni.testnet.obd;

import io.reactivex.rxjava3.core.Single;

public interface ObdWatchtowerService {

    Single<watchtowerrpc.WatchtowerOuterClass.GetInfoResponse> getInfo(watchtowerrpc.WatchtowerOuterClass.GetInfoRequest request);
}