package com.huzi.shared.result

import androidx.annotation.StringRes
import com.huzi.shared.result.ResultStatus.*

data class Resource<out T>(val status: ResultStatus, val data: T?, val message: String?, val code: Int?, @StringRes val messageRes: Int?) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(SUCCESS, data, null, null, null)
        }

        fun <T> error(msg: String?, data: T? = null, code: Int? = null, @StringRes messageRes: Int? = null): Resource<T> {
            return Resource(ERROR, data, msg, code, messageRes)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(LOADING, data, null, null, null)
        }
    }
}

val Resource<*>.isSucceed
    get() = this.status == SUCCESS && this.data != null

val Resource<*>.statusSuccess
    get() = this.status == SUCCESS

val Resource<*>.isFailed
    get() = this.status == ERROR