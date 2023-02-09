package com.omni.testnet.obd;

import android.support.annotation.NonNull;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Used to add the default Schedulers to a Single.
 * - Subscribing on Schedulers.io()
 * - Observing on AndroidSchedulers.mainThread()
 */
public abstract class DefaultSingle<T> extends Single<T> {

    public static <T> Single<T> createDefault(@NonNull SingleOnSubscribe<T> singleOnSubscribe) {
        return create(singleOnSubscribe).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
