package no.jmheiberg.jonma.note2self

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import no.jmheiberg.jonma.note2self.database.Note
import no.jmheiberg.jonma.note2self.database.NoteModel
import no.jmheiberg.jonma.note2self.database.WeatherModel
import no.jmheiberg.jonma.note2self.util.DateUtil
import no.jmheiberg.jonma.note2self.util.ImageUtil
import no.jmheiberg.jonma.note2self.util.LocationUtil

class NoteAdapter(
    val activity: MainActivity,
    val mContext: Context
) :
    RecyclerView.Adapter<NoteAdapter.MyViewHolder>() {
    var notes: List<Note> = listOf()
    lateinit var weatherModel: WeatherModel
    lateinit var noteModel: NoteModel


    override fun onCreateViewHolder(parent: ViewGroup, type: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_card, parent, false)
        weatherModel = WeatherModel(activity.application)
        noteModel = NoteModel(activity.application)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
        val note: Note = notes[position]

        viewHolder.cardView.setOnClickListener {
            showCard(note)
        }
        viewHolder.title.text = note.title
        viewHolder.desc.text = note.desc
        if (note.imagePath != "") {
            val util = ImageUtil(activity)
            var bitmap: Bitmap? = null
            Thread(Runnable {
                bitmap = util.decodeSampledBitmapFromFile(note.imagePath, 500, 500)
                viewHolder.image.post {
                    viewHolder.image.setImageBitmap(bitmap)
                }
            }).start()

        } else {
            viewHolder.image.setImageResource(R.drawable.notes_clean)
        }
        val converter = DateUtil()
        val dateTime = converter.formatDate(converter.fromStringToDate(note), false)
        viewHolder.timeDate.text = dateTime

        if (note.lat != 0.0) {
            viewHolder.imgLocation.visibility = View.VISIBLE
            viewHolder.imgLocation.setOnClickListener {
                LocationUtil(mContext).openLocationOnMap(note)
            }
            viewHolder.txtLocation.text = LocationUtil(mContext).getCityFromLocation(note.lat, note.lng)
        } else {
            viewHolder.imgLocation.visibility = View.GONE
            viewHolder.txtLocation.text = activity.resources.getString(R.string.no_location)
        }

        viewHolder.image.setOnClickListener {
            showCard(note)
        }
        viewHolder.menu.setOnClickListener {
            showMenu(it, note)
        }

    }

    fun setData(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    private fun showCard(note: Note) {

        val intent = Intent(mContext, ShowCard::class.java)
        intent.putExtra("note", note)

        mContext.startActivity(intent)
    }

    private fun showMenu(view: View?, note: Note) {
        val menu = PopupMenu(mContext, view)
        menu.menuInflater.inflate(R.menu.menu_note, menu.menu)
        menu.setOnMenuItemClickListener(MyMenuClickListener(note))
        menu.show()
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.txt_card_title)
        val desc: TextView = itemView.findViewById(R.id.txt_card_description)
        val image: ImageView = itemView.findViewById(R.id.card_image)
        val menu: TextView = itemView.findViewById(R.id.img_card_menu)
        val cardView: CardView = itemView.findViewById(R.id.card_view)
        val timeDate: TextView = itemView.findViewById(R.id.txt_timedate)
        val imgLocation: ImageView = itemView.findViewById(R.id.img_location)
        val txtLocation: TextView = itemView.findViewById(R.id.txt_location)


    }

    inner class MyMenuClickListener(val note: Note) : PopupMenu.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.menu_delete_note -> {
                    deleteNote(note)
                    return true
                }
            }
            return false
        }

    }

    fun deleteNote(note: Note) {
        AlertDialog.Builder(mContext)
            .setTitle("Delete ${note.title}?")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton(android.R.string.yes) { _: DialogInterface, _: Int ->
                noteModel.delete(note)
                weatherModel.delete(note.weatherId)
            }
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()


    }


}

