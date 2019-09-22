package no.jmheiberg.jonma.note2self.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context


@Database(entities = [Note::class, Weather::class], version = 5)
abstract class NoteRoomDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDAO

    abstract fun weatherDao(): WeatherDAO

    companion object {
        private var INSTANCE: NoteRoomDatabase? = null

        fun getDatabaase(context: Context): NoteRoomDatabase? {

            if (INSTANCE == null) {

                synchronized(NoteRoomDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        NoteRoomDatabase::class.java, "noteDatabase"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }


    }


}