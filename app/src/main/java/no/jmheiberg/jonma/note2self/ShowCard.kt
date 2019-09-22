package no.jmheiberg.jonma.note2self


import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.View
import kotlinx.android.synthetic.main.activity_show_card.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import no.jmheiberg.jonma.note2self.database.*
import no.jmheiberg.jonma.note2self.util.DateUtil
import no.jmheiberg.jonma.note2self.util.ImageUtil
import no.jmheiberg.jonma.note2self.util.LocationUtil
import no.jmheiberg.jonma.note2self.util.WeatherUtil


class ShowCard : AppCompatActivity() {

    lateinit var weather: Weather
    lateinit var database: NoteRoomDatabase
    lateinit var noteModel: NoteModel
    lateinit var weatherModel: WeatherModel
    lateinit var note: Note


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_card)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        note = intent.getParcelableExtra("note")


        database = NoteRoomDatabase.getDatabaase(this)!!
        noteModel = ViewModelProviders.of(this).get(NoteModel::class.java)
        weatherModel = WeatherModel(application)

        updateUi()


    }

    private fun updateUi() {
        txt_card_title_big.text = note.title
        txt_card_description_big.text = note.desc
        txt_card_description_big.movementMethod = ScrollingMovementMethod()
        if (note.imagePath != "") {
            val util = ImageUtil(this)
            var bitmap: Bitmap?
            Thread(Runnable {
                bitmap = util.decodeSampledBitmapFromFile(note.imagePath, 1500, 1500)
                card_image_big.post {
                    card_image_big.setImageBitmap(bitmap)
                }
            }).start()

        } else {
            card_image_big.setImageResource(R.drawable.notes)
        }

        val converter = DateUtil()
        txt_timedate_big.text = converter.formatDate(converter.fromStringToDate(note), true)

        if (note.lat != 0.0) {
            drawMap()
        } else {
            noLocation()
        }
    }

    private fun noLocation() {
        img_location_big.visibility = View.GONE
        txt_location_big.text = resources.getString(R.string.no_location)

        val noLocationFragment = NoLocationFragment()
        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        transaction.replace(R.id.map_container, noLocationFragment)
        transaction.commit()
    }

    private fun drawMap() {
        img_location_big.visibility = View.VISIBLE
        img_location_big.setOnClickListener {
            LocationUtil(this).openLocationOnMap(note)
        }
        txt_location_big.text = LocationUtil(this).getCityFromLocation(note.lat, note.lng)

        //get weather-icon and temp from database
        if (note.weatherId.isNotEmpty()) {
            getWeatherFromDatabase(note)
            WeatherUtil(this).setWeatherFromDatabase(weather)
            txt_weather_big.text = "${weather.temp} °C"
        }

        val mapFragment = MapFragment()
        val bundle = Bundle()
        bundle.putParcelable("note", note)
        mapFragment.arguments = bundle


        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        transaction.replace(R.id.map_container, mapFragment)
        transaction.commit()

    }


    fun getWeatherFromDatabase(note: Note) = runBlocking {
        val job = GlobalScope.launch {
            weather = database.weatherDao().getWeather(note.weatherId)
        }
        job.join()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }


}
