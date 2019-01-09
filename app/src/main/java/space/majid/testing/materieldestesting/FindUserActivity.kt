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

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_user)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")
        val currentUserId = auth.currentUser?.uid!!
        val inviteUserId = intent.getStringExtra("userId")


        currentLocationMarkerOptions = MarkerOptions()
        remoteCurrentLocationMarkerOptions = MarkerOptions()

        // Show markers on the map
        currentUserLocation()
        friendLocationChangesListener(inviteUserId)
    }

    override fun onMapReady(gmap: GoogleMap?) {
        map = gmap
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
    private fun currentUserLocation() {
        val currentUserId = auth.currentUser?.uid!!
        val locationRef = database.getReference("users/$currentUserId/location")
        val locationListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val latlng = snapshot.children
                lateinit var latitude: String
                lateinit var longitude: String
                latlng.forEach {
                    if (it.key == "latitude") {
                        latitude = it.value.toString()
                    } else if (it.key == "longitude") {
                        longitude = it.value.toString()
                    }
                }
                if (map != null) {
                    val latLng = LatLng(latitude.toDouble(), longitude.toDouble())
                    if (!::currentLocationMarker.isInitialized) {
                        currentLocationMarker = map!!.addMarker(MarkerOptions().position(latLng))
                    } else {
                        currentLocationMarker.position = latLng
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        locationRef.addValueEventListener(locationListener)
    }
}
