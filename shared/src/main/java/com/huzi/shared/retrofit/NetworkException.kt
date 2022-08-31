package com.huzi.shared.retrofit

import java.net.ConnectException

class NoInternetException(msg: String, override val cause: Throwable?) : ConnectException(msg)

class ServerDownException(msg: String, override val cause: Throwable?) : ConnectException(msg)