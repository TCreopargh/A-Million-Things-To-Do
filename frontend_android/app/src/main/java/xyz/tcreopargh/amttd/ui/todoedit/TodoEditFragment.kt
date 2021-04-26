package xyz.tcreopargh.amttd.ui.todoedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import xyz.tcreopargh.amttd.MainActivity
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.data.todo.Status
import xyz.tcreopargh.amttd.data.todo.Task
import xyz.tcreopargh.amttd.data.todo.TodoEntry
import xyz.tcreopargh.amttd.util.format
import java.util.*

class TodoEditFragment : Fragment() {

    companion object {
        fun newInstance() = TodoEditFragment()
    }

    private lateinit var viewModel: TodoEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.todo_edit_fragment, container, false)
        initView(view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TodoEditViewModel::class.java)
        initializeItems()
    }

    // TODO: Replace with actual data
    private fun initializeItems() {
        viewModel.entry.value = TodoEntry(
            (activity as? MainActivity)?.loggedInUser!!,
            "Complete essay",
            "Get your essay about the research done!",
            tasks = mutableListOf(
                Task("123"),
                Task("456", true)
            ),
            deadline = Calendar.getInstance().apply {
                set(Calendar.MONTH, get(Calendar.MONTH) + 1)
            },
            status = Status.IN_PROGRESS
        )
    }

    private fun initView(viewRoot: View) {
        val entry = viewModel.entry.value
        if (entry == null) {
            Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show()
            activity?.onBackPressed()
            return
        }
        viewRoot.apply {
            findViewById<EditText>(R.id.todoEditTitleText).setText(
                entry.title,
                TextView.BufferType.EDITABLE
            )
            findViewById<EditText>(R.id.todoEditDescriptionText)?.setText(
                entry.description,
                TextView.BufferType.EDITABLE
            )
            findViewById<TextView>(R.id.todoEditStatusText)?.text = entry.status.getDisplayString()
            findViewById<TextView>(R.id.todoEditDeadlineText)?.text =
                entry.deadline?.format() ?: getString(R.string.deadline_not_set)
            findViewById<ImageView>(R.id.todoEditIconColor)?.setColorFilter(
                entry.status.color,
                android.graphics.PorterDuff.Mode.SRC
            )
        }
    }

}