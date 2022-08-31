package com.huzi.shared.resource

import com.huzi.shared.result.*
import kotlinx.coroutines.flow.*

inline fun <ResultType> FlowResource(
    crossinline fetchFromNetwork: () -> Flow<ApiResponse<ResultType>>,
    crossinline processResponse: (response: ApiSuccessResponse<ResultType>) -> ResultType = { it.body },
    crossinline onFetchFailed: () -> Unit = {}
)  = flow {
    emit(Resource.loading(null))
    when (val fetch = fetchFromNetwork().first()) {
        is ApiSuccessResponse -> {
            emit(Resource.success(processResponse(fetch)))
        }
        is ApiErrorResponse -> {
            onFetchFailed()
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