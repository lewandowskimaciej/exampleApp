package pl.lewandowskimaciej.exampleApp

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

class LocationService : Service() {

    private var CHANNEL_ID = "mNotifyLocationServices"
    var TAG : String = "myLocationService"
    var locationManager : LocationManager? = null
    var locationListener : LocationListener? = null

//    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBind(intent: Intent): IBinder? {
        var iBinder : IBinder? = null
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
        var notification = Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(R.string.notification_message))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setTicker(getText(R.string.ticker_text))
            .build()
        startForeground(1, notification)
    }

    public fun createNotificationChannel() {

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

        var providerString: String? = this.createCoarseCriteria()?.let { locationManager.getBestProvider(it, true) }
        if (providerString == null) {
            Log.e(TAG, "initLocationProvider: no provider create CoarseCriteria")
            providerString = this.createFineCriteria()?.let { locationManager.getBestProvider(it, true)}
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

                }
                locationManager.requestLocationUpdates(low.name, 0, 0f, locationListener!!)
            }
        }
    }


    fun createCoarseCriteria(): Criteria? {
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
    fun createFineCriteria(): Criteria? {
        val c = Criteria()
        c.accuracy = Criteria.ACCURACY_FINE
        c.isAltitudeRequired = false
        c.isBearingRequired = false
        c.isSpeedRequired = false
        c.isCostAllowed = true
        c.powerRequirement = Criteria.POWER_HIGH
        return c
    }


    class MainBrodcastReceiver : BroadcastReceiver() {

        var TAG = "myLocationServiceBrodcastReceiver"

        override fun onReceive(context: Context?, intent: Intent?) {
            val extra = intent!!.getStringExtra("LocationServices")
            Log.d(TAG, "z Broadcast: extra dane z intent: $extra")

        }

    }
}