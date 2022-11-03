package com.omni.wallet.lnd;

import io.reactivex.rxjava3.core.Observable;

public interface LndChainNotifierService {

    Observable<chainrpc.Chainnotifier.ConfEvent> registerConfirmationsNtfn(chainrpc.Chainnotifier.ConfRequest request);

    Observable<chainrpc.Chainnotifier.SpendEvent> registerSpendNtfn(chainrpc.Chainnotifier.SpendRequest request);

    Observable<chainrpc.Chainnotifier.BlockEpoch> registerBlockEpochNtfn(chainrpc.Chainnotifier.BlockEpoch request);
}