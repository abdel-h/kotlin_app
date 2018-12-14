package space.majid.testing.materieldestesting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_search_users.*

class SearchUsersActivity : AppCompatActivity() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var searchListAdapter: UsersListAdapter

    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    var usersList: ArrayList<UserData> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_users)

        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference()

        linearLayoutManager = LinearLayoutManager(this)
        search_users_list_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        var usersSearchResults: List<UserData>
        searchListAdapter = UsersListAdapter(ArrayList())
        search_users_list_rv.adapter = searchListAdapter
        SearchUsers.onChange { searchString ->
            usersSearchResults = usersList.filter {
                it.username.contains(searchString)
            }
            searchListAdapter.filterUsers(usersSearchResults as ArrayList<UserData>)
            getUsers(searchString)
        }
    }

    private fun getUsers(username: String) {
        // Grab Users From FireBase
        usersList.clear()
        usersRef.child("users").orderByChild("username").startAt(username).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                var childs = snapshot.children
                childs.forEach {snap ->
                    val userId: String = snap.key.toString()

                    snap.children.forEach {
                        if(it.key == "username") {
                            var username = it.getValue(true).toString()
                            usersList.add(UserData(username, userId))
                        }
                    }
                }
            }
        })


    }

    private fun EditText.onChange(cb: (String) -> Unit) {
        this.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { cb(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
