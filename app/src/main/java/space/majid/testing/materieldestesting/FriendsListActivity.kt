package space.majid.testing.materieldestesting

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_friends_list.*
import org.jetbrains.anko.startActivity

class FriendsListActivity : AppCompatActivity() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var friendsListAdapter: UsersListAdapter
    private lateinit var pendingFriendsListAdapter: UsersListAdapter

    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var currentUserRef: DatabaseReference

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserId: String

    private var permissionsRequestCode = 110
    private var permissionsGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")

        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser?.uid!!
        currentUserRef = database.getReference("users/$currentUserId")

        //  Ask for GPS permission
        setupPermissions()
        //  Start LocationTrackerService
        intent = Intent(this, LocationTrackerService::class.java)
        startService(intent)

        linearLayoutManager = LinearLayoutManager(this)
        pending_friends_list_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        friends_list_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        friendsListAdapter = UsersListAdapter(ArrayList()) {
            // invite to share location
            // sendShareLocationInvite(currentUserId, it.userId)
            // open FindUserActivity with user info
            startActivity<FindUserActivity>("userId" to it.userId, "username" to it.username)
        }
        pendingFriendsListAdapter = UsersListAdapter(ArrayList()) {
            // accept friend request
            updateFriendStatus(it.userId, "approved")
            startActivity<FriendsListActivity>()
        }
        pending_friends_list_rv.adapter = pendingFriendsListAdapter
        friends_list_rv.adapter = friendsListAdapter

        getFriendsByStatus("pending") {
            pendingFriendsListAdapter.addUser(it)
        }
        getFriendsByStatus("approved") {
            friendsListAdapter.addUser(it)
        }
    }
    fun getFriendsByStatus(status: String, reslistener: (UserData) -> Unit) {
        auth = FirebaseAuth.getInstance()
        val pendingFriendsListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { child ->
                    getUserById(child.key!!) {
                        reslistener(it)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        }
        currentUserRef
                .child("friends")
                .orderByChild("status")
                .equalTo(status)
                .addListenerForSingleValueEvent(pendingFriendsListener)
    }

    fun getUserById(userId: String, resultsListener: (UserData) -> Unit) {
        var user: UserData
        usersRef.child(userId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    if(it.key == "username") {
                        user = UserData(it.value.toString(), userId)
                        resultsListener(user)
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    fun updateFriendStatus(userId: String, newStatus: String) {
        val status: Map<String, String> = hashMapOf("status" to newStatus)
        currentUserRef.child("friends").child(userId).setValue(status)
        usersRef.child(userId).child("friends").child(currentUserId).child("status").setValue("approved")
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
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
