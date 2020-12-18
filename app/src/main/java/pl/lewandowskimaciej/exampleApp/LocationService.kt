package pl.lewandowskimaciej.exampleApp

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.delay
import org.osmdroid.util.GeoPoint

class LocationService : Service() {

    private var CHANNEL_ID = "mNotifyLocationServices"
    var TAG : String = "myLocationService"
    var locationManager : LocationManager? = null
    var locationListener : LocationListener? = null

    var longitude : Double = 0.0
    var latitude: Double = 0.0

//    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBind(intent: Intent): IBinder? {
        val iBinder : IBinder? = null
        return iBinder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "work")
        notification()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        initLocationProvider(locationManager!!)
        Log.e("mylog", "działa")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "call onDestroy()")
        locationListener?.let { locationManager?.removeUpdates(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun notification() {
//        var notificationManager : NotificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        createNotificationChannel()
        val notification = Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(R.string.notification_message))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setTicker(getText(R.string.ticker_text))
            .build()
        startForeground(1, notification)
    }

    fun createNotificationChannel() {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun initLocationProvider(locationManager : LocationManager) {

        var providerString: String? = this.createCoarseCriteria().let { locationManager.getBestProvider(it, true) }
        if (providerString == null) {
            Log.e(TAG, "initLocationProvider: no provider create CoarseCriteria")
            providerString = this.createFineCriteria().let { locationManager.getBestProvider(it, true)}
        } else {
            val low: LocationProvider? = locationManager.getProvider(providerString)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "no permission")
                return
            }
            if (low != null) {

                locationListener = LocationListener { location -> // do something here to save this new location
                    Log.e(TAG, "location longitude: " + location.longitude)
                    Log.e(TAG, "location latitude: " + location.latitude)
                    Log.e(TAG, "location provider: " + location.provider)
                    Log.e(TAG, "location time: " + location.time)
                    Log.e(TAG, "location accuracy: " + location.accuracy)
                    Log.e(TAG, "location altitude: " + location.altitude)
                    Log.e(TAG, "location bearing: " + location.bearing)
                    Log.e(TAG, "location speed: " + location.speed)
                    this.longitude = location.longitude
                    this.latitude = location.latitude
                    startMapActivity()

                }
                locationManager.requestLocationUpdates(low.name, 0, 20f, locationListener!!)
            }
        }
    }

    fun startMapActivity() {
        val intent = Intent()
        intent.setAction("MapBrodcastReceiver")
        intent.putExtra("longitude", longitude)
        intent.putExtra("latitude", latitude)
        sendBroadcast(intent)
    }

    fun createCoarseCriteria(): Criteria {
        val c = Criteria()
        c.accuracy = Criteria.ACCURACY_COARSE
        c.isAltitudeRequired = false
        c.isBearingRequired = false
        c.isSpeedRequired = false
        c.isCostAllowed = true
        c.powerRequirement = Criteria.POWER_HIGH
        return c
    }

    /** this criteria needs high accuracy, high power, and cost  */
    fun createFineCriteria(): Criteria {
        val c = Criteria()
        c.accuracy = Criteria.ACCURACY_FINE
        c.isAltitudeRequired = false
        c.isBearingRequired = false
        c.isSpeedRequired = false
        c.isCostAllowed = true
        c.powerRequirement = Criteria.POWER_HIGH
        return c
    }

//    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
//        onTaskRemoved(intent)
//        Toast.makeText(
//            applicationContext, "This is a Service running in Background",
//            Toast.LENGTH_SHORT
//        ).show()
//        return START_STICKY
//    }
//
//    override fun onTaskRemoved(rootIntent: Intent) {
//        val restartServiceIntent = Intent(applicationContext, this.javaClass)
//        restartServiceIntent.setPackage(packageName)
//        startService(restartServiceIntent)
//        super.onTaskRemoved(rootIntent)
//    }

}