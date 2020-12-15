package pl.lewandowskimaciej.exampleApp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class Location : AppCompatActivity() {

    var condition : Boolean = false

    var locationServiceSwitch : Switch? = null

    var LOCATION_REQUEST_CODE : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        locationPermission()

        locationServiceSwitch = findViewById(R.id.locationServiceSwitch)
    }

    fun locationService(v: View) {
        condition = if(condition) false else true

        locationServiceSwitch?.text = if(condition) getString(R.string.locationSerciveSwitchOn) else getString(R.string.locationServiceSwitchOff)
        Log.e("switch", condition.toString())

        if(condition) startService(Intent(this, LocationService::class.java)) else stopService(Intent(this, LocationService::class.java))
        //CATION_REQUEST_CODE) startService(Intent(this, LocationService::class.java)) else Toast.makeText(this, "dupa", 22).show()

    }

    fun locationMapShow(v: View) {
        var intent : Intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

    fun locationPermission() {
        if (ContextCompat.checkSelfPermission(this@Location,
                Manifest.permission.ACCESS_FINE_LOCATION) !==
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@Location,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this@Location,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(this@Location,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@Location,
                            Manifest.permission.ACCESS_FINE_LOCATION) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                        LOCATION_REQUEST_CODE = true
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                        LOCATION_REQUEST_CODE = false
                }
                return
            }
        }
    }
}