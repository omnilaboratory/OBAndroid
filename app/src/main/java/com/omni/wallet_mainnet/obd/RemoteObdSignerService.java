package com.omni.wallet_mainnet.obd;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.reactivex.rxjava3.core.Single;
import signrpc.SignerGrpc;

public class RemoteObdSignerService implements ObdSignerService {

    private final SignerGrpc.SignerStub asyncStub;

    public RemoteObdSignerService(Channel channel, CallCredentials callCredentials) {
        asyncStub = SignerGrpc.newStub(channel).withCallCredentials(callCredentials);
    }

    @Override
    public Single<signrpc.SignerOuterClass.SignResp> signOutputRaw(signrpc.SignerOuterClass.SignReq request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.signOutputRaw(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<signrpc.SignerOuterClass.InputScriptResp> computeInputScript(signrpc.SignerOuterClass.SignReq request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.computeInputScript(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<signrpc.SignerOuterClass.SignMessageResp> signMessage(signrpc.SignerOuterClass.SignMessageReq request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.signMessage(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<signrpc.SignerOuterClass.VerifyMessageResp> verifyMessage(signrpc.SignerOuterClass.VerifyMessageReq request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.verifyMessage(request, new RemoteObdSingleObserver<>(emitter)));
    }

    @Override
    public Single<signrpc.SignerOuterClass.SharedKeyResponse> deriveSharedKey(signrpc.SignerOuterClass.SharedKeyRequest request) {
        return DefaultSingle.createDefault(emitter -> asyncStub.deriveSharedKey(request, new RemoteObdSingleObserver<>(emitter)));
    }

}