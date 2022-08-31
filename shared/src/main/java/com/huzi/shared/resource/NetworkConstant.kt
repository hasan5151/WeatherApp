package com.huzi.shared.resource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

val uiDispatcher: CoroutineDispatcher = Dispatchers.Main
val bgDispatcher: CoroutineDispatcher = Dispatchers.IO
val ioDispatcher: CoroutineDispatcher = Dispatchers.IO