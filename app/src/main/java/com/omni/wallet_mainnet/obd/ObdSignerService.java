package com.omni.wallet_mainnet.obd;

import io.reactivex.rxjava3.core.Single;

public interface ObdSignerService {

    Single<signrpc.SignerOuterClass.SignResp> signOutputRaw(signrpc.SignerOuterClass.SignReq request);

    Single<signrpc.SignerOuterClass.InputScriptResp> computeInputScript(signrpc.SignerOuterClass.SignReq request);

    Single<signrpc.SignerOuterClass.SignMessageResp> signMessage(signrpc.SignerOuterClass.SignMessageReq request);

    Single<signrpc.SignerOuterClass.VerifyMessageResp> verifyMessage(signrpc.SignerOuterClass.VerifyMessageReq request);

    Single<signrpc.SignerOuterClass.SharedKeyResponse> deriveSharedKey(signrpc.SignerOuterClass.SharedKeyRequest request);
}