package no.jmheiberg.jonma.note2self

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*
import no.jmheiberg.jonma.note2self.database.Note
import no.jmheiberg.jonma.note2self.database.NoteModel
import no.jmheiberg.jonma.note2self.services.BROADCAST_ACTION
import no.jmheiberg.jonma.note2self.services.SensorService


class MainActivity : AppCompatActivity() {

    private lateinit var noteModel: NoteModel
    private val NEW_NOTE_REQUEST = 1
    private val EDIT_NOTE_REQUEST = 2
    private lateinit var adapter: NoteAdapter
    private lateinit var sensorReceiver: SensorBroadCastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            addNote(false)
        }

        noteModel = ViewModelProviders.of(this).get(NoteModel::class.java)
        adapter = NoteAdapter(this, this)

        initCollapsingToolbar()
        loadDatabaseData()


        //service
        intent = Intent(this, SensorService::class.java)
        startService(intent)
        sensorReceiver = SensorBroadCastReceiver()
    }

    inner class SensorBroadCastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            addNote(true)
        }
    }


    override fun onResume() {
        super.onResume()
        val sensorIntentFilter = IntentFilter(BROADCAST_ACTION)
        LocalBroadcastManager.getInstance(this).registerReceiver(sensorReceiver, sensorIntentFilter)

    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(sensorReceiver)
    }


    fun addNote(shake: Boolean) {
        val intent = Intent(this, AddNote::class.java).apply {
            putExtra("shake", shake)
        }
        this.startActivityForResult(intent, NEW_NOTE_REQUEST)
    }


    private fun initCollapsingToolbar() {
        val toolbar: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout)
        val appbarLayout: AppBarLayout = findViewById(R.id.appbar_layout)

        toolbar.title = " "
        appbarLayout.setExpanded(true)
        var showing = false
        var range = -1
        appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { layout, offset ->

            if (range == -1) range = layout.totalScrollRange
            if (range + offset == 0) {
                toolbar.title = getString(R.string.app_name)
                toolbar.setCollapsedTitleTextColor(getColor(android.R.color.black))

                showing = true
            } else if (showing) {
                toolbar.title = " "
                showing = false
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == NEW_NOTE_REQUEST) {
            if (data != null && data.hasExtra("newNote")) {
                val note: Note = data.getParcelableExtra("newNote")
                noteModel.insert(note)
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == EDIT_NOTE_REQUEST) {
            if (data != null && data.hasExtra("newNote")) {
                val note: Note = data.getParcelableExtra("newNote")
                noteModel.update(note)
            }
        }
    }


    private fun loadDatabaseData() {
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)

        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()


        val noteObserver = Observer<List<Note>> {
            adapter.setData(it!!)
        }

        noteModel.notes.observe(this, noteObserver)

//        noteModel.notes.observe(this, Observer { liveNotes ->
//            adapter.setData(liveNotes!!)
//        })
        recyclerView.adapter = adapter


    }


}
