package xyz.tcreopargh.amttd.ui.group_user

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.bean.request.GroupRemoveUserRequest
import xyz.tcreopargh.amttd.common.bean.request.GroupTransferLeaderRequest
import xyz.tcreopargh.amttd.common.bean.response.WorkGroupDataSetChangedResponse
import xyz.tcreopargh.amttd.common.data.IUser
import xyz.tcreopargh.amttd.common.data.IWorkGroup
import xyz.tcreopargh.amttd.common.exception.AmttdException
import xyz.tcreopargh.amttd.util.gson
import xyz.tcreopargh.amttd.util.okHttpRequest
import xyz.tcreopargh.amttd.util.toJsonRequest
import java.util.*


/**
 * @author TCreopargh
 */
class GroupUserAdapter(
    var users: List<IUser>,
    private val fragment: GroupUserFragment,
    var workGroup: IWorkGroup?
) : RecyclerView.Adapter<GroupUserAdapter.ViewHolder>() {

    val mainActivity
        get() = fragment.mainActivity

    private val isLeader
        get() = workGroup?.leader?.uuid == fragment.loggedInUser?.uuid

    private fun isYou(uuid: UUID) = fragment.loggedInUser?.uuid == uuid

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootView: View = view
        val userAvatarView: ImageView = view.findViewById(R.id.userAvatarView)
        val usernameView: TextView = view.findViewById(R.id.usernameText)
        val userEmailView: TextView = view.findViewById(R.id.userEmailText)
        val youText: TextView = view.findViewById(R.id.textYou)
        val optionsButton: ImageButton = view.findViewById(R.id.moreButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.group_user_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.apply {
            usernameView.text = user.username
            userEmailView.text = user.email
            if (isYou(user.uuid)) {
                if (isLeader) {
                    youText.text = fragment.context?.getString(R.string.you_leader)
                } else {
                    youText.text = fragment.context?.getString(R.string.you)
                }
                optionsButton.isEnabled = false
                optionsButton.visibility = View.GONE
                rootView.setOnClickListener {
                    Toast.makeText(
                        fragment.context,
                        R.string.cant_kick_yourself,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                if (isLeader) {
                    optionsButton.isEnabled = true
                    optionsButton.visibility = View.VISIBLE
                    optionsButton.setOnClickListener {
                        showPopupMenu(it, user)
                    }
                    rootView.setOnClickListener {
                        showPopupMenu(optionsButton, user)
                    }
                } else {
                    optionsButton.isEnabled = false
                    optionsButton.visibility = View.GONE
                    rootView.setOnClickListener {
                        Toast.makeText(
                            fragment.context,
                            R.string.no_permission_to_manage_users,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                if (user.uuid == workGroup?.leader?.uuid) {
                    youText.text = fragment.context?.getString(R.string.leader)
                } else {
                    youText.text = ""
                }
            }
        }
    }

    private fun showPopupMenu(destView: View, user: IUser) {
        fragment.context?.let { context ->
            PopupMenu(context, destView).apply {
                menuInflater
                    .inflate(R.menu.options_group_user_operations, menu)
                setOnMenuItemClickListener {
                    return@setOnMenuItemClickListener when (it.itemId) {
                        R.id.actionKickUser       -> {
                            AlertDialog.Builder(context).apply {
                                setTitle(R.string.action_kick_user)
                                setMessage(
                                    context.getString(
                                        R.string.kick_user_confirm,
                                        user.username
                                    )
                                )
                                setPositiveButton(R.string.confirm) { dialog, _ ->
                                    kickUser(
                                        GroupRemoveUserRequest(
                                            groupId = workGroup?.groupId,
                                            actionPerformerId = fragment.loggedInUser?.uuid,
                                            targetUserId = user.uuid
                                        )
                                    )
                                    dialog.dismiss()
                                }
                                setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                            }.create().show()
                            true
                        }
                        R.id.actionTransferLeader -> {
                            AlertDialog.Builder(context).apply {
                                setTitle(R.string.action_transfer_leader)
                                setMessage(
                                    context.getString(
                                        R.string.transfer_leader_confirm,
                                        user.username
                                    )
                                )
                                setPositiveButton(R.string.confirm) { dialog, _ ->
                                    transferLeadership(
                                        GroupTransferLeaderRequest(
                                            groupId = workGroup?.groupId,
                                            actionPerformerId = fragment.loggedInUser?.uuid,
                                            targetUserId = user.uuid
                                        )
                                    )
                                    dialog.dismiss()
                                }
                                setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                            }.create().show()
                            true
                        }
                        else                      -> false
                    }
                }
                show()
            }
        }
    }

    override fun getItemCount(): Int = users.size

    private fun kickUser(requestBody: GroupRemoveUserRequest) {
        Thread {
            try {
                val request = okHttpRequest("/workgroup/users/kick")
                    .post(
                        requestBody.toJsonRequest()
                    ).build()
                val response = AMTTD.okHttpClient.newCall(request).execute()
                val body = response.body?.string()
                val result: WorkGroupDataSetChangedResponse =
                    gson.fromJson(
                        body,
                        object : TypeToken<WorkGroupDataSetChangedResponse>() {}.type
                    )
                if (result.success != true) {
                    throw AmttdException.getFromErrorCode(result.error)
                }
                fragment.viewModel.dirty.postValue(true)
                result.updatedWorkGroup?.let { fragment.viewModel.workGroup.postValue(it) }
            } catch (e: Exception) {
                Log.e(AMTTD.logTag, e.stackTraceToString())
                fragment.viewModel.exception.postValue(AmttdException.getFromException(e))
            }
        }.start()
    }

    private fun transferLeadership(requestBody: GroupTransferLeaderRequest) {
        Thread {
            try {
                val request = okHttpRequest("/workgroup/users/transfer")
                    .post(
                        requestBody.toJsonRequest()
                    ).build()
                val response = AMTTD.okHttpClient.newCall(request).execute()
                val body = response.body?.string()
                val result: WorkGroupDataSetChangedResponse =
                    gson.fromJson(
                        body,
                        object : TypeToken<WorkGroupDataSetChangedResponse>() {}.type
                    )
                if (result.success != true) {
                    throw AmttdException.getFromErrorCode(result.error)
                }
                fragment.viewModel.dirty.postValue(true)
                result.updatedWorkGroup?.let { fragment.viewModel.workGroup.postValue(it) }
            } catch (e: Exception) {
                Log.e(AMTTD.logTag, e.stackTraceToString())
                fragment.viewModel.exception.postValue(AmttdException.getFromException(e))
            }
        }.start()
    }
}