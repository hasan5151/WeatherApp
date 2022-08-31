package com.huzi.shared.main

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import dagger.hilt.android.lifecycle.HiltViewModel
import com.huzi.shared.models.SnackbarMessage
import com.huzi.shared.result.Event
import com.huzi.shared.result.SingleLiveEvent
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(mainRepository: MainRepository) : ViewModel() {

    private val _loadingAnim = MutableLiveData<Boolean>()
    val loadingAnim: LiveData<Boolean> = _loadingAnim

    private val _message = MutableLiveData<SnackbarMessage>()
    val message: LiveData<SnackbarMessage> = _message

    private val _toolbarTitle = MutableLiveData<String?>()
    val toolbarTitle: LiveData<String?> = _toolbarTitle

    private val _localeSelected = SingleLiveEvent<Locale>()
    val localeSelected: SingleLiveEvent<Locale> = _localeSelected

    private val _isKeyboardOpen = MutableLiveData<Boolean>()
    val isKeyboardOpen: LiveData<Boolean> = _isKeyboardOpen

    private val _logOutEvent = MutableLiveData<Event<Unit>>()
    val logOutEvent: LiveData<Event<Unit>> = _logOutEvent

    val isGpsOn = SingleLiveEvent<Boolean>()

    fun showLoadingAnim(show: Boolean) {
        if (this._loadingAnim.value != show)
            this._loadingAnim.value = show
    }

    fun showMessage(message: String?) {
        this._message.value = SnackbarMessage(message = message, isError = false)
    }

    fun showErrorMessage(message: String?) {
        this._message.value = SnackbarMessage(message = message, isError = true)
    }

    fun showErrorMessage(message: String?, @BaseTransientBottomBar.Duration length: Int) {
        this._message.value = SnackbarMessage(message = message, isError = true, length = length)
    }

    fun setKeyboardOpen(isOpen: Boolean) {
        if (this._isKeyboardOpen.value != isOpen)
            this._isKeyboardOpen.value = isOpen
    }

    fun setToolbarTitle(title: String?) {
        this._toolbarTitle.value = title
    }

    fun onLogOut() {
        _logOutEvent.postValue(Event(Unit))
    }

    /**
     * [Lifecycle.Event.ON_DESTROY] works only on fragment.viewLifecycleOwner.lifecycle
     * for fragment.lifecycle use [Lifecycle.Event.ON_STOP]
     */
    fun setLoaderOwner(fragment: Fragment) {
        val lifecycleOwner = fragment.viewLifecycleOwner.lifecycle
        lifecycleOwner.addObserver(AnimationLifecycle(lifecycleOwner))
    }

    private inner class AnimationLifecycle(private val lifecycle: Lifecycle) : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) {
            if (_loadingAnim.value != false) {
                _loadingAnim.value = false
            }
            lifecycle.removeObserver(this)
            super.onStop(owner)
        }
    }
}
