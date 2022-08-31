package com.huzi.shared.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

abstract class BaseViewModel(repository: BaseRepository) : ViewModel() {

    init {
        repository.initScope(viewModelScope)
    }

}