package xyz.tcreopargh.amttd.ui.todo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_work_group_share.*
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.MainActivity
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.bean.request.TodoEntryViewRequest
import xyz.tcreopargh.amttd.common.bean.response.TodoEntryViewResponse
import xyz.tcreopargh.amttd.common.data.ITodoEntry
import xyz.tcreopargh.amttd.common.exception.AmttdException
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
                    getString(R.string.error_occurred) + it.getLocalizedString(context),
                    Toast.LENGTH_SHORT
                ).show()
                todoSwipeContainer.isRefreshing = false
                viewModel.exception.value = null
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
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_todoentry, menu)
    }

    @SuppressLint("InflateParams")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionShareWorkGroup -> {
                val builder = AlertDialog.Builder(context).apply {
                    val rootView =
                        layoutInflater.inflate(R.layout.share_workgroup_dialog, null)?.apply {
                            val expirationTimeText = findViewById<TextView>(R.id.expirationTimeText)
                            var days = 1
                            val seekbar = findViewById<SeekBar>(R.id.expirationTimeSeekBar)?.apply {
                                val values: IntArray =
                                    context.resources.getIntArray(R.array.expiration_time_values)
                                max = values.size - 1
                                setOnSeekBarChangeListener(object :
                                    SeekBar.OnSeekBarChangeListener {
                                    @SuppressLint("SetTextI18n")
                                    override fun onProgressChanged(
                                        seekBar: SeekBar?,
                                        progress: Int,
                                        fromUser: Boolean
                                    ) {
                                        days = values[progress]
                                        val quantifier =
                                            if (days <= 1) getString(R.string.day) else getString(
                                                R.string.days
                                            )
                                        expirationTimeText.text = "$days$quantifier"
                                    }

                                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                                    }

                                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                                    }

                                })
                            }
                        }
                    setView(rootView)
                    setPositiveButton(R.string.confirm) { dialog, _ ->
                        dialog.dismiss()
                    }
                    setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                }.create().show()
                return true
            }
        }

        return false
    }

    private fun initializeItems() {
        todoSwipeContainer.isRefreshing = true
        Thread {
            val entries: List<ITodoEntry> = try {
                val uuid = groupId ?: return@Thread
                val request = okHttpRequest("/todo")
                    .post(
                        TodoEntryViewRequest(groupId = uuid).toJsonRequest()
                    )
                    .build()
                val response = AMTTD.okHttpClient.newCall(request).execute()
                val body = response.body?.string()
                // Don't simplify this
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
            viewModel.postEntry(entries.toMutableList())
        }.start()
    }

}