package com.omni.wallet_mainnet.obd;

import io.reactivex.rxjava3.core.Single;

public interface ObdVersionerService {

    Single<verrpc.Verrpc.Version> getVersion(verrpc.Verrpc.VersionRequest request);
}