package com.omni.wallet.lnd;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface LndLightningService {

    /*
     * GYL, oblnd mobile team.
     *
     * The interface walletBalance is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.WalletBalanceResponse> walletBalance(lnrpc.LightningOuterClass.WalletBalanceRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface channelBalance is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.ChannelBalanceResponse> channelBalance(lnrpc.LightningOuterClass.ChannelBalanceRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface getTransactions is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.TransactionDetails> getTransactions(lnrpc.LightningOuterClass.GetTransactionsRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface estimateFee is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.EstimateFeeResponse> estimateFee(lnrpc.LightningOuterClass.EstimateFeeRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface sendCoins is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.SendCoinsResponse> sendCoins(lnrpc.LightningOuterClass.SendCoinsRequest request);

    Single<lnrpc.LightningOuterClass.ListUnspentResponse> listUnspent(lnrpc.LightningOuterClass.ListUnspentRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface subscribeTransactions is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Observable<lnrpc.LightningOuterClass.Transaction> subscribeTransactions(lnrpc.LightningOuterClass.GetTransactionsRequest request);

    Single<lnrpc.LightningOuterClass.SendManyResponse> sendMany(lnrpc.LightningOuterClass.SendManyRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface newAddress is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.NewAddressResponse> newAddress(lnrpc.LightningOuterClass.NewAddressRequest request);

    Single<lnrpc.LightningOuterClass.SignMessageResponse> signMessage(lnrpc.LightningOuterClass.SignMessageRequest request);

    Single<lnrpc.LightningOuterClass.VerifyMessageResponse> verifyMessage(lnrpc.LightningOuterClass.VerifyMessageRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface connectPeer is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.ConnectPeerResponse> connectPeer(lnrpc.LightningOuterClass.ConnectPeerRequest request);

    Single<lnrpc.LightningOuterClass.DisconnectPeerResponse> disconnectPeer(lnrpc.LightningOuterClass.DisconnectPeerRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface listPeers is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.ListPeersResponse> listPeers(lnrpc.LightningOuterClass.ListPeersRequest request);

    Observable<lnrpc.LightningOuterClass.PeerEvent> subscribePeerEvents(lnrpc.LightningOuterClass.PeerEventSubscription request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface getInfo is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.GetInfoResponse> getInfo(lnrpc.LightningOuterClass.GetInfoRequest request);

    Single<lnrpc.LightningOuterClass.GetRecoveryInfoResponse> getRecoveryInfo(lnrpc.LightningOuterClass.GetRecoveryInfoRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface pendingChannels is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.PendingChannelsResponse> pendingChannels(lnrpc.LightningOuterClass.PendingChannelsRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface listChannels is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.ListChannelsResponse> listChannels(lnrpc.LightningOuterClass.ListChannelsRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface subscribeChannelEvents is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Observable<lnrpc.LightningOuterClass.ChannelEventUpdate> subscribeChannelEvents(lnrpc.LightningOuterClass.ChannelEventSubscription request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface closedChannels is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.ClosedChannelsResponse> closedChannels(lnrpc.LightningOuterClass.ClosedChannelsRequest request);

    Single<lnrpc.LightningOuterClass.ChannelPoint> openChannelSync(lnrpc.LightningOuterClass.OpenChannelRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface openChannel is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Observable<lnrpc.LightningOuterClass.OpenStatusUpdate> openChannel(lnrpc.LightningOuterClass.OpenChannelRequest request);

    Single<lnrpc.LightningOuterClass.BatchOpenChannelResponse> batchOpenChannel(lnrpc.LightningOuterClass.BatchOpenChannelRequest request);

    Single<lnrpc.LightningOuterClass.FundingStateStepResp> fundingStateStep(lnrpc.LightningOuterClass.FundingTransitionMsg request);

    // skipped ChannelAcceptor
    /*
     * GYL, oblnd mobile team.
     *
     * The interface closeChannel is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Observable<lnrpc.LightningOuterClass.CloseStatusUpdate> closeChannel(lnrpc.LightningOuterClass.CloseChannelRequest request);

    Single<lnrpc.LightningOuterClass.AbandonChannelResponse> abandonChannel(lnrpc.LightningOuterClass.AbandonChannelRequest request);

    // skipped SendPayment

    Single<lnrpc.LightningOuterClass.SendResponse> sendPaymentSync(lnrpc.LightningOuterClass.SendRequest request);

    // skipped SendToRoute

    Single<lnrpc.LightningOuterClass.SendResponse> sendToRouteSync(lnrpc.LightningOuterClass.SendToRouteRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface addInvoice is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.AddInvoiceResponse> addInvoice(lnrpc.LightningOuterClass.Invoice request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface listInvoices is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.ListInvoiceResponse> listInvoices(lnrpc.LightningOuterClass.ListInvoiceRequest request);

    Single<lnrpc.LightningOuterClass.Invoice> lookupInvoice(lnrpc.LightningOuterClass.PaymentHash request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface subscribeInvoices is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Observable<lnrpc.LightningOuterClass.Invoice> subscribeInvoices(lnrpc.LightningOuterClass.InvoiceSubscription request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface decodePayReq is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.PayReq> decodePayReq(lnrpc.LightningOuterClass.PayReqString request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface listPayments is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.ListPaymentsResponse> listPayments(lnrpc.LightningOuterClass.ListPaymentsRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface deletePayment is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.DeletePaymentResponse> deletePayment(lnrpc.LightningOuterClass.DeletePaymentRequest request);

    Single<lnrpc.LightningOuterClass.DeleteAllPaymentsResponse> deleteAllPayments(lnrpc.LightningOuterClass.DeleteAllPaymentsRequest request);

    Single<lnrpc.LightningOuterClass.ChannelGraph> describeGraph(lnrpc.LightningOuterClass.ChannelGraphRequest request);

    Single<lnrpc.LightningOuterClass.NodeMetricsResponse> getNodeMetrics(lnrpc.LightningOuterClass.NodeMetricsRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface getChanInfo is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.ChannelEdge> getChanInfo(lnrpc.LightningOuterClass.ChanInfoRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface getNodeInfo is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.NodeInfo> getNodeInfo(lnrpc.LightningOuterClass.NodeInfoRequest request);

    Single<lnrpc.LightningOuterClass.QueryRoutesResponse> queryRoutes(lnrpc.LightningOuterClass.QueryRoutesRequest request);

    Single<lnrpc.LightningOuterClass.NetworkInfo> getNetworkInfo(lnrpc.LightningOuterClass.NetworkInfoRequest request);

    Single<lnrpc.LightningOuterClass.StopResponse> stopDaemon(lnrpc.LightningOuterClass.StopRequest request);

    Observable<lnrpc.LightningOuterClass.GraphTopologyUpdate> subscribeChannelGraph(lnrpc.LightningOuterClass.GraphTopologySubscription request);

    Single<lnrpc.LightningOuterClass.DebugLevelResponse> debugLevel(lnrpc.LightningOuterClass.DebugLevelRequest request);

    Single<lnrpc.LightningOuterClass.FeeReportResponse> feeReport(lnrpc.LightningOuterClass.FeeReportRequest request);

    Single<lnrpc.LightningOuterClass.PolicyUpdateResponse> updateChannelPolicy(lnrpc.LightningOuterClass.PolicyUpdateRequest request);

    Single<lnrpc.LightningOuterClass.ForwardingHistoryResponse> forwardingHistory(lnrpc.LightningOuterClass.ForwardingHistoryRequest request);

    Single<lnrpc.LightningOuterClass.ChannelBackup> exportChannelBackup(lnrpc.LightningOuterClass.ExportChannelBackupRequest request);

    Single<lnrpc.LightningOuterClass.ChanBackupSnapshot> exportAllChannelBackups(lnrpc.LightningOuterClass.ChanBackupExportRequest request);

    Single<lnrpc.LightningOuterClass.VerifyChanBackupResponse> verifyChanBackup(lnrpc.LightningOuterClass.ChanBackupSnapshot request);

    Single<lnrpc.LightningOuterClass.RestoreBackupResponse> restoreChannelBackups(lnrpc.LightningOuterClass.RestoreChanBackupRequest request);

    Observable<lnrpc.LightningOuterClass.ChanBackupSnapshot> subscribeChannelBackups(lnrpc.LightningOuterClass.ChannelBackupSubscription request);

    Single<lnrpc.LightningOuterClass.BakeMacaroonResponse> bakeMacaroon(lnrpc.LightningOuterClass.BakeMacaroonRequest request);

    Single<lnrpc.LightningOuterClass.ListMacaroonIDsResponse> listMacaroonIDs(lnrpc.LightningOuterClass.ListMacaroonIDsRequest request);

    Single<lnrpc.LightningOuterClass.DeleteMacaroonIDResponse> deleteMacaroonID(lnrpc.LightningOuterClass.DeleteMacaroonIDRequest request);

    Single<lnrpc.LightningOuterClass.ListPermissionsResponse> listPermissions(lnrpc.LightningOuterClass.ListPermissionsRequest request);

    Single<lnrpc.LightningOuterClass.CheckMacPermResponse> checkMacaroonPermissions(lnrpc.LightningOuterClass.CheckMacPermRequest request);

    // skipped RegisterRPCMiddleware

    Single<lnrpc.LightningOuterClass.SendCustomMessageResponse> sendCustomMessage(lnrpc.LightningOuterClass.SendCustomMessageRequest request);

    Observable<lnrpc.LightningOuterClass.CustomMessage> subscribeCustomMessages(lnrpc.LightningOuterClass.SubscribeCustomMessagesRequest request);
}