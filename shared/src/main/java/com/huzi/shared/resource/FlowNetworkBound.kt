package com.huzi.shared.resource

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.huzi.shared.result.*
import kotlinx.coroutines.flow.*

abstract class FlowNetworkBound<ResultType, RequestType> {
    fun asFlow() = flow {
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

    protected open fun onFetchFailed() {
        // Implement in sub-classes to handle errors
    }

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.body

    @WorkerThread
    protected abstract suspend fun saveNetworkResult(item: RequestType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): Flow<ResultType>

    @MainThread
    protected abstract suspend fun fetchFromNetwork(): Flow<ApiResponse<RequestType>>
}