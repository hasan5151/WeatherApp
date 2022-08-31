package com.huzi.shared.extensions

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

fun Context.fetchColor(@ColorRes colorResId: Int): Int = ContextCompat.getColor(this, colorResId)

fun Context.fetchDrawable(@DrawableRes drawable: Int) = ContextCompat.getDrawable(this, drawable)

fun Context.fetchLayerDrawable(@DrawableRes drawable: Int) = ContextCompat.getDrawable(this, drawable)

inline fun postDelayed(delay: Long = 200, crossinline executor: () -> Unit) {
    val runnable = Runnable {
        executor.invoke()
    }
    Handler(Looper.getMainLooper()).postDelayed(runnable, delay)
}

inline fun Handler.postDelayed(delay: Long, crossinline action: () -> Unit): Runnable {
    val runnable = Runnable { action() }
    Handler(Looper.getMainLooper()).postDelayed(runnable, delay)
    return runnable
}

inline fun View.postKtx(delay: Long = 200, crossinline executor: () -> Unit) {
    val runnable = Runnable {
        executor.invoke()
    }
    this.postDelayed(runnable, delay)
}