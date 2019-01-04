package space.majid.testing.materieldestesting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_friends_list.*

class FriendsListActivity : AppCompatActivity() {

    lateinit var friendsListAdapter: UsersListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)
        val friends = getFriends()
        friendsListAdapter = UsersListAdapter(friends) {
            Log.d("getFriends", it.username)
        }
        friends_list_rv.adapter = friendsListAdapter
    }

    fun getFriends() : ArrayList<UserData> {
        var fl = ArrayList<UserData>()
        fl.add(UserData("username1", "userID"))
        fl.add(UserData("username2", "userID"))
        fl.add(UserData("username3", "userID"))

        Log.d("getFriends", fl[0].username)
        return fl
    }
}
