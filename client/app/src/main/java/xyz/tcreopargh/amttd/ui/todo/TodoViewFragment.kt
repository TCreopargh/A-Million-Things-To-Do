package xyz.tcreopargh.amttd.ui.todo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.reflect.TypeToken
import okhttp3.Request
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.MainActivity
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.data.bean.response.TodoEntryViewResponse
import xyz.tcreopargh.amttd.data.interactive.ITodoEntry
import xyz.tcreopargh.amttd.ui.FragmentOnMainActivityBase
import xyz.tcreopargh.amttd.util.*
import java.util.*

class TodoViewFragment : FragmentOnMainActivityBase() {

    private lateinit var todoSwipeContainer: SwipeRefreshLayout

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

        todoSwipeContainer = view.findViewById(R.id.todoSwipeContainer)
        val adapter = TodoViewAdapter(viewModel.entries.value ?: listOf(), activity)
        todoSwipeContainer.setOnRefreshListener { initializeItems() }
        todoRecyclerView.adapter = adapter
        todoRecyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.entries.observe(viewLifecycleOwner) {
            adapter.todoList = it ?: mutableListOf()
            adapter.notifyDataSetChanged()
            todoSwipeContainer.isRefreshing = false
        }

        viewModel.exception.observe(viewLifecycleOwner) {
            it?.run {
                Toast.makeText(
                    context,
                    getString(R.string.error_occured) + it.message,
                    Toast.LENGTH_SHORT
                ).show()
                todoSwipeContainer.isRefreshing = false
            }
        }

        val mainActivity = activity as? MainActivity
        mainActivity?.viewModel?.loginRepository?.observe(mainActivity) {
            initializeItems()
        }
        initializeItems()
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupId = arguments?.get("groupId") as? UUID
        viewModel = ViewModelProvider(this).get(TodoViewViewModel::class.java)
    }

    private fun initializeItems() {
        todoSwipeContainer.isRefreshing = true
        Thread {
            val entries: List<ITodoEntry> = try {
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
                // Don't simplify this
                val result: TodoEntryViewResponse =
                    gson.fromJson(body, object : TypeToken<TodoEntryViewResponse>() {}.type)
                if (result.success != true) {
                    throw result.error ?: RuntimeException("Invalid JSON")
                }
                result.entries ?: throw RuntimeException("Invalid data")
            } catch (e: Exception) {
                Log.e(AMTTD.logTag, e.stackTraceToString())
                viewModel.exception.postValue(e)
                listOf()
            }
            viewModel.postEntry(entries.toMutableList())
        }.start()
    }

}