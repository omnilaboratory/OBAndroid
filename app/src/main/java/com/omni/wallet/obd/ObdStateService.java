package com.omni.wallet.obd;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface ObdStateService {

    Observable<lnrpc.Stateservice.SubscribeStateResponse> subscribeState(lnrpc.Stateservice.SubscribeStateRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface getState is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.Stateservice.GetStateResponse> getState(lnrpc.Stateservice.GetStateRequest request);
}