package xyz.tcreopargh.amttd.ui.group_user

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
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.api.data.IUser
import xyz.tcreopargh.amttd.api.data.IWorkGroup
import xyz.tcreopargh.amttd.api.exception.AmttdException
import xyz.tcreopargh.amttd.api.json.request.GroupUserViewRequest
import xyz.tcreopargh.amttd.api.json.response.GroupUserViewResponse
import xyz.tcreopargh.amttd.ui.FragmentOnMainActivityBase
import xyz.tcreopargh.amttd.util.gson
import xyz.tcreopargh.amttd.util.okHttpRequest
import xyz.tcreopargh.amttd.util.toJsonRequest
import java.util.*

class GroupUserFragment : FragmentOnMainActivityBase(R.string.manage_users_title) {

    companion object {
        fun newInstance() = GroupUserFragment()
    }

    lateinit var viewModel: GroupUserViewModel
    private lateinit var swipeContainer: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.group_user_fragment, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.groupUserRecyclerView)

        swipeContainer = view.findViewById(R.id.groupUserSwipeContainer)
        val adapter =
            GroupUserAdapter(
                viewModel.users.value ?: listOf(),
                this,
                viewModel.workGroup.value
            )
        swipeContainer.setOnRefreshListener { initializeItems() }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.workGroup.observe(viewLifecycleOwner) {
            adapter.workGroup = it
        }
        viewModel.users.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.users = it
                adapter.notifyDataSetChanged()
                swipeContainer.isRefreshing = false
            }
        }

        viewModel.exception.observe(viewLifecycleOwner) {
            it?.run {
                Toast.makeText(
                    context,
                    getString(R.string.error_occurred) + it.getLocalizedString(context),
                    Toast.LENGTH_SHORT
                ).show()
                swipeContainer.isRefreshing = false
                viewModel.exception.value = null
            }
        }

        viewModel.dirty.observe(viewLifecycleOwner) {
            if (it) {
                adapter.workGroup = viewModel.workGroup.value
                initializeItems()
                viewModel.dirty.value = false
            }
        }
        initializeItems()
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments?.deepCopy()
        viewModel = ViewModelProvider(this).get(GroupUserViewModel::class.java)
        viewModel.workGroup.value = args?.getSerializable("workGroup") as? IWorkGroup
    }


    private fun initializeItems() {
        swipeContainer.isRefreshing = true
        Thread {
            val users: List<IUser> = try {
                val groupId = viewModel.workGroup.value?.groupId ?: return@Thread
                val request = okHttpRequest("/workgroup/users")
                    .post(
                        GroupUserViewRequest(
                            groupId = groupId,
                            userId = loggedInUser?.uuid
                        ).toJsonRequest()
                    ).build()
                val response = AMTTD.okHttpClient.newCall(request).execute()
                val body = response.body?.string()
                val result: GroupUserViewResponse =
                    gson.fromJson(body, object : TypeToken<GroupUserViewResponse>() {}.type)
                if (result.success != true) {
                    throw AmttdException.getFromErrorCode(result.error)
                }
                result.users ?: throw AmttdException(AmttdException.ErrorCode.INVALID_JSON)
            } catch (e: Exception) {
                Log.e(AMTTD.logTag, e.stackTraceToString())
                viewModel.exception.postValue(AmttdException.getFromException(e))
                listOf()
            }
            viewModel.users.postValue(users)
        }.start()
    }
}