package com.omni.wallet.lnd;

import io.grpc.stub.StreamObserver;
import io.reactivex.rxjava3.core.ObservableEmitter;

/**
 * Used to wrap gRPC Observer with Rx ObservableEmitter
 */
public class RemoteLndStreamObserver<V> implements StreamObserver<V> {

    private final ObservableEmitter<V> mEmitter;

    RemoteLndStreamObserver(ObservableEmitter<V> emitter) {
        mEmitter = emitter;
    }

    @Override
    public void onNext(V value) {
        mEmitter.onNext(value);
    }

    @Override
    public void onError(Throwable t) {
        mEmitter.tryOnError(t);
    }

    @Override
    public void onCompleted() {
        mEmitter.onComplete();
    }
}
