package com.huzi.shared.resource

import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.huzi.shared.result.*

// ResultType: Type for the Resource data.
// RequestType: Type for the API response.
abstract class NetworkProcessResource<ResultType, RequestType>
@MainThread constructor(private val viewModelScope: CoroutineScope) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        fetchFromNetwork()
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork() {
        val apiResponse = createCall()
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            Log.d("NetworkResource", "fetchFromNetwork: $response")
            when (response) {
                is ApiSuccessResponse -> {
                    viewModelScope.launch(bgDispatcher) {
                        val processedResponse = processResponse(response.body)
                        onCallSucceed(response.body, processedResponse)
                        withContext(viewModelScope.coroutineContext) {
                            setValue(Resource.success(processedResponse))
                        }
                    }
                }
                is ApiEmptyResponse -> {
                    setValue(Resource.success(null))
                }
                is ApiErrorResponse -> {
                    onFetchFailed()
                    setValue(Resource.error(msg = response.errorMessage,
                            code = response.errorCode))
                }
            }
        }
    }

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    protected open fun onFetchFailed() {}

    protected open suspend fun onCallSucceed(requestItem: RequestType, resultItem: ResultType) {}

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class
    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected abstract fun processResponse(body: RequestType): ResultType

    // Called to create the API call.
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
}