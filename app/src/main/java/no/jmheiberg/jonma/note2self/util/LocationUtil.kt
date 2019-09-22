package no.jmheiberg.jonma.note2self.util

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import no.jmheiberg.jonma.note2self.database.Note
import java.io.IOException
import java.util.*


class LocationUtil(val context: Context) {

    fun getCityFromLocation(lat: Double, lng: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addresses: List<Address>? = null
        var cityName = ""
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        if (addresses != null && addresses.isNotEmpty()) {
            if (addresses[0].subLocality != null) {
                cityName = addresses[0].subLocality
            } else if (addresses[0].subAdminArea != null){
                cityName = addresses[0].subAdminArea
            } else if (addresses[0].adminArea != null) {
                cityName = addresses[0].adminArea
            }
        }
        return cityName
    }

    fun openLocationOnMap(note: Note) {

        val uri = String.format(Locale.ENGLISH, "geo:${note.lat},${note.lng}?z=14")

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        context.startActivity(intent)
    }


}