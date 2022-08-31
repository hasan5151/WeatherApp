package com.huzi.shared.core

import androidx.lifecycle.LiveData
import com.huzi.shared.resource.NetworkBoundResource
import com.huzi.shared.resource.NetworkResource
import com.huzi.shared.resource.NetworkResourceEvent
import com.huzi.shared.resource.NetworkSuspendResource
import com.huzi.shared.result.ApiResponse
import com.huzi.shared.result.Event
import com.huzi.shared.result.Resource
import kotlinx.coroutines.CoroutineScope

abstract class BaseRepository {

    lateinit var viewModelScope: CoroutineScope

    fun initScope(viewModelScope: CoroutineScope) {
        this.viewModelScope = viewModelScope
    }

    inline fun <ResultType> BaseRepository.LiveNetworkResource (
        crossinline createCall: ()-> LiveData<ApiResponse<ResultType>>,
        crossinline onCallSucceed: suspend (resultType: ResultType) -> Unit = {},
    ) : LiveData<Resource<ResultType>> {
        return object: NetworkResource<ResultType>(viewModelScope) {
            override fun createCall(): LiveData<ApiResponse<ResultType>> {
                return createCall()
            }

            override suspend fun onCallSucceed(resultItem: ResultType) {
                onCallSucceed(resultItem)
            }
        }.asLiveData()
    }

    inline fun <ResultType> BaseRepository.LiveSingleNetworkResource (
        crossinline createCall: ()-> LiveData<ApiResponse<ResultType>>
    ) : LiveData<Resource<ResultType>> {
        return object: NetworkResource<ResultType>(viewModelScope) {
            override fun createCall(): LiveData<ApiResponse<ResultType>> {
                return createCall()
            }
        }.asLiveData()
    }

    inline fun <ResultType> BaseRepository.LiveSingleSuspendResource (
        crossinline createCall: suspend ()-> LiveData<ApiResponse<ResultType>>
    ) : LiveData<Resource<ResultType>> {
        return object: NetworkSuspendResource<ResultType>(viewModelScope) {
            override suspend fun createCall(): LiveData<ApiResponse<ResultType>> {
                return createCall()
            }
        }.asLiveData()
    }

    inline fun <ResultType,RequestType> BaseRepository.LiveNetworkBoundResource (
        crossinline loadFromDb: ()-> LiveData<ResultType>,
        crossinline createCall: ()-> LiveData<ApiResponse<RequestType>>,
        crossinline saveCallResult: suspend (item: RequestType)-> Unit,
    ) : LiveData<Resource<ResultType>> {
        return object: NetworkBoundResource<ResultType, RequestType>(viewModelScope) {
            override suspend fun saveCallResult(item: RequestType) {
                saveCallResult(item)
            }

            override fun loadFromDb(): LiveData<ResultType> {
                return loadFromDb()
            }

            override fun createCall(): LiveData<ApiResponse<RequestType>> {
                return createCall()
            }


        }.asLiveData()
    }

    inline fun <RequestType> BaseRepository.LiveSingleNetworkResourceEvent (
        crossinline createCall: ()-> LiveData<ApiResponse<RequestType>>,
    ) : LiveData<Event<Resource<RequestType>>> {
        return object: NetworkResourceEvent<RequestType>(viewModelScope) {
            override fun createCall(): LiveData<ApiResponse<RequestType>> {
                return createCall()
            }
        }.asLiveData()
    }

    inline fun <RequestType> BaseRepository.LiveNetworkResourceEvent (
        crossinline createCall: ()-> LiveData<ApiResponse<RequestType>>,
        crossinline onCallSucceed: suspend (resultType: RequestType) -> Unit = {},
    ) : LiveData<Event<Resource<RequestType>>> {
        return object: NetworkResourceEvent<RequestType>(viewModelScope) {
            override fun createCall(): LiveData<ApiResponse<RequestType>> {
                return createCall()
            }

            override suspend fun onCallSucceed(resultItem: RequestType) {
                onCallSucceed(resultItem)
            }
        }.asLiveData()
    }
}