package xyz.tcreopargh.amttd.ui.group_user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.data.IUser

/**
 * @author TCreopargh
 */
class GroupUserAdapter(
    var users: List<IUser>,
    private val fragment: GroupUserFragment
) : RecyclerView.Adapter<GroupUserAdapter.ViewHolder>() {

    val mainActivity
        get() = fragment.mainActivity

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userAvatarView: ImageView = view.findViewById(R.id.userAvatarView)
        val usernameView: TextView = view.findViewById(R.id.usernameText)
        val userEmailView: TextView = view.findViewById(R.id.userEmailText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.group_user_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.apply {
            usernameView.text = user.username
            userEmailView.text = user.email
        }
    }

    override fun getItemCount(): Int = users.size
}