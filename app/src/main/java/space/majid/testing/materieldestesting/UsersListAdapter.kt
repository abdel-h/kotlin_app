package space.majid.testing.materieldestesting

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

data class UserData(val username: String, val userId: String)

class UsersListAdapter(private var dataSet: ArrayList<UserData>, private var btnText: String, private val listener: (UserData) -> Unit) : RecyclerView.Adapter<UsersListAdapter.ListViewHolder>() {
    class ListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.UserName)
        val addButton: Button = view.findViewById(R.id.addUser)
        var userId = ""


        fun bind(itemView: UserData, listener: (UserData) -> Unit) = addButton.setOnClickListener {
           listener(itemView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ListViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.activity_users_list_item, parent, false))

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.username.text = dataSet[position].username
        holder.userId = dataSet[position].userId
        holder.addButton.text = btnText
        holder.bind(dataSet[position], listener)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun filterUsers(data: ArrayList<UserData>) {
        dataSet = data
        notifyDataSetChanged()
    }
    fun addUser(user: UserData) {
        dataSet.add(user)
        notifyDataSetChanged()
    }

}
