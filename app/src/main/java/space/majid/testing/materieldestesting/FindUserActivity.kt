package space.majid.testing.materieldestesting


import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FindUserActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG: String = "LTServiceF"
    private var map: GoogleMap? = null
    private lateinit var currentLocationMarkerOptions: MarkerOptions
    private lateinit var currentLocationMarker: Marker

    private lateinit var remoteCurrentLocationMarkerOptions: MarkerOptions
    private lateinit var remoteCurrentLocationMarker: Marker


    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    private var permissionsRequestCode = 110
    private var permissionsGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_user)
        setupPermissions()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")
        val currentUserId = auth.currentUser?.uid!!
        val inviteUserId = intent.getStringExtra("userId")

        val sendInvite: MaterialButton = findViewById(R.id.send_invite)
        sendInvite.setOnClickListener {
            sendLocationInvite(currentUserId, inviteUserId)
        }

        currentLocationMarkerOptions = MarkerOptions()
        remoteCurrentLocationMarkerOptions = MarkerOptions()

        intent = Intent(this, LocationTrackerService::class.java)
        startService(intent)
        friendLocationChangesListener(inviteUserId)

        broadcastReceiver = object: BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val longitude = intent?.getDoubleExtra("longitude", 0.0)
                    val latitude = intent?.getDoubleExtra("latitude", 0.0)
                    val latLng = LatLng(latitude!!, longitude!!)
                    Log.d(TAG, "Long: $longitude")
                    Log.d(TAG, "Lat: $latitude")
                    if(map != null) {
                        currentLocationMarkerOptions.position(latLng)
                        if(!::currentLocationMarker.isInitialized) {
                            currentLocationMarker = map!!.addMarker(MarkerOptions().position(latLng))
                        } else {
                            currentLocationMarker.position = latLng
                        }
                        map!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                        // push the new location to the database /users/:user_id/location
                        val latLng: Map<String, Double> = hashMapOf("latitude" to latitude, "longitude" to longitude)
                        usersRef.child(currentUserId).child("location").updateChildren(latLng)
                    }
                }

            }
        registerReceiver(broadcastReceiver, IntentFilter("locationChanged"))
    }

    override fun onResume() {
        super.onResume()
        if(broadcastReceiver == null) {
            broadcastReceiver = object: BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val longitude = intent?.getStringExtra("longitude")
                    val latitude = intent?.getStringExtra("latitude")
                }
            }
            registerReceiver(broadcastReceiver, IntentFilter("locationChanged"))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver)
        }
    }

    override fun onMapReady(gmap: GoogleMap?) {
        map = gmap
    }

    private inner class LocationBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val longitude = intent?.getStringExtra("longitude")
            val latitude = intent?.getStringExtra("latitude")
            Log.d(TAG, "Long: $longitude")
            Log.d(TAG, "Lat: $latitude")
        }
    }

    private fun friendLocationChangesListener(user_id: String) {
        val locationRef = database.getReference("users/$user_id/location")
        val locationListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val latlng = snapshot.children
                lateinit var latitude: String
                lateinit var longitude: String
                latlng.forEach{
                    if(it.key == "latitude") {
                        latitude = it.value.toString()
                    } else if(it.key == "longitude") {
                        longitude = it.value.toString()
                    }
                }
                if(map != null) {
                    val latLng = LatLng(latitude.toDouble(), longitude.toDouble())
                    if(!::remoteCurrentLocationMarker.isInitialized) {
                        remoteCurrentLocationMarker = map!!.addMarker(MarkerOptions().position(latLng))
                    } else {
                        remoteCurrentLocationMarker.position = latLng
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
        locationRef.addValueEventListener(locationListener)
    }
    fun sendLocationInvite(fromUserId: String, toUserId: String) {
        // modify users/:toUserId/invites/:from_user/pending
        // this change will trigger firebase functions to send a new notification
        usersRef.child(toUserId).child("invites").child(fromUserId).setValue("pending")
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions, permissionsRequestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            permissionsRequestCode -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // permissions denied
                } else {
                    // permissions granted
                    permissionsGranted = true
                }
            }
        }
    }
}
