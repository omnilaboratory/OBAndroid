package com.omni.wallet.lnd;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import lnrpc.LightningGrpc;

public class RemoteLndLightningService implements LndLightningService {

    private final LightningGrpc.LightningStub asyncStub;

    public RemoteLndLightningService(Channel channel, CallCredentials callCredentials) {
        asyncStub = LightningGrpc.newStub(channel).withCallCredentials(callCredentials);
    }

    @Override
    public Single<lnrpc.LightningOuterClass.WalletBalanceResponse> walletBalance(lnrpc.LightningOuterClass.WalletBalanceRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.walletBalance(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.ChannelBalanceResponse> channelBalance(lnrpc.LightningOuterClass.ChannelBalanceRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.channelBalance(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.TransactionDetails> getTransactions(lnrpc.LightningOuterClass.GetTransactionsRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.getTransactions(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.EstimateFeeResponse> estimateFee(lnrpc.LightningOuterClass.EstimateFeeRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.estimateFee(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.SendCoinsResponse> sendCoins(lnrpc.LightningOuterClass.SendCoinsRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.sendCoins(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.ListUnspentResponse> listUnspent(lnrpc.LightningOuterClass.ListUnspentRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.listUnspent(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Observable<lnrpc.LightningOuterClass.Transaction> subscribeTransactions(lnrpc.LightningOuterClass.GetTransactionsRequest request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.subscribeTransactions(request, new RemoteLndStreamObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.SendManyResponse> sendMany(lnrpc.LightningOuterClass.SendManyRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.sendMany(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.NewAddressResponse> newAddress(lnrpc.LightningOuterClass.NewAddressRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.newAddress(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.SignMessageResponse> signMessage(lnrpc.LightningOuterClass.SignMessageRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.signMessage(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.VerifyMessageResponse> verifyMessage(lnrpc.LightningOuterClass.VerifyMessageRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.verifyMessage(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.ConnectPeerResponse> connectPeer(lnrpc.LightningOuterClass.ConnectPeerRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.connectPeer(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.DisconnectPeerResponse> disconnectPeer(lnrpc.LightningOuterClass.DisconnectPeerRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.disconnectPeer(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.ListPeersResponse> listPeers(lnrpc.LightningOuterClass.ListPeersRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.listPeers(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Observable<lnrpc.LightningOuterClass.PeerEvent> subscribePeerEvents(lnrpc.LightningOuterClass.PeerEventSubscription request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.subscribePeerEvents(request, new RemoteLndStreamObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.GetInfoResponse> getInfo(lnrpc.LightningOuterClass.GetInfoRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.getInfo(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.GetRecoveryInfoResponse> getRecoveryInfo(lnrpc.LightningOuterClass.GetRecoveryInfoRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.getRecoveryInfo(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.PendingChannelsResponse> pendingChannels(lnrpc.LightningOuterClass.PendingChannelsRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.pendingChannels(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.ListChannelsResponse> listChannels(lnrpc.LightningOuterClass.ListChannelsRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.listChannels(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Observable<lnrpc.LightningOuterClass.ChannelEventUpdate> subscribeChannelEvents(lnrpc.LightningOuterClass.ChannelEventSubscription request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.subscribeChannelEvents(request, new RemoteLndStreamObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.ClosedChannelsResponse> closedChannels(lnrpc.LightningOuterClass.ClosedChannelsRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.closedChannels(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.ChannelPoint> openChannelSync(lnrpc.LightningOuterClass.OpenChannelRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.openChannelSync(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Observable<lnrpc.LightningOuterClass.OpenStatusUpdate> openChannel(lnrpc.LightningOuterClass.OpenChannelRequest request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.openChannel(request, new RemoteLndStreamObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.BatchOpenChannelResponse> batchOpenChannel(lnrpc.LightningOuterClass.BatchOpenChannelRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.batchOpenChannel(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.FundingStateStepResp> fundingStateStep(lnrpc.LightningOuterClass.FundingTransitionMsg request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.fundingStateStep(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Observable<lnrpc.LightningOuterClass.CloseStatusUpdate> closeChannel(lnrpc.LightningOuterClass.CloseChannelRequest request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.closeChannel(request, new RemoteLndStreamObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.AbandonChannelResponse> abandonChannel(lnrpc.LightningOuterClass.AbandonChannelRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.abandonChannel(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.SendResponse> sendPaymentSync(lnrpc.LightningOuterClass.SendRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.sendPaymentSync(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.SendResponse> sendToRouteSync(lnrpc.LightningOuterClass.SendToRouteRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.sendToRouteSync(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.AddInvoiceResponse> addInvoice(lnrpc.LightningOuterClass.Invoice request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.addInvoice(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.ListInvoiceResponse> listInvoices(lnrpc.LightningOuterClass.ListInvoiceRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.listInvoices(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.Invoice> lookupInvoice(lnrpc.LightningOuterClass.PaymentHash request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.lookupInvoice(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Observable<lnrpc.LightningOuterClass.Invoice> subscribeInvoices(lnrpc.LightningOuterClass.InvoiceSubscription request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.subscribeInvoices(request, new RemoteLndStreamObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.PayReq> decodePayReq(lnrpc.LightningOuterClass.PayReqString request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.decodePayReq(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.ListPaymentsResponse> listPayments(lnrpc.LightningOuterClass.ListPaymentsRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.listPayments(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.DeletePaymentResponse> deletePayment(lnrpc.LightningOuterClass.DeletePaymentRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.deletePayment(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.DeleteAllPaymentsResponse> deleteAllPayments(lnrpc.LightningOuterClass.DeleteAllPaymentsRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.deleteAllPayments(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.ChannelGraph> describeGraph(lnrpc.LightningOuterClass.ChannelGraphRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.describeGraph(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.NodeMetricsResponse> getNodeMetrics(lnrpc.LightningOuterClass.NodeMetricsRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.getNodeMetrics(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.ChannelEdge> getChanInfo(lnrpc.LightningOuterClass.ChanInfoRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.getChanInfo(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.NodeInfo> getNodeInfo(lnrpc.LightningOuterClass.NodeInfoRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.getNodeInfo(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.QueryRoutesResponse> queryRoutes(lnrpc.LightningOuterClass.QueryRoutesRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.queryRoutes(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.NetworkInfo> getNetworkInfo(lnrpc.LightningOuterClass.NetworkInfoRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.getNetworkInfo(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.StopResponse> stopDaemon(lnrpc.LightningOuterClass.StopRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.stopDaemon(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Observable<lnrpc.LightningOuterClass.GraphTopologyUpdate> subscribeChannelGraph(lnrpc.LightningOuterClass.GraphTopologySubscription request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.subscribeChannelGraph(request, new RemoteLndStreamObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.DebugLevelResponse> debugLevel(lnrpc.LightningOuterClass.DebugLevelRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.debugLevel(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.FeeReportResponse> feeReport(lnrpc.LightningOuterClass.FeeReportRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.feeReport(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.PolicyUpdateResponse> updateChannelPolicy(lnrpc.LightningOuterClass.PolicyUpdateRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.updateChannelPolicy(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.ForwardingHistoryResponse> forwardingHistory(lnrpc.LightningOuterClass.ForwardingHistoryRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.forwardingHistory(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.ChannelBackup> exportChannelBackup(lnrpc.LightningOuterClass.ExportChannelBackupRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.exportChannelBackup(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.ChanBackupSnapshot> exportAllChannelBackups(lnrpc.LightningOuterClass.ChanBackupExportRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.exportAllChannelBackups(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.VerifyChanBackupResponse> verifyChanBackup(lnrpc.LightningOuterClass.ChanBackupSnapshot request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.verifyChanBackup(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.RestoreBackupResponse> restoreChannelBackups(lnrpc.LightningOuterClass.RestoreChanBackupRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.restoreChannelBackups(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Observable<lnrpc.LightningOuterClass.ChanBackupSnapshot> subscribeChannelBackups(lnrpc.LightningOuterClass.ChannelBackupSubscription request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.subscribeChannelBackups(request, new RemoteLndStreamObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.BakeMacaroonResponse> bakeMacaroon(lnrpc.LightningOuterClass.BakeMacaroonRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.bakeMacaroon(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.ListMacaroonIDsResponse> listMacaroonIDs(lnrpc.LightningOuterClass.ListMacaroonIDsRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.listMacaroonIDs(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.DeleteMacaroonIDResponse> deleteMacaroonID(lnrpc.LightningOuterClass.DeleteMacaroonIDRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.deleteMacaroonID(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.ListPermissionsResponse> listPermissions(lnrpc.LightningOuterClass.ListPermissionsRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.listPermissions(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.CheckMacPermResponse> checkMacaroonPermissions(lnrpc.LightningOuterClass.CheckMacPermRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.checkMacaroonPermissions(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.SendCustomMessageResponse> sendCustomMessage(lnrpc.LightningOuterClass.SendCustomMessageRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.sendCustomMessage(request, new RemoteLndSingleObserver<>(emitter)));
    }

    @Override
    public Observable<lnrpc.LightningOuterClass.CustomMessage> subscribeCustomMessages(lnrpc.LightningOuterClass.SubscribeCustomMessagesRequest request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.subscribeCustomMessages(request, new RemoteLndStreamObserver<>(emitter)));
    }

}