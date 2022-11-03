package com.omni.wallet.lnd;

import io.reactivex.rxjava3.core.Single;

public interface LndVersionerService {

    Single<verrpc.Verrpc.Version> getVersion(verrpc.Verrpc.VersionRequest request);
}