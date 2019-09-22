package no.jmheiberg.jonma.note2self.database

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread

class WeatherRepository(private val weatherDAO: WeatherDAO) {
    val allWeather: LiveData<List<Weather>> = weatherDAO.getAllWeatherLive()

    @WorkerThread
    fun insert(weather: Weather) {
        weatherDAO.insertWeather(weather)
    }

    @WorkerThread
    fun delete(weather: Weather) {
        weatherDAO.deleteWeather(weather)
    }

    @WorkerThread
    fun get(id: String): Weather {
        return weatherDAO.getWeather(id)
    }

    @WorkerThread
    fun delete(id: String) {
        weatherDAO.deleteWeather(id)
    }
}