package xyz.tcreopargh.amttd.ui.todoedit

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.todo_view_fragment.*
import xyz.tcreopargh.amttd.MainActivity
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.bean.request.ActionCrudRequest
import xyz.tcreopargh.amttd.common.bean.request.TodoEntryCrudRequest
import xyz.tcreopargh.amttd.common.bean.response.ActionCrudResponse
import xyz.tcreopargh.amttd.common.bean.response.TodoEntryCrudResponse
import xyz.tcreopargh.amttd.common.data.*
import xyz.tcreopargh.amttd.common.data.action.ActionGeneric
import xyz.tcreopargh.amttd.common.data.action.ActionType
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

    lateinit var viewModel: TodoEditViewModel

    private lateinit var todoEditSwipeContainer: SwipeRefreshLayout

    var entryId: UUID? = null

    private var expandActions = true

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
        viewModel.dirty.observe(viewLifecycleOwner) {
            if (it) {
                initializeItems()
                viewModel.dirty.value = false
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
            findViewById<EditText>(R.id.todoEditTitleText)?.apply {
                setText(
                    entry.title,
                    TextView.BufferType.NORMAL
                )
                inputType = InputType.TYPE_NULL
                setOnClickListener {
                    AlertDialog.Builder(context).apply {
                        @SuppressLint("InflateParams")
                        val dialogView =
                            layoutInflater.inflate(R.layout.todo_title_edit_layout, null)
                        val titleText =
                            dialogView.findViewById<EditText>(R.id.todoTitleEditTitleText)
                        entry.title.let {
                            titleText.setText(it)
                        }
                        setView(dialogView)
                        setPositiveButton(R.string.confirm) { dialog, _ ->
                            object :
                                CrudTask<ActionGeneric, ActionCrudRequest, ActionCrudResponse>(
                                    request = ActionCrudRequest(
                                        operation = CrudType.CREATE,
                                        entity = ActionGeneric(
                                            actionType = ActionType.TITLE_CHANGED,
                                            oldValue = entry.title,
                                            newValue = titleText.text.toString()
                                        ),
                                        userId = (activity as? MainActivity)?.loggedInUser?.uuid,
                                        entryId = entryId
                                    ),
                                    path = "/action",
                                    responseType = object :
                                        TypeToken<ActionCrudResponse>() {}.type
                                ) {
                                override fun onSuccess(entity: ActionGeneric?) {
                                    viewModel.dirty.postValue(true)
                                }

                                override fun onFailure(e: Exception) {
                                    viewModel.exception.postValue(
                                        AmttdException.getFromException(
                                            e
                                        )
                                    )
                                }
                            }.execute()

                            dialog.cancel()
                        }
                        setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                    }.create().show()
                }
            }
            findViewById<EditText>(R.id.todoEditDescriptionText)?.apply {
                setText(
                    entry.description,
                    TextView.BufferType.NORMAL
                )
                inputType = InputType.TYPE_NULL
                setOnClickListener {
                    AlertDialog.Builder(context).apply {
                        @SuppressLint("InflateParams")
                        val dialogView =
                            layoutInflater.inflate(R.layout.todo_description_edit_layout, null)
                        val titleText =
                            dialogView.findViewById<EditText>(R.id.todoDescriptionEditTitleText)
                        entry.description.let {
                            titleText.setText(it)
                        }
                        setView(dialogView)
                        setPositiveButton(R.string.confirm) { dialog, _ ->
                            object :
                                CrudTask<ActionGeneric, ActionCrudRequest, ActionCrudResponse>(
                                    request = ActionCrudRequest(
                                        operation = CrudType.CREATE,
                                        entity = ActionGeneric(
                                            actionType = ActionType.DESCRIPTION_CHANGED,
                                            oldValue = entry.title,
                                            newValue = titleText.text.toString()
                                        ),
                                        userId = (activity as? MainActivity)?.loggedInUser?.uuid,
                                        entryId = entryId
                                    ),
                                    path = "/action",
                                    responseType = object :
                                        TypeToken<ActionCrudResponse>() {}.type
                                ) {
                                override fun onSuccess(entity: ActionGeneric?) {
                                    viewModel.dirty.postValue(true)
                                }

                                override fun onFailure(e: Exception) {
                                    viewModel.exception.postValue(
                                        AmttdException.getFromException(
                                            e
                                        )
                                    )
                                }
                            }.execute()

                            dialog.cancel()
                        }
                        setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                    }.create().show()
                }
            }
            findViewById<Button>(R.id.addTaskButton)?.apply {
                setOnClickListener {
                    showEditTaskDialog(true)
                }
            }
            findViewById<ImageButton>(R.id.todoEditStatusEditButton)?.apply {
                setOnClickListener {
                    setOnClickListener {
                        AlertDialog.Builder(context).apply {
                            setTitle(R.string.todo_status)
                            val items =
                                TodoStatus.values().map { it.getDisplayString() }.toTypedArray()
                            setItems(items) { dialog, which ->
                                object :
                                    CrudTask<ActionGeneric, ActionCrudRequest, ActionCrudResponse>(
                                        request = ActionCrudRequest(
                                            operation = CrudType.CREATE,
                                            entity = ActionGeneric(
                                                actionType = ActionType.STATUS_CHANGED,
                                                fromStatus = entry.status,
                                                toStatus = TodoStatus.values()[which]
                                            ),
                                            userId = (activity as? MainActivity)?.loggedInUser?.uuid,
                                            entryId = entryId
                                        ),
                                        path = "/action",
                                        responseType = object :
                                            TypeToken<ActionCrudResponse>() {}.type
                                    ) {
                                    override fun onSuccess(entity: ActionGeneric?) {
                                        viewModel.dirty.postValue(true)
                                    }

                                    override fun onFailure(e: Exception) {
                                        viewModel.exception.postValue(
                                            AmttdException.getFromException(
                                                e
                                            )
                                        )
                                    }
                                }.execute()

                                dialog.cancel()
                            }
                            setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                        }.create().show()
                    }
                }
            }
            findViewById<TextView>(R.id.todoEditStatusText)?.text = entry.status.getDisplayString()
            findViewById<TextView>(R.id.todoEditDeadlineText)?.text =
                entry.deadline?.format() ?: getString(R.string.deadline_not_set)
            findViewById<ImageView>(R.id.todoEditIconColor)?.setColorFilter(
                entry.status.color,
                PorterDuff.Mode.SRC
            )
            val tasks = findViewById<LinearLayout>(R.id.todoTaskItemView)
            val actions = findViewById<LinearLayout>(R.id.actionHistoryLayout)
            findViewById<Button>(R.id.actionExpandButton)?.apply {
                text =
                    if (expandActions) context.getString(R.string.collapse) else context.getString(R.string.expand)
                setOnClickListener {
                    if (expandActions) {
                        expandActions = false
                        text = context.getString(R.string.expand)
                        actions.visibility = View.GONE
                    } else {
                        expandActions = true
                        text = context.getString(R.string.collapse)
                        actions.visibility = View.VISIBLE
                    }
                }
            }

            tasks.removeAllViewsInLayout()
            actions.removeAllViewsInLayout()
            for (task in entry.tasks) {
                val itemView = layoutInflater.inflate(R.layout.task_view_item, null)
                itemView.apply {
                    val checkbox = findViewById<CheckBox>(R.id.taskCheckbox).apply {
                        text = task.name
                        isChecked = task.completed
                        setOnCheckedChangeListener { _, isChecked ->
                            object : CrudTask<ActionGeneric, ActionCrudRequest, ActionCrudResponse>(
                                request = ActionCrudRequest(
                                    operation = CrudType.CREATE,
                                    entity = ActionGeneric(
                                        actionType = if (isChecked) {
                                            ActionType.TASK_COMPLETED
                                        } else {
                                            ActionType.TASK_UNCOMPLETED
                                        },
                                        task = TaskImpl(
                                            task
                                        )
                                    ),
                                    userId = (activity as? MainActivity)?.loggedInUser?.uuid,
                                    entryId = entryId
                                ),
                                path = "/action",
                                responseType = object : TypeToken<ActionCrudResponse>() {}.type
                            ) {
                                override fun onSuccess(entity: ActionGeneric?) {
                                    viewModel.dirty.postValue(true)
                                }

                                override fun onFailure(e: Exception) {
                                    viewModel.exception.postValue(AmttdException.getFromException(e))
                                }
                            }.execute()
                        }
                    }
                    findViewById<View>(R.id.taskPlaceholderView).apply {
                        setOnClickListener {
                            checkbox.toggle()
                        }
                    }
                    findViewById<ImageButton>(R.id.taskEditButton).apply {
                        setOnClickListener {
                            showEditTaskDialog(false, task)
                        }
                    }
                }
                tasks.addView(itemView)
            }
            for (actionGeneric in entry.actionHistory) {
                val action = ActionGeneric(actionGeneric).action
                val itemView = layoutInflater.inflate(R.layout.action_view_item, null)
                itemView.apply {
                    findViewById<TextView>(R.id.actionText).apply {
                        setCompoundDrawablesRelativeWithIntrinsicBounds(
                            ContextCompat.getDrawable(
                                context,
                                action.getImageRes()
                            )?.apply {
                                setTint(context.getColor(R.color.grey))
                            }, null, null, null
                        )
                        text = TextUtils.concat(
                            action.getDisplayText(), " ",
                            SpannableString("@").setColor(context.getColor(R.color.primary)),
                            " ",
                            SpannableString(action.timeCreated.format(false)).setColor(
                                context.getColor(
                                    R.color.secondary
                                )
                            ),
                            " ",
                            SpannableString(action.timeCreated.format(true)).setColor(
                                context.getColor(
                                    R.color.purple_200
                                )
                            )
                        ) as Spanned
                    }
                }
                actions.addView(itemView)
            }
        }
    }

    private fun showEditTaskDialog(isAdd: Boolean, task: ITask? = null) {
        AlertDialog.Builder(context).apply {
            @SuppressLint("InflateParams")
            val viewRoot = layoutInflater.inflate(R.layout.task_edit_layout, null)
            val titleText = viewRoot.findViewById<EditText>(R.id.taskEditTitleText)
            task?.name?.let {
                titleText.setText(it)
            }
            setView(viewRoot)
            setPositiveButton(R.string.confirm) { dialog, _ ->
                val name = titleText.text.toString()
                if (isAdd) {
                    object : CrudTask<ActionGeneric, ActionCrudRequest, ActionCrudResponse>(
                        request = ActionCrudRequest(
                            operation = CrudType.CREATE,
                            entity = ActionGeneric(
                                actionType = ActionType.TASK_ADDED,
                                task = TaskImpl(
                                    name = name
                                )
                            ),
                            userId = (activity as? MainActivity)?.loggedInUser?.uuid,
                            entryId = entryId
                        ),
                        path = "/action",
                        responseType = object : TypeToken<ActionCrudResponse>() {}.type
                    ) {
                        override fun onSuccess(entity: ActionGeneric?) {
                            viewModel.dirty.postValue(true)
                        }

                        override fun onFailure(e: Exception) {
                            viewModel.exception.postValue(AmttdException.getFromException(e))
                        }
                    }.execute()
                } else {
                    object : CrudTask<ActionGeneric, ActionCrudRequest, ActionCrudResponse>(
                        request = ActionCrudRequest(
                            operation = CrudType.CREATE,
                            entity = ActionGeneric(
                                actionType = ActionType.TASK_EDITED,
                                task = TaskImpl(
                                    task
                                        ?: throw AmttdException(AmttdException.ErrorCode.JSON_NON_NULLABLE_VALUE_IS_NULL)
                                ).apply {
                                    this.name = name
                                }
                            ),
                            userId = (activity as? MainActivity)?.loggedInUser?.uuid,
                            entryId = entryId
                        ),
                        path = "/action",
                        responseType = object : TypeToken<ActionCrudResponse>() {}.type
                    ) {
                        override fun onSuccess(entity: ActionGeneric?) {
                            viewModel.dirty.postValue(true)
                        }

                        override fun onFailure(e: Exception) {
                            viewModel.exception.postValue(AmttdException.getFromException(e))
                        }
                    }.execute()
                }
                dialog.cancel()
            }
            if (!isAdd) {
                setNeutralButton(R.string.remove) { dialog, _ ->
                    AlertDialog.Builder(context).apply {
                        setTitle(R.string.remove_task)
                        setMessage(R.string.remove_task_confirm)
                        setPositiveButton(R.string.confirm) { dialogInner, _ ->
                            object : CrudTask<ActionGeneric, ActionCrudRequest, ActionCrudResponse>(
                                request = ActionCrudRequest(
                                    operation = CrudType.CREATE,
                                    entity = ActionGeneric(
                                        actionType = ActionType.TASK_REMOVED,
                                        task = TaskImpl(
                                            task
                                                ?: throw AmttdException(AmttdException.ErrorCode.JSON_NON_NULLABLE_VALUE_IS_NULL)
                                        )
                                    ),
                                    userId = (activity as? MainActivity)?.loggedInUser?.uuid,
                                    entryId = entryId
                                ),
                                path = "/action",
                                responseType = object : TypeToken<ActionCrudResponse>() {}.type
                            ) {
                                override fun onSuccess(entity: ActionGeneric?) {
                                    viewModel.dirty.postValue(true)
                                }

                                override fun onFailure(e: Exception) {
                                    viewModel.exception.postValue(AmttdException.getFromException(e))
                                }
                            }.execute()
                            dialog.cancel()
                            dialogInner.cancel()
                        }
                        setNegativeButton(R.string.cancel) { dialogInner, _ -> dialogInner.cancel() }
                    }.create().show()

                }
            }
            setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
        }.create().show()
    }
}