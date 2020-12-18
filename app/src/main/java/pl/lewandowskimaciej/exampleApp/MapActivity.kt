package pl.lewandowskimaciej.exampleApp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

class MapActivity : AppCompatActivity() {

    var TAG : String = "myMapActivity"

    var map : MapView? = null
    var mapController : MapController? = null
    var mLocationOverlay : MyLocationNewOverlay? = null
    var mCompassOverlay : CompassOverlay? = null
    var mScaleBarOverlay : ScaleBarOverlay? = null
    var displayMetrics : DisplayMetrics? = null
    var listGeoPoint : MutableList<GeoPoint>? = null
    var geoPoint : GeoPoint = GeoPoint(0.0, 0.0)

    var mapBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val longitude : Double = intent!!.getDoubleExtra("longitude", 0.0)
            val latitude : Double= intent!!.getDoubleExtra("latitude", 0.0)
            geoPoint = GeoPoint(latitude, longitude)
            listGeoPoint?.add(geoPoint)

            mapController!!.animateTo(geoPoint)
            mapController!!.setZoom(zoomValue)

            Log.e(TAG, "longitude: " + longitude + " latitude: " + latitude)
        }
    }

    var zoomValue : Double = 17.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))

        geoPoint = GeoPoint(intent.getDoubleExtra("latitude", 0.0), intent.getDoubleExtra("longitude", 0.0))
        setContentView(R.layout.activity_map)

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
        mapController!!.setCenter(geoPoint)
        mapController!!.setZoom(zoomValue)

        var intentFilter = IntentFilter()
        intentFilter.addAction("MapBrodcastReceiver")
        registerReceiver(mapBroadcastReceiver, intentFilter)

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

