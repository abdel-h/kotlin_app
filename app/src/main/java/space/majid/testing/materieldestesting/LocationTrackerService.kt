package space.majid.testing.materieldestesting

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception

class LocationTrackerService : Service() {
    private val TAG: String = "LTService"
    lateinit var mLocationManager: LocationManager

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var currentUserId: String

    override fun onCreate() {
        super.onCreate()
        Log.d("LTService", "onCreate")

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")
        currentUserId = auth.currentUser?.uid!!
        mLocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 3F, LocationTrackerListener())
        } catch (e: Exception) {
            Log.d(TAG, "ERROR")
        }
    }


    override fun onBind(intent: Intent): IBinder? {
        Log.d("LTService", "onBind")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("LTService", "Service onStartCommand " + startId)
        return Service.START_STICKY
    }



    private inner class LocationTrackerListener: LocationListener {
        override fun onLocationChanged(location: Location?) {
            val longitude = location?.longitude
            val latitude = location?.latitude

            // push the new location to the database /users/:user_id/location
            val latLng: Map<String, Double?> = hashMapOf("latitude" to latitude, "longitude" to longitude)
            usersRef.child(currentUserId).child("location").updateChildren(latLng)
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(p0: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(p0: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}
