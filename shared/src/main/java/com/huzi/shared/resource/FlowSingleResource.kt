package com.huzi.shared.resource

import com.huzi.shared.result.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

inline fun <ResultType> FlowSingleResource(
    crossinline fetchFromNetwork: () -> Flow<ApiResponse<ResultType>>,
)  = flow {
    emit(Resource.loading(null))
    when (val fetch = fetchFromNetwork().first()) {
        is ApiSuccessResponse -> {
            emit(Resource.success(fetch.body))
        }
        is ApiErrorResponse -> {
            val errorMessage = fetch.errorMessage
            val errorCode = fetch.errorCode
            emit(
                Resource.error(
                    msg = errorMessage,
                    code = errorCode
                )
            )
        }

        is ApiEmptyResponse -> {
            emit(Resource.success(null))
        }
    }
}
