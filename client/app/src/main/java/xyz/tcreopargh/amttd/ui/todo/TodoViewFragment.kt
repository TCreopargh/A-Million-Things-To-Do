package xyz.tcreopargh.amttd.ui.todo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_work_group_share.*
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.MainActivity
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.api.data.*
import xyz.tcreopargh.amttd.api.exception.AmttdException
import xyz.tcreopargh.amttd.api.json.request.TodoEntryCrudRequest
import xyz.tcreopargh.amttd.api.json.request.TodoEntryViewRequest
import xyz.tcreopargh.amttd.api.json.response.TodoEntryCrudResponse
import xyz.tcreopargh.amttd.api.json.response.TodoEntryViewResponse
import xyz.tcreopargh.amttd.ui.FragmentOnMainActivityBase
import xyz.tcreopargh.amttd.ui.group_user.GroupUserFragment
import xyz.tcreopargh.amttd.util.*
import java.util.*

class TodoViewFragment : FragmentOnMainActivityBase(R.string.todo_view_title) {

    private lateinit var todoSwipeContainer: SwipeRefreshLayout

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

        todoSwipeContainer = view.findViewById(R.id.todoSwipeContainer)
        val adapter = TodoViewAdapter(viewModel.entries.value ?: listOf(), this)
        todoSwipeContainer.setOnRefreshListener {
            initializeItems()
        }
        todoRecyclerView.adapter = adapter
        todoRecyclerView.layoutManager = LinearLayoutManager(context)

        val emptyText = view.findViewById<TextView>(R.id.todoListEmptyText)
        viewModel.entries.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.todoList = it
                adapter.notifyDataSetChanged()
                todoSwipeContainer.isRefreshing = false
                if (it.isEmpty()) {
                    emptyText.visibility = View.VISIBLE
                    todoRecyclerView.visibility = View.GONE
                } else {
                    emptyText.visibility = View.GONE
                    todoRecyclerView.visibility = View.VISIBLE
                }
            }
        }

        viewModel.dirty.observe(viewLifecycleOwner) {
            if (it) {
                initializeItems()
                viewModel.dirty.value = false
            }
        }

        viewModel.exception.observe(viewLifecycleOwner) {
            it?.run {
                Toast.makeText(
                    context,
                    getString(R.string.error_occurred) + it.getLocalizedString(context),
                    Toast.LENGTH_SHORT
                ).show()
                todoSwipeContainer.isRefreshing = false
                viewModel.exception.value = null
            }
        }

        val mainActivity = activity as? MainActivity
        mainActivity?.viewModel?.loginRepository?.observe(mainActivity) {
            if (it != null) {
                initializeItems()
            }
        }
        initializeItems()
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments?.deepCopy()
        viewModel = ViewModelProvider(this).get(TodoViewViewModel::class.java)
        viewModel.workGroup.value = args?.getSerializable("workGroup") as? IWorkGroup
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_todo_entry, menu)
    }

    @SuppressLint("InflateParams")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // This is a bit hacky
        val viewModel =
            (mainActivity.getCurrentlyDisplayedFragment() as? TodoViewFragment)?.viewModel
        when (item.itemId) {
            R.id.actionManageUsers -> {
                val targetFragment = GroupUserFragment.newInstance().apply {
                    arguments = bundleOf(
                        "workGroup" to viewModel?.workGroup?.value
                    )
                }
                parentFragmentManager.beginTransaction().apply {
                    replace(
                        R.id.main_fragment_parent,
                        targetFragment,
                        targetFragment::class.simpleName
                    )
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    addToBackStack(null)
                    commit()
                }
                return true
            }
        }

        return false
    }

    private fun initializeItems() {
        todoSwipeContainer.isRefreshing = true
        Thread {
            val entries: List<ITodoEntry> = try {
                val uuid = viewModel.workGroup.value?.groupId ?: return@Thread
                val request = okHttpRequest("/todo")
                    .post(
                        TodoEntryViewRequest(
                            groupId = uuid,
                            userId = loggedInUser?.uuid
                        ).toJsonRequest()
                    )
                    .build()
                val response = AMTTD.okHttpClient.newCall(request).execute()
                val body = response.body?.string()
                val result: TodoEntryViewResponse =
                    gson.fromJson(body, object : TypeToken<TodoEntryViewResponse>() {}.type)
                if (result.success != true) {
                    throw AmttdException.getFromErrorCode(result.error)
                }
                result.entries ?: throw AmttdException(AmttdException.ErrorCode.INVALID_JSON)
            } catch (e: Exception) {
                Log.e(AMTTD.logTag, e.stackTraceToString())
                viewModel.exception.postValue(AmttdException.getFromException(e))
                listOf()
            }
            viewModel.postEntry(entries)
        }.start()
    }

    fun addTodoEntry() {
        AlertDialog.Builder(context).apply {
            @SuppressLint("InflateParams")
            val dialogView =
                layoutInflater.inflate(R.layout.add_todo_layout, null)
            val titleText =
                dialogView.findViewById<EditText>(R.id.addTodoTitleText)
            setView(dialogView)
            setPositiveButton(R.string.confirm) { dialog, _ ->
                object :
                    CrudTask<TodoEntryImpl, TodoEntryCrudRequest, TodoEntryCrudResponse>(
                        request = TodoEntryCrudRequest(
                            operation = CrudType.CREATE,
                            entity = TodoEntryImpl(
                                title = titleText.text.toString(),
                                creator = UserImpl(
                                    loggedInUser
                                        ?: throw AmttdException(AmttdException.ErrorCode.LOGIN_REQUIRED)
                                )
                            ),
                            workGroupId = viewModel.workGroup.value?.groupId,
                            userId = loggedInUser?.uuid
                        ),
                        path = "/todo-entry",
                        responseType = object :
                            TypeToken<TodoEntryCrudResponse>() {}.type
                    ) {
                    override fun onSuccess(entity: TodoEntryImpl?) {
                        viewModel.dirty.postValue(true)
                    }

                    override fun onFailure(e: Exception) {
                        viewModel.exception.postValue(
                            AmttdException.getFromException(
                                e
                            )
                        )
                    }
                }.start()

                dialog.dismiss()
            }
            setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
        }.create().show()
    }

}