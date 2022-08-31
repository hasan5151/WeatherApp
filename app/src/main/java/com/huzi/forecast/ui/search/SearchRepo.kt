package com.huzi.forecast.ui.search

import com.huzi.forecast.db.dao.CityDao
import com.huzi.forecast.db.entity.CityList
import com.huzi.forecast.models.SearchCityDTO
import com.huzi.forecast.models.toCityList
import com.huzi.forecast.services.CityService
import com.huzi.forecast.utils.SharedPrefs
import com.huzi.shared.core.BaseRepository
import com.huzi.shared.resource.FlowSingleResource
import com.huzi.shared.result.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchRepo @Inject constructor(
    private val cityDao: CityDao,
    private val cityService: CityService,
    private val prefs: SharedPrefs
) : BaseRepository() {

    fun deleteCity(city: CityList) {
        viewModelScope.launch {
            cityDao.delete(city)
            if (city.isSelected){
                prefs.isGpsSelected=true
                cityDao.selectGPSLocation()
            }
        }
    }

    fun clearSelection() {
        viewModelScope.launch {
            cityDao.clearSelection()
        }
    }

    fun selectCity(city: CityList) {
        viewModelScope.launch {
            cityDao.select(city.id.toString())
        }
    }

    fun searchCity(name: String): Flow<Resource<List<SearchCityDTO>>> =
        FlowSingleResource{ cityService.searchCity(name) }

    fun insertCity(city: SearchCityDTO){
        viewModelScope.launch {
            cityDao.insert(city.toCityList())
        }
    }
}