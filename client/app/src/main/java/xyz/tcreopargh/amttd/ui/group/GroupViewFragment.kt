package xyz.tcreopargh.amttd.ui.group

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.NOT_FOCUSABLE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.reflect.TypeToken
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.group_view_fragment.*
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.MainActivity
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.bean.request.JoinWorkGroupRequest
import xyz.tcreopargh.amttd.common.bean.request.WorkGroupCrudRequest
import xyz.tcreopargh.amttd.common.bean.request.WorkGroupViewRequest
import xyz.tcreopargh.amttd.common.bean.response.JoinWorkGroupResponse
import xyz.tcreopargh.amttd.common.bean.response.WorkGroupCrudResponse
import xyz.tcreopargh.amttd.common.bean.response.WorkGroupViewResponse
import xyz.tcreopargh.amttd.common.data.CrudType
import xyz.tcreopargh.amttd.common.data.IWorkGroup
import xyz.tcreopargh.amttd.common.data.WorkGroupImpl
import xyz.tcreopargh.amttd.common.exception.AmttdException
import xyz.tcreopargh.amttd.ui.FragmentOnMainActivityBase
import xyz.tcreopargh.amttd.util.*
import java.util.*


/**
 * @author TCreopargh
 */
class GroupViewFragment : FragmentOnMainActivityBase(R.string.work_groups_title) {

    companion object {
        fun newInstance() = GroupViewFragment()
    }

    lateinit var viewModel: GroupViewModel

    private lateinit var groupSwipeContainer: SwipeRefreshLayout

    private var joinWorkGroupDialog: AlertDialog? = null
    private var joinWorkGroupDialogView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.group_view_fragment, container, false)
        val groupRecyclerView = view.findViewById<RecyclerView>(R.id.groupRecyclerView)
        groupSwipeContainer = view.findViewById(R.id.groupSwipeContainer)
        val adapter = GroupViewAdapter(viewModel.groups.value ?: listOf(), this)
        groupRecyclerView.adapter = adapter
        groupRecyclerView.layoutManager = LinearLayoutManager(context)
        groupSwipeContainer.setOnRefreshListener { initializeItems() }

        val emptyText = view.findViewById<TextView>(R.id.workGroupEmptyText)
        viewModel.groups.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.workGroups = it
                adapter.notifyDataSetChanged()
                groupSwipeContainer.isRefreshing = false
                if (it.isEmpty()) {
                    emptyText.visibility = View.VISIBLE
                    groupRecyclerView.visibility = View.GONE
                } else {
                    emptyText.visibility = View.GONE
                    groupRecyclerView.visibility = View.VISIBLE
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
                groupSwipeContainer.isRefreshing = false
                viewModel.exception.value = null
            }
        }

        viewModel.groupToEdit.observe(viewLifecycleOwner) {
            it?.run {
                val loggedInUserId =
                    loggedInUser?.uuid ?: throw AmttdException(
                        AmttdException.ErrorCode.LOGIN_REQUIRED
                    )
                AlertDialog.Builder(context).apply {
                    @SuppressLint("InflateParams")
                    val viewRoot = layoutInflater.inflate(R.layout.group_edit_layout, null)
                    val titleText = viewRoot.findViewById<EditText>(R.id.groupEditTitleText)
                    titleText.setText(it.name)
                    setView(viewRoot)
                    if (it.leader?.uuid != loggedInUserId) {
                        titleText.focusable = NOT_FOCUSABLE
                        titleText.inputType = InputType.TYPE_NULL
                        titleText.setOnClickListener {
                            Toast.makeText(
                                context,
                                R.string.work_group_edit_no_permission,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    setPositiveButton(R.string.confirm) { dialog, _ ->
                        object :
                            CrudTask<WorkGroupImpl, WorkGroupCrudRequest, WorkGroupCrudResponse>(
                                request = WorkGroupCrudRequest(
                                    operation = CrudType.UPDATE,
                                    entity = WorkGroupImpl(it).apply {
                                        name = titleText.text.toString()
                                    },
                                    userId = loggedInUserId
                                ),
                                path = "/workgroup",
                                responseType = object : TypeToken<WorkGroupCrudResponse>() {}.type
                            ) {
                            override fun onSuccess(entity: WorkGroupImpl?) {
                                Thread.sleep(200)
                                viewModel.dirty.postValue(true)
                            }

                            override fun onFailure(e: Exception) {
                                viewModel.exception.postValue(AmttdException.getFromException(e))
                            }
                        }.start()
                        dialog.dismiss()
                    }
                    val neutralButtonText =
                        if (it.leader?.uuid == loggedInUserId) {
                            R.string.remove
                        } else {
                            R.string.leave
                        }
                    setNeutralButton(neutralButtonText) { dialog, _ ->
                        AlertDialog.Builder(context).apply {
                            if (it.leader?.uuid == loggedInUserId) {
                                setTitle(R.string.remove_work_group)
                                setMessage(R.string.remove_work_group_confirm)
                            } else {
                                setTitle(R.string.leave_work_group)
                                setMessage(R.string.leave_work_group_confirm)
                            }
                            setPositiveButton(R.string.confirm) { dialogInner, _ ->
                                object :
                                    CrudTask<WorkGroupImpl, WorkGroupCrudRequest, WorkGroupCrudResponse>(
                                        request = WorkGroupCrudRequest(
                                            operation = CrudType.DELETE,
                                            entity = WorkGroupImpl(it),
                                            userId = loggedInUserId
                                        ),
                                        path = "/workgroup",
                                        responseType = object :
                                            TypeToken<WorkGroupCrudResponse>() {}.type
                                    ) {
                                    override fun onSuccess(entity: WorkGroupImpl?) {
                                        Thread.sleep(200)
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
                                dialogInner.dismiss()
                            }
                            setNegativeButton(R.string.cancel) { dialogInner, _ -> dialogInner.cancel() }
                        }.create().show()

                    }
                    setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                }.create().show()
                viewModel.groupToEdit.value = null
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
        viewModel =
            ViewModelProvider(this).get(GroupViewModel::class.java)
    }

    fun joinWorkGroup() {
        joinWorkGroupDialog = AlertDialog.Builder(context).apply {
            @SuppressLint("InflateParams")
            joinWorkGroupDialogView = layoutInflater.inflate(R.layout.group_join_layout, null)
            val invitationCodeText =
                joinWorkGroupDialogView?.findViewById<EditText>(R.id.groupJoinInvitationCodeText)
            val scanButton = joinWorkGroupDialogView?.findViewById<ImageButton>(R.id.scanButton)
            scanButton?.setOnClickListener {
                IntentIntegrator.forSupportFragment(this@GroupViewFragment)
                    .initiateScan(mutableSetOf(IntentIntegrator.QR_CODE))
            }
            setView(joinWorkGroupDialogView)
            setPositiveButton(R.string.confirm) { dialog, _ ->
                Thread {
                    try {
                        val request = okHttpRequest("/workgroups/join")
                            .post(
                                JoinWorkGroupRequest(
                                    userId = loggedInUser?.uuid
                                        ?: throw AmttdException(AmttdException.ErrorCode.LOGIN_REQUIRED),
                                    invitationCode = invitationCodeText?.text.toString()
                                ).toJsonRequest()
                            )
                            .build()
                        val response = AMTTD.okHttpClient.newCall(request).execute()
                        val body = response.body?.string()
                        val result: JoinWorkGroupResponse =
                            gson.fromJson(
                                body,
                                object : TypeToken<JoinWorkGroupResponse>() {}.type
                            )
                        if (result.success != true) {
                            throw AmttdException.getFromErrorCode(result.error)
                        }
                        viewModel.dirty.postValue(true)
                    } catch (e: Exception) {
                        Log.e(AMTTD.logTag, e.stackTraceToString())
                        viewModel.exception.postValue(AmttdException.getFromException(e))
                    }
                }.start()
                dialog.dismiss()
            }
            setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
        }.create()
        joinWorkGroupDialog?.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null && joinWorkGroupDialog?.isShowing == true) {
                val content = result.contents
                if (!isGroupUri(content)) {
                    Toast.makeText(context, R.string.invalid_group_qr_code, Toast.LENGTH_SHORT)
                        .show()
                    return
                }
                val code = getGroupInvitationCode(content)
                val invitationCodeText =
                    joinWorkGroupDialogView?.findViewById<EditText>(R.id.groupJoinInvitationCodeText)
                invitationCodeText?.setText(code, TextView.BufferType.EDITABLE)
            }
        } else {
            @Suppress("DEPRECATION")
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun addWorkGroup() {
        AlertDialog.Builder(context).apply {
            @SuppressLint("InflateParams")
            val viewRoot = layoutInflater.inflate(R.layout.group_edit_layout, null)
            val titleText = viewRoot.findViewById<EditText>(R.id.groupEditTitleText)
            titleText.setText("")
            setView(viewRoot)
            viewRoot.findViewById<TextView>(R.id.groupEditDialogTitle)?.text =
                getString(R.string.add_work_group)
            setPositiveButton(R.string.confirm) { dialog, _ ->
                object : CrudTask<WorkGroupImpl, WorkGroupCrudRequest, WorkGroupCrudResponse>(
                    request = WorkGroupCrudRequest(
                        operation = CrudType.CREATE,
                        entity = WorkGroupImpl(
                            name = titleText.text.toString()
                        ),
                        userId = (activity as? MainActivity)?.loggedInUser?.uuid
                    ),
                    path = "/workgroup",
                    responseType = object : TypeToken<WorkGroupCrudResponse>() {}.type
                ) {
                    override fun onSuccess(entity: WorkGroupImpl?) {
                        Thread.sleep(500)
                        viewModel.dirty.postValue(true)
                    }

                    override fun onFailure(e: Exception) {
                        viewModel.exception.postValue(AmttdException.getFromException(e))
                    }
                }.start()
                dialog.dismiss()
            }
            setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
        }.create().show()
    }

    private fun initializeItems() {
        groupSwipeContainer.isRefreshing = true
        Thread {
            val workGroups: List<IWorkGroup> = try {
                val uuid = (activity as? MainActivity)?.loggedInUser?.uuid ?: return@Thread
                val request = okHttpRequest("/workgroups")
                    .post(
                        WorkGroupViewRequest(uuid).toJsonRequest()
                    )
                    .build()
                val response = AMTTD.okHttpClient.newCall(request).execute()
                val body = response.body?.string()
                val result: WorkGroupViewResponse =
                    gson.fromJson(body, object : TypeToken<WorkGroupViewResponse>() {}.type)
                if (result.success != true) {
                    throw AmttdException.getFromErrorCode(result.error)
                }
                result.workGroups ?: throw RuntimeException("Invalid data")
            } catch (e: Exception) {
                Log.e(AMTTD.logTag, e.stackTraceToString())
                viewModel.exception.postValue(AmttdException.getFromException(e))
                listOf()
            }
            viewModel.postGroup(workGroups)
        }.start()
    }

}