package com.omni.wallet.obd;

import io.reactivex.rxjava3.core.Single;

public interface ObdWalletKitService {

    Single<walletrpc.Walletkit.ListUnspentResponse> listUnspent(walletrpc.Walletkit.ListUnspentRequest request);

    Single<walletrpc.Walletkit.LeaseOutputResponse> leaseOutput(walletrpc.Walletkit.LeaseOutputRequest request);

    Single<walletrpc.Walletkit.ReleaseOutputResponse> releaseOutput(walletrpc.Walletkit.ReleaseOutputRequest request);

    Single<walletrpc.Walletkit.ListLeasesResponse> listLeases(walletrpc.Walletkit.ListLeasesRequest request);

    Single<signrpc.SignerOuterClass.KeyDescriptor> deriveNextKey(walletrpc.Walletkit.KeyReq request);

    Single<signrpc.SignerOuterClass.KeyDescriptor> deriveKey(signrpc.SignerOuterClass.KeyLocator request);

    Single<walletrpc.Walletkit.AddrResponse> nextAddr(walletrpc.Walletkit.AddrRequest request);

    Single<walletrpc.Walletkit.ListAccountsResponse> listAccounts(walletrpc.Walletkit.ListAccountsRequest request);

    Single<walletrpc.Walletkit.ImportAccountResponse> importAccount(walletrpc.Walletkit.ImportAccountRequest request);

    Single<walletrpc.Walletkit.ImportPublicKeyResponse> importPublicKey(walletrpc.Walletkit.ImportPublicKeyRequest request);

    Single<walletrpc.Walletkit.PublishResponse> publishTransaction(walletrpc.Walletkit.Transaction request);

    Single<walletrpc.Walletkit.SendOutputsResponse> sendOutputs(walletrpc.Walletkit.SendOutputsRequest request);

    Single<walletrpc.Walletkit.EstimateFeeResponse> estimateFee(walletrpc.Walletkit.EstimateFeeRequest request);

    Single<walletrpc.Walletkit.PendingSweepsResponse> pendingSweeps(walletrpc.Walletkit.PendingSweepsRequest request);

    Single<walletrpc.Walletkit.BumpFeeResponse> bumpFee(walletrpc.Walletkit.BumpFeeRequest request);

    Single<walletrpc.Walletkit.ListSweepsResponse> listSweeps(walletrpc.Walletkit.ListSweepsRequest request);

    Single<walletrpc.Walletkit.LabelTransactionResponse> labelTransaction(walletrpc.Walletkit.LabelTransactionRequest request);

    Single<walletrpc.Walletkit.FundPsbtResponse> fundPsbt(walletrpc.Walletkit.FundPsbtRequest request);

    Single<walletrpc.Walletkit.FinalizePsbtResponse> finalizePsbt(walletrpc.Walletkit.FinalizePsbtRequest request);
}