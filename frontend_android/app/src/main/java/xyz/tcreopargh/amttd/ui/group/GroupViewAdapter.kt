package xyz.tcreopargh.amttd.ui.group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.data.group.WorkGroup
import xyz.tcreopargh.amttd.ui.todo.TodoViewFragment
import xyz.tcreopargh.amttd.util.format
import java.text.DateFormat

/**
 * @author TCreopargh
 */
class GroupViewAdapter(
    private val workGroupsData: LiveData<MutableList<WorkGroup>>,
    private val activity: FragmentActivity?
) : RecyclerView.Adapter<GroupViewAdapter.ViewHolder>() {

    private val workGroups
        get() = workGroupsData.value ?: mutableListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupNameText: TextView = view.findViewById(R.id.groupNameText)
        val groupTimeText: TextView = view.findViewById(R.id.groupTimeText)
        val groupUserCountText: TextView = view.findViewById(R.id.groupUserCountText)
        val groupCard: CardView = view.findViewById(R.id.groupCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.group_view_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val workGroup = workGroups[position]
        holder.apply {
            groupNameText.text = workGroup.name
            groupUserCountText.text = workGroup.users.size.toString()
            groupTimeText.text = workGroup.timeCreated.format()
            groupCard.setOnClickListener {
                val fragmentManager = activity?.supportFragmentManager
                fragmentManager?.beginTransaction()?.apply {
                    replace(R.id.main_fragment_parent, TodoViewFragment::class.java, null)
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }

    override fun getItemCount(): Int = workGroups.size
}