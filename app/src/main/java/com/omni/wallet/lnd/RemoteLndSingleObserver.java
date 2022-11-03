package com.omni.wallet.lnd;

import io.grpc.stub.StreamObserver;
import io.reactivex.rxjava3.core.SingleEmitter;

/**
 * Used to wrap gRPC Observer with Rx SingleEmitter
 */
public class RemoteLndSingleObserver<V> implements StreamObserver<V> {

    private final SingleEmitter<V> mEmitter;

    RemoteLndSingleObserver(SingleEmitter<V> emitter) {
        mEmitter = emitter;
    }

    @Override
    public void onNext(V value) {
        mEmitter.onSuccess(value);
    }

    @Override
    public void onError(Throwable t) {
        mEmitter.tryOnError(t);
    }

    @Override
    public void onCompleted() {

    }
}
