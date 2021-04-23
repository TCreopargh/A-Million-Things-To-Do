package xyz.tcreopargh.amttd.ui.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.tcreopargh.amttd.MainActivity
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.data.todo.Status
import xyz.tcreopargh.amttd.data.todo.Task
import xyz.tcreopargh.amttd.data.todo.TodoEntry

class TodoViewFragment : Fragment() {

    companion object {
        fun newInstance() = TodoViewFragment()
    }

    private lateinit var viewModel: TodoViewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.todo_view_fragment, container, false)
        val todoRecyclerView = view.findViewById<RecyclerView>(R.id.todoRecyclerView)
        viewModel.entries.value?.add(
            TodoEntry(
                (activity as? MainActivity)?.loggedInUser!!,
                "Wash clothes",
                "123456"
            )
        )

        viewModel.entries.value?.add(
            TodoEntry(
                (activity as? MainActivity)?.loggedInUser!!,
                "Do homework",
                "123456",
                tasks = mutableListOf(
                    Task("123", true),
                    Task("123", true),
                    Task("123", true),
                    Task("123", true)
                ),
                status = Status.COMPLETED
            )
        )

        viewModel.entries.value?.add(
            TodoEntry(
                (activity as? MainActivity)?.loggedInUser!!,
                "Buy groceries",
                "123456",
                tasks = mutableListOf(
                    Task("123"),
                    Task("123", true),
                    Task("123")
                ),
                status = Status.ON_HOLD
            )
        )

        viewModel.entries.value?.add(
            TodoEntry(
                (activity as? MainActivity)?.loggedInUser!!,
                "Complete essay",
                "123456",
                tasks = mutableListOf(
                    Task("123"),
                    Task("123", true)
                ),
                status = Status.IN_PROGRESS
            )
        )
        val adapter = TodoViewAdapter(viewModel.entries, activity)
        todoRecyclerView.adapter = adapter
        todoRecyclerView.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TodoViewViewModel::class.java)
    }

}