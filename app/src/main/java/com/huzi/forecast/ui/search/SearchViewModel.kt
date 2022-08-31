package com.huzi.forecast.ui.search

import android.text.Editable
import com.huzi.forecast.db.entity.CityList
import com.huzi.forecast.models.SearchCityDTO
import com.huzi.shared.core.BaseViewModel
import com.huzi.shared.extensions.MutableFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repo: SearchRepo): BaseViewModel(repo) {

    private val _searchTerm = MutableFlow<String>()
    val isSearchRvVisible = MutableStateFlow(false)

    val search = _searchTerm.flatMapLatest {
        repo.searchCity(it)
    }

    fun setSearchRvVisibility(isVisible: Boolean){
        isSearchRvVisible.tryEmit(isVisible)
    }

    fun onSearch(editable:  Editable?){
        if (editable==null) return
        setSearchRvVisibility(editable.toString().length>2)
        if (editable.toString().length>2){
            _searchTerm.tryEmit(editable.toString())
        }
    }

    fun deleteCity(city: CityList){
        repo.deleteCity(city)
    }

    fun selectCity(city: CityList){
        repo.clearSelection()
        repo.selectCity(city)
    }

    fun insertCity(cityDTO: SearchCityDTO){
        repo.clearSelection()
        repo.insertCity(cityDTO)
    }
}