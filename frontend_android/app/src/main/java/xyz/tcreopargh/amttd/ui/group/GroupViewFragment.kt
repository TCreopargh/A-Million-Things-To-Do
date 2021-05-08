package xyz.tcreopargh.amttd.ui.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.group_view_fragment.*
import okhttp3.Request
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.MainActivity
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.data.interactive.IWorkGroup
import xyz.tcreopargh.amttd.data.interactive.WorkGroupImpl
import xyz.tcreopargh.amttd.util.*
import java.util.*

/**
 * @author TCreopargh
 */
class GroupViewFragment : Fragment() {

    private lateinit var viewModel: GroupViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.group_view_fragment, container, false)
        val groupRecyclerView = view.findViewById<RecyclerView>(R.id.groupRecyclerView)
        val adapter = GroupViewAdapter(viewModel.groups.value ?: mutableListOf(), activity)
        val groupSwipeContainer = view.findViewById<SwipeRefreshLayout>(R.id.groupSwipeContainer)
        groupRecyclerView.adapter = adapter
        groupRecyclerView.layoutManager = LinearLayoutManager(context)
        groupSwipeContainer.setOnRefreshListener { initializeItems() }
        viewModel.groups.observe(viewLifecycleOwner) {
            println(it.joinToString { WorkGroupImpl(it).toString() })
            adapter.workGroups = it ?: mutableListOf()
            adapter.notifyDataSetChanged()
            groupSwipeContainer.isRefreshing = false
        }

        val mainActivity = activity as? MainActivity
        mainActivity?.viewModel?.loginRepository?.observe(mainActivity) {
            initializeItems()
        }
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(this).get(GroupViewModel::class.java)
        setHasOptionsMenu(true)
        initializeItems()
    }


    //TODO: Replace with actual data
    private fun initializeItems() {
        Thread {
            val uuid = (activity as? MainActivity)?.loggedInUser?.uuid ?: return@Thread
            val request = Request.Builder()
                .post(
                    jsonObjectOf(
                        "uuid" to uuid
                    ).toRequestBody()
                ).url(rootUrl.withPath("/workgroups"))
                .build()
            val response = AMTTD.okHttpClient.newCall(request).execute()
            val body = response.body?.string()
            val workGroups: List<IWorkGroup> =
                gson.fromJson(body, object : TypeToken<List<WorkGroupImpl>>() {}.type)
            viewModel.postValue(workGroups.toMutableList())
        }.start()
    }

}