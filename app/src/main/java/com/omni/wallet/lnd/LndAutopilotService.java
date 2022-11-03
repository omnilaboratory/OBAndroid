package com.omni.wallet.lnd;

import io.reactivex.rxjava3.core.Single;

public interface LndAutopilotService {

    Single<autopilotrpc.AutopilotOuterClass.StatusResponse> status(autopilotrpc.AutopilotOuterClass.StatusRequest request);

    Single<autopilotrpc.AutopilotOuterClass.ModifyStatusResponse> modifyStatus(autopilotrpc.AutopilotOuterClass.ModifyStatusRequest request);

    Single<autopilotrpc.AutopilotOuterClass.QueryScoresResponse> queryScores(autopilotrpc.AutopilotOuterClass.QueryScoresRequest request);

    Single<autopilotrpc.AutopilotOuterClass.SetScoresResponse> setScores(autopilotrpc.AutopilotOuterClass.SetScoresRequest request);
}