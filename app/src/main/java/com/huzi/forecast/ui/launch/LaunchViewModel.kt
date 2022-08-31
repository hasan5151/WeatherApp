package com.huzi.forecast.ui.launch

import android.location.Location
import com.huzi.forecast.db.entity.CurrentWeather
import com.huzi.forecast.db.entity.HourlyWeather
import com.huzi.shared.core.BaseViewModel
import com.huzi.shared.extensions.MutableFlow
import com.huzi.shared.result.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor(private val repo: LaunchRepo) : BaseViewModel(repo) {
    private var _location : Location? =null
    private val _currentLocation = MutableFlow<Location>()
    private val _selectedCityId = MutableFlow<String?>()

    fun setLocation(location: Location) {
        _currentLocation.tryEmit(location)
    }

    fun recordLocation(location: Location){
        _location = location
    }

    fun updateLocation() {
        if (_location==null) return
        _currentLocation.tryEmit(_location!!)
    }

    fun setSelectedCityId(selectedCityId: String?) {
        _selectedCityId.tryEmit(selectedCityId)
    }

    private val hourlyByLocation = _currentLocation.flatMapLatest{
        repo.hourlyByLocation(it)
    }

    private val currentByLocation = _currentLocation.flatMapLatest{
        repo.currentByLocation(it)
    }

    private val hourlyById = _selectedCityId.flatMapLatest{
        repo.hourly(it)
    }

    private val currentById = _selectedCityId.flatMapLatest{
        repo.current(it)
    }

    val current : Flow<Resource<CurrentWeather>>
            = merge(currentById,currentByLocation)

    val hourly : Flow<Resource<List<HourlyWeather>>>
            = merge(hourlyByLocation,hourlyById)

    val cityList = repo.listCity()
    val selectedCity = repo.selectedCity().distinctUntilChanged()
}