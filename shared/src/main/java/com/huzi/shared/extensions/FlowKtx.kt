package com.huzi.shared.extensions

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

fun <T> MutableFlow() : MutableSharedFlow<T>{
    return MutableSharedFlow(1,  Int.MAX_VALUE, BufferOverflow.DROP_OLDEST)
}
