package xyz.tcreopargh.amttd.ui.todo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.reflect.TypeToken
import okhttp3.Request
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.MainActivity
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.data.interactive.ITodoEntry
import xyz.tcreopargh.amttd.data.interactive.TodoEntryImpl
import xyz.tcreopargh.amttd.util.*
import java.util.*

class TodoViewFragment : Fragment() {

    companion object {
        fun newInstance() = TodoViewFragment()
    }

    private lateinit var viewModel: TodoViewViewModel

    private var groupId: UUID? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.todo_view_fragment, container, false)
        val todoRecyclerView = view.findViewById<RecyclerView>(R.id.todoRecyclerView)

        val todoSwipeContainer = view.findViewById<SwipeRefreshLayout>(R.id.todoSwipeContainer)
        val adapter = TodoViewAdapter(viewModel.entries.value ?: listOf(), activity)
        todoSwipeContainer.setOnRefreshListener { initializeItems() }
        todoRecyclerView.adapter = adapter
        todoRecyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.entries.observe(viewLifecycleOwner) {
            adapter.todoList = it ?: mutableListOf()
            adapter.notifyDataSetChanged()
            todoSwipeContainer.isRefreshing = false
        }

        val mainActivity = activity as? MainActivity
        mainActivity?.viewModel?.loginRepository?.observe(mainActivity) {
            initializeItems()
        }
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupId = arguments?.get("groupId") as? UUID
        viewModel = ViewModelProvider(this).get(TodoViewViewModel::class.java)
        initializeItems()
    }

    private fun initializeItems() {
        Thread {
            val uuid = groupId ?: return@Thread
            val request = Request.Builder()
                .post(
                    jsonObjectOf(
                        "groupId" to uuid
                    ).toRequestBody()
                ).url(rootUrl.withPath("/todo"))
                .build()
            val response = AMTTD.okHttpClient.newCall(request).execute()
            val body = response.body?.string()
            val entries: List<ITodoEntry> = try {
                gson.fromJson(body, object : TypeToken<List<TodoEntryImpl>>() {}.type)
            } catch (e: RuntimeException) {
                Log.e(AMTTD.logTag, e.stackTraceToString())
                listOf()
            }
            viewModel.postEntry(entries.toMutableList())
        }.start()
    }

}