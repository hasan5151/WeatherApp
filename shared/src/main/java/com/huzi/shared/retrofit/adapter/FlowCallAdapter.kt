package com.huzi.shared.retrofit.adapter

import com.huzi.shared.result.ApiResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

class FlowCallAdapter<R>(private val responseType: Type) :
    CallAdapter<R, Flow<ApiResponse<R>>> {
    var started = AtomicBoolean(false)
    var successResponse: ((response: Response<R>) -> Unit)? = null
    var errorResponse: ((throwable: Throwable) -> Unit)? = null

    fun success(listener: (response: Response<R>) -> Unit) {
        successResponse = listener
    }

    override fun responseType(): Type {
        return responseType
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun callNetwork(call: Call<R>): Flow<ApiResponse<R>> = flow {
       // if (started.compareAndSet(false, true)) {
            emit(
                suspendCancellableCoroutine { continuation ->
                    call.enqueue(object : Callback<R> {
                        override fun onResponse(call: Call<R>, response: Response<R>) {
                            continuation.resume(ApiResponse.create(response),null)
                        }

                        override fun onFailure(call: Call<R>, throwable: Throwable) {
                            continuation.resume(ApiResponse.create(throwable),null)
                        }
                    })
                    continuation.invokeOnCancellation { call.cancel() }
                }
            )
      //  }
    }

    override fun adapt(call: Call<R>): Flow<ApiResponse<R>> {
        return callNetwork(call)
    }
}