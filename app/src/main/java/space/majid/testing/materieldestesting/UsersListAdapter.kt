package space.majid.testing.materieldestesting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class UserData(val username: String, val userId: String)

class UsersListAdapter(private var dataSet: ArrayList<UserData>) : RecyclerView.Adapter<UsersListAdapter.ListViewHolder>() {
    class ListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.UserName)
        var userId = ""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.activity_users_list_item, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.username.text = dataSet[position].username
        holder.userId = dataSet[position].userId
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun filterUsers(data: ArrayList<UserData>) {
        dataSet = data
        notifyDataSetChanged()
    }
}
