package com.omni.wallet.obd;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface ObdInvoicesService {

    Observable<lnrpc.LightningOuterClass.Invoice> subscribeSingleInvoice(invoicesrpc.InvoicesOuterClass.SubscribeSingleInvoiceRequest request);

    Single<invoicesrpc.InvoicesOuterClass.CancelInvoiceResp> cancelInvoice(invoicesrpc.InvoicesOuterClass.CancelInvoiceMsg request);

    Single<invoicesrpc.InvoicesOuterClass.AddHoldInvoiceResp> addHoldInvoice(invoicesrpc.InvoicesOuterClass.AddHoldInvoiceRequest request);

    Single<invoicesrpc.InvoicesOuterClass.SettleInvoiceResp> settleInvoice(invoicesrpc.InvoicesOuterClass.SettleInvoiceMsg request);

    Single<lnrpc.LightningOuterClass.Invoice> lookupInvoiceV2(invoicesrpc.InvoicesOuterClass.LookupInvoiceMsg request);
}