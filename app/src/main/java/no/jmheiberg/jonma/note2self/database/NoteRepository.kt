package no.jmheiberg.jonma.note2self.database

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread

class NoteRepository(private val noteDao: NoteDAO) {
    val notesLive: LiveData<List<Note>> = noteDao.listAllNotesLive()


    @WorkerThread
    fun insert(note: Note) {
        noteDao.insertNote(note)
    }

    @WorkerThread
    fun delete(note: Note) {
        noteDao.deleteNote(note)
    }

    @WorkerThread
    fun update(note: Note) {
        noteDao.updateNote(note)
    }

}