package com.huzi.forecast.ui.launch

import android.location.Location
import com.huzi.forecast.constants.HASGPS
import com.huzi.forecast.constants.NOGPS
import com.huzi.forecast.db.dao.CityDao
import com.huzi.forecast.db.dao.CurrentDao
import com.huzi.forecast.db.dao.HourlyDao
import com.huzi.forecast.db.entity.CurrentWeather
import com.huzi.forecast.db.entity.HourlyWeather
import com.huzi.forecast.models.*
import com.huzi.forecast.services.WeatherService
import com.huzi.shared.core.BaseRepository
import com.huzi.shared.resource.FlowBoundResource
import com.huzi.shared.result.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LaunchRepo @Inject constructor(
    private val weatherService: WeatherService,
    private val cityDao: CityDao,
    private val hourlyDao: HourlyDao,
    private val currentDao: CurrentDao,
) : BaseRepository() {
    fun hourly(id: String?): Flow<Resource<List<HourlyWeather>>> = FlowBoundResource(
        fetchFromNetwork = {
            weatherService.getHourly(id)
        },
        loadFromDb = {
            hourlyDao.getAll()
        },
        saveNetworkResult = {
            withContext(Dispatchers.IO) {
                hourlyDao.deleteAll()
                it.list.forEach {
                    hourlyDao.insert(it.toHourWeather())
                }
            }
        }
    )

    fun hourlyByLocation(location: Location): Flow<Resource<List<HourlyWeather>>> = FlowBoundResource(
        fetchFromNetwork = {
            weatherService.getHourlyByLocation(
                location.latitude,
                location.longitude
            )
        },
        loadFromDb = {
            hourlyDao.getAll()
        },
        saveNetworkResult = {
            withContext(Dispatchers.IO) {
                hourlyDao.deleteAll()
                it.list.forEach {
                    hourlyDao.insert(it.toHourWeather())
                }
            }
        }
    )

    fun currentByLocation(location: Location): Flow<Resource<CurrentWeather>> = FlowBoundResource(
        fetchFromNetwork = {
            weatherService.getCurrentByLocation(
                location.latitude,
                location.longitude
            )
        },
        loadFromDb = {
            currentDao.getCurrent()
        },
        saveNetworkResult = {
            withContext(Dispatchers.IO) {
                currentDao.deleteALl()
                val city = cityDao.getGPSCity()
                city?.let { model ->
                    cityDao.delete(city)
                    cityDao.insert(it.toCity(HASGPS))
                } ?: apply {
                    cityDao.insert(it.toCity(HASGPS))
                }
                currentDao.insert(it.toCurrentWeather())
            }
        }
    )

    fun current(id: String?): Flow<Resource<CurrentWeather>> = FlowBoundResource(
        fetchFromNetwork = {
            weatherService.getCurrent(id)
        },
        loadFromDb = {
            currentDao.getCurrent()
        },
        saveNetworkResult = {
            withContext(Dispatchers.IO) {
                currentDao.deleteALl()
                val city = cityDao.getCityById(id)
                city?.let {} ?: apply {
                    cityDao.insert(it.toCity(NOGPS))
                }
                currentDao.insert(it.toCurrentWeather())
            }
        }
    )

    fun listCity() = cityDao.getAllList()
    fun selectedCity() = cityDao.getSelectedCity()
}