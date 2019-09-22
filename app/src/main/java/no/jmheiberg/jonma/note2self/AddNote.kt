package no.jmheiberg.jonma.note2self

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import no.jmheiberg.jonma.note2self.database.Note
import no.jmheiberg.jonma.note2self.database.NoteRoomDatabase
import no.jmheiberg.jonma.note2self.database.Weather
import no.jmheiberg.jonma.note2self.util.ImageUtil
import no.jmheiberg.jonma.note2self.util.WeatherUtil
import java.io.File
import java.io.IOException


class AddNote : AppCompatActivity() {

    private var file: File? = null
    lateinit var database: NoteRoomDatabase

    private var currentLocation: Location? = null
    private lateinit var request: LocationRequest
    private var locProviderClient: FusedLocationProviderClient? = null
    private val REQUEST_PERMISSION_LOCATION = 10
    private var firstLoad: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        img_camera_menu.setOnClickListener {
            showMenu(it)
        }

        btn_save_note.setOnClickListener {
            saveNote()
        }

        btn_cancel_note.setOnClickListener {
            cancelNote()
        }

        request = LocationRequest.create()

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertNoGps()
        }

        if (checkPermissionForLocation(this)) {
            startLocationUpdates()
        }

        database = NoteRoomDatabase.getDatabaase(this)!!

        val shake = intent.getBooleanExtra("shake", false)
        if (shake) {
            if (firstLoad) {
                takePicture()
                firstLoad = false
            }

        }


    }

    private fun startLocationUpdates() {
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(request)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        locProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locProviderClient!!.requestLocationUpdates(request, locationCallback, Looper.myLooper())
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation
            onLocationChanged(result.lastLocation)
        }
    }

    private fun onLocationChanged(location: Location) {
        this.currentLocation = location
    }

    private fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION_LOCATION
                )
                false
            }
        } else {
            true
        }
    }

    private fun alertNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Turn on GPS?")
            .setCancelable(false)
            .setPositiveButton(android.R.string.yes) { _, _ ->
                startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 11)
            }
            .setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.cancel()
                finish()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun cancelNote() {
        finish()
    }

    private fun saveNote() {

        if (etxt_title.text.isNotEmpty() && etxt_short_desc.text.isNotEmpty()) {
            val note = if (file != null) {
                Note(
                    etxt_title.text.toString(),
                    etxt_short_desc.text.toString(),
                    file?.absolutePath
                )
            } else {
                Note(etxt_title.text.toString(), etxt_short_desc.text.toString())
            }


            if (currentLocation != null) {
                note.lat = currentLocation!!.latitude
                note.lng = currentLocation!!.longitude
                //get weather from location
                val weatherUtil = WeatherUtil(this)
                val weather = weatherUtil.getWeatherForecast(note)
                note.weatherId = weather.id
                //insert weather to database
                insertWeather(weather)
            }


            val i = intent
            i.putExtra("newNote", note)
            setResult(Activity.RESULT_OK, i)
            finish()
        }
    }

    private fun insertWeather(weather: Weather) {
        GlobalScope.launch {
            database.weatherDao().insertWeather(weather)
        }


    }


    private fun showMenu(view: View?) {
        val menu = PopupMenu(this, view)
        menu.menuInflater.inflate(R.menu.menu_camera, menu.menu)
        menu.setOnMenuItemClickListener(MyCameraMenuClickListener())
        menu.show()
    }

    fun takePicture() {
        val util = ImageUtil(this)
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    util.createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    file = it
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "no.jmheiberg.jonma.note2self.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, 1)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            add_note_image.setImageBitmap(
                ImageUtil(this)
                    .decodeSampledBitmapFromFile(file?.absolutePath, 500, 500)
            )
        }
    }


    private fun deletePicture() {
        add_note_image.setImageDrawable(getDrawable(R.drawable.camera_icon))
    }

    inner class MyCameraMenuClickListener : PopupMenu.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.menu_add_image -> {
                    takePicture()
                    return true
                }
                R.id.menu_delete_image -> {
                    deletePicture()
                    return true
                }

            }
            return false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }


}
