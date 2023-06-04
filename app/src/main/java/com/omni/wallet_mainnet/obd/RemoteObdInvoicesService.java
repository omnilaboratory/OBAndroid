package com.omni.wallet_mainnet.obd;

import invoicesrpc.InvoicesGrpc;
import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class RemoteObdInvoicesService implements ObdInvoicesService {

    private final InvoicesGrpc.InvoicesStub asyncStub;

    public RemoteObdInvoicesService(Channel channel, CallCredentials callCredentials) {
        asyncStub = InvoicesGrpc.newStub(channel).withCallCredentials(callCredentials);
    }

    @Override
    public Observable<lnrpc.LightningOuterClass.Invoice> subscribeSingleInvoice(invoicesrpc.InvoicesOuterClass.SubscribeSingleInvoiceRequest request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.subscribeSingleInvoice(request, new RemoteObdStreamObserver<>(emitter)));
    }

    @Override
    public Single<invoicesrpc.InvoicesOuterClass.CancelInvoiceResp> cancelInvoice(invoicesrpc.InvoicesOuterClass.CancelInvoiceMsg request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.cancelInvoice(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<invoicesrpc.InvoicesOuterClass.AddHoldInvoiceResp> addHoldInvoice(invoicesrpc.InvoicesOuterClass.AddHoldInvoiceRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.addHoldInvoice(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<invoicesrpc.InvoicesOuterClass.SettleInvoiceResp> settleInvoice(invoicesrpc.InvoicesOuterClass.SettleInvoiceMsg request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.settleInvoice(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.Invoice> lookupInvoiceV2(invoicesrpc.InvoicesOuterClass.LookupInvoiceMsg request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.lookupInvoiceV2(request, new RemoteObdSingleObserver<>(emitter)));
    }

}