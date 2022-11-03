package com.omni.wallet.lnd;

import invoicesrpc.InvoicesGrpc;
import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class RemoteLndInvoicesService implements LndInvoicesService {

    private final InvoicesGrpc.InvoicesStub asyncStub;

    public RemoteLndInvoicesService(Channel channel, CallCredentials callCredentials) {
        asyncStub = InvoicesGrpc.newStub(channel).withCallCredentials(callCredentials);
    }

    @Override
    public Observable<lnrpc.LightningOuterClass.Invoice> subscribeSingleInvoice(invoicesrpc.InvoicesOuterClass.SubscribeSingleInvoiceRequest request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.subscribeSingleInvoice(request, new RemoteLndStreamObserver<>(emitter)));
    }

    @Override
    public Single<invoicesrpc.InvoicesOuterClass.CancelInvoiceResp> cancelInvoice(invoicesrpc.InvoicesOuterClass.CancelInvoiceMsg request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.cancelInvoice(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<invoicesrpc.InvoicesOuterClass.AddHoldInvoiceResp> addHoldInvoice(invoicesrpc.InvoicesOuterClass.AddHoldInvoiceRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.addHoldInvoice(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<invoicesrpc.InvoicesOuterClass.SettleInvoiceResp> settleInvoice(invoicesrpc.InvoicesOuterClass.SettleInvoiceMsg request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.settleInvoice(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.Invoice> lookupInvoiceV2(invoicesrpc.InvoicesOuterClass.LookupInvoiceMsg request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.lookupInvoiceV2(request, new RemoteLndSingleObserver<>(emitter)));
    }

}