package no.jmheiberg.jonma.note2self.database

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WeatherModel(application: Application) : AndroidViewModel(application) {
    private val repository: WeatherRepository
    private val parentJob = Job()
    private val allWeather: LiveData<List<Weather>>
    private val coroutineContext get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    init {
        val database = NoteRoomDatabase.getDatabaase(application.applicationContext)
        repository = WeatherRepository(database!!.weatherDao())
        allWeather = repository.allWeather
    }

    fun get(id: String) = scope.launch(Dispatchers.IO) {
        repository.get(id)
    }

    fun insert(weather: Weather) = scope.launch(Dispatchers.IO) {
        repository.insert(weather)
    }

    fun delete(weather: Weather) = scope.launch(Dispatchers.IO) {
        repository.delete(weather)
    }

    fun delete(id: String) = scope.launch(Dispatchers.IO) {
        repository.delete(id)
    }

}