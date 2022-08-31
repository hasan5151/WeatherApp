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
abstract class NetworkResourceEvent<ResultType>
@MainThread constructor(private val viewModelScope: CoroutineScope) {

    private val resultEvent = MediatorLiveData<Event<Resource<ResultType>>>()

    init {
        resultEvent.value = Event(Resource.loading(null))
        @Suppress("LeakingThis")
        fetchFromNetwork()
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (resultEvent.value?.peekContent() != newValue) {
            resultEvent.value = Event(newValue)
        }
    }

    private fun fetchFromNetwork() {
        val apiResponse = createCall()
        resultEvent.addSource(apiResponse) { response ->
            resultEvent.removeSource(apiResponse)
            Log.d("NetworkResource", "fetchFromNetwork: $response")
            when (response) {
                is ApiSuccessResponse -> {
                    viewModelScope.launch(bgDispatcher) {
                        val processedResponse = processResponse(response)
                        onCallSucceed(processedResponse)
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
                    setValue(Resource.error(response.errorMessage, null, code = response.errorCode))
                }
            }
        }
    }

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    protected open fun onFetchFailed() {}

    protected open suspend fun onCallSucceed(resultItem: ResultType) {}

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class
    fun asLiveData() = resultEvent as LiveData<Event<Resource<ResultType>>>

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<ResultType>) = response.body

    // Called to create the API call.
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<ResultType>>
}