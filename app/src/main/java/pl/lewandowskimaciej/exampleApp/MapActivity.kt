package pl.lewandowskimaciej.exampleApp

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapActivity : AppCompatActivity(), LocationListener {

    var TAG : String = "myMapActivity"

    var map : MapView? = null
    var mapController : MapController? = null
    var mLocationOverlay : MyLocationNewOverlay? = null
    var mCompassOverlay : CompassOverlay? = null
    var mScaleBarOverlay : ScaleBarOverlay? = null
    var displayMetrics : DisplayMetrics? = null
    var listGeoPoint : MutableList<GeoPoint>? = null
    var line : Polyline = Polyline()

    var location : Location? = null
    var zoomValue : Double = 17.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        map = findViewById(R.id.map)
        mapController = map!!.controller as MapController

        map!!.setTileSource(TileSourceFactory.MAPNIK)
        map!!.isTilesScaledToDpi

        map!!.setMultiTouchControls(true)

        this.mCompassOverlay = CompassOverlay(applicationContext, InternalCompassOrientationProvider(applicationContext), map)
        this.mCompassOverlay!!.enableCompass()
        map!!.overlays.add(this.mCompassOverlay)

        displayMetrics = applicationContext.resources.displayMetrics
            Log.e(TAG, "width: " + displayMetrics?.widthPixels
                        + "\nheight: " + displayMetrics?.heightPixels)
        mScaleBarOverlay = ScaleBarOverlay(map)
        mScaleBarOverlay!!.setCentred(true)
        mScaleBarOverlay!!.setScaleBarOffset(displayMetrics?.widthPixels!! /2, 10)

        map!!.overlays.add(this.mScaleBarOverlay)
        this.mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(applicationContext), map)

        map!!.overlays.add(this.mLocationOverlay)

    }

    override fun onLocationChanged(location: Location) {
        this.location = location

        var geoPoint = GeoPoint(location.latitude, location.longitude)
        listGeoPoint?.add(geoPoint)

        mapController!!.animateTo(geoPoint)
        mapController!!.setZoom(zoomValue)

        drawLineBeetweenGeoPoints()

    }

    fun drawLineBeetweenGeoPoints() {
        line.setPoints(listGeoPoint)
        line.setOnClickListener { polyline, mapView, eventPos ->
            Toast.makeText(
                mapView.context,
                "polyline with " + polyline.points.size + "pts was tapped",
                Toast.LENGTH_LONG
            ).show()
            false
        }
        map!!.overlayManager.add(line)
    }

    override fun onResume() {
        super.onResume()
        map?.onResume()
    }

    override fun onPause() {
        super.onPause()
        map?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        map?.onDetach()
    }
}