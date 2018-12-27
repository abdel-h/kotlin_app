package space.majid.testing.materieldestesting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_search_users.*

class SearchUsersActivity : AppCompatActivity() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var searchListAdapter: UsersListAdapter

    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    private lateinit var auth: FirebaseAuth

    var usersList: ArrayList<UserData> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_users)

        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")

        linearLayoutManager = LinearLayoutManager(this)
        search_users_list_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        searchListAdapter = UsersListAdapter(ArrayList()) { user ->
            // Handle on add friend
            // Send to notification to friend_if by adding the current
            // user ID to users/friend_id/friend_requests
            addFriend(user.userId)
        }
        search_users_list_rv.adapter = searchListAdapter
        SearchUsers.onChange { searchString ->
            getUsers(searchString, searchListAdapter)
        }
    }

    private fun getUsers(username: String, searchListAdapter: UsersListAdapter) {
        if(username.isEmpty()) {
            searchListAdapter.filterUsers(ArrayList())
        } else {
            usersRef.orderByChild("username")
                    .startAt(username)
                    .endAt(username + "\uf8ff").addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                        }
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var usersList: ArrayList<UserData> = ArrayList()
                            var childs = snapshot.children
                            childs.forEach {snap ->
                                val userId: String = snap.key.toString()
                                Log.d("onDataChange_USERID", userId)
                                snap.children.forEach {
                                    if(it.key == "username") {
                                        var username = it.getValue(true).toString()
                                        usersList.add(UserData(username, userId))
                                    }
                                }
                            }
                            searchListAdapter.filterUsers(usersList)
                        }
                    })
        }
    }

    private fun EditText.onChange(cb: (String) -> Unit) {
        this.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { cb(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }


    private fun addFriend(user: String) {
        auth = FirebaseAuth.getInstance()
        val currentUserId = auth.currentUser?.uid!!
        usersRef.child(user).child("friend_requests").setValue(currentUserId)
                .addOnSuccessListener {
                    Log.d("FIRE_BASE", "friendRequest:success")
                }
                .addOnFailureListener {
                    Log.d("FIRE_BASE", "friendRequest:failure")
                }
    }
}
