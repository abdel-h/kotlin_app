package space.majid.testing.materieldestesting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_friends_list.*

class FriendsListActivity : AppCompatActivity() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var friendsListAdapter: UsersListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)
        linearLayoutManager = LinearLayoutManager(this)
        friends_list_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        friendsListAdapter = UsersListAdapter(getFriends()) {
            Log.d("getFriends", it.username)
        }
        friends_list_rv.adapter = friendsListAdapter
    }

    fun getFriends() : ArrayList<UserData> {
        val fl = ArrayList<UserData>()
        fl.add(UserData("username1", "userID"))
        fl.add(UserData("username2", "userID"))
        fl.add(UserData("username3", "userID"))
        return fl
    }
}
