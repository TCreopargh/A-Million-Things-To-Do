package xyz.tcreopargh.amttd.ui.todoedit

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.todo_view_fragment.*
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.bean.request.ActionCrudRequest
import xyz.tcreopargh.amttd.common.bean.request.TodoEntryCrudRequest
import xyz.tcreopargh.amttd.common.bean.response.ActionCrudResponse
import xyz.tcreopargh.amttd.common.bean.response.TodoEntryCrudResponse
import xyz.tcreopargh.amttd.common.data.*
import xyz.tcreopargh.amttd.common.data.action.ActionDeadlineChanged
import xyz.tcreopargh.amttd.common.data.action.ActionGeneric
import xyz.tcreopargh.amttd.common.data.action.ActionType
import xyz.tcreopargh.amttd.common.exception.AmttdException
import xyz.tcreopargh.amttd.ui.FragmentOnMainActivityBase
import xyz.tcreopargh.amttd.util.CrudTask
import xyz.tcreopargh.amttd.util.format
import xyz.tcreopargh.amttd.util.setColor
import java.util.*

/**
 * This fragment allows the user to view or edit an to-do entry.
 *
 * An action list is displayed to show the history of action performed on this entry.
 */
class TodoEditFragment : FragmentOnMainActivityBase(R.string.todo_edit_title) {

    companion object {
        fun newInstance() = TodoEditFragment()
    }

    lateinit var viewModel: TodoEditViewModel

    private lateinit var todoEditSwipeContainer: SwipeRefreshLayout

    private var expandActions = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.todo_edit_fragment, container, false)
        viewModel.entry.observe(viewLifecycleOwner) {
            if (it != null) {
                initView(view, it)
                todoEditSwipeContainer.isRefreshing = false
            }
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

    private fun deleteEntry() {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.delete_todo_entry)
            setMessage(getString(R.string.delete_entry_confirm))
            setPositiveButton(R.string.confirm) { dialog, _ ->
                object :
                    CrudTask<TodoEntryImpl, TodoEntryCrudRequest, TodoEntryCrudResponse>(
                        request = TodoEntryCrudRequest(
                            operation = CrudType.DELETE,
                            entity = TodoEntryImpl(
                                viewModel.entry.value ?: throw AmttdException(
                                    AmttdException.ErrorCode.REQUESTED_ENTITY_INVALID
                                )
                            ),
                            userId = loggedInUser?.uuid
                        ),
                        path = "/todo-entry",
                        responseType = object :
                            TypeToken<ActionCrudResponse>() {}.type
                    ) {
                    override fun onSuccess(entity: TodoEntryImpl?) {
                        activity?.onBackPressed()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TodoEditViewModel::class.java)
        val args = arguments?.deepCopy()
        viewModel.entryId.value = UUID.fromString(args?.get("entryId").toString())
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_todo_edit, menu)
    }

    @SuppressLint("InflateParams")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionDeleteEntry -> {
                deleteEntry()
                return true
            }
        }

        return false
    }

    private fun initializeItems() {
        todoEditSwipeContainer.isRefreshing = true
        val uuid = viewModel.entryId.value ?: return
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
        }.start()
    }

    @SuppressLint("InflateParams")
    private fun initView(viewRoot: View, entry: ITodoEntry) {
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
                                        userId = loggedInUser?.uuid,
                                        entryId = viewModel.entryId.value
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
                            }.start()

                            dialog.dismiss()
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
                                        userId = loggedInUser?.uuid,
                                        entryId = viewModel.entryId.value
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
                            }.start()

                            dialog.dismiss()
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
                                            userId = loggedInUser?.uuid,
                                            entryId = viewModel.entryId.value
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
                                }.start()

                                dialog.dismiss()
                            }
                            setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                        }.create().show()
                    }
                }
            }
            findViewById<TextView>(R.id.todoEditStatusText)?.text = entry.status.getDisplayString()
            findViewById<TextView>(R.id.todoEditDeadlineText)?.text =
                entry.deadline?.run { format() + " " + format(true) }
                    ?: getString(R.string.deadline_not_set)
            findViewById<ImageView>(R.id.todoEditIconColor)?.setColorFilter(
                entry.status.color,
                PorterDuff.Mode.SRC
            )
            findViewById<ImageButton>(R.id.todoEditDeadlineEditButton)?.setOnClickListener {
                setDeadline()
            }
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
                                    userId = loggedInUser?.uuid,
                                    entryId = viewModel.entryId.value
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
                            }.start()
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
            val oldName = task?.name
            if (isAdd) {
                viewRoot.findViewById<TextView>(R.id.taskEditDialogTitle).setText(R.string.add_task)
            }
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
                            userId = loggedInUser?.uuid,
                            entryId = viewModel.entryId.value
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
                    }.start()
                } else {
                    object : CrudTask<ActionGeneric, ActionCrudRequest, ActionCrudResponse>(
                        request = ActionCrudRequest(
                            operation = CrudType.CREATE,
                            entity = ActionGeneric(
                                actionType = ActionType.TASK_EDITED,
                                oldValue = oldName,
                                newValue = task?.name,
                                task = TaskImpl(
                                    task
                                        ?: throw AmttdException(AmttdException.ErrorCode.JSON_NON_NULLABLE_VALUE_IS_NULL)
                                ).apply {
                                    this.name = name
                                }
                            ),
                            userId = loggedInUser?.uuid,
                            entryId = viewModel.entryId.value
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
                    }.start()
                }
                dialog.dismiss()
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
                                    userId = loggedInUser?.uuid,
                                    entryId = viewModel.entryId.value
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
                            }.start()
                            dialog.dismiss()
                            dialogInner.dismiss()
                        }
                        setNegativeButton(R.string.cancel) { dialogInner, _ -> dialogInner.cancel() }
                    }.create().show()

                }
            }
            setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
        }.create().show()
    }

    private fun setDeadline() {
        val currentDate = Calendar.getInstance()
        val date: Calendar = Calendar.getInstance()
        DatePickerDialog(
            context ?: return, { _, year, monthOfYear, dayOfMonth ->
                date.set(year, monthOfYear, dayOfMonth)
                TimePickerDialog(
                    context, { _, hourOfDay, minute ->
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        date.set(Calendar.MINUTE, minute)

                        object : CrudTask<ActionGeneric, ActionCrudRequest, ActionCrudResponse>(
                            request = ActionCrudRequest(
                                operation = CrudType.CREATE,
                                entity = ActionGeneric(ActionDeadlineChanged(
                                    actionId = UUID.randomUUID(),
                                    user = UserImpl(
                                        loggedInUser
                                            ?: throw AmttdException(AmttdException.ErrorCode.LOGIN_REQUIRED)
                                    ),
                                    timeCreated = Calendar.getInstance(),
                                    oldValue = null,
                                    newValue = null
                                ).apply {
                                    oldDeadline = viewModel.entry.value?.deadline
                                    newDeadline = date
                                }),
                                userId = loggedInUser?.uuid,
                                entryId = viewModel.entryId.value
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
                        }.start()
                    },
                    currentDate[Calendar.HOUR_OF_DAY],
                    currentDate[Calendar.MINUTE],
                    true
                ).show()
            },
            currentDate[Calendar.YEAR],
            currentDate[Calendar.MONTH],
            currentDate[Calendar.DATE]
        ).apply {
            datePicker.minDate = currentDate.timeInMillis
        }.show()
    }
}