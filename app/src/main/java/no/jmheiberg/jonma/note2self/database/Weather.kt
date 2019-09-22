package no.jmheiberg.jonma.note2self.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "weather")
data class Weather(
    var temp: Int,
    var icon: String
) {
    @PrimaryKey
    var id = UUID.randomUUID().toString()
}