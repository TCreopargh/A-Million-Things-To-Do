package xyz.tcreopargh.amttd.ui.todo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.api.data.ITodoEntry
import xyz.tcreopargh.amttd.ui.todoedit.TodoEditFragment
import xyz.tcreopargh.amttd.util.format

/**
 * @author TCreopargh
 */
class TodoViewAdapter(
    var todoList: List<ITodoEntry> = mutableListOf(),
    private val fragment: TodoViewFragment
) : RecyclerView.Adapter<TodoViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val todoNameText: TextView = view.findViewById(R.id.todoNameText)
        val todoTimeText: TextView = view.findViewById(R.id.todoTimeText)
        val todoTaskText: TextView = view.findViewById(R.id.todoTaskText)
        val todoBarColor: View = view.findViewById(R.id.todoBarColor)
        val todoIconColor: ImageView = view.findViewById(R.id.todoIconColor)
        val todoStatusText: TextView = view.findViewById(R.id.todoStatusText)
        val todoCard: CardView = view.findViewById(R.id.todoCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.todo_view_item, parent, false)
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = todoList[position]
        holder.apply {
            todoNameText.text = item.title
            todoTimeText.text = item.timeCreated.format()
            todoTaskText.text = "${item.completedTasks.size} / ${item.tasks.size}"
            todoStatusText.text = item.status.getDisplayString()
            todoBarColor.setBackgroundColor(item.status.color)
            todoIconColor.setColorFilter(item.status.color, android.graphics.PorterDuff.Mode.SRC)
            todoCard.setOnClickListener {
                val fragmentManager = fragment.activity?.supportFragmentManager
                val targetFragment = TodoEditFragment.newInstance().apply {
                    arguments = bundleOf("entryId" to item.entryId.toString())
                }
                fragmentManager?.beginTransaction()?.apply {
                    replace(
                        R.id.main_fragment_parent,
                        targetFragment,
                        targetFragment::class.simpleName
                    )
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }

    override fun getItemCount(): Int = todoList.size


}