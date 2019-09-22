package no.jmheiberg.jonma.note2self.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface WeatherDAO {
    @Insert
    fun insertWeather(weather: Weather)

    @Delete
    fun deleteWeather(weather: Weather)

    @Query("SELECT * FROM weather WHERE id = :id")
    fun getWeather(id: String): Weather

    @Query("SELECT * FROM weather")
    fun getAllWeatherLive(): LiveData<List<Weather>>

    @Query("DELETE FROM weather WHERE id = :id")
    fun deleteWeather(id: String)
}