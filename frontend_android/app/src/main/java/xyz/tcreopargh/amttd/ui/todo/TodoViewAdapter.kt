package xyz.tcreopargh.amttd.ui.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.data.todo.TodoEntry
import xyz.tcreopargh.amttd.util.format

/**
 * @author TCreopargh
 */
class TodoViewAdapter(
    private val todoData: LiveData<MutableList<TodoEntry>>,
    private val activity: FragmentActivity?
) : RecyclerView.Adapter<TodoViewAdapter.ViewHolder>() {

    private val todoList
        get() = todoData.value ?: mutableListOf()

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = todoList[position]
        holder.apply {
            todoNameText.text = item.title
            todoTimeText.text = item.timeCreated.format()
            todoTaskText.text = item.getCompletionText()
            todoStatusText.text = item.status.getDisplayString()
            todoBarColor.setBackgroundColor(item.status.color)
            todoIconColor.setColorFilter(item.status.color, android.graphics.PorterDuff.Mode.SRC)

        }
    }

    override fun getItemCount(): Int = todoList.size


}