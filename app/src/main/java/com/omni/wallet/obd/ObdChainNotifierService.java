package com.omni.wallet.obd;

import io.reactivex.rxjava3.core.Observable;

public interface ObdChainNotifierService {

    Observable<chainrpc.Chainnotifier.ConfEvent> registerConfirmationsNtfn(chainrpc.Chainnotifier.ConfRequest request);

    Observable<chainrpc.Chainnotifier.SpendEvent> registerSpendNtfn(chainrpc.Chainnotifier.SpendRequest request);

    Observable<chainrpc.Chainnotifier.BlockEpoch> registerBlockEpochNtfn(chainrpc.Chainnotifier.BlockEpoch request);
}