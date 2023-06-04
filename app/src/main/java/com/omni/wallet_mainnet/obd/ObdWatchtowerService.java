package com.omni.wallet_mainnet.obd;

import io.reactivex.rxjava3.core.Single;

public interface ObdWatchtowerService {

    Single<watchtowerrpc.WatchtowerOuterClass.GetInfoResponse> getInfo(watchtowerrpc.WatchtowerOuterClass.GetInfoRequest request);
}