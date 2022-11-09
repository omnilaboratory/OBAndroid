package com.omni.wallet.obd;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import routerrpc.RouterGrpc;

public class RemoteObdRouterService implements ObdRouterService {

    private final RouterGrpc.RouterStub asyncStub;

    public RemoteObdRouterService(Channel channel, CallCredentials callCredentials) {
        asyncStub = RouterGrpc.newStub(channel).withCallCredentials(callCredentials);
    }

    @Override
    public Observable<lnrpc.LightningOuterClass.Payment> sendPaymentV2(routerrpc.RouterOuterClass.SendPaymentRequest request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.sendPaymentV2(request, new RemoteObdStreamObserver<>(emitter)));
    }

    @Override
    public Observable<lnrpc.LightningOuterClass.Payment> trackPaymentV2(routerrpc.RouterOuterClass.TrackPaymentRequest request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.trackPaymentV2(request, new RemoteObdStreamObserver<>(emitter)));
    }

    @Override
    public Single<routerrpc.RouterOuterClass.RouteFeeResponse> estimateRouteFee(routerrpc.RouterOuterClass.RouteFeeRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.estimateRouteFee(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<routerrpc.RouterOuterClass.SendToRouteResponse> sendToRoute(routerrpc.RouterOuterClass.SendToRouteRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.sendToRoute(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<lnrpc.LightningOuterClass.HTLCAttempt> sendToRouteV2(routerrpc.RouterOuterClass.SendToRouteRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.sendToRouteV2(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<routerrpc.RouterOuterClass.ResetMissionControlResponse> resetMissionControl(routerrpc.RouterOuterClass.ResetMissionControlRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.resetMissionControl(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<routerrpc.RouterOuterClass.QueryMissionControlResponse> queryMissionControl(routerrpc.RouterOuterClass.QueryMissionControlRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.queryMissionControl(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<routerrpc.RouterOuterClass.XImportMissionControlResponse> xImportMissionControl(routerrpc.RouterOuterClass.XImportMissionControlRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.xImportMissionControl(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<routerrpc.RouterOuterClass.GetMissionControlConfigResponse> getMissionControlConfig(routerrpc.RouterOuterClass.GetMissionControlConfigRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.getMissionControlConfig(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<routerrpc.RouterOuterClass.SetMissionControlConfigResponse> setMissionControlConfig(routerrpc.RouterOuterClass.SetMissionControlConfigRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.setMissionControlConfig(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<routerrpc.RouterOuterClass.QueryProbabilityResponse> queryProbability(routerrpc.RouterOuterClass.QueryProbabilityRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.queryProbability(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<routerrpc.RouterOuterClass.BuildRouteResponse> buildRoute(routerrpc.RouterOuterClass.BuildRouteRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.buildRoute(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Observable<routerrpc.RouterOuterClass.HtlcEvent> subscribeHtlcEvents(routerrpc.RouterOuterClass.SubscribeHtlcEventsRequest request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.subscribeHtlcEvents(request, new RemoteObdStreamObserver<>(emitter)));
    }

    @Override
    public Observable<routerrpc.RouterOuterClass.PaymentStatus> sendPayment(routerrpc.RouterOuterClass.SendPaymentRequest request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.sendPayment(request, new RemoteObdStreamObserver<>(emitter)));
    }

    @Override
    public Observable<routerrpc.RouterOuterClass.PaymentStatus> trackPayment(routerrpc.RouterOuterClass.TrackPaymentRequest request) {
        return DefaultObservable.createDefault(emitter -> asyncStub.trackPayment(request, new RemoteObdStreamObserver<>(emitter)));
    }

    @Override
    public Single<routerrpc.RouterOuterClass.UpdateChanStatusResponse> updateChanStatus(routerrpc.RouterOuterClass.UpdateChanStatusRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.updateChanStatus(request, new RemoteObdSingleObserver<>(emitter)));
    }

}