package com.huzi.shared.result

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner) { t ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }

    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    @WorkerThread
    override fun postValue(t: T?) {
        mPending.set(true)
        super.postValue(t)
    }
    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @Suppress("UsePropertyAccessSyntax")
    @MainThread
    fun call() {
        setValue(null)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @WorkerThread
    fun postCall() {
        postValue(null)
    }
}