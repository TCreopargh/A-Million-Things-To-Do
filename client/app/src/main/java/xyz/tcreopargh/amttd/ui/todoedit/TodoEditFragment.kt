package xyz.tcreopargh.amttd.ui.todoedit

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.todo_view_fragment.*
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.bean.request.TodoEntryCrudRequest
import xyz.tcreopargh.amttd.common.bean.response.TodoEntryCrudResponse
import xyz.tcreopargh.amttd.common.data.CrudType
import xyz.tcreopargh.amttd.common.data.ITodoEntry
import xyz.tcreopargh.amttd.common.data.TodoEntryImpl
import xyz.tcreopargh.amttd.common.data.action.ActionGeneric
import xyz.tcreopargh.amttd.common.exception.AmttdException
import xyz.tcreopargh.amttd.ui.FragmentOnMainActivityBase
import xyz.tcreopargh.amttd.util.CrudTask
import xyz.tcreopargh.amttd.util.format
import xyz.tcreopargh.amttd.util.setColor
import java.util.*

class TodoEditFragment : FragmentOnMainActivityBase() {

    companion object {
        fun newInstance() = TodoEditFragment()
    }

    private lateinit var viewModel: TodoEditViewModel

    private lateinit var todoEditSwipeContainer: SwipeRefreshLayout

    private var entryId: UUID? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.todo_edit_fragment, container, false)
        viewModel.entry.observe(viewLifecycleOwner) {
            initView(view, it)
            todoEditSwipeContainer.isRefreshing = false
        }

        todoEditSwipeContainer = view.findViewById(R.id.todoEditSwipeContainer)

        todoEditSwipeContainer.setOnRefreshListener {
            initializeItems()
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
        initializeItems()

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TodoEditViewModel::class.java)
        entryId = arguments?.get("entryId") as? UUID
    }

    private fun initializeItems() {
        todoEditSwipeContainer.isRefreshing = true
        val uuid = entryId ?: return
        object : CrudTask<TodoEntryImpl, TodoEntryCrudRequest, TodoEntryCrudResponse>(
            request = TodoEntryCrudRequest(
                CrudType.READ,
                TodoEntryImpl(entryId = uuid)
            ),
            path = "/todo-entry",
            responseType = object : TypeToken<TodoEntryCrudResponse>() {}.type
        ) {
            override fun onSuccess(entity: TodoEntryImpl?) {
                viewModel.entry.postValue(entity)
            }

            override fun onFailure(e: Exception) {
                viewModel.exception.postValue(AmttdException.getFromException(e))
            }
        }.execute()
    }

    @SuppressLint("InflateParams")
    private fun initView(viewRoot: View, entry: ITodoEntry?) {
        if (entry == null) {
            Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show()
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
            val tasks = findViewById<LinearLayout>(R.id.todoTaskItemView)
            val actions = findViewById<LinearLayout>(R.id.actionHistoryLayout)
            for (task in entry.tasks) {
                val itemView = layoutInflater.inflate(R.layout.task_view_item, null)
                itemView.apply {
                    findViewById<CheckBox>(R.id.taskCheckbox).apply {
                        text = task.name
                        isChecked = task.completed
                    }
                    findViewById<ImageButton>(R.id.taskDeleteButton).apply {
                        // TODO: Implement task deletion
                        setOnClickListener(null)
                    }
                }
                tasks.addView(itemView)
            }
            for (actionGeneric in entry.actionHistory) {
                val action = ActionGeneric(actionGeneric).action
                val itemView = layoutInflater.inflate(R.layout.action_view_item, null)
                itemView.apply {
                    findViewById<TextView>(R.id.actionText).apply {
                        text = TextUtils.concat(
                            action.getDisplayText(), " ",
                            SpannableString("@").setColor(context.getColor(R.color.primary)),
                            " ",
                            SpannableString(action.timeCreated.format()).setColor(context.getColor(R.color.secondary))
                        ) as Spanned
                    }
                }
                actions.addView(itemView)
            }
        }
    }

}