package com.omni.wallet.lnd;

import android.support.annotation.NonNull;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Used to add the default Schedulers to an Observable.
 * - Subscribing on Schedulers.io()
 * - Observing on AndroidSchedulers.mainThread()
 */
public abstract class DefaultObservable<T> extends Observable<T> {

    public static <T> Observable<T> createDefault(@NonNull ObservableOnSubscribe<T> observableOnSubscribe) {
        return create(observableOnSubscribe).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
