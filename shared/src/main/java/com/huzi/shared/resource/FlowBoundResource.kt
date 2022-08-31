package com.huzi.shared.resource

import com.huzi.shared.result.*
import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType>  FlowBoundResource (
    crossinline fetchFromNetwork : ()->Flow<ApiResponse<RequestType>>,
    crossinline loadFromDb : ()->Flow<ResultType>,
    crossinline saveNetworkResult : suspend (item: RequestType)->Unit,
    crossinline processResponse :  (response: ApiSuccessResponse<RequestType>)->RequestType = { it.body  },
    crossinline onFetchFailed :  ()->Unit = {},
    crossinline shouldFetch :  (data: ResultType?)->Boolean = {true},
): Flow<Resource<ResultType>> = flow {
    emit(Resource.loading(null))
    val dbValue = loadFromDb().first()
    if (shouldFetch(dbValue)) {
        emit(Resource.loading(dbValue))
        when (val fetch = fetchFromNetwork().first()) {
            is ApiSuccessResponse -> {
                saveNetworkResult(processResponse(fetch))
                emitAll(loadFromDb().map { Resource.success(it) })
            }

            is ApiErrorResponse -> {
                onFetchFailed()
                val errorMessage = fetch.errorMessage
                val errorCode = fetch.errorCode
                emitAll(loadFromDb().map {
                    Resource.error(
                        errorMessage,
                        it,
                        errorCode
                    )
                })
            }

            is ApiEmptyResponse -> {
                emitAll(loadFromDb().map { Resource.success(it) })
            }
        }
    } else {
        emitAll(loadFromDb().map { Resource.success(it) })
    }
}