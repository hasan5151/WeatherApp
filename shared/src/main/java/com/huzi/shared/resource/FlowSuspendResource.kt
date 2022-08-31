package com.huzi.shared.resource

import com.huzi.shared.result.*
import kotlinx.coroutines.flow.*

inline fun <ResultType> FlowSuspendResource(
    crossinline fetchFromNetwork: () -> Flow<ApiResponse<ResultType>>,
    crossinline onCallSucceed: suspend  (item: ResultType) -> Unit,
    crossinline processResponse: (response: ApiSuccessResponse<ResultType>) -> ResultType = { it.body },
    crossinline onFetchFailed: () -> Unit = {}
)  = flow {
    emit(Resource.loading(null))
    when (val fetch = fetchFromNetwork().first()) {
        is ApiSuccessResponse -> {
            onCallSucceed(processResponse(fetch))
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
            emit(null)
        }
    }
}