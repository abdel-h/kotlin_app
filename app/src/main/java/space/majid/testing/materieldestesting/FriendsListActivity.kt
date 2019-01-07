package space.majid.testing.materieldestesting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")

        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser?.uid!!
        currentUserRef = database.getReference("users/$currentUserId")

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



}
