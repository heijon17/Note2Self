package no.jmheiberg.jonma.note2self


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import no.jmheiberg.jonma.note2self.database.Note


/**
 * A simple [Fragment] subclass.
 *
 */
class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap
    var note: Note? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        note = arguments!!.getParcelable("note")
        val latlng = LatLng(note!!.lat, note!!.lng)

        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        try {
            MapsInitializer.initialize(activity?.applicationContext)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        mapView.getMapAsync {
            map = it
            val markerNote = map.addMarker( //Marker variable could be used for something later
                MarkerOptions()
                    .position(latlng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title(note!!.title)
            )
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14f))
        }
        return view
    }
}
