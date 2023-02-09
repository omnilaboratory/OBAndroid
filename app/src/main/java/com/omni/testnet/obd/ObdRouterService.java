package com.omni.testnet.obd;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface ObdRouterService {

    /*
     * GYL, oblnd mobile team.
     *
     * The interface sendPaymentV2 is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Observable<lnrpc.LightningOuterClass.Payment> sendPaymentV2(routerrpc.RouterOuterClass.SendPaymentRequest request);

    Observable<lnrpc.LightningOuterClass.Payment> trackPaymentV2(routerrpc.RouterOuterClass.TrackPaymentRequest request);

    Single<routerrpc.RouterOuterClass.RouteFeeResponse> estimateRouteFee(routerrpc.RouterOuterClass.RouteFeeRequest request);

    Single<routerrpc.RouterOuterClass.SendToRouteResponse> sendToRoute(routerrpc.RouterOuterClass.SendToRouteRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface sendToRouteV2 is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Single<lnrpc.LightningOuterClass.HTLCAttempt> sendToRouteV2(routerrpc.RouterOuterClass.SendToRouteRequest request);

    Single<routerrpc.RouterOuterClass.ResetMissionControlResponse> resetMissionControl(routerrpc.RouterOuterClass.ResetMissionControlRequest request);

    Single<routerrpc.RouterOuterClass.QueryMissionControlResponse> queryMissionControl(routerrpc.RouterOuterClass.QueryMissionControlRequest request);

    Single<routerrpc.RouterOuterClass.XImportMissionControlResponse> xImportMissionControl(routerrpc.RouterOuterClass.XImportMissionControlRequest request);

    Single<routerrpc.RouterOuterClass.GetMissionControlConfigResponse> getMissionControlConfig(routerrpc.RouterOuterClass.GetMissionControlConfigRequest request);

    Single<routerrpc.RouterOuterClass.SetMissionControlConfigResponse> setMissionControlConfig(routerrpc.RouterOuterClass.SetMissionControlConfigRequest request);

    Single<routerrpc.RouterOuterClass.QueryProbabilityResponse> queryProbability(routerrpc.RouterOuterClass.QueryProbabilityRequest request);

    Single<routerrpc.RouterOuterClass.BuildRouteResponse> buildRoute(routerrpc.RouterOuterClass.BuildRouteRequest request);

    /*
     * GYL, oblnd mobile team.
     *
     * The interface subscribeHtlcEvents is exposed by OmniBOLT daemon(obd)
     * and is invocked by mobile apps.
     *
     */
    Observable<routerrpc.RouterOuterClass.HtlcEvent> subscribeHtlcEvents(routerrpc.RouterOuterClass.SubscribeHtlcEventsRequest request);

    Observable<routerrpc.RouterOuterClass.PaymentStatus> sendPayment(routerrpc.RouterOuterClass.SendPaymentRequest request);

    Observable<routerrpc.RouterOuterClass.PaymentStatus> trackPayment(routerrpc.RouterOuterClass.TrackPaymentRequest request);

    // skipped HtlcInterceptor

    Single<routerrpc.RouterOuterClass.UpdateChanStatusResponse> updateChanStatus(routerrpc.RouterOuterClass.UpdateChanStatusRequest request);
}