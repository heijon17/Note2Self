package no.jmheiberg.jonma.note2self.database

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NoteModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    val notes: LiveData<List<Note>>
    private val parentJob = Job()
    private val coroutineContext get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    init {
        val database = NoteRoomDatabase.getDatabaase(application.applicationContext)
        repository = NoteRepository(database!!.noteDao())
        notes = repository.notesLive
    }

    fun insert(note: Note) = scope.launch(Dispatchers.IO) {
        repository.insert(note)
    }

    fun delete(note: Note) = scope.launch(Dispatchers.IO) {
        repository.delete(note)
    }

    fun update(note: Note) = scope.launch(Dispatchers.IO) {
        repository.update(note)
    }


}