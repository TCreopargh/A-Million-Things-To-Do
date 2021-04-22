package xyz.tcreopargh.amttd.ui.group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.data.group.WorkGroup
import java.text.DateFormat

/**
 * @author TCreopargh
 */
class GroupViewAdapter(
    private val workGroupsData: LiveData<MutableList<WorkGroup>>
) : RecyclerView.Adapter<GroupViewAdapter.ViewHolder>() {

    private val workGroups
        get() = workGroupsData.value ?: mutableListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupNameText: TextView = view.findViewById(R.id.groupNameText)
        val groupTimeText: TextView = view.findViewById(R.id.groupTimeText)
        val groupUserCountText: TextView = view.findViewById(R.id.groupUserCountText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.group_view_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val workGroup = workGroups[position]
        holder.groupNameText.text = workGroup.name
        holder.groupUserCountText.text = workGroup.users.size.toString()
        holder.groupTimeText.text = DateFormat.getDateInstance(
            DateFormat.MEDIUM,
            holder.groupNameText.context.resources.configuration.locales.get(0)
        ).format(workGroup.timeCreated.time)
    }

    override fun getItemCount(): Int = workGroups.size
}