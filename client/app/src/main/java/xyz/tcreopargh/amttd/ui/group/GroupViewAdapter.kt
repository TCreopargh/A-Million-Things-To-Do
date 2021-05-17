package xyz.tcreopargh.amttd.ui.group

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import okhttp3.Request
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.bean.request.WorkGroupActionRequest
import xyz.tcreopargh.amttd.common.bean.response.WorkGroupActionResponse
import xyz.tcreopargh.amttd.common.data.CrudType
import xyz.tcreopargh.amttd.common.data.IWorkGroup
import xyz.tcreopargh.amttd.common.data.WorkGroupImpl
import xyz.tcreopargh.amttd.common.exception.AmttdException
import xyz.tcreopargh.amttd.ui.todo.TodoViewFragment
import xyz.tcreopargh.amttd.util.*

/**
 * @author TCreopargh
 */
class GroupViewAdapter(
    var workGroups: MutableList<IWorkGroup>,
    private val fragment: Fragment?
) : RecyclerView.Adapter<GroupViewAdapter.ViewHolder>() {

    val activity
        get() = fragment?.activity

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupNameText: TextView = view.findViewById(R.id.groupNameText)
        val groupTimeText: TextView = view.findViewById(R.id.groupTimeText)
        val groupUserCountText: TextView = view.findViewById(R.id.groupUserCountText)
        val groupCard: CardView = view.findViewById(R.id.groupCard)
        val groupEditButton: ImageButton = view.findViewById(R.id.workGroupEditButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.group_view_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val workGroup = workGroups[position]
        holder.apply {
            groupNameText.text = workGroup.name
            groupUserCountText.text = workGroup.usersInGroup.size.toString()
            groupTimeText.text = workGroup.timeCreated.format()
            groupCard.setOnClickListener {
                val fragmentManager = activity?.supportFragmentManager
                val targetFragment = TodoViewFragment.newInstance().apply {
                    arguments = bundleOf("groupId" to workGroup.groupId)
                }
                fragmentManager?.beginTransaction()?.apply {
                    replace(
                        R.id.main_fragment_parent,
                        targetFragment,
                        targetFragment::class.simpleName
                    )
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    addToBackStack(null)
                    commit()
                }
            }
            groupEditButton.setOnClickListener {
                val viewModel = (fragment as? GroupViewFragment)?.viewModel
                Thread {
                    val group: IWorkGroup? = try {
                        val uuid = workGroup.groupId
                        val request = Request.Builder()
                            .post(
                                WorkGroupActionRequest(
                                    CrudType.READ,
                                    WorkGroupImpl(groupId = uuid)
                                ).toJsonRequest()
                            ).url(rootUrl.withPath("/workgroup"))
                            .build()
                        val response = AMTTD.okHttpClient.newCall(request).execute()
                        val body = response.body?.string()
                        // Don't simplify this
                        val result: WorkGroupActionResponse =
                            gson.fromJson(
                                body,
                                object : TypeToken<WorkGroupActionResponse>() {}.type
                            )
                        if (result.success != true) {
                            throw AmttdException.getFromErrorCode(result.error)
                        }
                        result.entity ?: throw RuntimeException("Invalid data")
                    } catch (e: Exception) {
                        Log.e(AMTTD.logTag, e.stackTraceToString())
                        viewModel?.exception?.postValue(AmttdException.getFromException(e))
                        null
                    }
                    viewModel?.groupToEdit?.postValue(group)
                }.start()
            }
        }
    }

    override fun getItemCount(): Int = workGroups.size
}