package no.jmheiberg.jonma.note2self.database


import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface NoteDAO {
    @Insert
    fun insertNote(note: Note)

    @Delete
    fun deleteNote(note: Note)

    @Update
    fun updateNote(note: Note)


//    @Query("SELECT * FROM note")
//    fun  listAllNotes() : List<Note>

    @Query("SELECT * FROM note")
    fun listAllNotesLive(): LiveData<List<Note>>


}